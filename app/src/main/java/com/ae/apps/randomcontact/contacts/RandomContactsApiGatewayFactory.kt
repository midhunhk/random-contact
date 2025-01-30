package com.ae.apps.randomcontact.contacts

import android.content.Context
import com.ae.apps.lib.api.contacts.ContactsApiGateway
import com.ae.apps.lib.api.contacts.impl.ContactsApiGatewayImpl
import com.ae.apps.lib.api.contacts.types.ContactsApiGatewayFactory

class RandomContactsApiGatewayFactory : ContactsApiGatewayFactory {

    companion object {
        @Volatile
        private var INSTANCE: ContactsApiGateway? = null
    }

    override fun getContactsApiGateway(context: Context): ContactsApiGateway {
        return INSTANCE ?: synchronized(this) {
            INSTANCE ?: ContactsApiGatewayImpl.Builder(context).build()
                .also { INSTANCE = it }
        }
    }
}