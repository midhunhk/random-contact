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
package com.ae.apps.randomcontact.utils;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.telephony.PhoneNumberUtils;
import android.text.format.DateUtils;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static android.net.Uri.withAppendedPath;

/**
 * Temporary utility methods that should be moved to libAeApps
 */
public class Utils {

    private static final String DEFAULT_DATE_FORMAT = "MMM dd, yyyy hh:mm a";
    private static final String CONTENT_CONTACTS_DATA = "content://com.android.contacts/data/";

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

    public static String friendlyDateFormat(final Context context, final String formattedTimeString){
        String friendlyDateString = formattedTimeString;
        if (formattedTimeString != null && formattedTimeString.trim().length() > 0) {
            // Un format and convert to a date object
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT, Locale.getDefault());
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(dateFormat.parse(formattedTimeString).getTime());
                friendlyDateString = DateUtils.getRelativeDateTimeString(context,
                        dateFormat.getCalendar().getTimeInMillis(),
                        DateUtils.MINUTE_IN_MILLIS,
                        DateUtils.WEEK_IN_MILLIS,
                        0)
                        .toString();
            } catch(ParseException ex){
                // Silently catch the exception
            }
        }

        return friendlyDateString;
    }

    /**
     * Send a WhatsAppMessage to a contact number
     * _experimental feature_
     *
     * @param context context
     */
    public static void sendWhatsAppMessage(final Context context, final String contactNo){
        //sendWhatsAppMethod1(context, contactNo);
        //sendWhatsAppMethod2(context, contactNo);
        sendWhatsAppMethod3(context, contactNo);
    }

    public static void openWhatsAppContact(Context context, String contactId){
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("content://com.android.contacts/data/" + contactId));
        try{
            context.startActivity(i);
        } catch (ActivityNotFoundException ex){
            Toast.makeText(context, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * This method allows to send direct message to a contact provided, it is saved
     * with country code prefix
     * Ref: https://stackoverflow.com/a/40761890/592025
     *
     * @param context context
     * @param contactNo contactNo
     */
    private static void sendWhatsAppMethod2(Context context, String contactNo) {
        Intent sendIntent = new Intent("android.intent.action.MAIN");
        sendIntent.setComponent(new ComponentName(AppConstants.PACKAGE_NAME_WHATSAPP, "com.whatsapp.Conversation"));
        sendIntent.putExtra("jid", PhoneNumberUtils.stripSeparators(contactNo) + "@s.whatsapp.net");

        try {
            PackageManager packageManager = context.getPackageManager();
            if(null == sendIntent.resolveActivity(packageManager)){
                Toast.makeText(context, "No Activity to handle this Intent", Toast.LENGTH_SHORT).show();
            } else {
                context.startActivity(sendIntent);
            }
        } catch (ActivityNotFoundException ex){
            Toast.makeText(context, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * This method opens the conversation screen for the contact
     * The number should have the Country code prefixed when it was saved,
     * else whatsapp will not open
     *
     * @param context context
     * @param contactNo contactNo
     */
    private static void sendWhatsAppMethod3(Context context, String contactNo){
        String id = cleanupPhoneNumber(contactNo) + "@s.whatsapp.net";
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI,
                    new String[] { ContactsContract.Contacts.Data._ID },
                    ContactsContract.Data.DATA1 + "=?",
                    new String[] { id }, null);
            if(null != cursor && !cursor.moveToFirst()){
                Toast.makeText(context, "WhatsApp contact with this number not found. Make sure it has country code.",
                        Toast.LENGTH_LONG).show();
                return;
            }
            Intent sendIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(CONTENT_CONTACTS_DATA + cursor.getString(0)));
            PackageManager packageManager = context.getPackageManager();
            if(null == sendIntent.resolveActivity(packageManager)){
                Toast.makeText(context, "No Activity to handle this Intent", Toast.LENGTH_SHORT).show();
            } else {
                context.startActivity(sendIntent);
            }
        } catch (ActivityNotFoundException ex){
            Toast.makeText(context, ex.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            if(null != cursor){
                cursor.close();
            }
        }
    }

    /**
     * This method only opens the conversation screen and not the contact chat screen
     *
     * @param context context
     * @param contactNo contact no
     */
    private static void sendWhatsAppMethod1(Context context, String contactNo) {
        String uri = "smsto:" + cleanupPhoneNumber(contactNo);
        Intent sendIntent = new Intent(Intent.ACTION_SEND, Uri.parse(uri));
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Hello Friend.");
        sendIntent.putExtra("chat", true);
        sendIntent.setType("text/plain");
        sendIntent.setPackage(AppConstants.PACKAGE_NAME_WHATSAPP);
        try {
            PackageManager packageManager = context.getPackageManager();
            if(null == sendIntent.resolveActivity(packageManager)){
                Toast.makeText(context, "No Activity to handle this Intent", Toast.LENGTH_SHORT).show();
            } else {
                context.startActivity(Intent.createChooser(sendIntent, ""));
                //context.startActivity(sendIntent);
            }
        } catch (ActivityNotFoundException ex){
            Toast.makeText(context, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Checks if a package is installed
     *
     * @param packageName package name to check
     * @param packageManager package manager
     * @return true if package is installed, false otherwise
     */
    public static boolean isPackageInstalled(String packageName, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    @NonNull
    private static String cleanupPhoneNumber(String formattedPhoneNumber) {
        // Remove spaces, hyphens and + symbols from the phone number for comparison
        return formattedPhoneNumber.replaceAll("\\s+", "")
                .replaceAll("\\+", "")
                .replaceAll("-", "")
                .replaceAll("\\(", "")
                .replaceAll("\\)", "")
                .trim();
    }
}
