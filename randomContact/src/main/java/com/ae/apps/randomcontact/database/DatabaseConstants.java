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
package com.ae.apps.randomcontact.database;

import android.provider.BaseColumns;

/**
 * Defines Database Constants
 */

interface DatabaseConstants {

    // Database Config
    int DATABASE_VERSION = 1;
    String DATABASE_NAME = "db_random_contact";

    String TEXT = " TEXT";
    String NUMERIC = " NUMERIC";

    // Custom Contact Group Table
    String CONTACT_GROUP_TABLE = "custom_contact_group";
    String CONTACT_GROUP_ID = BaseColumns._ID;
    String CONTACT_GROUP_NAME = "contact_group_name";
    String CONTACT_GROUP_CONTACTS = "contact_ids";

    String CONTACT_GROUP_SQL = "CREATE TABLE " + CONTACT_GROUP_TABLE + " (" +
            CONTACT_GROUP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            CONTACT_GROUP_NAME + TEXT + "," +
            CONTACT_GROUP_CONTACTS + TEXT +
            ")";

    String[] CONTACT_GROUP_COLUMNS = {
            CONTACT_GROUP_ID,
            CONTACT_GROUP_NAME,
            CONTACT_GROUP_CONTACTS
    };

    String[] CREATE_TABLES_SQL = {CONTACT_GROUP_SQL};

}
