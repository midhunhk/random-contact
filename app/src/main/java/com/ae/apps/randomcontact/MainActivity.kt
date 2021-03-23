package com.ae.apps.randomcontact

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.ae.apps.lib.permissions.AbstractPermissionsAwareActivity
import com.ae.apps.lib.permissions.PermissionsAwareComponent
import com.ae.apps.randomcontact.databinding.ActivityMainBinding
import com.ae.apps.randomcontact.fragments.AboutFragment
import com.ae.apps.randomcontact.fragments.ManageGroupsFragment
import com.ae.apps.randomcontact.fragments.NoAccessFragment
import com.ae.apps.randomcontact.fragments.RandomContactFragment
import com.ae.apps.randomcontact.preferences.AppPreferences
import com.ae.apps.randomcontact.utils.hide
import com.ae.apps.randomcontact.utils.show
import com.ae.apps.randomcontact.utils.showShortToast
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import eu.dkaratzas.android.inapp.update.Constants
import eu.dkaratzas.android.inapp.update.InAppUpdateManager
import eu.dkaratzas.android.inapp.update.InAppUpdateStatus

/**
 * Entry point to the application
 */
class MainActivity : AbstractPermissionsAwareActivity(), PermissionsAwareComponent,
    AppRequestPermission, InAppUpdateManager.InAppUpdateHandler {

    companion object {
        private val PERMISSIONS: Array<String> = arrayOf(Manifest.permission.READ_CONTACTS)
    }

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavigation()
        bottomNavigationView.hide()

        // Check for permissions first and display the appropriate screen
        checkPermissions()
    }

    private fun setupBottomNavigation() {
        bottomNavigationView = binding.bottomNavigation
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_manage_group -> showFragment(ManageGroupsFragment.getInstance())
                R.id.action_random_contact -> showFragment(RandomContactFragment.getInstance())
                R.id.action_about -> showFragment(AboutFragment.newInstance())
            }
            true
        }
    }

    override fun requiredPermissions() = PERMISSIONS

    override fun onPermissionsGranted() {
        // Show bottom navigation bar and load the Random contact fragment
        bottomNavigationView.show()
        bottomNavigationView.selectedItemId = R.id.action_random_contact

        showFragment(RandomContactFragment.getInstance())
        showMessageAsBottomSheet()

        initInAppUpdate()
    }

    @SuppressLint("InflateParams")
    private fun showMessageAsBottomSheet() {
        val appPreferences = AppPreferences.getInstance(this)
        if (!appPreferences.getBooleanPref(AppPreferences.PREF_KEY_VERSION_4_REDESIGN_MSG_SHOWN, false)) {
            val bottomSheetLayout = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)
            val messageCloseButton = bottomSheetLayout.findViewById<View>(R.id.btnMessageClose)
            messageCloseButton.setOnClickListener {
                bottomSheetDialog.dismiss()
                appPreferences.setBooleanPref(AppPreferences.PREF_KEY_VERSION_4_REDESIGN_MSG_SHOWN, true)
            }
            bottomSheetDialog = BottomSheetDialog(this)
            bottomSheetDialog.setContentView(bottomSheetLayout)
            bottomSheetDialog.show()
        }
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

    private fun initInAppUpdate() {
        val inAppUpdateManager = InAppUpdateManager.Builder(this)
            //.mode(Constants.UpdateMode.FLEXIBLE)
            .mode(Constants.UpdateMode.IMMEDIATE)
            .snackBarMessage("An update has just been downloaded.")
            .snackBarAction("RESTART")
            .handler(this)

        inAppUpdateManager.checkForAppUpdate()
    }

    override fun onInAppUpdateError(code: Int, error: Throwable?) {
        showShortToast(R.string.str_in_app_update_error)
    }

    override fun onInAppUpdateStatus(status: InAppUpdateStatus?) {

    }

}

interface AppRequestPermission {
    fun invokeRequestPermissions()
}