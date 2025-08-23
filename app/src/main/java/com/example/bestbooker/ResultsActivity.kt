package com.example.bestbooker

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.bestbooker.data.AppDb
import com.example.bestbooker.data.Booking
import com.example.bestbooker.databinding.ActivityResultsBinding
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.*

class ResultsActivity : AppCompatActivity() {
    private lateinit var b: ActivityResultsBinding
    private lateinit var p: LatLng
    private lateinit var d: LatLng

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityResultsBinding.inflate(layoutInflater)
        setContentView(b.root)

        p = LatLng(intent.getDoubleExtra("pLat", 0.0), intent.getDoubleExtra("pLng", 0.0))
        d = LatLng(intent.getDoubleExtra("dLat", 0.0), intent.getDoubleExtra("dLng", 0.0))
        val km = haversineKm(p, d)
        b.tvRoute.text = "Route: %.2f km".format(km)

        val uberFare = fare(km, base = 30.0, perKm = 12.0)
        val olaFare  = fare(km, base = 28.0, perKm = 12.5)
        val rapFare  = fare(km, base = 20.0, perKm = 9.0)

        b.btnUber.text = "Book with Uber (₹%.0f)".format(uberFare)
        b.btnOla.text  = "Book with Ola (₹%.0f)".format(olaFare)
        b.btnRapido.text = "Book with Rapido (₹%.0f)".format(rapFare)

        b.btnUber.setOnClickListener {
            openUber(p, d)
            saveHistory("Uber", uberFare)
        }
        b.btnOla.setOnClickListener {
            openOla()   // Ola deep-link search isn’t public; open app
            saveHistory("Ola", olaFare)
        }
        b.btnRapido.setOnClickListener {
            openRapido() // Rapido deep-link not public; open app
            saveHistory("Rapido", rapFare)
        }
    }

    private fun openUber(p: LatLng, d: LatLng) {
        val uri = Uri.parse(
            "uber://?action=setPickup" +
                    "&pickup[latitude]=${p.latitude}&pickup[longitude]=${p.longitude}" +
                    "&dropoff[latitude]=${d.latitude}&dropoff[longitude]=${d.longitude}" +
                    "&product_id=auto" // optional: if you know the product ID for Auto
        )
        val intent = Intent(Intent.ACTION_VIEW, uri).setPackage("com.ubercab")
        if (isInstalled("com.ubercab")) startActivity(intent)
        else startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(
            "https://m.uber.com/ul/?action=setPickup&pickup[latitude]=${p.latitude}" +
                    "&pickup[longitude]=${p.longitude}&dropoff[latitude]=${d.latitude}" +
                    "&dropoff[longitude]=${d.longitude}"
        )))
    }

    private fun openOla() {
        val pkg = "com.olacabs.customer"
        if (isInstalled(pkg)) startActivity(packageManager.getLaunchIntentForPackage(pkg))
        else startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://ola.onelink.me/3614436312")) )
    }

    private fun openRapido() {
        val pkg = "com.rapido.passenger"
        if (isInstalled(pkg)) startActivity(packageManager.getLaunchIntentForPackage(pkg))
        else startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.rapido.bike/")) )
    }

    private fun isInstalled(pkg: String) =
        try { packageManager.getPackageInfo(pkg, 0); true } catch (_: Exception) { false }

    private fun haversineKm(a: LatLng, b: LatLng): Double {
        val R = 6371.0
        val dLat = Math.toRadians(b.latitude - a.latitude)
        val dLng = Math.toRadians(b.longitude - a.longitude)
        val sa = sin(dLat/2).pow(2.0) + cos(Math.toRadians(a.latitude)) * cos(Math.toRadians(b.latitude)) * sin(dLng/2).pow(2.0)
        return 2 * R * asin(sqrt(sa))
    }
    private fun fare(km: Double, base: Double, perKm: Double) = max(base, base + km * perKm)

    private fun saveHistory(provider: String, fare: Double) {
        lifecycleScope.launch(Dispatchers.IO) {
            AppDb.get(this@ResultsActivity).bookingDao().insert(
                Booking(
                    provider = provider,
                    pickupLat = p.latitude, pickupLng = p.longitude,
                    dropLat = d.latitude, dropLng = d.longitude,
                    fare = fare
                )
            )
        }
    }
}
