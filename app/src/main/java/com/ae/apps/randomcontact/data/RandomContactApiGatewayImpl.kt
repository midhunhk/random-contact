package com.ae.apps.randomcontact.data

import android.content.Context
import com.ae.apps.lib.api.contacts.ContactsApiGateway
import com.ae.apps.lib.api.contacts.impl.ContactsApiGatewayImpl
import com.ae.apps.lib.api.contacts.types.ContactInfoFilterOptions
import com.ae.apps.lib.api.contacts.types.ContactInfoOptions
import com.ae.apps.lib.api.contacts.types.ContactsDataConsumer
import com.ae.apps.lib.common.models.ContactInfo
import java.util.*

class RandomContactApiGatewayImpl : ContactsApiGateway, ContactsDataConsumer {

    private var index:Int = 0
    private lateinit var dataConsumer: ContactsDataConsumer

    companion object{
        @Volatile private var INSTANCE:ContactsApiGateway? = null
        @Volatile private lateinit var contactsApi:ContactsApiGateway

        fun getInstance(context: Context): ContactsApiGateway =
            INSTANCE ?: synchronized(this){
                contactsApi = ContactsApiGatewayImpl.Builder(context)
                    .build()
                INSTANCE ?: RandomContactApiGatewayImpl().also { INSTANCE = it }
            }
    }
    override fun initialize(options: ContactInfoFilterOptions?) {
        initializeAsync(ContactInfoFilterOptions.of(true), this)
    }

    override fun initializeAsync(
        options: ContactInfoFilterOptions?,
        consumer: ContactsDataConsumer
    ) {
        // Store the consumer and invoke the onContactsRead on it after our own initialization
        dataConsumer = consumer
        contactsApi.initializeAsync(options, this)
    }

    override fun onContactsRead() {
        // In order to avoid repetition of contacts, we simply start from a random point in the list of contacts
        // Hopefully the user has a very large number of contacts with phone numbers and may not notice :)
        // This would be based on the order the contacts were added to the contacts database which should
        // be random unless it was imported in a sorted order (the chances of which are less)
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

        dataConsumer.onContactsRead()
    }

    override fun getAllContacts(): List<ContactInfo> = contactsApi.allContacts

    override fun getReadContactsCount(): Long = contactsApi.readContactsCount

    override fun getRandomContact(): ContactInfo? {
        if(allContacts.isEmpty()){
            return null
        }
        var randomContactId: String? = null
        index = ((index + 1) % readContactsCount.toInt())
        randomContactId = allContacts.get(index).id

        val options = ContactInfoOptions.of(true, true,
            com.ae.apps.lib.R.drawable.profile_icon_3)
        return getContactInfo(randomContactId, options)
    }

    override fun getContactInfo(contactId: String): ContactInfo = contactsApi.getContactInfo(
        contactId
    )

    override fun getContactInfo(contactId: String?, options: ContactInfoOptions?): ContactInfo = contactsApi.getContactInfo(
        contactId,
        options
    )

    override fun getContactIdFromRawContact(contactId: String?): String = contactsApi.getContactIdFromRawContact(
        contactId
    )

    override fun getContactIdFromAddress(address: String?): String = contactsApi.getContactIdFromAddress(
        address
    )


}