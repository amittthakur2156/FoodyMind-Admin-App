package com.example.adminoffoodymind.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.adminoffoodymind.Repository.AdminRepository

class PendingOrderViewModel : ViewModel() {

    private val repository = AdminRepository()

    private val _pendingOrders = MutableLiveData<AdminRepository.PendingOrderResult?>()
    val pendingOrders: LiveData<AdminRepository.PendingOrderResult?> = _pendingOrders

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun loadPendingOrders() {
        repository.getPendingOrders(
            onSuccess = { _pendingOrders.postValue(it) },
            onFailure = { _errorMessage.postValue(it) }
        )
    }
}