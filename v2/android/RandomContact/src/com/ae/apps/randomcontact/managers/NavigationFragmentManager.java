package com.ae.apps.randomcontact.managers;

import java.util.ArrayList;
import java.util.List;

import com.ae.apps.randomcontact.fragments.AboutFragment;
import com.ae.apps.randomcontact.fragments.FrequentContactsFragment;
import com.ae.apps.randomcontact.fragments.RandomContactFragment;

import android.support.v4.app.Fragment;

/**
 * Manages the Fragments that aree used in this app
 * 
 * @author Midhun
 *
 */
public class NavigationFragmentManager {

	private List<Fragment>	mFragments;

	/**
	 * Create and hold on to all fragments that we need
	 */
	public NavigationFragmentManager() {
		mFragments = new ArrayList<Fragment>();
		mFragments.add(new RandomContactFragment());
		mFragments.add(new FrequentContactsFragment());
		mFragments.add(new AboutFragment());
	}

	/**
	 * Returns the fragment at the position
	 * 
	 * @param position
	 * @return
	 */
	public Fragment getFragmentInstance(int position) {
		if (position >= 0 && position < mFragments.size()) {
			return mFragments.get(position);
		}
		return null;
	}
}
