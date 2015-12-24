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

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar.OnMenuItemClickListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.ae.apps.common.managers.ContactManager;
import com.ae.apps.common.utils.DialogUtils;
import com.ae.apps.randomcontact.activities.ToolBarBaseActivity;
import com.ae.apps.randomcontact.adapters.NavDrawerListAdapter;
import com.ae.apps.randomcontact.data.ContactManagerConsumer;
import com.ae.apps.randomcontact.data.ContactManagerProvider;
import com.ae.apps.randomcontact.fragments.AboutFragment;
import com.ae.apps.randomcontact.fragments.FrequentContactsFragment;
import com.ae.apps.randomcontact.fragments.RandomContactFragment;
import com.ae.apps.randomcontact.managers.RandomContactManager;

public class MainActivity extends ToolBarBaseActivity implements OnMenuItemClickListener, ContactManagerProvider,
		OnItemClickListener {

	private View					mToolbarExtend;
	private DrawerLayout			mDrawerLayout;
	private ActionBarDrawerToggle	mDrawerToggle;
	private ContactManager			mContactManager;
	private ContactManagerConsumer	mConsumer;
	private ListView				mDrawerList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Create a Contact Manager instance. Lets use RandomContactManager since we need a random contact
		mContactManager = new RandomContactManager(getContentResolver(), getResources());

		mToolbarExtend = findViewById(R.id.toolbarExtend);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		mDrawerList = (ListView) findViewById(R.id.left_drawer_list);

		List<String> navItems = new ArrayList<String>();
		navItems.add("Random Contact");
		navItems.add("Frequent Contact");
		navItems.add("About");

		// Create the list for the main fragments to be shown in the drawer
		NavDrawerListAdapter drawerListAdapter = new NavDrawerListAdapter(this, navItems);

		mDrawerList.setAdapter(drawerListAdapter);
		mDrawerList.setOnItemClickListener(this);

		// displayHomeAsUp();

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, getToolBar(), R.string.app_name,
				R.string.app_name);

		mDrawerLayout.setDrawerListener(mDrawerToggle);
		mDrawerToggle.syncState();

		/*
		 * mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
		 * 
		 * @Override public boolean onNavigationItemSelected(MenuItem item) {
		 * 
		 * // Toggle the checked state item.setChecked(!item.isChecked());
		 * 
		 * switch (item.getItemId()) { case R.id.navigation_item_1: showFragment(new Fragment()); return true; }
		 * 
		 * return false; }
		 * 
		 * });
		 */

		// Inflate and handle menu clicks
		getToolBar().inflateMenu(R.menu.main);
		getToolBar().setOnMenuItemClickListener(this);

		if (null == savedInstanceState) {
			// Message Counter is the default fragment
			showFragment(new RandomContactFragment());
		}
	}

	private void showFragment(Fragment fragment) {
		getSupportFragmentManager().beginTransaction().replace(R.id.frame, fragment).commit();

	}

	@Override
	protected int getLayoutResId() {
		return R.layout.activity_main;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_refresh:
			// Show another Random Contact
			if (null != mConsumer) {
				mConsumer.showRandomContact();
			}
			return true;
		/*case R.id.action_about:
			// show about screen
			startActivity(new Intent(getBaseContext(), AboutFragment.class));
			return true;
		case R.id.action_license:
			// show license - Remember to pass "this" instead of getBaseContext() etc...
			DialogUtils.showWithMessageAndOkButton(this, R.string.action_license, R.string.str_license,
					android.R.string.ok);
			return true;*/
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void applyThemeFromImage(Palette palette) {
		int darkVibrantColor = palette.getDarkVibrantColor(android.support.v7.appcompat.R.color.material_blue_grey_950);

		Drawable colorDrawable = new ColorDrawable(darkVibrantColor);
		getSupportActionBar().setBackgroundDrawable(colorDrawable);
		mToolbarExtend.setBackgroundColor(darkVibrantColor);
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_refresh:
			// Show another Random Contact
			if (null != mConsumer) {
				mConsumer.showRandomContact();
			}
			return true;
/*
		case R.id.action_about:
			// show about screen
			// startActivity(new Intent(getBaseContext(), AboutFragment.class));
			showFragment(new AboutFragment());
			return true;

		case R.id.action_license:
			// show license - Remember to pass "this" instead of getBaseContext() etc...
			DialogUtils.showWithMessageAndOkButton(this, R.string.action_license, R.string.str_license,
					android.R.string.ok);
			return true;
*/
		case R.id.action_view_contact:
			if (null != mConsumer) {
				mContactManager.showContactInAddressBook(this, mConsumer.getCurrentContact());
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public ContactManager getContactDataManager() {
		return mContactManager;
	}

	@Override
	public void registerConsumer(ContactManagerConsumer consumer) {
		mConsumer = consumer;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long id) {
		// TODO Auto-generated method stub
		switch(pos){
		case 0:
			showFragment(new RandomContactFragment());
			break;
		case 1:
			showFragment(new FrequentContactsFragment());
			break;
		case 2:
			showFragment(new AboutFragment());
			break;
		}
		mDrawerLayout.closeDrawers();
	}

}
