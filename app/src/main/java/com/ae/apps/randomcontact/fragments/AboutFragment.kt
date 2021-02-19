package com.ae.apps.randomcontact.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ae.apps.lib.common.utils.DialogUtils
import com.ae.apps.randomcontact.R


/**
 * A simple [Fragment] subclass.
 */
class AboutFragment : Fragment() {

    companion object{
        fun newInstance() = AboutFragment()
    }

    private var viewSource:View? = null
    private var viewLicense:View? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_about, container, false)
        setupViews(rootView)
        return rootView
    }

    private fun setupViews(rootView: View) {
        viewSource = rootView.findViewById(R.id.viewSourceCode)
        viewLicense = rootView.findViewById(R.id.viewLicense)

        viewSource?.setOnClickListener {
            val url = getString(R.string.github_source_url)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }

        viewLicense?.setOnClickListener {
            DialogUtils.showMaterialInfoDialog(context, R.string.action_license, R.string.str_license,
                android.R.string.ok)
        }
    }

}