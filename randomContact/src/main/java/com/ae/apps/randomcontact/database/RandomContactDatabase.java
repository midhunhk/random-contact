package com.ae.apps.randomcontact.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.ae.apps.common.db.DataBaseHelper;
import com.ae.apps.randomcontact.data.ContactGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the database for RandomContact
 */
public class RandomContactDatabase extends DataBaseHelper {

    public RandomContactDatabase(Context context) {
        super(context, DatabaseConstants.DATABASE_NAME, null,
                DatabaseConstants.DATABASE_VERSION,
                DatabaseConstants.CREATE_TABLES_SQL);
    }

    /**
     * Creates a Custom Contact Group
     *
     * @param contactGroup contact group to be added
     * @return insert status
     */
    public long createContactGroup(ContactGroup contactGroup) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseConstants.CONTACT_GROUP_CONTACTS, contactGroup.getSelectedContacts());
        return insert(DatabaseConstants.CONTACT_GROUP_TABLE, contentValues);
    }

    /**
     * Retrieve a ContactGroup by its id
     *
     * @param id id of the contact group
     * @return ContactGroup
     */
    public ContactGroup getContactGroupById(String id) {
        String[] args = {id};
        Cursor cursor = query(DatabaseConstants.CONTACT_GROUP_TABLE,
                DatabaseConstants.CONTACT_GROUP_COLUMNS,
                DatabaseConstants.CONTACT_GROUP_ID + "=?",
                args, null, null, null);
        if (null == cursor || cursor.getCount() == 0) {
            return null;
        }

        ContactGroup contactGroup;
        try {
            cursor.moveToFirst();
            contactGroup = mapContactGroupModel(cursor);
        } finally {
            cursor.close();
        }

        return contactGroup;
    }

    /**
     * Deletes a contact group
     *
     * @param contactGroup contactGroup to delete
     * @return delete status
     */
    public long deleteContactGroup(ContactGroup contactGroup) {
        String[] contactIdToDelete = {contactGroup.getId()};
        return delete(DatabaseConstants.CONTACT_GROUP_TABLE,
                DatabaseConstants.CONTACT_GROUP_ID + "=?",
                contactIdToDelete);
    }

    /**
     * Returns all custom contact groups
     *
     * @return list of contact groups
     */
    public List<ContactGroup> getAllContactGroups() {
        Cursor contactGroupsCursor = query(DatabaseConstants.DATABASE_NAME,
                DatabaseConstants.CONTACT_GROUP_COLUMNS,
                null, null, null, null, null);
        List<ContactGroup> contactGroups = new ArrayList<>();
        try {
            while (contactGroupsCursor.moveToNext()) {
                contactGroups.add(mapContactGroupModel(contactGroupsCursor));
            }
        } finally {
            contactGroupsCursor.close();
        }

        return contactGroups;
    }

    private ContactGroup mapContactGroupModel(Cursor cursor) {
        String id = cursor.getString(cursor.getColumnIndex(DatabaseConstants.CONTACT_GROUP_ID));
        String contacts = cursor.getString(cursor.getColumnIndex(DatabaseConstants.CONTACT_GROUP_CONTACTS));
        return new ContactGroup(id, contacts);
    }

}
