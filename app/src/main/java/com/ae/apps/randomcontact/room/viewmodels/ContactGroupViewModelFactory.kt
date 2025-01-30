package com.ae.apps.randomcontact.room.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ae.apps.randomcontact.room.repositories.ContactGroupRepository

class ContactGroupViewModelFactory(private val repository: ContactGroupRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContactGroupViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ContactGroupViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}