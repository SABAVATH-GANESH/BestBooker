package com.example.bestbooker.ui

import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.*
import com.example.bestbooker.R
import com.example.bestbooker.data.model.UberFareResponse

class FareAdapter : ListAdapter<UberFareResponse, FareAdapter.FareViewHolder>(DiffCallback()) {

    class FareViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(fare: UberFareResponse) {
            itemView.findViewById<TextView>(R.id.tvRideType).text = fare.ride_type
            itemView.findViewById<TextView>(R.id.tvFare).text = "${fare.amount} ${fare.currency}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FareViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_fare, parent, false)
        return FareViewHolder(view)
    }

    override fun onBindViewHolder(holder: FareViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<UberFareResponse>() {
        override fun areItemsTheSame(oldItem: UberFareResponse, newItem: UberFareResponse) = oldItem.fare_id == newItem.fare_id
        override fun areContentsTheSame(oldItem: UberFareResponse, newItem: UberFareResponse) = oldItem == newItem
    }
}