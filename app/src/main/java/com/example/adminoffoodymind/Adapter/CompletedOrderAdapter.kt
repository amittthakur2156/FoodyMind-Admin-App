package com.example.adminoffoodymind.Adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.adminoffoodymind.databinding.CompletedOrderItemBinding

class CompletedOrderAdapter(

    private val customerNames: ArrayList<String>,
    private val paymentStatus: ArrayList<String>,
    private val orderKeys: ArrayList<String>

) : RecyclerView.Adapter<CompletedOrderAdapter.CompletedViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CompletedViewHolder {

        val binding = CompletedOrderItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return CompletedViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: CompletedViewHolder,
        position: Int
    ) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = customerNames.size

    inner class CompletedViewHolder(
        private val binding: CompletedOrderItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {

            binding.customerName.text =
                customerNames[position]

            val status =
                paymentStatus[position].trim()

            binding.statusMoney.text = status

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

            // Completed screen me hamesha Completed dikhana
            binding.orderStatus.text = "Completed"
        }
    }
}