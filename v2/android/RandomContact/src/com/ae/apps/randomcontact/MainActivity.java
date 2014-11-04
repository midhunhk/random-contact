/*
 * Copyright 2014 Midhun Harikumar
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

package com.ae.apps.randomcontact;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ae.apps.common.managers.ContactManager;
import com.ae.apps.common.utils.DialogUtils;
import com.ae.apps.common.views.RoundedImageView;
import com.ae.apps.common.vo.ContactVo;
import com.ae.apps.randomcontact.activities.AboutActivity;
import com.ae.apps.randomcontact.adapters.ContactRecyclerAdapter;
import com.ae.apps.randomcontact.managers.RandomContactManager;

public class MainActivity extends Activity {

	private static final String		SAVED_CONTACT_ID	= "savedContactId";

	private TextView				mUserName;
	private TextView				mUserContactedCount;
	private TextView				mLastContactedTime;
	private ContactManager			mContactManager;
	private ContactRecyclerAdapter	mRecyclerAdapter;
	private ContactVo				mPreviousContact;
	private ContactVo				mCurrentContact;
	private LinearLayout			mLastContactedLayout;
	private RoundedImageView		mUserImage;
	private View					mListContainer;
	private Animation				mFadeInAnimation;
	private Animation				mSlideInAnimation;
	private Bitmap					mDefaultUserImage;

	@TargetApi(11)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Create a Contact Manager instance. Lets use RandomContactManager since we need a random contact
		mContactManager = new RandomContactManager(getContentResolver(), getResources());

		// Find some UI controls
		mUserName = (TextView) findViewById(R.id.userDisplayName);
		mUserImage = (RoundedImageView) findViewById(R.id.userProfileImage);
		mUserContactedCount = (TextView) findViewById(R.id.userContactedCount);
		mLastContactedTime = (TextView) findViewById(R.id.lastContactedTime);
		mLastContactedLayout = (LinearLayout) findViewById(R.id.lastContactedLayout);
		mListContainer = findViewById(R.id.listContainer);

		// Decode the default image only once
		mDefaultUserImage = BitmapFactory.decodeResource(getResources(), R.drawable.ic_user_default);

		// Hide the last contacted time initially
		mLastContactedLayout.setVisibility(View.GONE);

		// Access the Actionbar only if its available
		if (android.os.Build.VERSION.SDK_INT > 11) {
			// hide the app icon from the actionbar
			getActionBar().setDisplayShowHomeEnabled(false);
			// style the background color for the actionbar
			getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dark_blue)));
		}
		
		// Create the Recycler Adapter
		mRecyclerAdapter = new ContactRecyclerAdapter(null, R.layout.contact_info_row, this);

		// Find the RecyclerView and set some properties
		RecyclerView recyclerView = (RecyclerView) findViewById(android.R.id.list);
		recyclerView.setHasFixedSize(true);
		recyclerView.setAdapter(mRecyclerAdapter);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));
		recyclerView.setItemAnimator(new DefaultItemAnimator());
		
		// Configure some animations
		mFadeInAnimation = AnimationUtils.loadAnimation(this, R.animator.fade_in);
		mSlideInAnimation = AnimationUtils.loadAnimation(this, R.animator.slide_in_top);
		mFadeInAnimation.setStartOffset(250);

		// Lets start with showing a random contact
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_refresh:
			// Show another Random Contact
			showRandomContact();
			return true;
		case R.id.action_about:
			// show about screen
			startActivity(new Intent(getBaseContext(), AboutActivity.class));
			return true;
		case R.id.action_license:
			// show license - Remember to pass "this" instead of getBaseContext() etc...
			DialogUtils.showWithMessageAndOkButton(this, R.string.action_license, R.string.str_license,
					android.R.string.ok);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * This method will display a random contact entry
	 */
	private void showRandomContact() {
		// generate a random number less than contactsList.size();
		ContactVo contactVo = mContactManager.getRandomContact();
		if (null != contactVo) {
			displayContact(contactVo);
		} else {
			Toast.makeText(this, getResources().getString(R.string.str_empty_contact_list), Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
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
			String lastContacted = contactVo.getLastContactedTime();
			if (null != lastContacted && lastContacted.trim().length() > 0) {
				mLastContactedTime.setText(lastContacted);
				mLastContactedLayout.setVisibility(View.VISIBLE);
				mLastContactedLayout.startAnimation(mFadeInAnimation);
			} else {
				mLastContactedLayout.setVisibility(View.GONE);
			}

			// User has the right for a image. if they do not, one will be provided.
			Bitmap bitmap = mContactManager.getContactPhoto(contactVo.getId());
			if (null == bitmap) {
				bitmap = mDefaultUserImage;
			}
			mUserImage.setImageBitmap(bitmap);

			// Update the List Adapter with the phonenumbers
			// mListAdapter.setArrayList(contactVo.getPhoneNumbersList());
			// mListAdapter.notifyDataSetChanged();
			
			// Change the data for the RecyclerView
			mRecyclerAdapter.setList(contactVo.getPhoneNumbersList());

			// Do Animations
			mUserName.startAnimation(mSlideInAnimation);
			mListContainer.startAnimation(mFadeInAnimation);

			// Track the previous and Current Contct objects
			// Future implementation?
			mPreviousContact = mCurrentContact;
			mCurrentContact = contactVo;
			if (null != mPreviousContact) {
				// Enable the prev button
				// Button previousContact = (Button)
				// findViewById(R.id.btnPreviousContact);
				// previousContact.setEnabled(true);
			}
		}
	}
}
