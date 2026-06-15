package com.example.adminoffoodymind

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.adminoffoodymind.databinding.ActivityAddItemBinding
import com.example.adminoffoodymind.viewmodel.AddItemViewModel

class AddItemActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddItemBinding
    private val viewModel: AddItemViewModel by viewModels()

    private var foodImageUri: android.net.Uri? = null
    private var isEdit = false
    private var itemKey: String? = null
    private var oldImageUrl: String? = null

    private val pickImage =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                binding.AddedImage.setImageURI(uri)
                foodImageUri = uri
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        isEdit = intent.getBooleanExtra("isEdit", false)

        if (isEdit) {
            itemKey = intent.getStringExtra("key")
            oldImageUrl = intent.getStringExtra("foodImage")
            binding.itemName.setText(intent.getStringExtra("foodName"))
            binding.itemPrice.setText(intent.getStringExtra("foodPrice"))
            binding.addItemButton.text = "Update Item"
            Glide.with(this).load(oldImageUrl).into(binding.AddedImage)
        }

        observeViewModel()

        binding.Backbtn.setOnClickListener { finish() }

        binding.AddImage.setOnClickListener {
            pickImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.addItemButton.setOnClickListener {
            val foodName = binding.itemName.text.toString().trim()
            val foodPrice = binding.itemPrice.text.toString().trim()
            val foodDescription = binding.Description.text.toString().trim()
            val foodIngredient = binding.ingredient.text.toString().trim()

            if (foodName.isBlank() || foodPrice.isBlank() ||
                foodDescription.isBlank() || foodIngredient.isBlank()) {
                Toast.makeText(this, "Please fill all the details", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            binding.addItemButton.isEnabled = false

            if (isEdit) {
                viewModel.updateItem(
                    uri = foodImageUri,
                    itemKey = itemKey!!,
                    foodName = foodName,
                    foodPrice = foodPrice,
                    foodDescription = foodDescription,
                    foodIngredient = foodIngredient,
                    oldImageUrl = oldImageUrl
                )
            } else {
                if (foodImageUri == null) {
                    binding.addItemButton.isEnabled = true
                    Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                viewModel.uploadItem(foodImageUri!!, foodName, foodPrice, foodDescription, foodIngredient)
            }
        }
    }

    private fun observeViewModel() {
        viewModel.message.observe(this) { message ->
            message ?: return@observe
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

        viewModel.status.observe(this) { status ->
            status ?: return@observe
            binding.addItemButton.isEnabled = !status
            if (status) finish()
            else binding.addItemButton.isEnabled = true
            viewModel.resetStatus()
        }
    }
}