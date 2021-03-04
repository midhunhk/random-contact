package com.ae.apps.randomcontact.contacts

import com.ae.apps.lib.api.contacts.ContactsApiGateway
import com.ae.apps.lib.api.contacts.types.ContactInfoFilterOptions
import com.ae.apps.lib.api.contacts.types.ContactInfoOptions
import com.ae.apps.lib.api.contacts.types.ContactsDataConsumer
import com.ae.apps.lib.common.models.ContactInfo

class MockContactsApiGateway : ContactsApiGateway {
    override fun initialize(p0: ContactInfoFilterOptions?) {
        TODO("Not yet implemented")
    }

    override fun initializeAsync(p0: ContactInfoFilterOptions?, p1: ContactsDataConsumer?) {
        TODO("Not yet implemented")
    }

    override fun getAllContacts(): MutableList<ContactInfo> {
        TODO("Not yet implemented")
    }

    override fun getReadContactsCount(): Long {
        TODO("Not yet implemented")
    }

    override fun getRandomContact(): ContactInfo {
        TODO("Not yet implemented")
    }

    override fun getContactInfo(p0: String?): ContactInfo {
        TODO("Not yet implemented")
    }

    override fun getContactInfo(p0: String?, p1: ContactInfoOptions?): ContactInfo {
        TODO("Not yet implemented")
    }

    override fun getContactIdFromRawContact(p0: String?): String {
        TODO("Not yet implemented")
    }

    override fun getContactIdFromAddress(p0: String?): String {
        TODO("Not yet implemented")
    }
}