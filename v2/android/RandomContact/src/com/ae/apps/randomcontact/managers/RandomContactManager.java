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

package com.ae.apps.randomcontact.managers;

import java.util.Random;

import android.content.ContentResolver;
import android.content.res.Resources;

import com.ae.apps.common.managers.ContactManager;
import com.ae.apps.common.vo.ContactVo;

/**
 * Adds the special bit of randomness to Contact Manager. Overrides the basic random contact generation of the
 * ContactManager
 * 
 * @author Midhunhk
 * 
 */
public class RandomContactManager extends ContactManager {

	private int	index	= 0;

	public RandomContactManager(ContentResolver contentResolver, Resources res) {
		super(contentResolver, res);
		// In order to avoid repetition of contacts, we simply start from a random point in the list of contacts
		// Hopefully the user has a very large number of contacts with phone numbers and may not notice :)
		index = new Random().nextInt(getTotalContactCount());
	}

	/**
	 * Override the default implementation
	 * @return a random contact object, null if no contacts found
	 */
	@Override
	public ContactVo getRandomContact() {
		if ( !contactsList.isEmpty() ) {
			// Increment the index - we will wrap around when we reach the end
			index = (index + 1) % contactsList.size();
			// Make sure the phone details are present
			ContactVo contactVo = getContactPhoneDetails(contactsList.get(index));
			// Update the contacts list with the updated contact item
			contactsList.set(index, contactVo);
			return contactVo;
		} else {
			return null;
		}
	}

}
