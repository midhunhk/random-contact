package com.ae.apps.randomcontact.data;

import com.ae.apps.common.vo.ContactVo;

public interface ContactManagerConsumer {

	/**
	 * Show a random contact
	 */
	void showRandomContact();

	/**
	 * Gets current contact
	 *
	 * @return
	 */
	ContactVo getCurrentContact();
}
