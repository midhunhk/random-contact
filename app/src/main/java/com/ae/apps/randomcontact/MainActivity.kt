package com.ae.apps.randomcontact

import android.Manifest
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.ae.apps.lib.permissions.AbstractPermissionsAwareActivity
import com.ae.apps.lib.permissions.PermissionsAwareComponent
import com.ae.apps.randomcontact.fragments.AboutFragment
import com.ae.apps.randomcontact.fragments.ManageGroupsFragment
import com.ae.apps.randomcontact.fragments.NoAccessFragment
import com.ae.apps.randomcontact.fragments.RandomContactFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * Entry point to the application
 */
class MainActivity : AbstractPermissionsAwareActivity(), PermissionsAwareComponent,
    AppRequestPermission {

    companion object {
        private val PERMISSIONS: Array<String> = arrayOf(Manifest.permission.READ_CONTACTS)
    }

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupBottomNavigation()
        bottomNavigationView.visibility = View.GONE

        // Check for permissions first and display the appropriate screen
        checkPermissions()
    }

    private fun setupBottomNavigation() {
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_manage_group -> showFragment(ManageGroupsFragment.newInstance())
                R.id.action_random_contact -> showFragment(RandomContactFragment.getInstance(this))
                R.id.action_about -> showFragment(AboutFragment.newInstance())
            }
            true
        }
    }

    override fun requiredPermissions() = PERMISSIONS

    override fun onPermissionsGranted() {
        // Show bottom navigation bar and load the Random contact fragment
        bottomNavigationView.visibility = View.VISIBLE
        bottomNavigationView.selectedItemId = R.id.action_random_contact

        showFragment(RandomContactFragment.getInstance(baseContext))
    }

    override fun showPermissionsRequiredView() {
        showFragment(NoAccessFragment.newInstance())
    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commitAllowingStateLoss()
    }

}

interface AppRequestPermission {
    fun invokeRequestPermissions()
}