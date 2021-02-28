package com.ae.apps.randomcontact.room.viewmodels

import androidx.lifecycle.MutableLiveData
import com.ae.apps.randomcontact.room.entities.ContactGroup
import com.ae.apps.randomcontact.room.repositories.ContactGroupRepository
import com.ae.apps.randomcontact.room.repositories.ContactGroupTestRepository
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException

class ContactGroupViewModelMockTest {

    @get:Rule
    val exceptionRule = ExpectedException.none()

    @Test
    fun test_allContactsEmpty(){
        val expected = 0;
        val repository:ContactGroupRepository = mock()
        whenever(repository.getAllContactGroups())
            .thenReturn(MutableLiveData(arrayListOf()))

        val model = ContactGroupViewModel(repository)

        val contactGroups = model.getAllContactGroups().value

        Assert.assertEquals(expected, contactGroups!!.size)
    }

    @Test
    fun test_allContactsSingle(){
        val expected = 1;
        val repository:ContactGroupRepository = mock()
        whenever(repository.getAllContactGroups())
            .thenReturn(MutableLiveData(arrayListOf(
                ContactGroup(1, "Group 1", "1,2,3,4")
            )))

        val model = ContactGroupViewModel(repository)

        val contactGroups = model.getAllContactGroups().value

        Assert.assertEquals(expected, contactGroups!!.size)
    }

    @Test
    fun test_allContactsMutable(){
        val expected = 3;
        val repository:ContactGroupRepository = mock()
        whenever(repository.getAllContactGroups())
            .thenReturn(MutableLiveData(arrayListOf(
                ContactGroup(1, "Group 1", "1,2,3,4"),
                ContactGroup(1, "Group 1", "1,2,3,4"),
                ContactGroup(1, "Group 1", "1,2,3,4")
                )))

        val model = ContactGroupViewModel(repository)

        val contactGroups = model.getAllContactGroups().value

        Assert.assertEquals(expected, contactGroups!!.size)
    }

    @Test
    fun test_findContactByGroupIdWasCalled(){
        val id = "1";
        val repository:ContactGroupRepository = mock()
        whenever(repository.getAllContactGroups())
            .thenReturn(MutableLiveData(arrayListOf(
                ContactGroup(1, "Group 1", "1,2,3,4")
            )))

        val model = ContactGroupViewModel(repository)

        model.findContactGroupById(id)

        verify(repository)
            .findContactGroupById(id)
    }

    @Test
    fun test_findContactByGroupIdNotFound(){
        val id = "1";
        val repository:ContactGroupRepository = mock()
        whenever(repository.getAllContactGroups())
            .thenReturn(MutableLiveData(arrayListOf(
                ContactGroup(1, "Group 1", "1,2,3,4")
            )))

        val model = ContactGroupViewModel(repository)

        model.findContactGroupById(id)

        verify(repository)
            .findContactGroupById(id)
    }

    @Test
    fun test_create(){
        val repository:ContactGroupRepository = mock()
        val model = ContactGroupViewModel(repository)
        val contactGroup = ContactGroup(1, "Group 1", "1,2,3,4")
        model.createContactGroup( contactGroup )

        runBlocking {
            verify(repository)
                .createContactGroup(any())
        }

    }

}