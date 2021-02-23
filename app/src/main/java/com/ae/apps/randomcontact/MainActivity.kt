package com.ae.apps.randomcontact

import android.Manifest
import android.annotation.TargetApi
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.ae.apps.lib.permissions.PermissionsAwareComponent
import com.ae.apps.lib.permissions.RuntimePermissionChecker
import com.ae.apps.randomcontact.fragments.AboutFragment
import com.ae.apps.randomcontact.fragments.ManageGroupsFragment
import com.ae.apps.randomcontact.fragments.NoAccessFragment
import com.ae.apps.randomcontact.fragments.RandomContactFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * Entry point to the application
 */
class MainActivity : AppCompatActivity(), PermissionsAwareComponent, AppRequestPermission {

    companion object {
        private const val PERMISSION_CHECK_REQUEST_CODE = 8000
        private val PERMISSIONS: Array<String> = arrayOf(Manifest.permission.READ_CONTACTS)
    }

    private lateinit var permissionChecker: RuntimePermissionChecker
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Check for permissions first and display the appropriate screen
        permissionChecker = RuntimePermissionChecker(this)
        permissionChecker.checkPermissions()

        setupBottomNavigation()
        bottomNavigationView.visibility = View.GONE
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

    override fun invokeRequestPermissions() = requestPermissionsForAPI()

    override fun requestForPermissions() = showPermissionsRequiredView()

    override fun onPermissionsRequired() = showPermissionsRequiredView()

    override fun onPermissionsDenied() = showPermissionsRequiredView()

    override fun onPermissionsGranted() {
        // Show bottom navigation bar and load the Random contact fragment
        bottomNavigationView.visibility = View.VISIBLE
        bottomNavigationView.selectedItemId = R.id.action_random_contact

        showFragment(RandomContactFragment.getInstance(baseContext))
    }

    private fun showPermissionsRequiredView() {
        showFragment(NoAccessFragment.newInstance())
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun requestPermissionsForAPI() = requestPermissions(
        requiredPermissions(),
        PERMISSION_CHECK_REQUEST_CODE
    )

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_CHECK_REQUEST_CODE -> {
                permissionChecker.handlePermissionsResult(permissions, grantResults)
            }
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
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