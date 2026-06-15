package com.example.adminoffoodymind.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.adminoffoodymind.Repository.AdminRepository

class LoginViewModel : ViewModel() {

    private val repository = AdminRepository()

    private val _loginStatus = MutableLiveData<Boolean?>()
    val loginStatus: LiveData<Boolean?> = _loginStatus

    private val _loginMessage = MutableLiveData<String?>()
    val loginMessage: LiveData<String?> = _loginMessage

    private val _isCollision = MutableLiveData<Boolean>()
    val isCollision: LiveData<Boolean> = _isCollision

    fun isLoggedIn() = repository.isLoggedIn()

    fun loginWithEmail(email: String, password: String) {
        repository.loginWithEmail(email, password,
            onSuccess = {
                _loginStatus.postValue(true)
                _loginMessage.postValue("Login Successful")
            },
            onFailure = {
                _loginStatus.postValue(false)
                _loginMessage.postValue(it)
            }
        )
    }

    fun sendPasswordReset(email: String) {
        repository.sendPasswordReset(email,
            onSuccess = { _loginMessage.postValue("Reset link sent") },
            onFailure = { _loginMessage.postValue(it) }
        )
    }

    fun loginWithGoogle(idToken: String) {
        repository.loginWithGoogle(idToken,
            onSuccess = {
                _loginStatus.postValue(true)
                _loginMessage.postValue("Google Login Successful")
            },
            onFailure = {
                _loginStatus.postValue(false)
                _loginMessage.postValue(it)
            }
        )
    }

    fun loginWithFacebook(token: String) {
        repository.loginWithFacebook(token,
            onSuccess = {
                _loginStatus.postValue(true)
                _loginMessage.postValue("Facebook Login Successful")
            },
            onFailure = { message, isCollision ->
                _loginStatus.postValue(false)
                _loginMessage.postValue(message)
                _isCollision.postValue(isCollision)
            }
        )
    }

    fun resetStatus() {
        _loginStatus.value = null
        _loginMessage.value = null
    }
}