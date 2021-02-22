package com.ae.apps.randomcontact.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ae.apps.lib.api.contacts.ContactsApiGateway
import com.ae.apps.lib.common.models.ContactInfo
import com.ae.apps.lib.multicontact.MultiContactPickerConstants
import com.ae.apps.randomcontact.R
import com.ae.apps.randomcontact.activities.MultiContactPickerActivity
import com.ae.apps.randomcontact.adapters.GroupMemberRecyclerAdapter
import com.ae.apps.randomcontact.data.RandomContactApiGatewayImpl
import com.ae.apps.randomcontact.listeners.ContactGroupInteractionListener
import com.ae.apps.randomcontact.room.entities.ContactGroup
import com.ae.apps.randomcontact.room.repositories.ContactGroupRepository
import com.ae.apps.randomcontact.utils.CONTACT_ID_SEPARATOR

/**
 * A simple [Fragment] subclass.
 * Use the [AddContactGroupDialogFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddContactGroupDialogFragment(
    private val interactionListener: ContactGroupInteractionListener,
    private val contactGroupToUpdate: ContactGroup? = null
) :
    AppCompatDialogFragment() {

    private lateinit var btnClose: Button
    private lateinit var btnSave: Button
    private lateinit var btnSelect: Button
    private lateinit var txtGroupName: EditText
    private lateinit var selectedContactIdStr: String
    private lateinit var contactsApi: ContactsApiGateway
    private var selectedContactInfoList: MutableList<ContactInfo> = mutableListOf()
    private var viewAdapter: GroupMemberRecyclerAdapter? = null

    companion object {
        private const val MULTI_CONTACT_PICKER_RESULT = 2001

        @JvmStatic
        fun newInstance(
            interactionListener: ContactGroupInteractionListener,
            contactGroup: ContactGroup? = null
        ) = AddContactGroupDialogFragment(interactionListener, contactGroup)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_contact_group_dialog, container, false)
        contactsApi = RandomContactApiGatewayImpl.getInstance(requireContext())

        initViews(view)
        setupRecyclerView(view)

        return view
    }

    private fun initViews(view: View) {
        btnClose = view.findViewById(R.id.btnClose)
        btnSave = view.findViewById(R.id.btnSave)
        btnSelect = view.findViewById(R.id.btnSelectMembers)
        txtGroupName = view.findViewById(R.id.txtGroupName)

        contactGroupToUpdate?.let { it ->
            // If there is a contactGroupId to edit
            // TODO Update text on btnSave to "Update"

            // Populate the details on the UI
            txtGroupName.setText(it.name)
            populateContactInfo(it.selectedContacts)
        }
    }

    private fun populateContactInfo(tempSelectedContacts: String) {
        val selectedContactIds = tempSelectedContacts.split(CONTACT_ID_SEPARATOR)
        selectedContactIds.forEach {
            selectedContactInfoList.add(contactsApi.getContactInfo(it))
        }
    }

    private fun setupRecyclerView(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.list)
        viewAdapter = GroupMemberRecyclerAdapter(selectedContactInfoList)
        recyclerView.adapter = viewAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.itemAnimator = DefaultItemAnimator()
    }

    private fun startMultiContactPicker() {
        val multiContactPickerIntent = Intent(
            requireContext(),
            MultiContactPickerActivity::class.java
        )
        multiContactPickerIntent.putExtra(
            MultiContactPickerConstants.PRESELECTED_CONTACT_IDS,
            selectedContactIdStr
        )
        val startForResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    if (it.data != null) {
                        val selectedContactIds2 =
                            it.data!!.getStringExtra(MultiContactPickerConstants.RESULT_CONTACT_IDS)!!
                        Toast.makeText(requireContext(), selectedContactIds2, Toast.LENGTH_LONG)
                            .show()

                        // TODO Verify this
                        selectedContactInfoList.clear()
                        populateContactInfo(selectedContactIds2)
                    }
                }
            }
        startForResult.launch(multiContactPickerIntent)
    }

    override fun getTheme(): Int {
        return R.style.DialogTheme
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnSelect.setOnClickListener {
            startMultiContactPicker()
        }

        btnClose.setOnClickListener {
            dismiss()
        }

        btnSave.setOnClickListener {
            try {
                val contactGroup = validateContactGroup()
                if(contactGroupToUpdate != null){
                    // Update the validated contact group with the existing contactGroup id
                    // if this is the update flow
                    contactGroup.id = it.id
                    interactionListener.onContactGroupUpdated(contactGroupToUpdate, contactGroup)
                } else {
                    interactionListener.onContactGroupAdded(contactGroup)
                }

                dismiss()
            } catch (e: ContactGroupValidationException) {
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private fun validateContactGroup(): ContactGroup {
        if (TextUtils.isEmpty(txtGroupName.text.toString())) {
            throw ContactGroupValidationException("Please enter group name")
        }

        val builder = StringBuilder()
        for (contactInfo in selectedContactInfoList) {
            builder.append(contactInfo.id)
                .append(CONTACT_ID_SEPARATOR)
        }

        if (builder.isEmpty()) {
            throw ContactGroupValidationException("Please select contacts for this group")
        }
        builder.deleteCharAt(builder.lastIndexOf(CONTACT_ID_SEPARATOR))

        return ContactGroup(
            name = txtGroupName.text.toString(),
            selectedContacts = builder.toString()
        )
    }

    /**
     * Internal Exception that is thrown when validating a ContactGroup
     */
    internal class ContactGroupValidationException(message: String) : Exception(message)

}