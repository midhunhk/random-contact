package com.ae.apps.randomcontact.managers;

import java.util.List;

import com.ae.apps.common.vo.ContactVo;

/**
 * Interface for ContactManager implementations to return list of contacts after filtering
 *
 * @author MidhunHK
 */
public interface FilteredContactList {

    /**
     * Get the top frequenlty listed contacts list
     *
     * @param maxResults
     * @return
     */
    List<ContactVo> getTopFrequentlyContacted(int maxResults);
}
