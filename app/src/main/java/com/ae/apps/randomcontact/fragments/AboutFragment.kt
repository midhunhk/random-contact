package com.ae.apps.randomcontact.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.ae.apps.lib.common.utils.CommonUtils
import com.ae.apps.lib.common.utils.DialogUtils
import com.ae.apps.randomcontact.R
import com.ae.apps.randomcontact.databinding.FragmentAboutBinding


/**
 * A simple [Fragment] subclass.
 */
class AboutFragment : Fragment(R.layout.fragment_about) {

    companion object {
        fun newInstance() = AboutFragment()
    }

    private lateinit var binding: FragmentAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FragmentAboutBinding.inflate(layoutInflater)
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