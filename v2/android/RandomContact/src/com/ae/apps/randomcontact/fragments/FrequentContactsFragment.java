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

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ae.apps.common.vo.ContactVo;
import com.ae.apps.randomcontact.R;
import com.ae.apps.randomcontact.adapters.FrequentContactAdapter;
import com.ae.apps.randomcontact.data.ContactManagerProvider;

/**
 * 
 * @author MidhunHk
 *
 */
public class FrequentContactsFragment extends Fragment {

	private ContactManagerProvider	mContactManagerProvider	= null;
	private Context					mContext;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		mContactManagerProvider = (ContactManagerProvider) getActivity();

		View layout = inflater.inflate(R.layout.fragment_frequent_contacts, container, false);

		mContext = getActivity().getBaseContext();

		List<ContactVo> contacts = new ArrayList<ContactVo>();
		// Adding dummy list for testing
		contacts.add(mContactManagerProvider.getContactDataManager().getRandomContact());
		contacts.add(mContactManagerProvider.getContactDataManager().getRandomContact());
		contacts.add(mContactManagerProvider.getContactDataManager().getRandomContact());
		contacts.add(mContactManagerProvider.getContactDataManager().getRandomContact());
		contacts.add(mContactManagerProvider.getContactDataManager().getRandomContact());
		contacts.add(mContactManagerProvider.getContactDataManager().getRandomContact());
		contacts.add(mContactManagerProvider.getContactDataManager().getRandomContact());
		contacts.add(mContactManagerProvider.getContactDataManager().getRandomContact());
		contacts.add(mContactManagerProvider.getContactDataManager().getRandomContact());

		FrequentContactAdapter frequentContactAdapter = new FrequentContactAdapter(contacts,
				R.layout.contact_row, mContext);

		RecyclerView recyclerView = (RecyclerView) layout.findViewById(android.R.id.list);
		recyclerView.setHasFixedSize(true);
		recyclerView.setAdapter(frequentContactAdapter);
		recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
		recyclerView.setItemAnimator(new DefaultItemAnimator());

		return layout;
	}

}
