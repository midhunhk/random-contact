/*
 * Copyright 2015 Midhun Harikumar
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

package com.ae.apps.randomcontact.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ae.apps.common.vo.ContactVo;
import com.ae.apps.randomcontact.R;
import com.ae.apps.randomcontact.adapters.FrequentContactAdapter;
import com.ae.apps.randomcontact.data.ContactManagerProvider;
import com.ae.apps.randomcontact.managers.FilteredContactList;

import java.util.List;

/**
 * Fragment that displays the Frequent Contacts
 *
 * @author MidhunHk
 */
public class FrequentContactsFragment extends Fragment {

    private ContactManagerProvider mContactManagerProvider = null;
    private Context mContext;
    private FilteredContactList mFilteredContacts;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mContactManagerProvider = (ContactManagerProvider) getActivity();
        mContext = getActivity().getBaseContext();

        View layout = inflater.inflate(R.layout.fragment_frequent_contacts, container, false);
        // We need to display a Filtered set of Contacts
        try {
            mFilteredContacts = (FilteredContactList) mContactManagerProvider.getContactDataManager();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement FilteredContactList");
        }

        List<ContactVo> contacts = mFilteredContacts.getTopFrequentlyContacted(40);

        // Create the Adapter for the RecyclerView here
        FrequentContactAdapter frequentContactAdapter = new FrequentContactAdapter(contacts, R.layout.frequent_contact_item,
                mContext, mContactManagerProvider.getContactDataManager());

        // Find the recycler view and set required properties
        RecyclerView recyclerView = (RecyclerView) layout.findViewById(android.R.id.list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(frequentContactAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        return layout;
    }

}
