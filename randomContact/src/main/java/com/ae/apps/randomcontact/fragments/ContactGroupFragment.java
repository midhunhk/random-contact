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
import android.widget.Toast;

import com.ae.apps.randomcontact.R;
import com.ae.apps.randomcontact.adapters.ContactGroupRecyclerViewAdapter;
import com.ae.apps.randomcontact.data.ContactGroup;
import com.ae.apps.randomcontact.fragments.dummy.DummyContent;
import com.ae.apps.randomcontact.fragments.dummy.DummyContent.DummyItem;
import com.ae.apps.randomcontact.managers.ContactGroupManager;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ContactGroupFragment extends Fragment implements AddContactGroupDialogFragment.AddContactGroupDialogListener {

    private OnListFragmentInteractionListener mListener;

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

        // Set the adapter
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);
        if (null != recyclerView) {
            Context context = view.getContext();
            recyclerView.setAdapter(new ContactGroupRecyclerViewAdapter(DummyContent.ITEMS, mListener));
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
        }

        View createButton = view.findViewById(R.id.btnCreateContactGroup);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddContactGroupDialog();
            }
        });

        return view;
    }

    private void showAddContactGroupDialog() {
        FragmentManager fragmentManager = getFragmentManager();
        AddContactGroupDialogFragment fragment = AddContactGroupDialogFragment.newInstance();
        fragment.setTargetFragment(ContactGroupFragment.this, 300);
        fragment.show(fragmentManager, "fragment_add_contact_group");
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onContactGroupAdded(final ContactGroup contactGroup) {
        ContactGroup addedContactGroup = ContactGroupManager.getInstance(getActivity())
                .addContactGroup(contactGroup);
        Toast.makeText(getContext(), "Added group" + addedContactGroup.getName() + " with id " + addedContactGroup.getId(),
                Toast.LENGTH_SHORT).show();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(DummyItem item);
    }
}
