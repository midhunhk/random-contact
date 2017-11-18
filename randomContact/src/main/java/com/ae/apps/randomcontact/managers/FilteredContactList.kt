package com.ae.apps.randomcontact.managers

import com.ae.apps.common.vo.ContactVo

/**
 * Interface for ContactManager implementations to return list of contacts after filtering
 *
 * @author MidhunHK
 */
interface FilteredContactList {

    /**
     * Get the top frequenlty listed contacts list
     *
     * @param maxResults
     * @return
     */
    fun getTopFrequentlyContacted(maxResults: Int): List<ContactVo>
}
