package com.ae.apps.randomcontact.room.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ae.apps.randomcontact.room.repositories.ContactGroupRepository

@Suppress("UNCHECKED_CAST")
class ContactGroupViewModelFactory(private val repository: ContactGroupRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ContactGroupViewModel(repository) as T
    }
}