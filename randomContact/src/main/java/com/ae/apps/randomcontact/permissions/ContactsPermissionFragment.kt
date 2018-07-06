package com.ae.apps.randomcontact.permissions

import android.Manifest
import android.view.View
import android.widget.Button
import android.widget.TextView

import com.ae.apps.randomcontact.R
import com.ae.apps.randomcontact.utils.AppConstants

/**
 * An abstract implementation of PermissionCheckingFragment for Read Contact permission
 */
abstract class ContactsPermissionFragment : PermissionCheckingFragment() {
    override fun getRequestCode(): Int {
        return AppConstants.PERMISSIONS_REQUEST_READ_CONTACTS
    }

    override fun getRequiredPermissions(): Array<String> {
        return arrayOf(Manifest.permission.READ_CONTACTS)
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
