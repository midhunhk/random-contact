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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

        String selectedContactGroup = mContactGroupManager.selectedContactGroup();

        initViews(view, selectedContactGroup);

        setUpRecyclerView(view, selectedContactGroup);

        return view;
    }

    private void initViews(View view, String selectedContactGroup) {
        mRadioAllContacts = view.findViewById(R.id.radioAllContacts);

        mRadioAllContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewAdapter.setSelectedGroupId(AppConstants.DEFAULT_CONTACT_GROUP);
                mViewAdapter.notifyDataSetChanged();
                mContactGroupManager.setSelectedContactGroup(getActivity(), AppConstants.DEFAULT_CONTACT_GROUP);
            }
        });

        View createButton = view.findViewById(R.id.btnCreateContactGroup);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddContactGroupDialog();
            }
        });

        if (AppConstants.DEFAULT_CONTACT_GROUP.equals(selectedContactGroup)) {
            mRadioAllContacts.setSelected(true);
            mRadioAllContacts.setChecked(true);
        }
    }

    private void setUpRecyclerView(View view, String selectedContactGroup) {
        RecyclerView recyclerView = view.findViewById(R.id.list);
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
        ContactGroup addedContactGroup = mContactGroupManager.addContactGroup(contactGroup);
        mContactGroups.add(contactGroup);
        mViewAdapter.notifyItemInserted(mContactGroups.indexOf(contactGroup));
        Toast.makeText(getContext(), "Added group " + addedContactGroup.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onContactGroupSelected(@NotNull ContactGroup item) {
        mRadioAllContacts.setSelected(false);
        mRadioAllContacts.setChecked(false);
        mContactGroupManager.setSelectedContactGroup(getActivity(), item.getId());
    }

    @Override
    public void onContactGroupDeleted(@NotNull final ContactGroup item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setCancelable(true)
                .setTitle(R.string.str_contact_group_delete_title)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        processContactGroupRemoval(item);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.show();
    }

    private void processContactGroupRemoval(@NotNull ContactGroup item) {
        int position = mContactGroups.indexOf(item);
        mContactGroups.remove(item);
        mViewAdapter.notifyItemRemoved(position);

        // Remove the contact group item from the database
        mContactGroupManager.deleteContactGroup(item);

        // Make the All Contacts radio checked if the deleted item was previously selected
        if (mContactGroupManager.selectedContactGroup().equals(item.getId())) {
            mRadioAllContacts.setChecked(true);
            mContactGroupManager.setSelectedContactGroup(getContext(), AppConstants.DEFAULT_CONTACT_GROUP);
        }
    }
}
