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

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ae.apps.common.managers.contact.AeContactManager;
import com.ae.apps.common.vo.ContactVo;
import com.ae.apps.randomcontact.R;
import com.ae.apps.randomcontact.data.ContactGroup;
import com.ae.apps.randomcontact.exceptions.ContactGroupValidationException;
import com.ae.apps.randomcontact.managers.RandomContactManager;
import com.ae.apps.randomcontact.utils.AppConstants;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Add a contact group
 */
public class AddContactGroupDialogFragment extends AppCompatDialogFragment {

    private final String TAG = getClass().getSimpleName();
    private final int CONTACT_PICKER_RESULT = 1001;

    private Collection<ContactVo> mSelectedContacts = new ArrayList<>();
    private AeContactManager mContactManager;

    private Button mCancelDialog;
    private Button mAddGroupMembers;
    private Button mAddContactGroup;
    private TextView mContactGroupName;
    private LinearLayout mSelectedContactsContainer;

    public AddContactGroupDialogFragment() {
        // Required empty public constructor
    }

    public static AddContactGroupDialogFragment newInstance() {
        return new AddContactGroupDialogFragment();
    }

    @Override
    public void onStart() {
        super.onStart();

        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, 0);
        mContactManager = RandomContactManager.getInstance(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_contact_group_dialog, container, false);

        initViews(view);

        handleButtonClicks();

        return view;
    }

    private void initViews(View view) {
        mSelectedContactsContainer = (LinearLayout) view.findViewById(R.id.selectedContactsContainer);
        mContactGroupName = (TextView) view.findViewById(R.id.txtGroupName);
        mAddGroupMembers = (Button) view.findViewById(R.id.btnGroupMemberAdd);
        mAddContactGroup = (Button) view.findViewById(R.id.btnGroupAdd);
        mCancelDialog = (Button) view.findViewById(R.id.btnCancel);
    }

    private void handleButtonClicks() {
        mAddGroupMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickContact();
            }
        });

        mAddContactGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ContactGroup contactGroup = saveContactGroup();

                    sendResult(contactGroup);

                    dismiss();
                } catch (ContactGroupValidationException e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        mCancelDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void sendResult(ContactGroup contactGroup) {
        if (getTargetFragment() instanceof AddContactGroupDialogListener) {
            ((AddContactGroupDialogListener) getTargetFragment()).onContactGroupAdded(contactGroup);
        } else {
            throw new RuntimeException(getTargetFragment().toString() + "Must implement the interface AddContactGroupDialogListener");
        }
    }

    private ContactGroup saveContactGroup() throws ContactGroupValidationException {
        if (TextUtils.isEmpty(mContactGroupName.getText().toString())) {
            throw new ContactGroupValidationException("Please enter group name");
        }

        StringBuilder builder = new StringBuilder();
        for (ContactVo contactVo : mSelectedContacts) {
            builder.append(contactVo.getId())
                    .append(AppConstants.CONTACT_ID_SEPARATOR);
        }

        if (builder.length() == 0) {
            throw new ContactGroupValidationException("Please select contacts for this group");
        }
        builder.deleteCharAt(builder.lastIndexOf(AppConstants.CONTACT_ID_SEPARATOR));

        return new ContactGroup("", mContactGroupName.getText().toString(), builder.toString());
    }

    private void pickContact() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, CONTACT_PICKER_RESULT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == CONTACT_PICKER_RESULT) {
            // Handle Contact pick
            Uri result = data.getData();
            String contactId = result.getLastPathSegment();

            ContactVo contactVo = mContactManager.getContactInfo(contactId);

            if (null != contactVo && null != contactVo.getId()) {
                mSelectedContacts.add(contactVo);
                addMemberToContainer(contactVo);
            }
        }
    }

    private void addMemberToContainer(ContactVo contactVo) {
        TextView textView = new TextView(getActivity());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(layoutParams);
        textView.setText(contactVo.getName());
        mSelectedContactsContainer.addView(textView);
    }

    interface AddContactGroupDialogListener {
        void onContactGroupAdded(ContactGroup contactGroup);
    }

}
