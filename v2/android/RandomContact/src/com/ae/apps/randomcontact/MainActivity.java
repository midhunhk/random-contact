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

package com.ae.apps.randomcontact;

import android.annotation.TargetApi;
import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ae.apps.common.managers.ContactManager;
import com.ae.apps.common.utils.DialogUtils;
import com.ae.apps.common.views.RoundedImageView;
import com.ae.apps.common.vo.ContactVo;
import com.ae.apps.randomcontact.activities.AboutActivity;
import com.ae.apps.randomcontact.adapters.ContactListAdapter;
import com.ae.apps.randomcontact.managers.RandomContactManager;

public class MainActivity extends ListActivity {

	private TextView			mUserName;
	private TextView			mUserContactedCount;
	private TextView			mLastContactedTime;
	private ContactManager		contactManager;
	private ContactListAdapter	mListAdapter;
	private ContactVo			mPreviousContact;
	private ContactVo			mCurrentContact;
	private LinearLayout		mLastContactedLayout;
	private RoundedImageView	mUserImage;

	@TargetApi(11)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Create Contact Manager insatnce from RandomContactManager
		contactManager = new RandomContactManager(getContentResolver(), getResources());
		mListAdapter = new ContactListAdapter(this);
		setListAdapter(mListAdapter);

		mUserName = (TextView) findViewById(R.id.userDisplayName);
		mUserImage = (RoundedImageView) findViewById(R.id.userProfileImage);
		mUserContactedCount = (TextView) findViewById(R.id.userContactedCount);
		mLastContactedTime = (TextView) findViewById(R.id.lastContactedTime);
		mLastContactedLayout = (LinearLayout) findViewById(R.id.lastContactedLayout);

		// Hide the last contacted time initially
		mLastContactedLayout.setVisibility(View.INVISIBLE);

		// Access the Actionbar only if its available
		if (android.os.Build.VERSION.SDK_INT > 11) {
			getActionBar().setDisplayShowHomeEnabled(false);
		}

		// Lets start with showing a random contact
		showRandomContact();
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
			// show license - Remember to pass "this" instead of getBaseContext90 etc...
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
		ContactVo contactVo = contactManager.getRandomContact();
		if (null != contactVo) {
			displayContact(contactVo);
		} else {
			Toast.makeText(this, getResources().getString(R.string.str_empty_contact_list), Toast.LENGTH_LONG).show();
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
			} else {
				mLastContactedLayout.setVisibility(View.INVISIBLE);
			}

			// Show the user image
			Bitmap bitmap = contactManager.getContactPhoto(contactVo.getId());
			if (null == bitmap) {
				bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_user_default);
			}
			mUserImage.setImageBitmap(bitmap);

			// Update the List Adapter with the phonenumbers
			ContactListAdapter.setArrayList(contactVo.getPhoneNumbersList());
			mListAdapter.notifyDataSetChanged();

			// Track the previous and Current Contct objects
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
