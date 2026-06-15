package com.example.adminoffoodymind.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.adminoffoodymind.Model.AllMenu
import com.example.adminoffoodymind.Repository.AdminRepository
import com.google.firebase.database.ValueEventListener

class AllItemViewModel : ViewModel() {

    private val repository = AdminRepository()

    private val _menuList = MutableLiveData<ArrayList<AllMenu>>()
    val menuList: LiveData<ArrayList<AllMenu>> = _menuList

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private var menuListener: ValueEventListener? = null

    fun startListening() {
        menuListener = repository.getAllMenuItems(
            onUpdate = { _menuList.postValue(it) },
            onError = { _errorMessage.postValue(it) }
        )
    }

    fun stopListening() {
        menuListener?.let { repository.removeMenuListener(it) }
        menuListener = null
    }

    override fun onCleared() {
        super.onCleared()
        stopListening()
    }
}