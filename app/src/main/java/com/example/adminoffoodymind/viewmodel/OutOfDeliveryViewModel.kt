package com.example.adminoffoodymind.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.adminoffoodymind.Repository.AdminRepository

class OutOfDeliveryViewModel : ViewModel() {

    private val repository = AdminRepository()

    private val _outOfDeliveryOrders = MutableLiveData<AdminRepository.OutOfDeliveryResult?>()
    val outOfDeliveryOrders: LiveData<AdminRepository.OutOfDeliveryResult?> = _outOfDeliveryOrders

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun loadOutOfDeliveryOrders() {
        repository.getOutOfDeliveryOrders(
            onSuccess = { _outOfDeliveryOrders.postValue(it) },
            onFailure = { _errorMessage.postValue(it) }
        )
    }
}