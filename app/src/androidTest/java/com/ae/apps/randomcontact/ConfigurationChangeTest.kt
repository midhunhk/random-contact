package com.ae.apps.randomcontact

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.ae.apps.randomcontact.fragments.AboutFragment
import com.ae.apps.randomcontact.fragments.AddContactGroupDialogFragment
import com.ae.apps.randomcontact.fragments.ManageGroupsFragment
import com.ae.apps.randomcontact.fragments.RandomContactFragment
import com.ae.apps.randomcontact.room.AppDatabase
import com.ae.apps.randomcontact.room.entities.ContactGroup
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * The activity being recreated (rotation, dark mode switch, process death) must neither crash
 * nor throw the user out of the screen they were on.
 *
 * Before the fix, recreating with the Add/Edit group dialog open crashed with
 * Fragment$InstantiationException, and every recreation switched back to the Random Contact tab.
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class ConfigurationChangeTest {

    private companion object {
        const val DIALOG_TAG = "addContactGroupDialog"
    }

    private var scenario: ActivityScenario<MainActivity>? = null

    @After
    fun tearDown() {
        scenario?.close()
        AppDatabase.getInstance(TestSetup.context).clearAllTables()
    }

    @Test
    fun freshStart_showsRandomContactTab() {
        val scenario = launch()

        assertEquals(RandomContactFragment::class.java, currentFragment(scenario).javaClass)
    }

    @Test
    fun recreate_onManageGroups_staysOnManageGroups() {
        val scenario = launch()
        onView(withId(R.id.action_manage_group)).perform(click())

        scenario.recreate()

        onView(withId(R.id.txtManageGroups)).check(matches(isDisplayed()))
        assertEquals(ManageGroupsFragment::class.java, currentFragment(scenario).javaClass)
    }

    @Test
    fun recreate_onAbout_staysOnAbout() {
        val scenario = launch()
        onView(withId(R.id.action_about)).perform(click())

        scenario.recreate()

        assertEquals(AboutFragment::class.java, currentFragment(scenario).javaClass)
    }

    @Test
    fun recreate_withAddGroupDialogOpen_keepsTheDialogAndTypedName() {
        val scenario = launch()
        onView(withId(R.id.action_manage_group)).perform(click())
        onView(withId(R.id.btnAddGroup)).perform(click())
        onView(withId(R.id.txtGroupName)).perform(typeText("Work"), closeSoftKeyboard())

        scenario.recreate()

        assertEquals(Lifecycle.State.RESUMED, scenario.state)
        onView(withId(R.id.textView)).check(matches(withText(R.string.str_contact_group_create_title)))
        onView(withId(R.id.txtGroupName)).check(matches(withText("Work")))
        scenario.onActivity { activity ->
            val dialog = groupDialog(activity)
            // Saving reports back to Manage Groups, which must still be reachable after recreation
            assertTrue(dialog.parentFragment is ManageGroupsFragment)
        }
    }

    @Test
    fun recreate_withEditGroupDialogOpen_keepsTheGroupBeingEdited() {
        val scenario = launchWithGroup(ContactGroup(name = "Friends", selectedContacts = "998,999"))
        onView(withId(R.id.action_manage_group)).perform(click())
        // The group list is loaded asynchronously from the database
        waitForView(R.id.btnEditContactGroup).perform(click())
        onView(withId(R.id.btnSave)).check(matches(withText(R.string.str_contact_group_update)))

        scenario.recreate()

        assertEquals(Lifecycle.State.RESUMED, scenario.state)
        onView(withId(R.id.txtGroupName)).check(matches(withText("Friends")))
        onView(withId(R.id.btnSave)).check(matches(withText(R.string.str_contact_group_update)))
        scenario.onActivity { activity ->
            val dialog = groupDialog(activity)
            assertEquals("998,999", dialog.selectedContactIdStr)
            assertTrue(dialog.parentFragment is ManageGroupsFragment)
        }
    }

    @Test
    fun recreate_withEditGroupDialogOpen_keepsTheNameTheUserChanged() {
        val scenario = launchWithGroup(ContactGroup(name = "Friends", selectedContacts = "999"))
        onView(withId(R.id.action_manage_group)).perform(click())
        // The group list is loaded asynchronously from the database
        waitForView(R.id.btnEditContactGroup).perform(click())
        onView(withId(R.id.txtGroupName)).perform(typeText(" old"), closeSoftKeyboard())

        scenario.recreate()

        // The edited text wins over the original group name
        onView(withId(R.id.txtGroupName)).check(matches(withText("Friends old")))
    }

    private fun launch() = TestSetup.launchMainActivity().also { scenario = it }

    /** Launches with [group] saved; prepareApp() clears the database, so insert after it */
    private fun launchWithGroup(group: ContactGroup): ActivityScenario<MainActivity> {
        TestSetup.prepareApp()
        runBlocking { AppDatabase.getInstance(TestSetup.context).contactGroupDao().insert(group) }
        return ActivityScenario.launch(MainActivity::class.java).also { scenario = it }
    }

    private fun waitForView(viewId: Int, timeoutMillis: Long = 5000): ViewInteraction {
        val deadline = System.currentTimeMillis() + timeoutMillis
        while (true) {
            try {
                return onView(withId(viewId)).check(matches(isDisplayed()))
            } catch (e: Throwable) {
                if (System.currentTimeMillis() > deadline) throw e
                Thread.sleep(100)
            }
        }
    }

    private fun currentFragment(scenario: ActivityScenario<MainActivity>): Fragment {
        var fragment: Fragment? = null
        scenario.onActivity {
            fragment = it.supportFragmentManager.findFragmentById(R.id.fragmentContainer)
        }
        return checkNotNull(fragment) { "No fragment shown" }
    }

    private fun groupDialog(activity: MainActivity): AddContactGroupDialogFragment {
        val manageGroups = activity.supportFragmentManager.findFragmentById(R.id.fragmentContainer)
        return checkNotNull(
            manageGroups?.childFragmentManager?.findFragmentByTag(DIALOG_TAG) as? AddContactGroupDialogFragment
        ) { "Add/Edit group dialog is not shown" }
    }
}
