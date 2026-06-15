package com.example.adminoffoodymind.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.adminoffoodymind.Repository.AdminRepository

class MainViewModel : ViewModel() {

    private val repository = AdminRepository()

    private val _dashboardStats = MutableLiveData<AdminRepository.DashboardStats?>()
    val dashboardStats: LiveData<AdminRepository.DashboardStats?> = _dashboardStats

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun isLoggedIn() = repository.isLoggedIn()

    fun loadDashboardStats() {
        repository.getDashboardStats(
            onSuccess = { _dashboardStats.postValue(it) },
            onFailure = { _errorMessage.postValue(it) }
        )
    }

    fun logout(
        onComplete: () -> Unit
    ) {
        repository.logout { onComplete() }
    }
}