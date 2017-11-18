package com.ae.apps.randomcontact.data

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
}