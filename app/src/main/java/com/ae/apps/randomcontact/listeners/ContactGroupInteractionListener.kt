package com.ae.apps.randomcontact.listeners

import com.ae.apps.randomcontact.room.entities.ContactGroup

/**
 * Listener for interaction with ContactGroup
 */
interface ContactGroupInteractionListener {

    /**
     * Invoked when a contact group is added
     */
    fun onContactGroupAdded(contactGroup: ContactGroup)

    /**
     * Invoked when a contact group has been updated
     */
    fun onContactGroupUpdated(originalItem:ContactGroup, updatedItem: ContactGroup)

    /**
     * Invoked when a contact group is selected
     */
    fun selectContactGroup(contactGroup:ContactGroup)

    /**
     * Invoked when a contact group is selected for update
     */
    fun editContactGroup(contactGroup:ContactGroup)

    /**
     * Invoked when a contact group is selected for delete
     */
    fun deleteContactGroup(contactGroup:ContactGroup)
}