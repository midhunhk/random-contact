package com.ae.apps.randomcontact.listeners

interface GroupMemberInteractionListener {

    /**
     * Invoked when a GroupMember is removed
     */
    fun onGroupMemberRemoved(contactId:String)

}