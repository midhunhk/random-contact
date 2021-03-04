package com.ae.apps.randomcontact.contacts

import android.content.Context
import com.ae.apps.lib.api.contacts.ContactsApiGateway
import com.ae.apps.lib.api.contacts.impl.ContactsApiGatewayImpl
import com.ae.apps.lib.api.contacts.types.ContactsApiGatewayFactory

class RandomContactsApiGatewayFactory : ContactsApiGatewayFactory {

    override fun getContactsApiGateway(context: Context): ContactsApiGateway {
        return ContactsApiGatewayImpl.Builder(context).build()
    }
}