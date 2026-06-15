package com.example.adminoffoodymind

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adminoffoodymind.Adapter.PendingOrderAdapter
import com.example.adminoffoodymind.databinding.ActivityPendingOrdersBinding
import com.example.adminoffoodymind.viewmodel.PendingOrderViewModel

class PendingOrdersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPendingOrdersBinding
    private val viewModel: PendingOrderViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPendingOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.pendingOrdersRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.backbtn3.setOnClickListener { finish() }

        observeViewModel()
        viewModel.loadPendingOrders()
    }

    private fun observeViewModel() {
        viewModel.pendingOrders.observe(this) { result ->
            result ?: return@observe
            binding.pendingOrdersRecyclerView.adapter = PendingOrderAdapter(
                result.customerNames,
                result.quantities,
                result.foodNamesList,
                result.foodImages,
                result.orderKeys
            )
        }

        viewModel.errorMessage.observe(this) { error ->
            error ?: return@observe
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        }
    }
}