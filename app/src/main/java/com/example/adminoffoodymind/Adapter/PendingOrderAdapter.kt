package com.example.adminoffoodymind.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.adminoffoodymind.databinding.PendingOrderItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class PendingOrderAdapter(
    private val customerNames: ArrayList<String>,
    private val quantity: ArrayList<String>,
    private val itemNames: ArrayList<String>,
    private val foodImage: ArrayList<String>,
    private val orderKeys: ArrayList<String>
) : RecyclerView.Adapter<PendingOrderAdapter.PendingOrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PendingOrderViewHolder {
        val binding = PendingOrderItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PendingOrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PendingOrderViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = customerNames.size

    inner class PendingOrderViewHolder(
        private val binding: PendingOrderItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            binding.customerName.text = customerNames[position]
            binding.ItemQuantity.text = quantity[position]
            binding.foodName.text = itemNames[position]

            Glide.with(binding.root.context)
                .load(foodImage[position])
                .into(binding.image)

            binding.AcceptBtn.text = "Accept"

            binding.AcceptBtn.setOnClickListener {
                val pos = adapterPosition
                if (pos == RecyclerView.NO_POSITION) return@setOnClickListener

                val key = orderKeys[pos]

                // Current admin ka uid
                val currentUid = FirebaseAuth.getInstance().currentUser?.uid
                    ?: run {
                        Toast.makeText(itemView.context, "Not logged in", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                val updates = hashMapOf<String, Any>(
                    "status" to "Accepted",
                    "acceptedBy" to currentUid  // ← is admin ne accept kiya
                )

                FirebaseDatabase.getInstance()
                    .reference
                    .child("orderDetails")
                    .child(key)
                    .updateChildren(updates)
                    .addOnSuccessListener {
                        Toast.makeText(itemView.context, "Order Accepted", Toast.LENGTH_SHORT).show()
                        customerNames.removeAt(pos)
                        quantity.removeAt(pos)
                        itemNames.removeAt(pos)
                        foodImage.removeAt(pos)
                        orderKeys.removeAt(pos)
                        notifyItemRemoved(pos)
                    }
                    .addOnFailureListener {
                        Toast.makeText(itemView.context, "Failed to accept order", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
}