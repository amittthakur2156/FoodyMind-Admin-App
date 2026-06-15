package com.example.adminoffoodymind.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.adminoffoodymind.Repository.AdminRepository

class CompletedOrderViewModel : ViewModel() {

    private val repository = AdminRepository()

    private val _completedOrders = MutableLiveData<AdminRepository.CompletedOrderResult?>()
    val completedOrders: LiveData<AdminRepository.CompletedOrderResult?> = _completedOrders

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun loadCompletedOrders() {
        repository.getCompletedOrders(
            onSuccess = { _completedOrders.postValue(it) },
            onFailure = { _errorMessage.postValue(it) }
        )
    }
}