package com.ae.apps.randomcontact.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ae.apps.randomcontact.AppRequestPermission
import com.ae.apps.randomcontact.databinding.FragmentNoAccessBinding

/**
 * A simple [Fragment] subclass.
 */
class NoAccessFragment : Fragment() {

    companion object {
        fun newInstance(): NoAccessFragment {
            return NoAccessFragment()
        }
    }

    private lateinit var permissionsAwareContext: AppRequestPermission

    private var _binding: FragmentNoAccessBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNoAccessBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.btnRequestPermissions.setOnClickListener {
            permissionsAwareContext.invokeRequestPermissions()
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            permissionsAwareContext = activity as AppRequestPermission
        } catch (ex: ClassCastException) {
            throw IllegalAccessException("Parent ${activity.toString()} must implement AppRequestPermission interface")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}