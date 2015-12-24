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

import com.ae.apps.common.managers.ContactManager;
import com.ae.apps.common.mock.MockContactDataUtils;
import com.ae.apps.common.views.RoundedImageView;
import com.ae.apps.common.vo.ContactVo;
import com.ae.apps.randomcontact.R;
import com.ae.apps.randomcontact.adapters.ContactRecyclerAdapter;
import com.ae.apps.randomcontact.data.ContactManagerProvider;

public class RandomContactFragment extends Fragment {

	private static final String		SAVED_CONTACT_ID	= "savedContactId";

	private TextView				mUserName;
	private TextView				mUserContactedCount;
	private TextView				mLastContactedTime;
	private TextView				mContactNowText;
	private LinearLayout			mLastContactedLayout;
	private RoundedImageView		mUserImage;
	private View					mListContainer;
	private Toolbar					mFragmentToolbar;
	private View					mToolbarExtend;

	private Animation				mFadeInAnimation;
	private Animation				mSlideInAnimation;
	private Bitmap					mDefaultUserImage;
	private ContactRecyclerAdapter	mRecyclerAdapter;

	private Context					mContext;
	private ContactManagerProvider	mContactManagerProvider;

	private ContactVo				mCurrentContact;

	/**
	 * This wont work when orientation switched to landscape
	 */
	private boolean					isMockMode			= false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.fragment_random_contact, container, false);

		mContactManagerProvider = (ContactManagerProvider) getActivity();
		mContext = getActivity().getBaseContext();

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
				com.ae.apps.aeappslibrary.R.drawable.profile_icon_5);

		// Hide the last contacted time initially
		mLastContactedLayout.setVisibility(View.GONE);

		// Create the Recycler Adapter
		mRecyclerAdapter = new ContactRecyclerAdapter(null, R.layout.contact_info_row, mContext);

		// Find the RecyclerView and set some properties
		RecyclerView recyclerView = (RecyclerView) layout.findViewById(android.R.id.list);
		recyclerView.setHasFixedSize(true);
		recyclerView.setAdapter(mRecyclerAdapter);
		recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
		recyclerView.setItemAnimator(new DefaultItemAnimator());

		// Configure some animations
		mFadeInAnimation = AnimationUtils.loadAnimation(mContext, R.animator.fade_in);
		mSlideInAnimation = AnimationUtils.loadAnimation(mContext, R.animator.slide_in_top);
		mFadeInAnimation.setStartOffset(250);

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
					if (null != mContactManagerProvider) {
						mContactManagerProvider.getContactDataManager()
								.showContactInAddressBook(getActivity(), getCurrentContact());
					}
					return true;
				}

				return false;
			}
		});

		// Lets start with showing a random contact
		if (null != savedInstanceState) {
			String savedContactId = savedInstanceState.getString(SAVED_CONTACT_ID);
			mCurrentContact = mContactManagerProvider.getContactDataManager()
					.getContactWithPhoneDetails(savedContactId);
			if (null != mCurrentContact) {
				displayContact(mCurrentContact);
			}
		} else {
			showRandomContact();
		}

		return layout;
	}

	/**
	 * Displays a random contact entry
	 */
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

			// Show the last contcted time of the user if it exists
			showLastContactedTime(contactVo);

			// User has the right for a profile image. if they do not have it, one will be provided.
			Bitmap bitmap = getContactManager().getContactPhotoWithMock(contactVo, mDefaultUserImage, getResources());
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
			mLastContactedTime.setText(lastContacted);
			mLastContactedLayout.setVisibility(View.VISIBLE);
			mLastContactedLayout.startAnimation(mFadeInAnimation);
		} else {
			mLastContactedLayout.setVisibility(View.GONE);
		}
	}

	private ContactManager getContactManager() {
		return mContactManagerProvider.getContactDataManager();
	}

	private void applyTheme(Bitmap bitmap) {
		// Use palette to generate a color from the contact image and apply to
		Palette palette = Palette.generate(bitmap);

		// Upadte the toolbar colors
		mContactManagerProvider.applyThemeFromImage(palette);

		int vibrantColor = palette.getVibrantColor(R.color.bright_orange);
		mUserContactedCount.setTextColor(vibrantColor);
		mContactNowText.setTextColor(vibrantColor);

		int toolbarColor = palette.getLightVibrantColor(R.color.bright_foreground_material_dark);
		mFragmentToolbar.setBackgroundColor(toolbarColor);
		mToolbarExtend.setBackgroundColor(toolbarColor);
	}

	public void showRandomContact() {
		ContactVo contactVo = null;
		if (isMockMode) {
			contactVo = MockContactDataUtils.getMockContact();
		} else {
			contactVo = mContactManagerProvider.getContactDataManager().getRandomContact();
		}

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
