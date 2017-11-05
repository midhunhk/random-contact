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
package com.ae.apps.randomcontact.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.Toast;

import com.ae.apps.randomcontact.R;
import com.ae.apps.randomcontact.adapters.ContactGroupRecyclerViewAdapter;
import com.ae.apps.randomcontact.data.ContactGroup;
import com.ae.apps.randomcontact.data.ContactGroupInteractionListener;
import com.ae.apps.randomcontact.managers.ContactGroupManager;
import com.ae.apps.randomcontact.utils.AppConstants;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link ContactGroupInteractionListener}
 * interface.
 */
public class ContactGroupFragment extends Fragment
        implements AddContactGroupDialogFragment.AddContactGroupDialogListener,
        ContactGroupInteractionListener {

    private RadioButton mRadioAllContacts;
    private List<ContactGroup> mContactGroups;
    private ContactGroupManager mContactGroupManager;
    private ContactGroupRecyclerViewAdapter mViewAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ContactGroupFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_group, container, false);

        mContactGroupManager = ContactGroupManager.getInstance(getActivity());

        initViews(view);

        return view;
    }

    private void initViews(View view) {
        String selectedContactGroup = mContactGroupManager.selectedContactGroup();
        mRadioAllContacts = (RadioButton) view.findViewById(R.id.radioAllContacts);

        mRadioAllContacts.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mViewAdapter.setSelectedGroupId(AppConstants.DEFAULT_CONTACT_ID);
                mViewAdapter.notifyDataSetChanged();
                mContactGroupManager.setSelectedContactGroup(getActivity(), AppConstants.DEFAULT_CONTACT_ID);
            }
        });

        View createButton = view.findViewById(R.id.btnCreateContactGroup);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddContactGroupDialog();
            }
        });

        setUpRecyclerView(view, selectedContactGroup);

        if (AppConstants.DEFAULT_CONTACT_ID.equals(selectedContactGroup)) {
            mRadioAllContacts.setSelected(true);
            mRadioAllContacts.setChecked(true);
        }
    }

    private void setUpRecyclerView(View view, String selectedContactGroup) {
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);
        mContactGroups = mContactGroupManager.getAllContactGroups();
        if (null != recyclerView) {
            Context context = view.getContext();
            mViewAdapter = new ContactGroupRecyclerViewAdapter(mContactGroups, this);
            mViewAdapter.setSelectedGroupId(selectedContactGroup);
            recyclerView.setAdapter(mViewAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
        }
    }

    private void showAddContactGroupDialog() {
        FragmentManager fragmentManager = getFragmentManager();
        AddContactGroupDialogFragment fragment = AddContactGroupDialogFragment.newInstance();
        fragment.setTargetFragment(ContactGroupFragment.this, 300);
        fragment.show(fragmentManager, "fragment_add_contact_group");
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onContactGroupAdded(final ContactGroup contactGroup) {
        ContactGroup addedContactGroup = ContactGroupManager.getInstance(getActivity())
                .addContactGroup(contactGroup);
        mContactGroups.add(contactGroup);
        mViewAdapter.notifyItemInserted(mContactGroups.indexOf(contactGroup));
        Toast.makeText(getContext(), "Added group " + addedContactGroup.getName() + " with id " + addedContactGroup.getId(),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onContactGroupSelected(@NotNull ContactGroup item) {
        mRadioAllContacts.setSelected(false);
        mRadioAllContacts.setChecked(false);
        mContactGroupManager.setSelectedContactGroup(getActivity(), item.getId());
    }
}
