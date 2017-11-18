/*
 * Copyright 2017 Midhun Harikumar
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
        customizeActivity();
    }

    private void customizeActivity() {
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
    protected int getLayoutResourceId() {
        return R.layout.activity_multi_contact_picker;
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
