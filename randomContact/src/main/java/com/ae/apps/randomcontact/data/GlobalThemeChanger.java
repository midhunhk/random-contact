package com.ae.apps.randomcontact.data;

import android.support.v7.graphics.Palette;

import com.ae.apps.common.managers.ContactManager;
import com.ae.apps.common.managers.contact.AeContactManager;

/**
 * Interface to be implemented by the parent Activity that needs to be notified when to
 * change theme globally
 * 
 * @author midhunhk
 *
 */
public interface GlobalThemeChanger {

	/**
	 * Apply a theme to toolbar from given image
	 * 
	 * @param palette palette
	 */
	void applyThemeFromImage(Palette palette);

}
