package com.ae.apps.randomcontact

import android.Manifest
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import com.ae.apps.lib.api.contacts.ContactsApiGateway
import com.ae.apps.lib.api.contacts.types.ContactInfoFilterOptions
import com.ae.apps.randomcontact.contacts.RandomContactApiGatewayImpl
import com.ae.apps.randomcontact.contacts.RandomContactsApiGatewayFactory
import com.ae.apps.randomcontact.preferences.AppPreferences
import com.ae.apps.randomcontact.room.AppDatabase
import com.ae.apps.randomcontact.room.repositories.ContactGroupRepositoryImpl
import com.ae.apps.randomcontact.utils.DEFAULT_CONTACT_GROUP

/**
 * Shared setup for instrumented tests that drive [MainActivity].
 */
object TestSetup {

    private val instrumentation get() = InstrumentationRegistry.getInstrumentation()
    val context get() = instrumentation.targetContext

    /**
     * Grants the contacts permission, starts from an empty database with All Contacts selected,
     * and suppresses the one-time bottom sheet so that it does not cover the screen under test.
     */
    fun prepareApp() {
        wakeUpDevice()
        instrumentation.uiAutomation.grantRuntimePermission(
            context.packageName,
            Manifest.permission.READ_CONTACTS
        )
        AppDatabase.getInstance(context).clearAllTables()
        AppPreferences.getInstance(context).apply {
            setSelectedContactGroup(DEFAULT_CONTACT_GROUP)
            setBooleanPref(AppPreferences.PREF_KEY_VERSION_4_REDESIGN_MSG_SHOWN, true)
        }
    }

    /** Activities never reach RESUMED while the screen is off or locked */
    private fun wakeUpDevice() {
        listOf("input keyevent KEYCODE_WAKEUP", "wm dismiss-keyguard").forEach { command ->
            instrumentation.uiAutomation.executeShellCommand(command).close()
        }
    }

    fun launchMainActivity(): ActivityScenario<MainActivity> {
        prepareApp()
        return ActivityScenario.launch(MainActivity::class.java)
    }

    /** The gateway the app shares between screens, read and ready to use when this returns. */
    fun readyContactsGateway(): ContactsApiGateway {
        val gateway = RandomContactApiGatewayImpl.getInstance(
            context,
            ContactGroupRepositoryImpl.getInstance(AppDatabase.getInstance(context).contactGroupDao()),
            RandomContactsApiGatewayFactory(),
            AppPreferences.getInstance(context)
        )
        // Blocks until any read already in progress has finished
        gateway.initialize(ContactInfoFilterOptions.of(true))
        return gateway
    }
}
