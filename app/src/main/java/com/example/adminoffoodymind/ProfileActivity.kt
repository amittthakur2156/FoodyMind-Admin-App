package com.example.adminoffoodymind

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.adminoffoodymind.databinding.ActivityProfileBinding
import com.example.adminoffoodymind.viewmodel.ProfileViewModel

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private val viewModel: ProfileViewModel by viewModels()
    private var isEditMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toggleEditable(false)
        observeViewModel()
        viewModel.loadProfile()

        binding.backbtn.setOnClickListener { finish() }

        binding.Password.setOnClickListener {
            Toast.makeText(this, "Password cannot be edited here. Use Reset Password.", Toast.LENGTH_SHORT).show()
        }

        binding.EditProfile.setOnClickListener {
            if (isEditMode) {
                val name = binding.Name.text.toString().trim()
                val address = binding.Address.text.toString().trim()
                val email = binding.email.text.toString().trim()
                val phone = binding.Phone.text.toString().trim()
                viewModel.updateProfile(name, address, email, phone)
            } else {
                isEditMode = true
                toggleEditable(true)
                binding.Name.requestFocus()
                binding.EditProfile.text = "Save Profile"
            }
        }
    }

    private fun observeViewModel() {
        viewModel.user.observe(this) { user ->
            user ?: return@observe
            binding.Name.setText(user.name)
            binding.Address.setText(user.address)
            binding.email.setText(user.email)
            binding.Phone.setText(user.phone)
            binding.Password.setText(user.password)
        }

        viewModel.updateStatus.observe(this) { status ->
            status ?: return@observe
            if (status) {
                isEditMode = false
                toggleEditable(false)
                binding.EditProfile.text = "Edit Profile"
            }
            viewModel.resetStatus()
        }

        viewModel.message.observe(this) { message ->
            message ?: return@observe
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun toggleEditable(enable: Boolean) {
        binding.Name.isEnabled = enable
        binding.Address.isEnabled = enable
        binding.email.isEnabled = enable
        binding.Phone.isEnabled = enable
        // Password always read-only
        binding.Password.isEnabled = true
        binding.Password.isFocusable = false
        binding.Password.isFocusableInTouchMode = false
        binding.Password.isCursorVisible = false
        binding.Password.isLongClickable = false
        binding.Password.setTextIsSelectable(false)
    }
}