package com.ae.apps.randomcontact.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.ae.apps.randomcontact.utils.DEFAULT_CONTACT_GROUP

class AppPreferences private constructor(context: Context){

    private val PREF_KEY_SELECTED_CONTACT_GROUP = "pref_key_selected_contact_group"

    companion object{

        @Volatile private var instance:AppPreferences? = null
        @Volatile private lateinit var preferences:SharedPreferences

        fun getInstance(context: Context):AppPreferences =
            instance?: synchronized(this){
                preferences = PreferenceManager.getDefaultSharedPreferences(context)
                instance ?: AppPreferences(context).also { instance = it }
            }

    }

    /**
     * Returns the selected contact group
     *
     * @return selected contact group id
     */
    fun selectedContactGroup(): String? {
        return preferences.getString(
            PREF_KEY_SELECTED_CONTACT_GROUP,
            DEFAULT_CONTACT_GROUP
        )
    }

    /**
     * Sets the selected contact group
     *
     * @param context context
     * @param groupId groupId
     */
    fun setSelectedContactGroup(context: Context?, groupId: String?) {
        preferences
            .edit()
            .putString(PREF_KEY_SELECTED_CONTACT_GROUP, groupId)
            .apply()
    }

}