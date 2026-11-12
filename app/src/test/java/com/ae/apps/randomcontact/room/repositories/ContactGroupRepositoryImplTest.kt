package com.ae.apps.randomcontact.room.repositories

import com.ae.apps.randomcontact.room.dao.ContactGroupDao
import com.ae.apps.randomcontact.room.entities.ContactGroup
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.reset
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

class ContactGroupRepositoryImplTest {

    private companion object {
        // The repository is a process wide singleton, so every test shares one DAO mock
        val dao: ContactGroupDao = mock(ContactGroupDao::class.java)
        val repository = ContactGroupRepositoryImpl.getInstance(dao)
    }

    @Before
    fun setUp() {
        reset(dao)
    }

    @Test
    fun findContactGroupById_returnsTheGroup() {
        val group = ContactGroup(id = 3, name = "Family", selectedContacts = "1,2")
        `when`(dao.getContactGroupById(3)).thenReturn(group)

        assertEquals(group, repository.findContactGroupById("3"))
    }

    @Test
    fun findContactGroupById_returnsNullForADeletedGroup() {
        `when`(dao.getContactGroupById(3)).thenReturn(null)

        assertNull(repository.findContactGroupById("3"))
    }

    @Test
    fun findContactGroupById_returnsNullForAnIdThatIsNotANumber() {
        // The selected group id is stored as a String preference
        assertNull(repository.findContactGroupById("not-a-number"))
        assertNull(repository.findContactGroupById(""))
        verify(dao, never()).getContactGroupById(anyInt())
    }
}
