package com.ae.apps.randomcontact.fragments

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.Fragment
import com.ae.apps.lib.api.contacts.ContactsApiGateway
import com.ae.apps.lib.common.models.ContactInfo
import com.ae.apps.randomcontact.R
import com.ae.apps.randomcontact.data.RandomContactApiGatewayImpl
import com.ae.apps.randomcontact.room.entities.ContactGroup
import com.ae.apps.randomcontact.room.repositories.ContactGroupRepository
import com.ae.apps.randomcontact.utils.CONTACT_ID_SEPARATOR


/**
 * A simple [Fragment] subclass.
 * Use the [AddContactGroupDialogFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddContactGroupDialogFragment(
    private val repository: ContactGroupRepository,
    private val contactGroupToUpdate: ContactGroup? = null
) :
    AppCompatDialogFragment() {

    private lateinit var btnClose: Button
    private lateinit var btnSave: Button
    private lateinit var btnSelect: Button
    private lateinit var txtGroupName: EditText
    private lateinit var selectedContacts: MutableList<ContactInfo>
    private lateinit var selectedContactIds: String
    private lateinit var contactsApi: ContactsApiGateway

    companion object {

        @JvmStatic
        fun newInstance(
            contactGroupRepository: ContactGroupRepository,
            contactGroup: ContactGroup? = null
        ) = AddContactGroupDialogFragment(contactGroupRepository, contactGroup)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_contact_group_dialog, container, false)
        contactsApi = RandomContactApiGatewayImpl.getInstance(requireContext(), repository)

        initViews(view)
        setupRecyclerView(view)

        return view
    }

    private fun setupRecyclerView(view: View) {
        // TODO
    }

    private fun initViews(view: View) {
        btnClose = view.findViewById(R.id.btnClose)
        btnSave = view.findViewById(R.id.btnSave)
        btnSelect = view.findViewById(R.id.btnSelectMembers)
        txtGroupName = view.findViewById(R.id.txtGroupName)

        contactGroupToUpdate?.let { it ->
            // If there is a contactGroupId to edit

            // Populate the details on the UI
            txtGroupName.setText(it.name)
            populateContactInfo(it)
        }
    }

    private fun populateContactInfo(it: ContactGroup) {
        val selectedContactIds = it.selectedContacts.split(CONTACT_ID_SEPARATOR)
        selectedContactIds.forEach {
            selectedContacts.add(contactsApi.getContactInfo(it))
        }
    }

    override fun getTheme(): Int {
        return R.style.DialogTheme
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnSelect.setOnClickListener {
            // TODO - Open multi contact picker
        }

        btnClose.setOnClickListener {
            dismiss()
        }

        btnSave.setOnClickListener {
            try {
                val contactGroup = validateContactGroup()
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
        for (contactInfo in selectedContacts) {
            builder.append(contactInfo.id)
                .append(CONTACT_ID_SEPARATOR)
        }

        if (builder.isEmpty()) {
            throw ContactGroupValidationException("Please select contacts for this group")
        }
        builder.deleteCharAt(builder.lastIndexOf(CONTACT_ID_SEPARATOR))

        return ContactGroup(name = txtGroupName.text.toString(), selectedContacts =  builder.toString())
    }

    /**
     * Internal Exception that is thrown when validating a ContactGroup
     */
    internal class ContactGroupValidationException(message: String) : Exception(message)

    /**
     * Parent fragment must implement this interface to receive callbacks on add or update of a contact group
     */
    internal interface AddContactGroupDialogListener {
        /**
         * Invoked when a contact group has been created
         */
        fun onContactGroupAdded(contactGroup: ContactGroup)

        /**
         * Invoked when a contact group has been updated
         * Only name and members can be updated
         */
        fun onContactGroupUpdated(contactGroup: ContactGroup)
    }
}