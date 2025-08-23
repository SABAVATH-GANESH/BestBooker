package com.example.bestbooker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bestbooker.data.Booking
import com.example.bestbooker.R
import java.text.SimpleDateFormat
import java.util.*

class BookingAdapter : RecyclerView.Adapter<BookingAdapter.VH>() {
    private val data = mutableListOf<Booking>()

    fun submit(list: List<Booking>) {
        data.clear()
        data.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int = data.size

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvProvider: TextView = itemView.findViewById(R.id.tvProvider)
        private val tvSummary: TextView = itemView.findViewById(R.id.tvSummary)
        private val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        private val fmt = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())

        fun bind(bk: Booking) {
            tvProvider.text = bk.provider
            tvSummary.text = "₹%.0f  (%.4f, %.4f) → (%.4f, %.4f)".format(
                bk.fare, bk.pickupLat, bk.pickupLng, bk.dropLat, bk.dropLng
            )
            tvTime.text = fmt.format(Date(bk.createdAt))
        }
    }
}