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

package com.ae.apps.randomcontact.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ae.apps.common.utils.MobileNetworkUtils;
import com.ae.apps.common.vo.PhoneNumberVo;
import com.ae.apps.randomcontact.R;

/**
 * Custom Adatper for displaying contact information
 * 
 * @author user
 * 
 */
public class ContactListAdapter extends BaseAdapter {

	private Context						context;
	private LayoutInflater				inflater;
	private static List<PhoneNumberVo>	arrayList;

	public ContactListAdapter(Context context) {
		arrayList = new ArrayList<PhoneNumberVo>();
		this.context = context;
		this.inflater = LayoutInflater.from(context);
	}

	/**
	 * @return the arrayList
	 */
	public static List<PhoneNumberVo> getArrayList() {
		return arrayList;
	}

	/**
	 * @param arrayList
	 *            the arrayList to set
	 */
	public static void setArrayList(List<PhoneNumberVo> arrayList) {
		ContactListAdapter.arrayList = arrayList;
	}

	@Override
	public int getCount() {
		return arrayList.size();
	}

	@Override
	public Object getItem(int position) {
		return arrayList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			// Use the custom layout
			convertView = inflater.inflate(R.layout.contact_info_row, null);

			// Get references to the TextViews in the layout
			holder = new ViewHolder();
			holder.txtPhoneType = (TextView) convertView.findViewById(R.id.txtPhoneType);
			holder.txtContactNumber = (TextView) convertView.findViewById(R.id.txtContactNumber);
			holder.btnCall = (ImageButton) convertView.findViewById(R.id.btnCall);
			holder.btnText = (ImageButton) convertView.findViewById(R.id.btnText);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final PhoneNumberVo phoneNumberVo = arrayList.get(position);
		if (null != phoneNumberVo) {
			holder.txtContactNumber.setText(phoneNumberVo.getPhoneNumber());
			holder.txtPhoneType.setText(phoneNumberVo.getPhoneType());

			final String contactNo = phoneNumberVo.getPhoneNumber();
			holder.btnCall.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// call
					MobileNetworkUtils.callContact(context, contactNo);
				}
			});

			holder.btnText.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// text
					MobileNetworkUtils.textContact(context, contactNo);
				}
			});
		}

		return convertView;
	}

	static class ViewHolder {
		TextView	txtContactNumber;
		TextView	txtPhoneType;
		ImageButton	btnCall;
		ImageButton	btnText;
	}

}
