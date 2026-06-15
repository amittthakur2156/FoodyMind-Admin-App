package com.example.adminoffoodymind.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.adminoffoodymind.Repository.AdminRepository

class SignUpViewModel : ViewModel() {

    private val repository = AdminRepository()

    private val _signUpStatus = MutableLiveData<Boolean?>()
    val signUpStatus: LiveData<Boolean?> = _signUpStatus

    private val _signUpMessage = MutableLiveData<String?>()
    val signUpMessage: LiveData<String?> = _signUpMessage

    fun signUp(
        username: String,
        nameOfRestaurant: String,
        email: String,
        password: String,
        location: String
    ) {
        repository.signUpWithEmail(
            username, nameOfRestaurant, email, password, location,
            onSuccess = {
                _signUpStatus.postValue(true)
                _signUpMessage.postValue("Account Created Successfully")
            },
            onFailure = {
                _signUpStatus.postValue(false)
                _signUpMessage.postValue(it)
            }
        )
    }

    fun resetStatus() {
        _signUpStatus.value = null
        _signUpMessage.value = null
    }
}