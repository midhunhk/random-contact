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

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.graphics.Palette;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.ae.apps.common.activities.ToolBarBaseActivity;
import com.ae.apps.common.managers.contact.AeContactManager;
import com.ae.apps.randomcontact.adapters.NavDrawerListAdapter;
import com.ae.apps.randomcontact.data.ContactManagerProvider;
import com.ae.apps.randomcontact.managers.NavigationFragmentManager;
import com.ae.apps.randomcontact.managers.RandomContactManager;

public class MainActivity extends ToolBarBaseActivity implements OnItemClickListener, ContactManagerProvider {

    private static final String PREF_KEY_NAV_DRAWER_INTRO_GIVEN = "pref_key_nav_drawer_intro";

    private DrawerLayout mDrawerLayout;
    private AeContactManager mContactManager;
    private NavigationFragmentManager mNavFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create a Contact Manager instance. We will use RandomContactManager since we need a random contact
        mContactManager = RandomContactManager.getInstance(getContentResolver(), getResources());
        mNavFragmentManager = new NavigationFragmentManager();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ListView mDrawerList = (ListView) findViewById(R.id.left_drawer_list);


        // Create the list for the main fragments to be shown in the drawer
        NavDrawerListAdapter drawerListAdapter = new NavDrawerListAdapter(this, mNavFragmentManager.getNavTitles());

        if (null != mDrawerList) {
            mDrawerList.setAdapter(drawerListAdapter);
            mDrawerList.setOnItemClickListener(this);
        }

        displayHomeAsUp();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, getToolBar(), R.string.app_name,
                R.string.app_name);

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        if (null == savedInstanceState) {
            // Show the default fragment
            showFragment(mNavFragmentManager.getFragmentInstance(0));
        }

        showNavDrawerIntro();
    }

    private void showFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame, fragment)
                .commit();
    }

    @SuppressLint({"InlinedApi", "NewApi"})
    public void applyThemeFromImage(Palette palette) {
        int toolbarColor = palette.getVibrantColor(
                getResources().getColor(R.color.colorAccent));

        Drawable colorDrawable = new ColorDrawable(toolbarColor);
        getSupportActionBar().setBackgroundDrawable(colorDrawable);

        // Theme the status bar on Lollipop and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int color = getResources().getColor(R.color.colorPrimary);
            int statusBarColor = palette.getDarkMutedColor(color);
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(statusBarColor);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long id) {
        showFragment(mNavFragmentManager.getFragmentInstance(pos));
        setToolbarTitle(mNavFragmentManager.getItemTitle(pos));
        mDrawerLayout.closeDrawers();
    }

    @Override
    public AeContactManager getContactDataManager() {
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

    @SuppressLint("RtlHardcoded")
    private void showNavDrawerIntro() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean helloNavDrawer = sharedPreferences.getBoolean(PREF_KEY_NAV_DRAWER_INTRO_GIVEN, false);

        // Check and introduce the Navigation Drawer on first use to the user
        if (null != mDrawerLayout && !helloNavDrawer) {
            mDrawerLayout.openDrawer(Gravity.LEFT);
            sharedPreferences.edit()
                    .putBoolean(PREF_KEY_NAV_DRAWER_INTRO_GIVEN, true)
                    .apply();
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
            mDrawerLayout.closeDrawers();
            return;
        }
        super.onBackPressed();
    }

}
