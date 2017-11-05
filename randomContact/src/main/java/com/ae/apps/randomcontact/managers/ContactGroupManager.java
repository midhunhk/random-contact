/*
 * Copyright 2017 Midhun Harikumar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ae.apps.randomcontact.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.ae.apps.randomcontact.data.ContactGroup;
import com.ae.apps.randomcontact.database.RandomContactDatabase;

import java.util.List;

/**
 * Manages the data for ContactGroups
 */
public class ContactGroupManager {

    private static final String DEFAULT_CONTACT_GROUP = "0";
    private static final String PREF_KEY_SELECTED_CONTACT_GROUP = "pref_key_selected_contact_group";

    private static ContactGroupManager sInstance;

    private SharedPreferences mSharedPreferences;
    private RandomContactDatabase mRandomContactDatabase;

    /**
     * Returns an instance of ContactGroupManager
     *
     * @param context context
     * @return
     */
    public static ContactGroupManager getInstance(final Context context) {
        if (null == sInstance) {
            sInstance = new ContactGroupManager(context);
        }
        return sInstance;
    }

    private ContactGroupManager(final Context context) {

        mRandomContactDatabase = new RandomContactDatabase(context);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * Returns the selected contact group
     *
     * @return
     */
    public String selectedContactGroup() {
        return mSharedPreferences.getString(PREF_KEY_SELECTED_CONTACT_GROUP, DEFAULT_CONTACT_GROUP);
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

    /**
     * Return the specific ContactGroup details by id
     *
     * @param mCurrentContactGroupId
     * @return
     */
    public ContactGroup getContactGroupById(final String mCurrentContactGroupId) {
        return mRandomContactDatabase.getContactGroupById(mCurrentContactGroupId);
    }
}
