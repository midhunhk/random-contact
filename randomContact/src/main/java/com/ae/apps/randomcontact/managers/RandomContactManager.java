/*
 * Copyright 2013 Midhun Harikumar
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
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.ae.apps.common.managers.ContactManager;
import com.ae.apps.common.managers.contact.AeContactManager;
import com.ae.apps.common.managers.contact.ContactDataConsumer;
import com.ae.apps.common.vo.ContactVo;
import com.ae.apps.common.vo.MessageVo;
import com.ae.apps.randomcontact.data.ContactGroup;
import com.ae.apps.randomcontact.utils.AppConstants;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * The RandomContactManager adds custom behavior to the ContactManager for this app.
 * 1. Give TopContacts using the interface {@link FilteredContactList}
 * 2. Generate Random contacts with a selected list of contacts
 *
 * @author Midhunhk
 */
public class RandomContactManager implements FilteredContactList, AeContactManager {

    private static RandomContactManager sRandomContactManager;

    private int index = 0;
    private String mCurrentContactGroupId;
    private List<String> mCustomContactIds;
    private final AeContactManager mContactManager;
    private final ContactGroupManager mContactGroupManager;

    /**
     * Returns an instance of RandomContactManager
     *
     * @param context context
     * @return instance
     */
    public static AeContactManager getInstance(final Context context) {
        if (null == sRandomContactManager) {
            sRandomContactManager = new RandomContactManager(context);
        }
        return sRandomContactManager;
    }

    private RandomContactManager(final Context context) {
        mContactGroupManager = ContactGroupManager.getInstance(context);
        mCurrentContactGroupId = mContactGroupManager.selectedContactGroup();

        mContactManager = new ContactManager.Builder(context.getContentResolver(), context.getResources())
                .addContactsWithPhoneNumbers(true)
                .build();
        mContactManager.fetchAllContacts();

        // In order to avoid repetition of contacts, we simply start from a random point in the list of contacts
        // Hopefully the user has a very large number of contacts with phone numbers and may not notice :)
        // This would be based on the order the contacts were added to the contacts database which should
        // be random unless it was imported in a sorted order (the chances of which are less)
        int totalContactCount = getTotalContactCount();

        // Handle conditions when there are no contacts. This can happen when
        // 	1. The user has no contacts yet
        //	2. Access to Contacts permission denied in Marshmallow and up
        if (totalContactCount > 0) {
            index = new Random().nextInt(totalContactCount);
        }
    }

    @Override
    public void fetchAllContacts() {
        mContactManager.fetchAllContacts();
    }

    @Override
    public void fetchAllContactsAsync(ContactDataConsumer consumer) {
        mContactManager.fetchAllContactsAsync(consumer);
    }

    @Override
    public List<ContactVo> getAllContacts() throws UnsupportedOperationException {
        return mContactManager.getAllContacts();
    }

    @Override
    public int getTotalContactCount() {
        return mContactManager.getTotalContactCount();
    }

    /**
     * Supply a RandomContact
     *
     * @return a random contact object, null if no contacts found
     * @throws UnsupportedOperationException if ContactManager is not yet initialized
     */
    @Override
    public ContactVo getRandomContact() {
        final List<ContactVo> allContacts = getAllContacts();
        if (!allContacts.isEmpty()) {
            String randomContactId;

            boolean selectionChanged = isContactGroupSelectionChanged();

            if (AppConstants.DEFAULT_CONTACT_GROUP.equals(mCurrentContactGroupId)) {
                // Increment the index - we will wrap around when we reach the end
                index = (index + 1) % getTotalContactCount();
                randomContactId = allContacts.get(index).getId();
            } else {
                // Get the Random Contact from a sublist
                if (selectionChanged || null == mCustomContactIds || mCustomContactIds.isEmpty()) {
                    // If the source selection was changed, we need to update the custom contacts list
                    updateCustomContactGroup();
                }
                index = new Random().nextInt(mCustomContactIds.size());
                randomContactId = mCustomContactIds.get(index);
            }

            // Make sure the phone details are present by wrapping the contact details
            // in a call to get contact with phone details
            return getContactWithPhoneDetails(randomContactId);
        }
        return null;
    }

    private boolean isContactGroupSelectionChanged() {
        String selectedContactGroupId = mContactGroupManager.selectedContactGroup();
        if (!selectedContactGroupId.equals(mCurrentContactGroupId)) {
            mCurrentContactGroupId = selectedContactGroupId;
            return true;
        }
        return false;
    }

    private void updateCustomContactGroup() {
        ContactGroup contactGroup = mContactGroupManager.getContactGroupById(mCurrentContactGroupId);
        String[] selectedContacts = contactGroup.getSelectedContacts().split(AppConstants.CONTACT_ID_SEPARATOR);
        mCustomContactIds = Arrays.asList(selectedContacts);
    }

    @Override
    public ContactVo getContactWithPhoneDetails(String contactId) {
        return mContactManager.getContactWithPhoneDetails(contactId);
    }

    @Override
    public Bitmap getContactPhoto(String contactId, Bitmap defaultImage) {
        return mContactManager.getContactPhoto(contactId, defaultImage);
    }

    @Override
    public ContactVo getContactInfo(String contactId) {
        return mContactManager.getContactInfo(contactId);
    }

    @Override
    public List<MessageVo> getContactMessages(String contactId) {
        return mContactManager.getContactMessages(contactId);
    }

    @Override
    public long getContactIdFromRawContactId(String rawContactId) {
        return mContactManager.getContactIdFromRawContactId(rawContactId);
    }

    @Override
    public String getContactIdFromAddress(String address) {
        return mContactManager.getContactIdFromAddress(address);
    }

    @Override
    public Bitmap getContactPhoto(String contactId) {
        return mContactManager.getContactPhoto(contactId);
    }

    @NonNull
    @Override
    public List<ContactVo> getTopFrequentlyContacted(int maxResults) {
        List<ContactVo> filteredList = Collections.EMPTY_LIST;
        if (mContactManager.getTotalContactCount() > 0) {
            // The listToFilter would be sorted in place
            List<ContactVo> listToFilter = getAllContacts();
            int contactsToShow = Math.min(maxResults, mContactManager.getTotalContactCount());

            // Sort the contacts list based on the number of times each are contacted
            Collections.sort(listToFilter, new Comparator<ContactVo>() {
                @Override
                public int compare(ContactVo contact1, ContactVo contact2) {
                    return Integer.valueOf(contact2.getTimesContacted())
                            .compareTo(Integer.valueOf(contact1.getTimesContacted()));
                }
            });

            // We shall respect the maxResults param and return only a subset of the filtered data
            filteredList = listToFilter.subList(0, contactsToShow);
        }
        return filteredList;
    }

}
