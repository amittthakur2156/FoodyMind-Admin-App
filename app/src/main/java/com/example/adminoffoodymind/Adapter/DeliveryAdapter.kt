package com.example.adminoffoodymind.Adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.adminoffoodymind.databinding.DeliveryItemBinding
import com.google.firebase.database.FirebaseDatabase

class DeliveryAdapter(
    private val customerNames: ArrayList<String>,
    private val moneyStatus: ArrayList<String>,
    private val orderKeys: ArrayList<String>
) : RecyclerView.Adapter<DeliveryAdapter.DelivarViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DelivarViewHolder {

        val binding = DeliveryItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return DelivarViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DelivarViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = customerNames.size

    inner class DelivarViewHolder(
        private val binding: DeliveryItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {

            binding.customerName.text = customerNames[position]

            val status = moneyStatus[position].trim()
            binding.statusMoney.text = status
            binding.deleverybtn.setOnClickListener {

                val key = orderKeys[adapterPosition]

                FirebaseDatabase.getInstance()
                    .reference
                    .child("orderDetails")
                    .child(key)
                    .child("status")
                    .setValue("Delivered")
                    .addOnSuccessListener {

                        Toast.makeText(
                            itemView.context,
                            "Order Delivered",
                            Toast.LENGTH_SHORT
                        ).show()

                        customerNames.removeAt(adapterPosition)
                        moneyStatus.removeAt(adapterPosition)
                        orderKeys.removeAt(adapterPosition)

                        notifyItemRemoved(adapterPosition)
                    }
            }

            when (status) {

                "Received" -> {
                    binding.statusMoney.setTextColor(Color.GREEN)
                    binding.statusColor.setColorFilter(Color.GREEN)
                }

                "Not Received" -> {
                    binding.statusMoney.setTextColor(Color.RED)
                    binding.statusColor.setColorFilter(Color.RED)
                }

                "Pending" -> {
                    binding.statusMoney.setTextColor(
                        Color.parseColor("#FFC107")
                    )
                    binding.statusColor.setColorFilter(
                        Color.parseColor("#FFC107")
                    )
                }

                else -> {
                    binding.statusMoney.setTextColor(Color.BLACK)
                    binding.statusColor.setColorFilter(Color.GRAY)
                }

            }
        }
    }
}