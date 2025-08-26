package com.example.bestbooker

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.bestbooker.databinding.ActivityMainBinding
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import kotlinx.coroutines.*
import java.util.Locale

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var b: ActivityMainBinding
    private var map: GoogleMap? = null
    private var pickup: LatLng? = null
    private var drop: LatLng? = null
    private var isSelectingPickup = true

    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val reqLoc = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { /* no-op */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        val prefs = getSharedPreferences("user", MODE_PRIVATE)
        val name = prefs.getString("name", null)
        if (name == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)

        // ✅ Initialize Places SDK
        Places.initialize(applicationContext, "YOUR GOOGLE API KEY")
        val placesClient = Places.createClient(this)

        // ✅ Setup Bottom Navigation
        b.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> true
                R.id.nav_profile -> {
                    Toast.makeText(this, "Opening Profile...", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                else -> false
            }
        }

        // ✅ Setup Map
        (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment).getMapAsync(this)

        // ✅ Request Location Permissions
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            reqLoc.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        }

        // ✅ Setup Autocomplete Fragments
        val pickupFragment = supportFragmentManager.findFragmentById(R.id.pickupFragment) as AutocompleteSupportFragment
        val dropFragment = supportFragmentManager.findFragmentById(R.id.dropFragment) as AutocompleteSupportFragment

        val placeFields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS)
        pickupFragment.setPlaceFields(placeFields)
        dropFragment.setPlaceFields(placeFields)

        pickupFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                pickup = place.latLng
                map?.animateCamera(CameraUpdateFactory.newLatLngZoom(place.latLng!!, 15f))
                updateMap()
                Toast.makeText(this@MainActivity, "Pickup selected", Toast.LENGTH_SHORT).show()
            }

            override fun onError(status: Status) {
                Toast.makeText(this@MainActivity, "Pickup error: ${status.statusMessage}", Toast.LENGTH_SHORT).show()
            }
        })

        dropFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                drop = place.latLng
                map?.animateCamera(CameraUpdateFactory.newLatLngZoom(place.latLng!!, 15f))
                updateMap()
                Toast.makeText(this@MainActivity, "Drop selected", Toast.LENGTH_SHORT).show()
            }

            override fun onError(status: Status) {
                Toast.makeText(this@MainActivity, "Drop error: ${status.statusMessage}", Toast.LENGTH_SHORT).show()
            }
        })

        // ✅ Confirm Button Logic
        b.btnConfirm.setOnClickListener {
            val currentMap = map ?: return@setOnClickListener
            val selected = currentMap.cameraPosition.target
            val address = b.tvSelectedAddress.text.toString()

            if (isSelectingPickup) {
                pickup = selected
                Toast.makeText(this, "Pickup confirmed. Now select drop location.", Toast.LENGTH_SHORT).show()
                isSelectingPickup = false
                b.tvSelectedAddress.text = ""
            } else {
                drop = selected
                Toast.makeText(this, "Drop confirmed.", Toast.LENGTH_SHORT).show()
            }

            updateMap()
        }

        // ✅ Find Ride Button
        b.btnFindRide.setOnClickListener {
            val p = pickup
            val d = drop
            if (p == null || d == null) {
                Toast.makeText(this, "Select pickup and drop locations", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this, ResultsActivity::class.java).apply {
                putExtra("pLat", p.latitude)
                putExtra("pLng", p.longitude)
                putExtra("dLat", d.latitude)
                putExtra("dLng", d.longitude)
            }
            startActivity(intent)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        val defaultCity = LatLng(28.6139, 77.2090) // Delhi
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultCity, 11f))

        googleMap.setOnCameraIdleListener {
            val center = googleMap.cameraPosition.target
            coroutineScope.launch {
                val address = withContext(Dispatchers.IO) { reverseGeocode(center) }
                withContext(Dispatchers.Main) {
                    b.tvSelectedAddress.text = address ?: "Selected location"
                }
            }
        }
    }

    private fun reverseGeocode(latLng: LatLng): String? {
        return try {
            val geo = Geocoder(this, Locale.getDefault())
            val result = geo.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (!result.isNullOrEmpty()) {
                result[0].getAddressLine(0)
            } else null
        } catch (e: Exception) {
            null
        }
    }

    private fun updateMap() {
        map?.clear()

        pickup?.let {
            map?.addMarker(
                MarkerOptions()
                    .position(it)
                    .title("Pickup")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            )
        }

        drop?.let {
            map?.addMarker(
                MarkerOptions()
                    .position(it)
                    .title("Drop")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            )
        }

        b.btnFindRide.visibility = if (pickup != null && drop != null) View.VISIBLE else View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }
}
