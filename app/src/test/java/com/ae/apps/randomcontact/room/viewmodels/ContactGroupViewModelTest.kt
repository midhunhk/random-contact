package com.ae.apps.randomcontact.room.viewmodels

import com.ae.apps.randomcontact.room.entities.ContactGroup
import com.ae.apps.randomcontact.room.repositories.ContactGroupRepository
import com.ae.apps.randomcontact.room.repositories.ContactGroupTestRepository
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException

class ContactGroupViewModelTest {
    private lateinit var  repository: ContactGroupRepository
    private lateinit var contactGroups: ArrayList<ContactGroup>


    @get:Rule
    val exceptionRule = ExpectedException.none()

    @Before
    fun setUp(){
        contactGroups = ArrayList<ContactGroup>()
        var contactGroup = ContactGroup(1, "Group 1", "1,2,3,4")
        contactGroups.add(contactGroup)
        contactGroup = ContactGroup(1, "Group 2", "1,2")
        contactGroups.add(contactGroup)
        contactGroup = ContactGroup(1, "Group 3", "3,4")
        contactGroups.add(contactGroup)
        contactGroup = ContactGroup(1, "Group 4", "1,2,3,4")
        contactGroups.add(contactGroup)
        contactGroup = ContactGroup(1, "Group 5", "1,2")
        contactGroups.add(contactGroup)

        repository = ContactGroupTestRepository(contactGroups)
    }

    @Test
    fun test_allContacts(){
        val expected = 5;
        val model = ContactGroupViewModel(repository)

        val contactGroups = model.getAllContactGroups().value

        Assert.assertEquals(expected, contactGroups!!.size)
    }

    @Test
    fun test_findFirst(){
        val expected = contactGroups[1].id!!
        val model = ContactGroupViewModel(repository)

        val firstContactGroup = model.findContactGroupById(expected.toString())

        Assert.assertEquals(expected, firstContactGroup.id)
    }

}