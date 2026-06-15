package com.example.adminoffoodymind

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adminoffoodymind.Adapter.DeliveryAdapter
import com.example.adminoffoodymind.databinding.ActivityOutOfDeliveryBinding
import com.example.adminoffoodymind.viewmodel.OutOfDeliveryViewModel

class OutOfDeliveryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOutOfDeliveryBinding
    private val viewModel: OutOfDeliveryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOutOfDeliveryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.deliveryRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.backbtn2.setOnClickListener { finish() }

        observeViewModel()
        viewModel.loadOutOfDeliveryOrders()
    }

    private fun observeViewModel() {
        viewModel.outOfDeliveryOrders.observe(this) { result ->
            result ?: return@observe
            binding.deliveryRecyclerView.adapter = DeliveryAdapter(
                result.customerNames,
                result.moneyStatus,
                result.orderKeys
            )
        }

        viewModel.errorMessage.observe(this) { error ->
            error ?: return@observe
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        }
    }
}