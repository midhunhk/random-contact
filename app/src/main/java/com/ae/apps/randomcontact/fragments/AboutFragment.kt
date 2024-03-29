package com.ae.apps.randomcontact.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.ae.apps.lib.common.utils.CommonUtils
import com.ae.apps.lib.common.utils.DialogUtils
import com.ae.apps.randomcontact.R
import com.ae.apps.randomcontact.databinding.FragmentAboutBinding

/**
 * A About [Fragment] which displays meta information.
 */
class AboutFragment : Fragment(R.layout.fragment_about) {

    companion object {
        fun newInstance() = AboutFragment()
    }

    private lateinit var binding: FragmentAboutBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAboutBinding.bind(view)
        setupViews()
    }

    private fun setupViews() {
        binding.viewSourceCode.setOnClickListener {
            val url = getString(R.string.github_source_url)
            CommonUtils.launchWebPage(requireContext(), url)
        }

        binding.viewLicense.setOnClickListener {
            DialogUtils.showMaterialInfoDialog(
                context, R.string.action_license, R.string.str_license,
                android.R.string.ok
            )
        }

        binding.viewPrivacyPolicy.setOnClickListener {
            val url = getString(R.string.privacy_policy_url)
            CommonUtils.launchWebPage(requireContext(), url)
        }
    }

}