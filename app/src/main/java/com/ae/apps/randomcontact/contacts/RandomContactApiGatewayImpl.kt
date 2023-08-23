package com.ae.apps.randomcontact.contacts

import android.content.Context
import com.ae.apps.lib.api.contacts.ContactsApiGateway
import com.ae.apps.lib.api.contacts.types.ContactInfoFilterOptions
import com.ae.apps.lib.api.contacts.types.ContactInfoOptions
import com.ae.apps.lib.api.contacts.types.ContactsApiGatewayFactory
import com.ae.apps.lib.api.contacts.types.ContactsDataConsumer
import com.ae.apps.lib.common.models.ContactInfo
import com.ae.apps.randomcontact.preferences.AppPreferences
import com.ae.apps.randomcontact.room.repositories.ContactGroupRepository
import com.ae.apps.randomcontact.utils.CONTACT_ID_SEPARATOR
import com.ae.apps.randomcontact.utils.DEFAULT_CONTACT_GROUP
import java.util.*

/**
 * An instance of the ContactsApiGateway which provides a custom implementation
 * for the random contact operation
 */
class RandomContactApiGatewayImpl(
    private val contactGroupRepository: ContactGroupRepository,
    private val contactsApi: ContactsApiGateway,
    private val appPreferences: AppPreferences
) : ContactsApiGateway, ContactsDataConsumer {
    private var index: Int = 0
    private var dataConsumer: ContactsDataConsumer? = null
    private var isContactsRead = false
    private var contactsShuffled = false
    private lateinit var shuffledContacts: List<ContactInfo>

    companion object {

        @Volatile
        private var INSTANCE: ContactsApiGateway? = null

        fun getInstance(
            context: Context,
            contactsGroupRepo: ContactGroupRepository,
            contactsApiFactory: ContactsApiGatewayFactory,
            appPreferences: AppPreferences
        ): ContactsApiGateway =
            INSTANCE ?: synchronized(this) {
                val contactsApi = contactsApiFactory.getContactsApiGateway(context)
                INSTANCE ?: RandomContactApiGatewayImpl(
                    contactsGroupRepo,
                    contactsApi,
                    appPreferences
                ).also { INSTANCE = it }
            }
    }

    override fun initialize(options: ContactInfoFilterOptions?) {
        contactsApi.initialize(ContactInfoFilterOptions.of(true))
    }

    override fun initializeAsync(
        options: ContactInfoFilterOptions?,
        consumer: ContactsDataConsumer?
    ) {
        // Store the consumer and invoke the onContactsRead on it after our own initialization
        // If data is already initialized, immediately callback to the consumer
        dataConsumer = consumer
        if (isContactsRead) {
            dataConsumer?.onContactsRead()
        } else {
            contactsApi.initializeAsync(options, this)
        }
    }

    override fun onContactsRead() {
        // In order to avoid repetition of contacts, we simply start from a random point in the list of contacts
        // Hopefully the user has a very large number of contacts with phone numbers and may not notice :)
        // This would be based on the order the contacts were added to the contacts database which should
        // be random unless it was imported in a sorted order (the chances of which are less)
        val totalContactCount = readContactsCount

        // Handle conditions when there are no contacts. This can happen when
        // 	1. The user has no contacts yet
        //	2. Access to Contacts permission denied in Marshmallow and up

        if (totalContactCount > 0) {
            index = Random().nextInt(totalContactCount.toInt())
        }

        dataConsumer?.onContactsRead()

        isContactsRead = true
    }

    override fun getAllContacts(): List<ContactInfo> = contactsApi.allContacts

    override fun getReadContactsCount(): Long = contactsApi.readContactsCount

    override fun getRandomContact(): ContactInfo? {
        if (allContacts.isEmpty()) {
            return null
        }

        // Introducing more randomness by using a shuffled list of contacts using standard api
        // All permutations occur with approximately equal likelihood.
        if(!contactsShuffled){
            // Shuffle needs to be done on a mutable set
            val temp = allContacts.toMutableList()
            temp.shuffle()
            // Store an immutable version once initialized
            shuffledContacts = Collections.unmodifiableList(temp)
            contactsShuffled = true
        }

        // If there are no custom contact groups, default to all contacts
        // The database is the source of truth, override the values in appPreference
        val customContactGroups = contactGroupRepository.getContactGroupCount()
        val selectedGroup = if(customContactGroups > 0){
            appPreferences.selectedContactGroup()
        } else {
            // Update the Selected Contact Group so that it correctly selects the
            // All Contacts option in Manage Contact Groups Fragment
            appPreferences.setSelectedContactGroup(DEFAULT_CONTACT_GROUP)
            DEFAULT_CONTACT_GROUP
        }

        val randomContactId: String
        if (DEFAULT_CONTACT_GROUP == selectedGroup) {
            index = ((index + 1) % readContactsCount.toInt())
            randomContactId = shuffledContacts[index].id
        } else {
            val contactGroup = contactGroupRepository.findContactGroupById(selectedGroup)
            val subList: List<String> = contactGroup.selectedContacts.split(CONTACT_ID_SEPARATOR)
            randomContactId = subList[Random().nextInt(subList.size)]
        }

        val options = ContactInfoOptions.Builder()
            .includePhoneDetails(true)
            .includeContactPicture(true)
            .defaultContactPicture(com.ae.apps.lib.core.R.drawable.profile_icon_3)
            .filterDuplicatePhoneNumbers(true)
            .build()

        return getContactInfo(randomContactId, options)
    }

    override fun getContactInfo(contactId: String): ContactInfo = contactsApi.getContactInfo(contactId)

    override fun getContactInfo(contactId: String?, options: ContactInfoOptions?): ContactInfo =
        contactsApi.getContactInfo(
            contactId,
            options
        )

    override fun getContactIdFromRawContact(contactId: String?): String =
        contactsApi.getContactIdFromRawContact(contactId)

    override fun getContactIdFromAddress(address: String?): String = contactsApi.getContactIdFromAddress(address)

}