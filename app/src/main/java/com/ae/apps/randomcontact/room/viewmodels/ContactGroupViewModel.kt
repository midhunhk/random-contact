package com.ae.apps.randomcontact.room.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ae.apps.randomcontact.room.entities.ContactGroup
import com.ae.apps.randomcontact.room.repositories.ContactGroupRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ContactGroupViewModel(private val repository: ContactGroupRepository) : ViewModel() {

    private val contactGroups: LiveData<List<ContactGroup>> = repository.getAllContactGroups()

    fun getAllContactGroups() = contactGroups

    fun findContactGroupById(groupId: String): ContactGroup {
        return repository.findContactGroupById(groupId)
    }

    fun createContactGroup(contactGroup: ContactGroup) {
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