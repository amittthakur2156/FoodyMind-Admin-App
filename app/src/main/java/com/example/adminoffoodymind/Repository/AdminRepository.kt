package com.example.adminoffoodymind.Repository

import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.adminoffoodymind.Model.AllMenu
import com.example.adminoffoodymind.Model.UserModel
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdminRepository {

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    // ─── Auth ─────────────────────────────────────────────

    fun isLoggedIn(): Boolean = auth.currentUser != null

    fun loginWithEmail(
        email: String, password: String,
        onSuccess: () -> Unit, onFailure: (String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) onSuccess()
                else onFailure(task.exception?.message ?: "Login Failed")
            }
    }

    fun sendPasswordReset(
        email: String,
        onSuccess: () -> Unit, onFailure: (String) -> Unit
    ) {
        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it.message ?: "Failed") }
    }

    fun loginWithGoogle(
        idToken: String,
        onSuccess: () -> Unit, onFailure: (String) -> Unit
    ) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser ?: run { onFailure("User not found"); return@addOnCompleteListener }
                val userRef = database.child("user").child(user.uid)
                userRef.get().addOnSuccessListener { snapshot ->
                    if (!snapshot.exists()) {
                        userRef.setValue(hashMapOf(
                            "uid" to user.uid,
                            "name" to (user.displayName ?: ""),
                            "email" to (user.email ?: ""),
                            "photoUrl" to (user.photoUrl?.toString() ?: ""),
                            "provider" to "google"
                        ))
                    }
                    onSuccess()
                }.addOnFailureListener { onFailure(it.message ?: "Failed") }
            } else onFailure(task.exception?.message ?: "Google Login Failed")
        }
    }

    fun loginWithFacebook(
        token: String,
        onSuccess: () -> Unit,
        onFailure: (String, Boolean) -> Unit
    ) {
        val credential = FacebookAuthProvider.getCredential(token)
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser ?: run { onFailure("User not found", false); return@addOnCompleteListener }
                val userRef = database.child("user").child(user.uid)
                userRef.get().addOnSuccessListener { snapshot ->
                    if (!snapshot.exists()) {
                        userRef.setValue(hashMapOf(
                            "uid" to user.uid,
                            "name" to (user.displayName ?: ""),
                            "email" to (user.email ?: ""),
                            "photoUrl" to (user.photoUrl?.toString() ?: ""),
                            "provider" to "facebook"
                        ))
                    }
                    onSuccess()
                }.addOnFailureListener { onFailure(it.message ?: "Failed", false) }
            } else {
                val isCollision = task.exception is FirebaseAuthUserCollisionException
                onFailure(task.exception?.localizedMessage ?: "Facebook Login Failed", isCollision)
            }
        }
    }

    fun signUpWithEmail(
        username: String, nameOfRestaurant: String,
        email: String, password: String, location: String,
        onSuccess: () -> Unit, onFailure: (String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val uid = auth.currentUser?.uid ?: run { onFailure("UID not found"); return@addOnCompleteListener }
                val user = UserModel(
                    name = username, nameOfRestaurant = nameOfRestaurant,
                    email = email, password = password, address = location, uid = uid
                )
                database.child("user").child(uid).setValue(user)
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { onFailure(it.message ?: "Save failed") }
            } else onFailure(task.exception?.message ?: "Registration Failed")
        }
    }

    // ─── Menu Items ───────────────────────────────────────

    fun uploadMenuItem(
        uri: Uri, foodName: String, foodPrice: String,
        foodDescription: String, foodIngredient: String,
        onStart: () -> Unit, onSuccess: () -> Unit, onFailure: (String) -> Unit
    ) {
        val menuRef = database.child("menu")
        val newItemKey = menuRef.push().key ?: run { onFailure("Key generation failed"); return }
        onStart()
        MediaManager.get().upload(uri).unsigned("AdminofFoodyMind")
            .option("folder", "menu_images")
            .callback(object : UploadCallback {
                override fun onStart(requestId: String?) {}
                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}
                override fun onReschedule(requestId: String?, error: ErrorInfo?) {}
                override fun onError(requestId: String?, error: ErrorInfo?) {
                    onFailure(error?.description ?: "Image Upload Failed")
                }
                override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
                    val imageUrl = resultData?.get("secure_url").toString()
                    val newItem = AllMenu(
                        key = newItemKey, foodName = foodName, foodPrice = foodPrice,
                        foodDescription = foodDescription, foodImage = imageUrl,
                        foodIngredient = foodIngredient
                    )
                    menuRef.child(newItemKey).setValue(newItem)
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener { onFailure(it.message ?: "Failed to save") }
                }
            }).dispatch()
    }

    fun updateMenuItemWithImage(
        uri: Uri, itemKey: String, foodName: String, foodPrice: String,
        foodDescription: String, foodIngredient: String,
        onSuccess: () -> Unit, onFailure: (String) -> Unit
    ) {
        MediaManager.get().upload(uri).unsigned("AdminofFoodyMind")
            .option("folder", "menu_images")
            .callback(object : UploadCallback {
                override fun onStart(requestId: String?) {}
                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}
                override fun onReschedule(requestId: String?, error: ErrorInfo?) {}
                override fun onError(requestId: String?, error: ErrorInfo?) {
                    onFailure(error?.description ?: "Upload Failed")
                }
                override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
                    val imageUrl = resultData?.get("secure_url").toString()
                    val updatedItem = AllMenu(
                        key = itemKey, foodName = foodName, foodPrice = foodPrice,
                        foodDescription = foodDescription, foodImage = imageUrl,
                        foodIngredient = foodIngredient
                    )
                    database.child("menu").child(itemKey).setValue(updatedItem)
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener { onFailure(it.message ?: "Update Failed") }
                }
            }).dispatch()
    }

    fun updateMenuItemWithoutImage(
        itemKey: String, foodName: String, foodPrice: String,
        foodDescription: String, foodIngredient: String, oldImageUrl: String?,
        onSuccess: () -> Unit, onFailure: (String) -> Unit
    ) {
        val updatedItem = AllMenu(
            key = itemKey, foodName = foodName, foodPrice = foodPrice,
            foodDescription = foodDescription, foodImage = oldImageUrl,
            foodIngredient = foodIngredient
        )
        database.child("menu").child(itemKey).setValue(updatedItem)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it.message ?: "Update Failed") }
    }

    fun getAllMenuItems(
        onUpdate: (ArrayList<AllMenu>) -> Unit,
        onError: (String) -> Unit
    ): ValueEventListener {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = ArrayList<AllMenu>()
                for (foodSnapshot in snapshot.children) {
                    foodSnapshot.getValue(AllMenu::class.java)?.let { list.add(it) }
                }
                onUpdate(list)
            }
            override fun onCancelled(error: DatabaseError) { onError(error.message) }
        }
        database.child("menu").addValueEventListener(listener)
        return listener
    }

    fun removeMenuListener(listener: ValueEventListener) {
        database.child("menu").removeEventListener(listener)
    }

    // ─── Pending Orders ───────────────────────────────────

    data class PendingOrderResult(
        val customerNames: ArrayList<String>,
        val foodNamesList: ArrayList<String>,
        val quantities: ArrayList<String>,
        val foodImages: ArrayList<String>,
        val orderKeys: ArrayList<String>
    )

    fun getPendingOrders(
        onSuccess: (PendingOrderResult) -> Unit,
        onFailure: (String) -> Unit
    ) {
        database.child("orderDetails").get()
            .addOnSuccessListener { snapshot ->
                val customerNames = ArrayList<String>()
                val foodNamesList = ArrayList<String>()
                val quantities = ArrayList<String>()
                val foodImages = ArrayList<String>()
                val orderKeys = ArrayList<String>()

                for (order in snapshot.children) {
                    val status = order.child("status").getValue(String::class.java)
                    if (status == "Pending") {
                        val name = order.child("name").getValue(String::class.java) ?: ""
                        val foodNames = order.child("foodNames").children.toList()
                        val images = order.child("foodImages").children.toList()
                        val foodQuantities = order.child("foodQuantities").children.toList()
                        for (i in foodNames.indices) {
                            customerNames.add(name)
                            foodNamesList.add(foodNames[i].getValue(String::class.java) ?: "")
                            quantities.add((foodQuantities[i].getValue(Int::class.java) ?: 0).toString())
                            foodImages.add(images[i].getValue(String::class.java) ?: "")
                            orderKeys.add(order.key ?: "")
                        }
                    }
                }
                onSuccess(PendingOrderResult(customerNames, foodNamesList, quantities, foodImages, orderKeys))
            }
            .addOnFailureListener { onFailure(it.message ?: "Failed to load orders") }
    }

    // ─── Completed Orders (sirf is admin ki) ─────────────

    data class CompletedOrderResult(
        val customerNames: ArrayList<String>,
        val paymentStatus: ArrayList<String>,
        val orderKeys: ArrayList<String>
    )

    fun getCompletedOrders(
        onSuccess: (CompletedOrderResult) -> Unit,
        onFailure: (String) -> Unit
    ) {
        // Sirf current admin ka uid
        val currentUid = auth.currentUser?.uid ?: run {
            onFailure("Not logged in")
            return
        }

        database.child("orderDetails").get()
            .addOnSuccessListener { snapshot ->
                val customerNames = ArrayList<String>()
                val paymentStatus = ArrayList<String>()
                val orderKeys = ArrayList<String>()

                for (order in snapshot.children) {
                    val status = order.child("status").getValue(String::class.java)
                    // acceptedBy se filter — sirf is admin ke orders
                    val acceptedBy = order.child("acceptedBy").getValue(String::class.java)

                    if (status == "Delivered" && acceptedBy == currentUid) {
                        customerNames.add(order.child("name").getValue(String::class.java) ?: "")
                        paymentStatus.add("Received")
                        orderKeys.add(order.key ?: "")
                    }
                }
                onSuccess(CompletedOrderResult(customerNames, paymentStatus, orderKeys))
            }
            .addOnFailureListener { onFailure(it.message ?: "Failed to load completed orders") }
    }

    // ─── Out Of Delivery (sirf is admin ke accepted orders) ──

    data class OutOfDeliveryResult(
        val customerNames: ArrayList<String>,
        val moneyStatus: ArrayList<String>,
        val orderKeys: ArrayList<String>
    )

    fun getOutOfDeliveryOrders(
        onSuccess: (OutOfDeliveryResult) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val currentUid = auth.currentUser?.uid ?: run {
            onFailure("Not logged in")
            return
        }

        database.child("orderDetails").get()
            .addOnSuccessListener { snapshot ->
                val customerNames = ArrayList<String>()
                val moneyStatus = ArrayList<String>()
                val orderKeys = ArrayList<String>()

                for (order in snapshot.children) {
                    val status = order.child("status").getValue(String::class.java)
                    // acceptedBy se filter — sirf is admin ke orders
                    val acceptedBy = order.child("acceptedBy").getValue(String::class.java)

                    if (status == "Accepted" && acceptedBy == currentUid) {
                        customerNames.add(order.child("name").getValue(String::class.java) ?: "")
                        moneyStatus.add("Pending")
                        orderKeys.add(order.key ?: "")
                    }
                }
                onSuccess(OutOfDeliveryResult(customerNames, moneyStatus, orderKeys))
            }
            .addOnFailureListener { onFailure(it.message ?: "Failed to load delivery details") }
    }

    // ─── Profile ──────────────────────────────────────────

    fun getUserProfile(
        onSuccess: (UserModel) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val uid = auth.currentUser?.uid ?: run { onFailure("Not logged in"); return }
        database.child("user").child(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(UserModel::class.java)
                    if (user != null) onSuccess(user)
                    else onFailure("User not found")
                }
                override fun onCancelled(error: DatabaseError) {
                    onFailure(error.message)
                }
            })
    }

    fun updateUserProfile(
        name: String, address: String, email: String, phone: String,
        onSuccess: () -> Unit, onFailure: (String) -> Unit
    ) {
        val uid = auth.currentUser?.uid ?: run { onFailure("Not logged in"); return }
        val updates = hashMapOf<String, Any>(
            "name" to name, "address" to address,
            "email" to email, "phone" to phone
        )
        database.child("user").child(uid).updateChildren(updates)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it.localizedMessage ?: "Update Failed") }
    }
    // ─── Dashboard Stats ──────────────────────────────────

    data class DashboardStats(
        val pendingQty: Int,
        val completedQty: Int,
        val totalEarning: Int
    )

    fun getDashboardStats(
        onSuccess: (DashboardStats) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val currentUid = auth.currentUser?.uid ?: run {
            onFailure("Not logged in")
            return
        }

        database.child("orderDetails").get()
            .addOnSuccessListener { snapshot ->

                var pendingQty = 0
                var completedQty = 0
                var totalEarning = 0

                for (order in snapshot.children) {
                    val status = order.child("status").getValue(String::class.java)
                    val acceptedBy = order.child("acceptedBy").getValue(String::class.java)

                    when {
                        // Pending — sabko dikhega, uid filter nahi
                        status == "Pending" -> {
                            for (item in order.child("foodQuantities").children) {
                                pendingQty += item.getValue(Int::class.java) ?: 0
                            }
                        }
                        // Completed — sirf is admin ki
                        status == "Delivered" && acceptedBy == currentUid -> {
                            for (item in order.child("foodQuantities").children) {
                                completedQty += item.getValue(Int::class.java) ?: 0
                            }
                            val price = order.child("totalPrice")
                                .getValue(String::class.java)
                                ?.replace("$", "")
                                ?.replace("₹", "")
                                ?.trim()
                                ?.toIntOrNull() ?: 0
                            totalEarning += price
                        }
                    }
                }

                onSuccess(DashboardStats(pendingQty, completedQty, totalEarning))
            }
            .addOnFailureListener { onFailure(it.message ?: "Failed to load stats") }
    }
    // ─── Logout ───────────────────────────────────────────

    fun logout(onComplete: () -> Unit) {
        auth.signOut()
        onComplete()
    }
}