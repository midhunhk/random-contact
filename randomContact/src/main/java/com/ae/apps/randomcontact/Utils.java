package com.ae.apps.randomcontact;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static android.net.Uri.withAppendedPath;

/**
 * Temporary utility methods that should be moved to libAeApps
 */
public class Utils {

    /**
     * Shows this contact in the Android's Contact Manager
     *
     * @param contactId contactId
     */
    public static void showContactInAddressBook(@NonNull Context context, @Nullable String contactId) {
        if (null != contactId) {
            Intent intent = new Intent(Intent.ACTION_VIEW);

            Uri uri = withAppendedPath(ContactsContract.Contacts.CONTENT_URI, contactId);
            intent.setData(uri);

            context.startActivity(intent);
        }
    }
}
