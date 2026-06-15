package com.example.adminoffoodymind.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.adminoffoodymind.Model.UserModel
import com.example.adminoffoodymind.Repository.AdminRepository

class ProfileViewModel : ViewModel() {

    private val repository = AdminRepository()

    private val _user = MutableLiveData<UserModel?>()
    val user: LiveData<UserModel?> = _user

    private val _updateStatus = MutableLiveData<Boolean?>()
    val updateStatus: LiveData<Boolean?> = _updateStatus

    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> = _message

    fun loadProfile() {
        repository.getUserProfile(
            onSuccess = { _user.postValue(it) },
            onFailure = { _message.postValue(it) }
        )
    }

    fun updateProfile(name: String, address: String, email: String, phone: String) {
        repository.updateUserProfile(name, address, email, phone,
            onSuccess = {
                _updateStatus.postValue(true)
                _message.postValue("Profile Updated Successfully")
                loadProfile() // Refresh data
            },
            onFailure = {
                _updateStatus.postValue(false)
                _message.postValue(it)
            }
        )
    }

    fun resetStatus() {
        _updateStatus.value = null
    }
}