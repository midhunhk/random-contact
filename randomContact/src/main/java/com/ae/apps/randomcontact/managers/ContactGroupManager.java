package com.ae.apps.randomcontact.managers;

import android.content.Context;
import android.preference.PreferenceManager;

import com.ae.apps.randomcontact.data.ContactGroup;
import com.ae.apps.randomcontact.database.RandomContactDatabase;

import java.util.List;

/**
 * Manages the data for ContactGroups
 */
class ContactGroupManager {

    private static final String DEFAULT_CONTACT_GROUP = "0";
    private static final String PREF_KEY_SELECTED_CONTACT_GROUP = "pref_key_selected_contact_group";

    private static ContactGroupManager sInstance;

    private RandomContactDatabase mRandomContactDatabase;

    /**
     * Returns an instance of ContactGroupManager
     *
     * @param context context
     * @return
     */
    static ContactGroupManager getInstance(final Context context) {
        if (null == sInstance) {
            sInstance = new ContactGroupManager(context);
        }
        return sInstance;
    }

    private ContactGroupManager(final Context context) {
        mRandomContactDatabase = new RandomContactDatabase(context);
    }

    /**
     * Returns the selected contact group
     *
     * @param context context
     * @return
     */
    public String selectedContactGroup(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_KEY_SELECTED_CONTACT_GROUP, DEFAULT_CONTACT_GROUP);
    }

    /**
     * Sets the selected contact group
     *
     * @param context context
     * @param groupId groupId
     */
    public void setSelectedContactGroup(Context context, String groupId) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_KEY_SELECTED_CONTACT_GROUP, groupId)
                .apply();
    }

    /**
     * Adds a new Contact Group
     *
     * @param contactGroup contactGroup
     * @return
     */
    public ContactGroup addContactGroup(final ContactGroup contactGroup) {
        long rowId = mRandomContactDatabase.createContactGroup(contactGroup);
        contactGroup.setId(String.valueOf(rowId));
        return contactGroup;
    }

    /**
     * Deletes a ContactGroup
     *
     * @param contactGroup contactGroup
     */
    public void deleteContactGroup(final ContactGroup contactGroup) {
        mRandomContactDatabase.deleteContactGroup(contactGroup);
    }

    /**
     * Returns all contact groups
     *
     * @return
     */
    public List<ContactGroup> getAllContactGroups() {
        return mRandomContactDatabase.getAllContactGroups();
    }
}
