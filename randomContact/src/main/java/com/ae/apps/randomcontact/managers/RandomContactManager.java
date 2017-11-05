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

import android.content.ContentResolver;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;

import com.ae.apps.common.managers.ContactManager;
import com.ae.apps.common.managers.contact.AeContactManager;
import com.ae.apps.common.managers.contact.ContactDataConsumer;
import com.ae.apps.common.vo.ContactVo;
import com.ae.apps.common.vo.MessageVo;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Adds the special bit of randomness to Contact Manager. Overrides the basic random contact generation of the
 * ContactManager
 *
 * @author Midhunhk
 */
public class RandomContactManager implements FilteredContactList, AeContactManager {

    private int index = 0;

    private final AeContactManager mContactManager;

    private static RandomContactManager sRandomContactManager;

    /**
     * Returns an instance of RandomContactManager
     *
     * @param contentResolver contentResolver
     * @param res             resources
     * @return instance
     */
    public static AeContactManager getInstance(ContentResolver contentResolver, Resources res) {
        if (null == sRandomContactManager) {
            sRandomContactManager = new RandomContactManager(contentResolver, res);
        }
        return sRandomContactManager;
    }

    private RandomContactManager(ContentResolver contentResolver, Resources res) {

        mContactManager = new ContactManager.Builder(contentResolver, res)
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
            index = new Random().nextInt(getTotalContactCount());
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
     * Override the default implementation
     *
     * @return a random contact object, null if no contacts found
     */
    @Override
    public ContactVo getRandomContact() {
        if (!getAllContacts().isEmpty()) {
            // Increment the index - we will wrap around when we reach the end
            index = (index + 1) % getTotalContactCount();
            // Make sure the phone details are present
            return getContactWithPhoneDetails(getAllContacts().get(index).getId());
        }
        return null;
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

    @Nullable
    @Override
    public List<ContactVo> getTopFrequentlyContacted(int maxResults) {
        List<ContactVo> filteredList = null;
        if (mContactManager.getTotalContactCount() > 0) {
            // The listToFilter would be sorted in place
            List<ContactVo> listToFilter = getAllContacts();
            int contactsToShow = Math.min(maxResults, mContactManager.getTotalContactCount());

            // Apply the filter to the contacts
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
