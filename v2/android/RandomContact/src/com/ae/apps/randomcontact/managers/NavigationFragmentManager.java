package com.ae.apps.randomcontact.managers;

import java.util.ArrayList;
import java.util.List;

import com.ae.apps.randomcontact.fragments.AboutFragment;
import com.ae.apps.randomcontact.fragments.FrequentContactsFragment;
import com.ae.apps.randomcontact.fragments.RandomContactFragment;

import android.support.v4.app.Fragment;

/**
 * Manages the Fragments that are used in this app
 * 
 * @author Midhun
 *
 */
public class NavigationFragmentManager {

	private List<Fragment>	mFragments;
	
	private List<String> mItemTitles;

	/**
	 * Create and hold on to all fragments that we need
	 */
	public NavigationFragmentManager() {
		mFragments = new ArrayList<Fragment>();
		mFragments.add(new RandomContactFragment());
		mFragments.add(new FrequentContactsFragment());
		mFragments.add(new AboutFragment());
		
		// Create the titles, we should be reading them from resources
		mItemTitles = new ArrayList<String>();
		mItemTitles.add("Random Contact");
		mItemTitles.add("Frequent Contacts");
		mItemTitles.add("About");
	}
	
	/**
	 * Returns all the titles
	 * 
	 * @return
	 */
	public List<String> getNavTitles(){
		return mItemTitles;
	}
	
	/**
	 * Returns the title of the item at the position
	 * 
	 * @param position
	 * @return
	 */
	public String getItemTitle(int position){
		if(position >= 0 && position < mItemTitles.size()){
			return mItemTitles.get(position);
		}
		return null;
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
