package com.ae.apps.randomcontact.listeners

import com.ae.apps.randomcontact.room.entities.ContactGroup

/**
 * Listener for interaction with ContactGroup
 */
interface ContactGroupInteractionListener {

    /**
     * Invoked when a contact group is selected
     */
    fun onContactGroupSelected(item: ContactGroup)

    /**
     * Invoked when a contact group is to be deleted
     */
    fun onContactGroupDeleted(item: ContactGroup)

    /**
     * Invoked when a contact group has been updated
     */
    fun onContactGroupUpdated(item: ContactGroup)
}