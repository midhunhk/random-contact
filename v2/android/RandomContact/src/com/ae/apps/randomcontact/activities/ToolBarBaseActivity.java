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

package com.ae.apps.randomcontact.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

import com.ae.apps.randomcontact.R;

/**
 * Base activity to support toolbar in activity layouts
 * 
 * @author user
 *
 */
public abstract class ToolBarBaseActivity extends ActionBarActivity {

	Toolbar mToolbar = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(getLayoutResId());

		// Find the toolbar and set it as action bar
		mToolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(mToolbar);

		mToolbar.inflateMenu(R.menu.main);
	}
	
	/**
	 * Returns the toolbar instance
	 * @return
	 */
	protected Toolbar getToolBar(){
		return mToolbar;
	}

	/**
	 * Returns the layout resource id
	 * 
	 * @return
	 */
	protected abstract int getLayoutResId();

}
