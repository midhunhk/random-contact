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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.appcompat.widget.AppCompatImageButton
import androidx.coordinatorlayout.widget.CoordinatorLayout
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
import com.ae.apps.randomcontact.contacts.RandomContactApiGatewayImpl
import com.ae.apps.randomcontact.listeners.ContactGroupInteractionListener
import com.ae.apps.randomcontact.listeners.GroupMemberInteractionListener
import com.ae.apps.randomcontact.room.entities.ContactGroup
import com.ae.apps.randomcontact.utils.CONTACT_ID_SEPARATOR
import com.google.android.material.snackbar.Snackbar

/**
 * A simple [Fragment] subclass.
 * Use the [AddContactGroupDialogFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddContactGroupDialogFragment(
    private val interactionListener: ContactGroupInteractionListener,
    private val contactGroupToUpdate: ContactGroup? = null
) :
    AppCompatDialogFragment(), GroupMemberInteractionListener {

    private lateinit var btnClose: AppCompatImageButton
    private lateinit var btnSave: Button
    private lateinit var btnSelect: Button
    private lateinit var txtGroupName: EditText
    private lateinit var coordinatorLayout: CoordinatorLayout
    private lateinit var contactsApi: ContactsApiGateway
    private lateinit var startForResult:ActivityResultLauncher<Intent>
    private var selectedContactIdStr: String = ""
    private var selectedContactInfoList: MutableList<ContactInfo> = mutableListOf()
    private var viewAdapter: GroupMemberRecyclerAdapter? = null

    companion object {

        @JvmStatic
        fun newInstance(
            interactionListener: ContactGroupInteractionListener,
            contactGroup: ContactGroup? = null
        ) = AddContactGroupDialogFragment(interactionListener, contactGroup)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startForResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    if (it.data != null) {
                        val selectedContactIds =
                            it.data!!.getStringExtra(MultiContactPickerConstants.RESULT_CONTACT_IDS)!!
                        Toast.makeText(requireContext(), selectedContactIds, Toast.LENGTH_LONG)
                            .show()
                        Snackbar.make(
                            coordinatorLayout,
                            selectedContactIds,
                            Snackbar.LENGTH_SHORT
                        ).show()

                        // TODO Verify this
                        selectedContactIdStr = selectedContactIds
                        selectedContactInfoList = populateContactInfo()
                        viewAdapter?.setList(selectedContactInfoList)
                    }
                }
            }
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
        coordinatorLayout = view.findViewById(R.id.addContactGroupCoordinatorLayout)

        contactGroupToUpdate?.let { it ->
            // If there is a contactGroupId to edit
            btnSave.setText(R.string.str_contact_group_update)

            // Populate the details on the UI
            txtGroupName.setText(it.name)
            selectedContactIdStr = it.selectedContacts
            selectedContactInfoList = populateContactInfo()
        }
    }

    private fun populateContactInfo(): MutableList<ContactInfo> {
        val selectedContactIds = selectedContactIdStr.split(CONTACT_ID_SEPARATOR)
        val tempList: MutableList<ContactInfo> = mutableListOf()
        selectedContactIds.forEach {
            tempList.add(contactsApi.getContactInfo(it))
        }
        return tempList
    }

    private fun setupRecyclerView(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.list)
        viewAdapter = GroupMemberRecyclerAdapter(this, selectedContactInfoList)
        recyclerView.adapter = viewAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.itemAnimator = DefaultItemAnimator()
    }

    private fun startMultiContactPicker() {
        val multiContactPickerIntent = Intent(
            requireContext(),
            MultiContactPickerActivity::class.java
        )
        if (selectedContactIdStr.isNotEmpty()) {
            multiContactPickerIntent.putExtra(
                MultiContactPickerConstants.PRESELECTED_CONTACT_IDS,
                selectedContactIdStr
            )
        }
        startForResult.launch(multiContactPickerIntent)
    }

    override fun getTheme(): Int = R.style.DialogTheme

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
                if (contactGroupToUpdate != null) {
                    // Update the validated contact group with the existing contactGroup id
                    // if this is the update flow
                    contactGroup.id = it.id
                    interactionListener.onContactGroupUpdated(contactGroupToUpdate, contactGroup)
                } else {
                    interactionListener.onContactGroupAdded(contactGroup)
                }

                dismiss()
            } catch (e: ContactGroupValidationException) {
                Snackbar.make(
                    coordinatorLayout,
                    e.message.toString(),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun validateContactGroup(): ContactGroup {
        if (TextUtils.isEmpty(txtGroupName.text.toString())) {
            throw ContactGroupValidationException("Please enter group name")
        }

        val builder = convertToSelectedContactString()

        if (builder.isEmpty()) {
            throw ContactGroupValidationException("Please select contacts for this group")
        }

        return ContactGroup(
            name = txtGroupName.text.toString(),
            selectedContacts = builder.toString()
        )
    }

    private fun convertToSelectedContactString(): StringBuilder {
        val builder = StringBuilder()
        for (contactInfo in selectedContactInfoList) {
            builder.append(contactInfo.id)
                .append(CONTACT_ID_SEPARATOR)
        }
        if(builder.isNotEmpty()) {
            builder.deleteCharAt(builder.lastIndexOf(CONTACT_ID_SEPARATOR))
        }
        return builder
    }

    override fun onGroupMemberRemoved(contactId: String) {
        val index = selectedContactInfoList.indexOfFirst { it.id.equals(contactId) }
        selectedContactInfoList.removeAt(index)

        selectedContactIdStr = convertToSelectedContactString().toString()

        viewAdapter?.setList(selectedContactInfoList)
    }

    /**
     * Internal Exception that is thrown when validating a ContactGroup
     */
    internal class ContactGroupValidationException(message: String) : Exception(message)

}