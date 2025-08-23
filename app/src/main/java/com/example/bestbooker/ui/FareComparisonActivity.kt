package com.example.bestbooker.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels // ✅ Required for viewModels() delegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bestbooker.databinding.ActivityFareComparisonBinding
import com.example.bestbooker.data.viewmodel.FareViewModel

class FareComparisonActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFareComparisonBinding
    private val viewModel: FareViewModel by viewModels() // ✅ Simplified ViewModel initialization
    private val adapter = FareAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFareComparisonBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 🧭 Get coordinates from intent
        val pLat = intent.getDoubleExtra("plat", 0.0)
        val pLng = intent.getDoubleExtra("plng", 0.0)
        val dLat = intent.getDoubleExtra("dlat", 0.0)
        val dLng = intent.getDoubleExtra("dlng", 0.0)

        if (pLat == 0.0 || dLat == 0.0) {
            Toast.makeText(this, "Invalid coordinates", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // 🛠️ Setup RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        // 🔑 Replace with your actual token
        val token = "your_uber_api_token_here"

        // 🚀 Fetch fares
        viewModel.getFares(pLat, pLng, dLat, dLng, token)

        // 👀 Observe results
        viewModel.fares.observe(this) { fares ->
            if (fares.isEmpty()) {
                Toast.makeText(this, "No fares found", Toast.LENGTH_SHORT).show()
            }
            adapter.submitList(fares)
        }
    }
}