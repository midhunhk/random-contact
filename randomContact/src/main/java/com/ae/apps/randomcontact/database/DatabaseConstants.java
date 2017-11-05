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
    String CONTACT_GROUP_CONTACTS = "contact_ids";

    String CONTACT_GROUP_SQL = "CREATE TABLE " + CONTACT_GROUP_TABLE + " (" +
            CONTACT_GROUP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            CONTACT_GROUP_CONTACTS + TEXT + "," +
            ")";

    String[] CONTACT_GROUP_COLUMNS = {
            CONTACT_GROUP_ID,
            CONTACT_GROUP_CONTACTS
    };

    String[] CREATE_TABLES_SQL = {CONTACT_GROUP_SQL};

}
