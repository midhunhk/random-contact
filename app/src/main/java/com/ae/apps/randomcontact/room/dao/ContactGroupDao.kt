package com.ae.apps.randomcontact.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ae.apps.randomcontact.room.entities.ContactGroup

@Dao
interface ContactGroupDao {

    @Query("SELECT * FROM custom_contact_group")
    fun getAll(): LiveData<List<ContactGroup>>

    @Query("SELECT * from custom_contact_group where _id = :groupId")
    fun getContactGroupById(groupId: Int): ContactGroup

    @Query("SELECT count(*) from custom_contact_group")
    fun getContactGroupCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(contactGroup: ContactGroup)

    @Update
    suspend fun update(contactGroup: ContactGroup)

    @Delete
    suspend fun delete(contactGroup: ContactGroup)
}