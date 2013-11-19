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

package com.ae.apps.randomcontact.activities;

import com.ae.apps.randomcontact.R;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Bundle;

/**
 * The aboutActivity
 * 
 * @author user
 * 
 */
public class AboutActivity extends Activity {
	@TargetApi(11)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		// Hide the app icon from the actionbar
		if (android.os.Build.VERSION.SDK_INT > 11) {
			getActionBar().setDisplayShowHomeEnabled(false);
		}
	}
}
