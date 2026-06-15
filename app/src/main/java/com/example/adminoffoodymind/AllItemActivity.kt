package com.example.adminoffoodymind

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.adminoffoodymind.Adapter.AllitemAdapter
import com.example.adminoffoodymind.databinding.ActivityAllItemBinding
import com.example.adminoffoodymind.viewmodel.AllItemViewModel
import com.google.firebase.database.FirebaseDatabase

class AllItemActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAllItemBinding
    private val viewModel: AllItemViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAllItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backbtn.setOnClickListener { finish() }

        observeViewModel()
        viewModel.startListening()
    }

    private fun observeViewModel() {
        viewModel.menuList.observe(this) { list ->
            binding.AllItemRecyclerView.adapter =
                AllitemAdapter(this, list, FirebaseDatabase.getInstance().reference)
        }

        viewModel.errorMessage.observe(this) { error ->
            error ?: return@observe
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.stopListening()
    }
}