package com.ae.apps.randomcontact.room.repositories

import com.ae.apps.randomcontact.room.dao.ContactGroupDao
import com.ae.apps.randomcontact.room.entities.ContactGroup

class ContactGroupRepository private constructor(private val dao:ContactGroupDao) {

    companion object{
        @Volatile
        private var instance: ContactGroupRepository? = null

        fun getInstance(dao:ContactGroupDao) =
            instance?: synchronized(this){
                instance ?: ContactGroupRepository(dao).also { instance = it }
            }
    }

    fun getAllContactGroups() = dao.getAll()

    // Id is stored in the database as an Integer inorder to apply AutoIncrement
    // But it is stored in Preferences as a String, so inorder to maintain backwards compatibility,
    // we are converting to an int here
    fun findContactGroupById(contactId:String) = dao.findContactGroupById(contactId.toInt())

    fun createContactGroup(contactGroup:ContactGroup) = dao.insert(contactGroup)

    fun updateContactGroup(contactGroup: ContactGroup) = dao.update(contactGroup)

    fun deleteContactGroup(contactGroup: ContactGroup) = dao.delete(contactGroup)
}