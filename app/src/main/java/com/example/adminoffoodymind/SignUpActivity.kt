package com.example.adminoffoodymind

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.adminoffoodymind.databinding.ActivitySignUpBinding
import com.example.adminoffoodymind.viewmodel.SignUpViewModel

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private val viewModel: SignUpViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val locationList = arrayOf("Agra", "Delhi", "Mumbai", "Kolkata", "Chennai", "Bangalore",
            "Hyderabad", "Pune", "Ahmedabad", "Jaipur", "Lucknow", "Kanpur", "Surat", "Bhopal",
            "Indore", "Patna", "Chandigarh", "Noida", "Gurgaon", "Varanasi", "Prayagraj",
            "Amritsar", "Jammu", "Srinagar", "Shimla", "Dehradun", "Rishikesh", "Haridwar",
            "Goa", "Kochi", "Mysore", "Nagpur", "Nashik", "Vadodara", "Raipur", "Ranchi",
            "Bhubaneswar", "Guwahati", "Shillong", "Imphal", "Aizawl", "Gangtok", "Udaipur",
            "Jodhpur", "Ajmer", "Aligarh", "Meerut", "Faridabad", "Jabalpur", "Gwalior")

        binding.listofLocation.setAdapter(
            ArrayAdapter(this, android.R.layout.simple_list_item_1, locationList)
        )

        observeViewModel()

        binding.AlredyHaveAnAccount.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.createAccounBtn.setOnClickListener {
            val username = binding.signUpName.text.toString().trim()
            val restaurant = binding.restorantName.text.toString().trim()
            val email = binding.email.text.toString().trim()
            val password = binding.password.text.toString().trim()
            val location = binding.listofLocation.text.toString().trim()

            if (username.isBlank() || restaurant.isBlank() || email.isBlank() ||
                password.isBlank() || location.isBlank()) {
                Toast.makeText(this, "Please fill all details", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            binding.progressBar.visibility = View.VISIBLE
            viewModel.signUp(username, restaurant, email, password, location)
        }
    }

    private fun observeViewModel() {
        viewModel.signUpStatus.observe(this) { status ->
            status ?: return@observe
            binding.progressBar.visibility = View.GONE
            if (status) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
            viewModel.resetStatus()
        }

        viewModel.signUpMessage.observe(this) { message ->
            message ?: return@observe
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
}