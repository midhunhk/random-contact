package com.ae.apps.randomcontact.contacts

import com.ae.apps.lib.api.contacts.ContactsApiGateway
import com.ae.apps.lib.common.models.ContactInfo
import com.ae.apps.randomcontact.preferences.AppPreferences
import com.ae.apps.randomcontact.room.entities.ContactGroup
import com.ae.apps.randomcontact.room.repositories.ContactGroupRepository
import com.ae.apps.randomcontact.utils.DEFAULT_CONTACT_GROUP
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

/**
 * Covers how [RandomContactApiGatewayImpl.getRandomContact] picks a contact when a custom
 * contact group is selected, in particular when the group refers to contacts or a group that
 * no longer exist.
 */
class RandomContactGroupSelectionTest {

    private companion object {
        const val GROUP_ID = "7"
        const val REPEATS = 50
    }

    private lateinit var contactsApi: ContactsApiGateway
    private lateinit var repository: ContactGroupRepository
    private lateinit var appPreferences: AppPreferences
    private lateinit var gateway: RandomContactApiGatewayImpl

    /** Contacts that still exist in the address book, by id */
    private val existingContacts = listOf("1", "2", "3").associateWith { contact(it) }

    @Before
    fun setUp() {
        contactsApi = mock(ContactsApiGateway::class.java)
        `when`(contactsApi.allContacts).thenReturn(existingContacts.values.toList())
        `when`(contactsApi.readContactsCount).thenReturn(existingContacts.size.toLong())
        // Like the library: null for an id that was not read, e.g. a deleted contact
        `when`(contactsApi.getContactInfo(anyString(), any())).thenAnswer {
            existingContacts[it.getArgument<String>(0)]
        }

        repository = mock(ContactGroupRepository::class.java)
        `when`(repository.getContactGroupCount()).thenReturn(1)

        appPreferences = mock(AppPreferences::class.java)
        `when`(appPreferences.selectedContactGroup()).thenReturn(GROUP_ID)

        gateway = RandomContactApiGatewayImpl(repository, contactsApi, appPreferences)
    }

    @Test
    fun groupWithDeletedMember_onlyReturnsMembersThatStillExist() {
        // "99" was deleted from the address book after the group was saved
        selectGroup("99,2")

        repeat(REPEATS) {
            assertEquals("2", gateway.randomContact?.id)
        }
    }

    @Test
    fun groupWhoseMembersWereAllDeleted_returnsNull() {
        selectGroup("98,99")

        assertNull(gateway.randomContact)
    }

    @Test
    fun selectedGroupThatNoLongerExists_fallsBackToAllContacts() {
        `when`(repository.findContactGroupById(GROUP_ID)).thenReturn(null)

        val contact = gateway.randomContact

        assert(contact?.id in existingContacts.keys) { "Expected one of all contacts, got ${contact?.id}" }
        verify(appPreferences).setSelectedContactGroup(DEFAULT_CONTACT_GROUP)
    }

    @Test
    fun allContactsSelected_doesNotQueryGroups() {
        `when`(appPreferences.selectedContactGroup()).thenReturn(DEFAULT_CONTACT_GROUP)

        val contact = gateway.randomContact

        assert(contact?.id in existingContacts.keys) { "Expected one of all contacts, got ${contact?.id}" }
        verify(repository, never()).findContactGroupById(anyString())
    }

    private fun selectGroup(memberIds: String) {
        `when`(repository.findContactGroupById(GROUP_ID)).thenReturn(
            ContactGroup(id = GROUP_ID.toInt(), name = "Friends", selectedContacts = memberIds)
        )
    }

    private fun contact(id: String) = ContactInfo().apply {
        this.id = id
        name = "Contact $id"
    }
}
