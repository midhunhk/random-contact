package com.ae.apps.randomcontact.room.dao

import androidx.room.*
import com.ae.apps.randomcontact.room.entities.ContactGroup

@Dao
interface ContactGroupDao {

    @Query("SELECT * FROM custom_contact_group")
    suspend fun getAll(): List<ContactGroup>

    @Query("SELECT * from custom_contact_group where _id = :groupId")
    suspend fun findContactGroupById(groupId:Int): ContactGroup

    @Insert
    suspend fun insert(contactGroup: ContactGroup)

    @Update
    suspend fun update(contactGroup: ContactGroup)

    @Delete
    suspend fun delete(contactGroup: ContactGroup)
}