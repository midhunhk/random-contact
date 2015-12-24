package com.ae.apps.randomcontact.data;

import android.support.v7.graphics.Palette;

import com.ae.apps.common.managers.ContactManager;

/**
 * Interface to be implemented by the parent Activity that returns an instance of the ContactManager
 * 
 * @author Midhun Harikumar
 *
 */
public interface ContactManagerProvider {

	/**
	 * Returns a ContactManager instance
	 * 
	 * @return
	 */
	ContactManager getContactDataManager();

	/**
	 * Apply a theme to toolbar from given image
	 * 
	 * @param bitmap
	 */
	void applyThemeFromImage(Palette palette);

}
