package com.ae.apps.randomcontact.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.ae.apps.lib.common.utils.DialogUtils
import com.ae.apps.randomcontact.R


/**
 * A simple [Fragment] subclass.
 */
class AboutFragment : Fragment(R.layout.fragment_about) {

    companion object {
        fun newInstance() = AboutFragment()
    }

    private var viewSource: View? = null
    private var viewLicense: View? = null
    private var viewPrivacyPolicy: View? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews(view)
    }

    private fun setupViews(rootView: View) {
        viewSource = rootView.findViewById(R.id.viewSourceCode)
        viewLicense = rootView.findViewById(R.id.viewLicense)
        viewPrivacyPolicy = rootView.findViewById(R.id.viewPrivacyPolicy)

        viewSource?.setOnClickListener {
            // TODO use from lib-aeapps
            // CommonUtils.launchWebPage()
            val url = getString(R.string.github_source_url)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }

        viewLicense?.setOnClickListener {
            DialogUtils.showMaterialInfoDialog(
                context, R.string.action_license, R.string.str_license,
                android.R.string.ok
            )
        }

        viewPrivacyPolicy?.setOnClickListener {
            val url = getString(R.string.privacy_policy_url)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }
    }

}