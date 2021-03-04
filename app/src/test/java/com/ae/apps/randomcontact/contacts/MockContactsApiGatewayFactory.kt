package com.ae.apps.randomcontact.contacts

import android.content.Context
import com.ae.apps.lib.api.contacts.types.ContactsApiGatewayFactory

class MockContactsApiGatewayFactory : ContactsApiGatewayFactory {

    override fun getContactsApiGateway(p0: Context?) = MockContactsApiGateway()

}