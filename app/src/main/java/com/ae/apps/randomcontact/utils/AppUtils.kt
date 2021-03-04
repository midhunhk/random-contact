package com.ae.apps.randomcontact.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract
import android.widget.Toast
import com.ae.apps.lib.common.utils.ContactUtils.cleanupPhoneNumber

var DEFAULT_CONTACT_GROUP = "0"
var CONTACT_ID_SEPARATOR = ","
var PACKAGE_NAME_WHATSAPP = "com.whatsapp"

private const val DEFAULT_DATE_FORMAT = "MMM dd, yyyy hh:mm a"
private const val CONTENT_CONTACTS_DATA = "content://com.android.contacts/data/"

/**
 * Send a WhatsAppMessage to a contact number
 * _experimental feature_
 *
 * @param context context
 */
fun sendWhatsAppMessage(context: Context, contactNo: String) {
    sendWhatsAppMethod3(context, contactNo)
}

/**
 * This method opens the conversation screen for the contact
 * The number should have the Country code prefixed when it was saved,
 * else whatsapp will not open
 *
 * @param context context
 * @param contactNo contactNo
 */
private fun sendWhatsAppMethod3(context: Context, contactNo: String) {
    val id = cleanupPhoneNumber(contactNo) + "@s.whatsapp.net"
    var cursor: Cursor? = null
    try {
        cursor = context.contentResolver.query(
            ContactsContract.Data.CONTENT_URI, arrayOf(ContactsContract.Contacts.Data._ID),
            ContactsContract.Data.DATA1 + "=?", arrayOf(id), null
        )
        if (null != cursor && !cursor.moveToFirst()) {
            Toast.makeText(
                context,
                "WhatsApp contact with this number not found. Make sure it has country code.",
                Toast.LENGTH_LONG
            ).show()
            return
        }
        val sendIntent =
            Intent(Intent.ACTION_VIEW, Uri.parse(CONTENT_CONTACTS_DATA + (cursor?.getString(0))))
        val packageManager = context.packageManager
        if (null == sendIntent.resolveActivity(packageManager)) {
            Toast.makeText(context, "No Activity to handle this Intent", Toast.LENGTH_SHORT).show()
        } else {
            context.startActivity(sendIntent)
        }
    } catch (ex: ActivityNotFoundException) {
        Toast.makeText(context, ex.message, Toast.LENGTH_SHORT).show()
    } finally {
        cursor?.close()
    }
}
