package com.example.adminoffoodymind

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.adminoffoodymind.databinding.ActivityMainBinding
import com.example.adminoffoodymind.viewmodel.MainViewModel
import com.facebook.AccessToken
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Login check sabse pehle
        if (!viewModel.isLoggedIn()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        observeViewModel()
        viewModel.loadDashboardStats()
        setupClickListeners()
    }

    private fun observeViewModel() {
        viewModel.dashboardStats.observe(this) { stats ->
            stats ?: return@observe
            binding.PendingOrder.text = stats.pendingQty.toString()
            binding.completedOrder.text = stats.completedQty.toString()
            binding.WholeTimeErning.text = "$${stats.totalEarning}"
        }

        viewModel.errorMessage.observe(this) { error ->
            error ?: return@observe
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupClickListeners() {
        binding.AddItem.setOnClickListener {
            startActivity(Intent(this, AddItemActivity::class.java))
        }
        binding.AllItemView.setOnClickListener {
            startActivity(Intent(this, AllItemActivity::class.java))
        }
        binding.Profile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
        binding.CreateUser.setOnClickListener {
            startActivity(Intent(this, CreateNewUserActivity::class.java))
        }
        binding.OrederDispatch.setOnClickListener {
            startActivity(Intent(this, OutOfDeliveryActivity::class.java))
        }
        binding.PendingOrders.setOnClickListener {
            startActivity(Intent(this, PendingOrdersActivity::class.java))
        }
        binding.pendingimg.setOnClickListener {
            startActivity(Intent(this, PendingOrdersActivity::class.java))
        }
        binding.completedOrder.setOnClickListener {
            startActivity(Intent(this, CompletedOrdersActivity::class.java))
        }
        binding.completeOrderimage.setOnClickListener {
            startActivity(Intent(this, CompletedOrdersActivity::class.java))
        }

        binding.logout.setOnClickListener {
            viewModel.logout {
                // Google + Facebook logout UI level pe
                googleSignInClient.signOut()
                if (AccessToken.getCurrentAccessToken() != null) {
                    LoginManager.getInstance().logOut()
                }
                Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
                finish()
            }
        }
    }

    // Dashboard refresh — jab bhi wapas aao
    override fun onResume() {
        super.onResume()
        viewModel.loadDashboardStats()
    }
}