package com.ae.apps.randomcontact.room.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.ae.apps.randomcontact.room.AppDatabase
import com.ae.apps.randomcontact.room.entities.ContactGroup
import com.ae.apps.randomcontact.room.repositories.ContactGroupRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ContactGroupViewModel(application: Application) : AndroidViewModel(application){

    private val contactGroups: LiveData<List<ContactGroup>>
    private val repository: ContactGroupRepository

    fun getAllContactGroups() = contactGroups

    init {
        val contactGroupDao = AppDatabase.getInstance(application).contactGroupDao()
        repository = ContactGroupRepository.getInstance(contactGroupDao)
        contactGroups = repository.getAllContactGroups()
    }

    fun createContactGroup(contactGroup:ContactGroup){
        viewModelScope.launch(Dispatchers.IO) {
            repository.createContactGroup(contactGroup)
        }
    }

    fun updateContactGroup(contactGroup: ContactGroup) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateContactGroup(contactGroup)
        }
    }

    fun deleteContactGroup(contactGroup: ContactGroup) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteContactGroup(contactGroup)
        }
    }

}