package com.ae.apps.randomcontact

import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.espresso.Espresso.closeSoftKeyboard as closeKb

@RunWith(AndroidJUnit4::class)
@LargeTest
class ManageGroupsTest {

    @get:Rule
    var activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun navigateToManageGroups(){
        onView( withId(R.id.action_manage_group) )
            .perform(click())

        onView( withId(R.id.txtManageGroups))
            .check( matches(withText("Manage Groups")))

        // Launch the Add Group Dialog
        onView( withId(R.id.btnAddGroup) )
            .perform( click() )

        onView( withId(R.id.textView))
            .check( matches(withText("Create Group")))

        val stringToType = "Test Group"
        onView( withId(R.id.txtGroupName))
            .perform(typeText(stringToType), closeSoftKeyboard())

        onView( withId(R.id.btnSave))
            .perform(click())

        onView( withId(R.id.txtGroupName) )
            .check( matches(withText(stringToType)))
    }

}