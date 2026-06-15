package com.example.adminoffoodymind.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.adminoffoodymind.AddItemActivity
import com.example.adminoffoodymind.Model.AllMenu
import com.example.adminoffoodymind.databinding.ItemItemBinding
import com.google.firebase.database.DatabaseReference

class AllitemAdapter(
    private val context: Context,
    private val menuList: ArrayList<AllMenu>,
    private val database: DatabaseReference
) : RecyclerView.Adapter<AllitemAdapter.AllItemViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AllItemViewHolder {

        val binding = ItemItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return AllItemViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: AllItemViewHolder,
        position: Int
    ) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = menuList.size

    inner class AllItemViewHolder(
        private val binding: ItemItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {

            val item = menuList[position]

            binding.FoodNameAllItem.setText(item.foodName)
            binding.itmePrice.setText(item.foodPrice)

            Glide.with(binding.root.context)
                .load(item.foodImage)
                .into(binding.ItemImage)

            // ---------------- EDIT ----------------

            binding.editItem.setOnClickListener {

                val intent = Intent(context, AddItemActivity::class.java)

                intent.putExtra("isEdit", true)
                intent.putExtra("key", item.key)
                intent.putExtra("foodName", item.foodName)
                intent.putExtra("foodPrice", item.foodPrice)
                intent.putExtra("foodImage", item.foodImage)

                context.startActivity(intent)
            }

            // ---------------- DELETE ----------------

            binding.ItemDeleteBtn.setOnClickListener {

                AlertDialog.Builder(binding.root.context)
                    .setTitle("Delete Item")
                    .setMessage("Are you sure you want to delete this item?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("Yes") { _, _ ->

                        val pos = bindingAdapterPosition

                        if (pos == RecyclerView.NO_POSITION)
                            return@setPositiveButton

                        val key = menuList[pos].key ?: return@setPositiveButton

                        database.child("menu")
                            .child(key)
                            .removeValue()
                            .addOnSuccessListener {

                                menuList.removeAt(pos)

                                notifyItemRemoved(pos)
                                notifyItemRangeChanged(pos, menuList.size)

                                Toast.makeText(
                                    binding.root.context,
                                    "Item Deleted Successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            .addOnFailureListener {

                                Toast.makeText(
                                    binding.root.context,
                                    "Failed : ${it.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                    .setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
        }
    }
}