package com.ae.apps.randomcontact.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ae.apps.randomcontact.AppRequestPermission
import com.ae.apps.randomcontact.databinding.FragmentNoAccessBinding

/**
 * NoAccessFragment
 * This fragment will display the screen explaining the need to request permissions for accessing the system contacts
 */
class NoAccessFragment : Fragment() {

    companion object {
        fun newInstance(): NoAccessFragment {
            return NoAccessFragment()
        }
    }

    private lateinit var permissionsAwareContext: AppRequestPermission
    private lateinit var binding: FragmentNoAccessBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // This is called when the Fragment is created and before UI elements are created
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNoAccessBinding.inflate(inflater, container, false)
        binding.btnRequestPermissions.setOnClickListener {
            permissionsAwareContext.invokeRequestPermissions()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            permissionsAwareContext = activity as AppRequestPermission
        } catch (ex: ClassCastException) {
            throw IllegalAccessException("Parent ${activity.toString()} must implement AppRequestPermission interface")
        }
    }

}