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

package com.ae.apps.randomcontact;

import java.util.ArrayList;
import java.util.List;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.graphics.Palette;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.ae.apps.common.activities.ToolBarBaseActivity;
import com.ae.apps.common.managers.ContactManager;
import com.ae.apps.randomcontact.adapters.NavDrawerListAdapter;
import com.ae.apps.randomcontact.data.ContactManagerProvider;
import com.ae.apps.randomcontact.managers.NavigationFragmentManager;
import com.ae.apps.randomcontact.managers.RandomContactManager;

public class MainActivity extends ToolBarBaseActivity implements OnItemClickListener, ContactManagerProvider {

	private DrawerLayout				mDrawerLayout;
	private ActionBarDrawerToggle		mDrawerToggle;
	private ContactManager				mContactManager;
	private ListView					mDrawerList;
	private NavigationFragmentManager	mNavFragmentManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Create a Contact Manager instance. Lets use RandomContactManager since we need a random contact
		mContactManager = new RandomContactManager(getContentResolver(), getResources());
		mNavFragmentManager = new NavigationFragmentManager();

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer_list);

		// NavDrawer Items - read from strings
		List<String> navItems = new ArrayList<String>();
		navItems.add("Random Contact");
		navItems.add("Frequent Contacts");
		navItems.add("About");

		// Create the list for the main fragments to be shown in the drawer
		NavDrawerListAdapter drawerListAdapter = new NavDrawerListAdapter(this, navItems);

		if (null != mDrawerList) {
			mDrawerList.setAdapter(drawerListAdapter);
			mDrawerList.setOnItemClickListener(this);
		}

		displayHomeAsUp();

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, getToolBar(), R.string.app_name,
				R.string.app_name);

		mDrawerLayout.setDrawerListener(mDrawerToggle);
		mDrawerToggle.syncState();

		// Inflate and handle menu clicks on MainToolbar
		// getToolBar().inflateMenu(R.menu.main);
		// getToolBar().setOnMenuItemClickListener(this);

		if (null == savedInstanceState) {
			// Message Counter is the default fragment
			showFragment(mNavFragmentManager.getFragmentInstance(0));
		}
	}

	private void showFragment(Fragment fragment) {
		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.frame, fragment)
				.commit();
	}

	public void applyThemeFromImage(Palette palette) {
		int toolbarColor = palette.getVibrantColor(android.support.v7.appcompat.R.color.material_blue_grey_950);

		Drawable colorDrawable = new ColorDrawable(toolbarColor);
		getSupportActionBar().setBackgroundDrawable(colorDrawable);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long id) {
		showFragment(mNavFragmentManager.getFragmentInstance(pos));
		mDrawerLayout.closeDrawers();
	}

	@Override
	public ContactManager getContactDataManager() {
		return mContactManager;
	}

	@Override
	protected int getToolbarResourceId() {
		return R.id.toolbar;
	}

	@Override
	protected int getLayoutResourceId() {
		return R.layout.activity_main;
	}

}
