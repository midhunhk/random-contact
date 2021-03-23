package com.ae.apps.randomcontact.room.repositories

import androidx.lifecycle.LiveData
import com.ae.apps.randomcontact.room.dao.ContactGroupDao
import com.ae.apps.randomcontact.room.entities.ContactGroup

class ContactGroupRepositoryImpl private constructor(private val dao:ContactGroupDao) : ContactGroupRepository{

    companion object{
        @Volatile
        private var instance: ContactGroupRepositoryImpl? = null

        fun getInstance(dao:ContactGroupDao) =
            instance?: synchronized(this){
                instance ?: ContactGroupRepositoryImpl(dao).also { instance = it }
            }
    }

    override fun getAllContactGroups(): LiveData<List<ContactGroup>> = dao.getAll()

    override fun getContactGroupCount(): Int = dao.getContactGroupCount()

    // Id is stored in the database as an Integer inorder to apply AutoIncrement
    // But it is stored in Preferences as a String, so inorder to maintain backwards compatibility,
    // we are converting to an int here
    override fun findContactGroupById(contactId:String):ContactGroup = dao.getContactGroupById(contactId.toInt())

    override suspend fun createContactGroup(contactGroup:ContactGroup) = dao.insert(contactGroup)

    override suspend fun updateContactGroup(contactGroup: ContactGroup) = dao.update(contactGroup)

    override suspend fun deleteContactGroup(contactGroup: ContactGroup) = dao.delete(contactGroup)
}