package com.example.adminoffoodymind

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.adminoffoodymind.Model.UserModel
import com.example.adminoffoodymind.databinding.ActivityCreateNewUserBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class CreateNewUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateNewUserBinding
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreateNewUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        binding.backbtn.setOnClickListener {
            finish()
        }

        binding.createUserButton.setOnClickListener {

            val name = binding.Name.text.toString().trim()
            val email = binding.EmailorPhone.text.toString().trim()
            val password = binding.password.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all details", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {

                    val uid = auth.currentUser!!.uid

                    val user = UserModel(
                        name = name,
                        email = email,
                        password = password,
                        phone = "",
                        address = "",
                        uid = uid
                    )

                    database.child("user")
                        .child(uid)
                        .setValue(user)
                        .addOnSuccessListener {

                            Toast.makeText(
                                this,
                                "User Created Successfully",
                                Toast.LENGTH_SHORT
                            ).show()

                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(
                                this,
                                it.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                }
                .addOnFailureListener {
                    Toast.makeText(
                        this,
                        it.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }
}