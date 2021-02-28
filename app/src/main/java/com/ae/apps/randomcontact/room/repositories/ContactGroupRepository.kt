package com.ae.apps.randomcontact.room.repositories

import androidx.lifecycle.LiveData
import com.ae.apps.randomcontact.room.entities.ContactGroup

interface ContactGroupRepository {

    fun getAllContactGroups(): LiveData<List<ContactGroup>>

    fun findContactGroupById(contactId:String): ContactGroup

    suspend fun createContactGroup(contactGroup: ContactGroup)

    suspend fun updateContactGroup(contactGroup: ContactGroup)

    suspend fun deleteContactGroup(contactGroup: ContactGroup)
}