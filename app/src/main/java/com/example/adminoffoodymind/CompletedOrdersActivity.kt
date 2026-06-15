package com.example.adminoffoodymind

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adminoffoodymind.Adapter.CompletedOrderAdapter
import com.example.adminoffoodymind.databinding.ActivityCompletedOrdersBinding
import com.example.adminoffoodymind.viewmodel.CompletedOrderViewModel

class CompletedOrdersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCompletedOrdersBinding
    private val viewModel: CompletedOrderViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCompletedOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.completedOrdersRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.backbtnCompleted.setOnClickListener { finish() }

        observeViewModel()
        viewModel.loadCompletedOrders()
    }

    private fun observeViewModel() {
        viewModel.completedOrders.observe(this) { result ->
            result ?: return@observe
            binding.completedOrdersRecyclerView.adapter = CompletedOrderAdapter(
                result.customerNames,
                result.paymentStatus,
                result.orderKeys
            )
        }

        viewModel.errorMessage.observe(this) { error ->
            error ?: return@observe
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        }
    }
}