package com.ae.apps.randomcontact.room.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ae.apps.randomcontact.room.entities.ContactGroup
import com.ae.apps.randomcontact.room.repositories.ContactGroupRepository
import org.jetbrains.anko.doAsync

class ContactGroupViewModel(private val repository: ContactGroupRepository) : ViewModel(){

    private var contactGroups:MutableLiveData<List<ContactGroup>> = MutableLiveData()

    fun getAllContactGroups() = contactGroups

    init {
        doAsync {
            readAllContactGroups()
        }
    }

    private fun readAllContactGroups(){
        contactGroups.postValue(repository.getAllContactGroups())
    }

    fun createContactGroup(contactGroup:ContactGroup) = doAsync {
        repository.createContactGroup(contactGroup)
        readAllContactGroups()
    }

    fun updateContactGroup(contactGroup: ContactGroup) = doAsync {
        repository.updateContactGroup(contactGroup)
        readAllContactGroups()
    }

    fun deleteContactGroup(contactGroup: ContactGroup) = doAsync {
        repository.deleteContactGroup(contactGroup)
        readAllContactGroups()
    }

}