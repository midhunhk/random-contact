package com.ae.apps.randomcontact.room.dao

import androidx.room.*
import com.ae.apps.randomcontact.room.entities.ContactGroup

@Dao
interface ContactGroupDao {

    @Query("SELECT * FROM custom_contact_group")
    fun getAll(): List<ContactGroup>

    @Query("SELECT * from custom_contact_group where _id = :groupId")
    fun getContactGroupById(groupId:Int): ContactGroup

    @Insert
    fun insert(contactGroup: ContactGroup)

    @Update
    fun update(contactGroup: ContactGroup)

    @Delete
    fun delete(contactGroup: ContactGroup)
}