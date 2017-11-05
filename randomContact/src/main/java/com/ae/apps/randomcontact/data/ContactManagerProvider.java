package com.ae.apps.randomcontact.data;

import android.support.v7.graphics.Palette;

import com.ae.apps.common.managers.ContactManager;
import com.ae.apps.common.managers.contact.AeContactManager;

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
	AeContactManager getContactDataManager();

	/**
	 * Apply a theme to toolbar from given image
	 * 
	 * @param palette palette
	 */
	void applyThemeFromImage(Palette palette);

}
