package com.ae.apps.randomcontact

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ae.apps.randomcontact.room.AppDatabase
import com.ae.apps.randomcontact.room.dao.ContactGroupDao
import com.ae.apps.randomcontact.room.entities.ContactGroup
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class ContactGroupDaoTest {

    private lateinit var contactGroupDao: ContactGroupDao
    private lateinit var database: AppDatabase

    @Before
    fun createDatabase(){
        val context: Context = ApplicationProvider.getApplicationContext()
        database =  Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        contactGroupDao = database.contactGroupDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDatabase(){
        database.close()
    }

    @Test
    @Throws(IOException::class)
    fun insertAndGetContactGroup() = runBlocking {
        val contactGroup = ContactGroup(name = "Test Group", selectedContacts = "3,4,6")
        contactGroupDao.insert(contactGroup)
        val allGroups = contactGroupDao.getAll().value
        // TODO assertion for LiveData
        // Assert.assertEquals(allGroups?.get(0)?.name, contactGroup.name)
    }

    @Test
    fun getAllGroups() = runBlocking {
        var contactGroup = ContactGroup(name = "Test Group", selectedContacts = "3,4,6")
        contactGroupDao.insert(contactGroup)
        contactGroup = ContactGroup(name = "Test Group2", selectedContacts = "6")
        contactGroupDao.insert(contactGroup)

        val all = contactGroupDao.getAll().value

        if (all != null) {
            Assert.assertEquals(2, all.size)
        }
    }

}