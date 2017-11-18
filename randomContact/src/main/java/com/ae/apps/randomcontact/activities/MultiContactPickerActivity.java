package com.ae.apps.randomcontact.activities;

import android.os.Build;
import android.os.Bundle;

import com.ae.apps.common.activities.multicontact.MultiContactBaseActivity;
import com.ae.apps.common.vo.ContactVo;
import com.ae.apps.randomcontact.R;
import com.ae.apps.randomcontact.managers.RandomContactManager;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Implements the MultiContactPicker with app specific customizations
 */
public class MultiContactPickerActivity extends MultiContactBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setToolbarTitle(getString(R.string.str_multi_contact_title));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mCancelButton.setBackgroundTintList(getResources().getColorStateList(R.color.cancel_button_tint));
            mContinueButton.setBackgroundTintList(getResources().getColorStateList(R.color.continue_button_tint));
        } else {
            mCancelButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            mContinueButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        }
    }

    @Override
    protected int getToolbarResourceId() {
        return R.id.toolbar;
    }

    @Override
    public List<ContactVo> contactsList() {
        List<ContactVo> list = RandomContactManager.getInstance(getBaseContext()).getAllContacts();
        // Sort the contacts based on name
        Collections.sort(list, new Comparator<ContactVo>() {
            @Override
            public int compare(ContactVo o1, ContactVo o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });
        return list;
    }
}
