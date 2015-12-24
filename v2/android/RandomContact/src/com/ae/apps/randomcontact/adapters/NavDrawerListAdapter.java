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

package com.ae.apps.randomcontact.adapters;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Adapter for the Navigation Drawer. Handles the display of the Navigation drawer
 * 
 * @author MidhunHK
 *
 */
public class NavDrawerListAdapter extends BaseAdapter {

	private Context			mContext;
	private List<String>	navDrawerItems;

	public NavDrawerListAdapter(Context context, List<String> navItems) {
		mContext = context;
		navDrawerItems = navItems;
	}

	@Override
	public int getCount() {
		return navDrawerItems.size();
	}

	@Override
	public Object getItem(int pos) {
		return navDrawerItems.get(pos);
	}

	@Override
	public long getItemId(int pos) {
		return pos;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int pos, View convertView, ViewGroup parent) {
		if (null == convertView) {
			// Create the view first time
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(android.R.layout.simple_list_item_1, null);
		}

		TextView textView = (TextView) convertView.findViewById(android.R.id.text1);
		textView.setText(getItem(pos).toString());

		return convertView;
	}

}
