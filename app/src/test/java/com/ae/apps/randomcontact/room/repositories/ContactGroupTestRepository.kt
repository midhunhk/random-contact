package com.ae.apps.randomcontact.room.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ae.apps.randomcontact.room.entities.ContactGroup

class ContactGroupTestRepository(private val contactGroups: ArrayList<ContactGroup>) : ContactGroupRepository {

    override fun getAllContactGroups(): LiveData<List<ContactGroup>> = MutableLiveData(contactGroups)

    override fun findContactGroupById(contactId: String): ContactGroup {
        return contactGroups[0]
    }

    override suspend fun createContactGroup(contactGroup: ContactGroup) {
        TODO("Not yet implemented")
    }

    override suspend fun updateContactGroup(contactGroup: ContactGroup) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteContactGroup(contactGroup: ContactGroup) {
        TODO("Not yet implemented")
    }

}