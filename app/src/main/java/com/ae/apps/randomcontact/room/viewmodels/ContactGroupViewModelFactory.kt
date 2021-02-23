package com.ae.apps.randomcontact.room.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ae.apps.randomcontact.room.repositories.ContactGroupRepository

class ContactGroupViewModelFactory(private val repository: ContactGroupRepository) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ContactGroupViewModel(repository) as T
    }

}