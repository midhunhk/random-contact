package com.ae.apps.randomcontact.room.repositories

import com.ae.apps.randomcontact.room.dao.ContactGroupDao
import com.ae.apps.randomcontact.room.entities.ContactGroup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ContactGroupRepository private constructor(private val dao:ContactGroupDao) {

    companion object{
        @Volatile
        private var instance: ContactGroupRepository? = null

        fun getInstance(dao:ContactGroupDao) =
            instance?: synchronized(this){
                instance ?: ContactGroupRepository(dao).also { instance = it }
            }
    }

    fun getAllContactGroups():List<ContactGroup> {
        var allContactGroups:List<ContactGroup> = listOf()
        CoroutineScope(Dispatchers.IO).launch {
            allContactGroups = dao.getAll()
        }
        return allContactGroups
    }

    // Id is stored in the database as an Integer inorder to apply AutoIncrement
    // But it is stored in Preferences as a String, so inorder to maintain backwards compatibility,
    // we are converting to an int here
    fun findContactGroupById(contactId:String):ContactGroup {
        var contactGroup:ContactGroup? = null
        CoroutineScope((Dispatchers.IO)).launch {
            contactGroup = dao.findContactGroupById(contactId.toInt())
        }
        return contactGroup!!
    }

    fun createContactGroup(contactGroup:ContactGroup) = CoroutineScope(Dispatchers.IO)
        .launch {
            dao.insert(contactGroup)
        }
    fun updateContactGroup(contactGroup: ContactGroup) = CoroutineScope(Dispatchers.IO)
        .launch {
            dao.update(contactGroup)
        }
    fun deleteContactGroup(contactGroup: ContactGroup) = CoroutineScope(Dispatchers.IO)
        .launch {
        dao.delete(contactGroup)
    }
}