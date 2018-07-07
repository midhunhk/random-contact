/*
 * Copyright 2018 Midhun Harikumar
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
package com.ae.apps.randomcontact.permissions

import android.Manifest
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.ae.apps.randomcontact.R

/**
 * An abstract implementation of PermissionCheckingFragment for Read Contact permission
 */
abstract class ContactsPermissionFragment : PermissionCheckingFragment() {

    override fun getRequiredPermissions(): Array<String> {
        return arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.CALL_PHONE)
    }

    public override fun setupViewWithoutPermission(): View {
        val noAccessView = mInflater.inflate(R.layout.fragment_random_contact_placeholder, mContainer, false)
        val txtMessage = noAccessView.findViewById<TextView>(R.id.txt_random_contact_placeholder)
        txtMessage.setText(R.string.str_permission_required)
        val btnProvideAccess = noAccessView.findViewById<Button>(R.id.btnProvideAccess)
        btnProvideAccess.setOnClickListener { requestForPermissions() }
        return noAccessView
    }

}
