package com.ae.apps.randomcontact.room.repositories

import androidx.lifecycle.LiveData
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

    fun getAllContactGroups(): LiveData<List<ContactGroup>> = dao.getAll()

    // Id is stored in the database as an Integer inorder to apply AutoIncrement
    // But it is stored in Preferences as a String, so inorder to maintain backwards compatibility,
    // we are converting to an int here
    fun findContactGroupById(contactId:String):ContactGroup = dao.getContactGroupById(contactId.toInt())

    suspend fun createContactGroup(contactGroup:ContactGroup) = dao.insert(contactGroup)

    suspend fun updateContactGroup(contactGroup: ContactGroup) = dao.update(contactGroup)

    suspend fun deleteContactGroup(contactGroup: ContactGroup) = dao.delete(contactGroup)
}