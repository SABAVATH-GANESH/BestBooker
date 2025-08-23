package com.example.bestbooker

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bestbooker.data.AppDb
import com.example.bestbooker.data.Booking
import com.example.bestbooker.databinding.ActivityProfileBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class ProfileActivity : AppCompatActivity() {
    private lateinit var b: ActivityProfileBinding
    private val adapter = BookingAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(b.root)

        // Load user info
        val prefs = getSharedPreferences("user", MODE_PRIVATE)
        b.tvName.text = "Name: ${prefs.getString("name", "User")}"
        b.tvEmail.text = "Email: ${prefs.getString("email", "-")}"
        b.tvPhone.text = "Phone: ${prefs.getString("phone", "-")}"

        // Setup RecyclerView
        b.rvHistory.layoutManager = LinearLayoutManager(this)
        b.rvHistory.adapter = adapter

        // Load booking history safely
        lifecycleScope.launch {
            try {
                val items = withContext(Dispatchers.IO) {
                    AppDb.get(this@ProfileActivity).bookingDao().all()
                }
                if (items.isEmpty()) {
                    Toast.makeText(this@ProfileActivity, "No bookings yet", Toast.LENGTH_SHORT).show()
                }
                adapter.submit(items)
            } catch (e: Exception) {
                Toast.makeText(this@ProfileActivity, "Error loading history: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}