package com.ae.apps.randomcontact.activities

import android.os.Bundle
import android.os.PersistableBundle
import com.ae.apps.lib.api.contacts.ContactsApiGateway
import com.ae.apps.lib.common.models.ContactInfo
import com.ae.apps.lib.multicontact.MultiContactBaseActivity
import com.ae.apps.randomcontact.R
import com.ae.apps.randomcontact.contacts.RandomContactApiGatewayImpl
import java.util.*


class MultiContactPickerActivity : MultiContactBaseActivity() {

    private var contactsApiGateway: ContactsApiGateway? = null

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

        customize()
    }

    override fun contactsList(): MutableList<ContactInfo> {
        if (null == contactsApiGateway) {
            contactsApiGateway = RandomContactApiGatewayImpl.getInstance(this)
        }
        val list = contactsApiGateway!!.allContacts
        // Sort the contacts based on name
        Collections.sort(list, object : Comparator<ContactInfo?> {

            override fun compare(o1: ContactInfo?, o2: ContactInfo?): Int {
                if (null == o1?.name && null == o2?.name) return 0
                if (null == o1?.name) return -1
                if (null == o2?.name) return 1

                return o1.name.compareTo(o2.name)
            }
        })

        return list
    }

    override fun getLayoutResourceId(): Int = R.layout.activity_multi_contact_picker

    fun customize(){
        // TODO
    }
}