package com.example.adminoffoodymind.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.adminoffoodymind.Repository.AdminRepository

class AddItemViewModel : ViewModel() {

    private val repository = AdminRepository()

    private val _status = MutableLiveData<Boolean?>()
    val status: LiveData<Boolean?> = _status

    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> = _message

    fun uploadItem(
        uri: Uri,
        foodName: String, foodPrice: String,
        foodDescription: String, foodIngredient: String
    ) {
        repository.uploadMenuItem(
            uri, foodName, foodPrice, foodDescription, foodIngredient,
            onStart = { _message.postValue("Uploading Image...") },
            onSuccess = {
                _status.postValue(true)
                _message.postValue("Item Added Successfully")
            },
            onFailure = {
                _status.postValue(false)
                _message.postValue(it)
            }
        )
    }

    fun updateItem(
        uri: Uri?, itemKey: String,
        foodName: String, foodPrice: String,
        foodDescription: String, foodIngredient: String,
        oldImageUrl: String?
    ) {
        if (uri != null) {
            repository.updateMenuItemWithImage(
                uri, itemKey, foodName, foodPrice, foodDescription, foodIngredient,
                onSuccess = {
                    _status.postValue(true)
                    _message.postValue("Item Updated Successfully")
                },
                onFailure = {
                    _status.postValue(false)
                    _message.postValue(it)
                }
            )
        } else {
            repository.updateMenuItemWithoutImage(
                itemKey, foodName, foodPrice, foodDescription, foodIngredient, oldImageUrl,
                onSuccess = {
                    _status.postValue(true)
                    _message.postValue("Item Updated Successfully")
                },
                onFailure = {
                    _status.postValue(false)
                    _message.postValue(it)
                }
            )
        }
    }

    fun resetStatus() {
        _status.value = null
        _message.value = null
    }
}