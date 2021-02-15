package com.ae.apps.randomcontact.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ae.apps.randomcontact.R


/**
 * A simple [Fragment] subclass.
 */
class RandomContactFragment : Fragment() {

    companion object {
        fun newInstance(): RandomContactFragment {
            return RandomContactFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_random_contact, container, false)
    }

}