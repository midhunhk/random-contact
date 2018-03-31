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

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.format.DateFormat;
import android.text.format.DateUtils;

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
}
