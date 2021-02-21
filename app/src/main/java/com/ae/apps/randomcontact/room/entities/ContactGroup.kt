package com.ae.apps.randomcontact.room.entities

import android.provider.BaseColumns
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Model that Represents a ContactGroup
 */
@Entity(tableName = "custom_contact_group")
data class ContactGroup(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = BaseColumns._ID)
    var id: Int? = null,
    @ColumnInfo(name = "contact_group_name")
    var name: String,
    @ColumnInfo(name = "contact_ids")
    var selectedContacts: String)

/*

@Entity(tableName = "student")
data class Student(
    val name: String,
    val age: Int,
    val gpa: Double,
    val isSingle: Boolean
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null
}
 */