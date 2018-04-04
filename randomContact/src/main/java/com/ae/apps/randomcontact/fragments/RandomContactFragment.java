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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ae.apps.common.managers.contact.AeContactManager;
import com.ae.apps.common.views.RoundedImageView;
import com.ae.apps.common.vo.ContactVo;
import com.ae.apps.randomcontact.R;
import com.ae.apps.randomcontact.adapters.ContactRecyclerAdapter;
import com.ae.apps.randomcontact.data.GlobalThemeChanger;
import com.ae.apps.randomcontact.managers.RandomContactManager;
import com.ae.apps.randomcontact.utils.AppConstants;
import com.ae.apps.randomcontact.utils.Utils;

import java.util.Collections;

public class RandomContactFragment extends Fragment {

    private static final String SAVED_CONTACT_ID = "savedContactId";

    private TextView mUserName;
    private TextView mUserContactedCount;
    private TextView mLastContactedTime;
    private TextView mContactNowText;
    private LinearLayout mLastContactedLayout;
    private RoundedImageView mUserImage;
    private View mListContainer;
    private Toolbar mFragmentToolbar;
    private View mToolbarExtend;

    private Animation mFadeInAnimation;
    private Animation mSlideInAnimation;
    private Bitmap mDefaultUserImage;
    private ContactRecyclerAdapter mRecyclerAdapter;
    private GlobalThemeChanger mContactManagerProvider;
    private Context mContext;
    private AeContactManager mContactManager;
    private ContactVo mCurrentContact;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_random_contact, container, false);

        mContext = getActivity().getBaseContext();
        mContactManager = RandomContactManager.getInstance(mContext);
        mContactManagerProvider = (GlobalThemeChanger) getActivity();

        initViews(layout);

        setupRecyclerView(layout);

        configureAnimations();

        setupMenu();

        showInitialContact(savedInstanceState);

        return layout;
    }

    private void showInitialContact(Bundle savedInstanceState) {
        // Show same contact if orientation has been changed
        if (null != savedInstanceState) {
            String savedContactId = savedInstanceState.getString(SAVED_CONTACT_ID);
            mCurrentContact = mContactManager.getContactWithPhoneDetails(savedContactId);
            if (null != mCurrentContact) {
                displayContact(mCurrentContact);
            }
        } else {
            showRandomContact();
        }
    }

    private void setupMenu() {
        // Set Menu for the fragment's toolbar and the click handler
        mFragmentToolbar.inflateMenu(R.menu.main);
        mFragmentToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.action_refresh:
                        // Show another Random Contact
                        showRandomContact();
                        return true;
                    case R.id.action_view_contact:
                        Toast.makeText(getContext(), "Opening contact in Contacts app", Toast.LENGTH_SHORT).show();
                        Utils.showContactInAddressBook(getActivity(), getCurrentContact().getId());
                        return true;
                }

                return false;
            }
        });
    }

    private void configureAnimations() {
        // Configure some animations
        mFadeInAnimation = AnimationUtils.loadAnimation(mContext, R.anim.fade_in);
        mSlideInAnimation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_top);
        mFadeInAnimation.setStartOffset(250);
    }

    private void setupRecyclerView(View layout) {
        // Create the Recycler Adapter
        mRecyclerAdapter = new ContactRecyclerAdapter(Collections.EMPTY_LIST,
                R.layout.contact_info_item,
                getActivity());

        boolean whatsAppInstalled = Utils.isPackageInstalled(AppConstants.PACKAGE_NAME_WHATSAPP,
                getActivity().getPackageManager());
        mRecyclerAdapter.setEnableWhatsAppIntegration(whatsAppInstalled);

        // Find the RecyclerView and set some properties
        RecyclerView recyclerView = (RecyclerView) layout.findViewById(android.R.id.list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mRecyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void initViews(View layout) {
        // Find some UI controls
        mUserName = (TextView) layout.findViewById(R.id.userDisplayName);
        mUserImage = (RoundedImageView) layout.findViewById(R.id.userProfileImage);
        mUserContactedCount = (TextView) layout.findViewById(R.id.userContactedCount);
        mLastContactedTime = (TextView) layout.findViewById(R.id.lastContactedTime);
        mLastContactedLayout = (LinearLayout) layout.findViewById(R.id.lastContactedLayout);
        mContactNowText = (TextView) layout.findViewById(R.id.contactNowText);
        mListContainer = layout.findViewById(R.id.listContainer);
        mFragmentToolbar = (Toolbar) layout.findViewById(R.id.toolbar2);
        mToolbarExtend = layout.findViewById(R.id.toolbarExtend);

        // Decode the default image and cache it
        mDefaultUserImage = BitmapFactory.decodeResource(getResources(),
                com.ae.apps.aeappslibrary.R.drawable.profile_icon_4);

        // Hide the last contacted time initially
        mLastContactedLayout.setVisibility(View.GONE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (null != mCurrentContact) {
            outState.putString(SAVED_CONTACT_ID, mCurrentContact.getId());
        }
    }

    private void displayContact(ContactVo contactVo) {
        if (null != contactVo) {
            mUserName.setText(contactVo.getName());
            mUserContactedCount.setText(contactVo.getTimesContacted());

            // Show the last contacted time of the user if it exists
            showLastContactedTime(contactVo);

            // User has the right for a profile image. if they do not have it, one will be provided.
            Bitmap bitmap = mContactManager.getContactPhoto(contactVo.getId(), mDefaultUserImage);
            mUserImage.setImageBitmap(bitmap);

            // theme some UI elements based on the image color
            applyTheme(bitmap);

            // Change the data for the RecyclerView
            mRecyclerAdapter.setList(contactVo.getPhoneNumbersList());

            // Do some basic Animations
            mUserName.startAnimation(mSlideInAnimation);
            mListContainer.startAnimation(mFadeInAnimation);

            mCurrentContact = contactVo;
        }
    }

    private void showLastContactedTime(ContactVo contactVo) {
        String lastContacted = contactVo.getLastContactedTime();
        if (null != lastContacted && lastContacted.trim().length() > 0) {
            mLastContactedTime.setText(Utils.friendlyDateFormat(getContext(), lastContacted));
            mLastContactedLayout.setVisibility(View.VISIBLE);
            mLastContactedLayout.startAnimation(mFadeInAnimation);
        } else {
            mLastContactedLayout.setVisibility(View.GONE);
        }
    }

    private void applyTheme(Bitmap bitmap) {
        // Use palette to generate a color from the contact image and apply to
        Palette palette = Palette.from(bitmap).generate();

        // Update the toolbar colors
        mContactManagerProvider.applyThemeFromImage(palette);

        int vibrantColor = palette.getVibrantColor(getResources().getColor(R.color.colorAccent));
        mUserContactedCount.setTextColor(vibrantColor);
        mContactNowText.setTextColor(vibrantColor);

        int toolbarColor = palette.getDarkVibrantColor(getResources()
                .getColor(R.color.colorPrimary));
        mFragmentToolbar.setBackgroundColor(toolbarColor);
        mToolbarExtend.setBackgroundColor(vibrantColor);
    }

    public void showRandomContact() {
        ContactVo contactVo = mContactManager.getRandomContact();

        if (null != contactVo) {
            displayContact(contactVo);
        } else {
            Toast.makeText(mContext, getResources().getString(R.string.str_empty_contact_list), Toast.LENGTH_LONG)
                    .show();
        }
    }

    public ContactVo getCurrentContact() {
        return mCurrentContact;
    }

}
