package com.ae.apps.randomcontact

import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.ae.apps.randomcontact.fragments.RandomContactFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Loading a random contact finishes asynchronously. If the user has switched to another tab by
 * then, RandomContactFragment is no longer attached and must ignore the result.
 *
 * Before the fix this crashed with IllegalStateException ("Fragment not attached to a context")
 * from resources/getString, or a NullPointerException from Toast.makeText(null, ...). A crash on
 * the main thread fails the test run.
 */
@RunWith(AndroidJUnit4::class)
class RandomContactFragmentLifecycleTest {

    private companion object {
        // Long enough for the background lookup to finish and post its result
        const val BACKGROUND_WORK_MILLIS = 1500L
    }

    private lateinit var scenario: ActivityScenario<MainActivity>

    @Before
    fun setUp() {
        scenario = TestSetup.launchMainActivity()
        // Make sure the initial load has finished so that only the calls below are in flight
        TestSetup.readyContactsGateway()
        waitForBackgroundWork()
    }

    @After
    fun tearDown() {
        scenario.close()
    }

    @Test
    fun contactsReadCallback_afterSwitchingTab_isIgnored() {
        scenario.onActivity { activity ->
            val fragment = randomContactFragment(activity)
            switchToAboutTab(activity)

            // The gateway calls back into a fragment that is no longer shown
            fragment.onContactsRead()
        }

        waitForBackgroundWork()
        assertEquals(Lifecycle.State.RESUMED, scenario.state)
    }

    @Test
    fun refreshResult_arrivingAfterSwitchingTab_isIgnored() {
        scenario.onActivity { activity ->
            val fragment = randomContactFragment(activity)

            // Start loading a contact, then leave the tab before the result can be delivered:
            // the result is posted to the main thread, which is busy running this block
            fragment.requireView().findViewById<View>(R.id.btnRefresh).performClick()
            switchToAboutTab(activity)
        }

        waitForBackgroundWork()
        assertEquals(Lifecycle.State.RESUMED, scenario.state)
    }

    @Test
    fun refresh_whileOnTheTab_stillWorks() {
        scenario.onActivity { activity ->
            randomContactFragment(activity).requireView().findViewById<View>(R.id.btnRefresh).performClick()
        }

        waitForBackgroundWork()
        assertEquals(Lifecycle.State.RESUMED, scenario.state)
    }

    private fun randomContactFragment(activity: MainActivity) =
        activity.supportFragmentManager.findFragmentById(R.id.fragmentContainer) as RandomContactFragment

    private fun switchToAboutTab(activity: MainActivity) {
        activity.findViewById<BottomNavigationView>(R.id.bottom_navigation).selectedItemId = R.id.action_about
        activity.supportFragmentManager.executePendingTransactions()
    }

    private fun waitForBackgroundWork() {
        Thread.sleep(BACKGROUND_WORK_MILLIS)
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
    }
}
