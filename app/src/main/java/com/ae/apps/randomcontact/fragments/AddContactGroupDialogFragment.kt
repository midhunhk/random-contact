package com.ae.apps.randomcontact.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.ae.apps.lib.api.contacts.ContactsApiGateway
import com.ae.apps.lib.api.contacts.types.ContactInfoFilterOptions
import com.ae.apps.lib.api.contacts.types.ContactsDataConsumer
import com.ae.apps.lib.common.models.ContactInfo
import com.ae.apps.lib.multicontact.MultiContactPickerConstants
import com.ae.apps.randomcontact.R
import com.ae.apps.randomcontact.activities.MultiContactPickerActivity
import com.ae.apps.randomcontact.adapters.GroupMemberRecyclerAdapter
import com.ae.apps.randomcontact.contacts.RandomContactApiGatewayImpl
import com.ae.apps.randomcontact.contacts.RandomContactsApiGatewayFactory
import com.ae.apps.randomcontact.databinding.FragmentAddContactGroupDialogBinding
import com.ae.apps.randomcontact.listeners.ContactGroupInteractionListener
import com.ae.apps.randomcontact.listeners.GroupMemberInteractionListener
import com.ae.apps.randomcontact.preferences.AppPreferences
import com.ae.apps.randomcontact.room.AppDatabase
import com.ae.apps.randomcontact.room.entities.ContactGroup
import com.ae.apps.randomcontact.room.repositories.ContactGroupRepositoryImpl
import com.ae.apps.randomcontact.utils.CONTACT_ID_SEPARATOR
import com.ae.apps.randomcontact.utils.showShortToast
import com.google.android.material.snackbar.Snackbar

/**
 * AddContactGroupDialogFragment
 * A dialog fragment used to add a new contact group or edit an existing one.
 *
 * The parent fragment (or else the activity) must implement [ContactGroupInteractionListener].
 * The group to edit is passed through the fragment arguments, so that Android can recreate this
 * dialog after a configuration change or process death.
 */
class AddContactGroupDialogFragment :
    AppCompatDialogFragment(), GroupMemberInteractionListener, ContactsDataConsumer {

    private lateinit var contactsApi: ContactsApiGateway
    private lateinit var startForResult:ActivityResultLauncher<Intent>
    private lateinit var binding:FragmentAddContactGroupDialogBinding
    /** Comma separated ids of the members picked so far */
    @VisibleForTesting
    internal var selectedContactIdStr: String = ""
    private var selectedContactInfoList: MutableList<ContactInfo> = mutableListOf()
    private var viewAdapter: GroupMemberRecyclerAdapter? = null
    private var contactsApiInitialized = false

    companion object {
        private const val ARG_GROUP_ID = "contact_group_id"
        private const val ARG_GROUP_NAME = "contact_group_name"
        private const val ARG_GROUP_CONTACTS = "contact_group_contacts"
        private const val STATE_SELECTED_CONTACT_IDS = "selected_contact_ids"

        /**
         * @param contactGroup the group to edit, or null to add a new group
         */
        @JvmStatic
        fun newInstance(contactGroup: ContactGroup? = null) = AddContactGroupDialogFragment().apply {
            if (null != contactGroup) {
                arguments = Bundle().apply {
                    contactGroup.id?.let { putInt(ARG_GROUP_ID, it) }
                    putString(ARG_GROUP_NAME, contactGroup.name)
                    putString(ARG_GROUP_CONTACTS, contactGroup.selectedContacts)
                }
            }
        }
    }

    /** The group being edited, or null when adding a new group. */
    private val contactGroupToUpdate: ContactGroup? by lazy {
        val args = arguments ?: return@lazy null
        val name = args.getString(ARG_GROUP_NAME) ?: return@lazy null
        ContactGroup(
            id = if (args.containsKey(ARG_GROUP_ID)) args.getInt(ARG_GROUP_ID) else null,
            name = name,
            selectedContacts = args.getString(ARG_GROUP_CONTACTS).orEmpty()
        )
    }

    private val interactionListener: ContactGroupInteractionListener
        get() = (parentFragment ?: activity) as? ContactGroupInteractionListener
            ?: throw IllegalStateException(
                "Parent of AddContactGroupDialogFragment must implement ContactGroupInteractionListener"
            )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Keep the members picked so far when the dialog is recreated; otherwise start from the
        // group being edited, if any
        selectedContactIdStr = savedInstanceState?.getString(STATE_SELECTED_CONTACT_IDS)
            ?: contactGroupToUpdate?.selectedContacts.orEmpty()

        startForResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    if (it.data != null) {
                        val selectedContactIds =
                            it.data!!.getStringExtra(MultiContactPickerConstants.RESULT_CONTACT_IDS)!!
                        selectedContactIdStr = selectedContactIds
                        showSelectedMembers()
                    }
                }
            }

        setupContactsApi()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddContactGroupDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSelectMembers.setOnClickListener {
            startMultiContactPicker()
        }

        binding.btnClose.setOnClickListener {
            dismiss()
        }

        binding.btnSave.setOnClickListener {
            try {
                val contactGroup = validateContactGroup()
                val originalGroup = contactGroupToUpdate
                if (originalGroup != null) {
                    // Update the validated contact group with the existing contactGroup id
                    // if this is the update flow
                    contactGroup.id = originalGroup.id
                    interactionListener.onContactGroupUpdated(originalGroup, contactGroup)
                } else {
                    interactionListener.onContactGroupAdded(contactGroup)
                }

                dismiss()
            } catch (e: ContactGroupValidationException) {
                Snackbar.make(
                    binding.addContactGroupCoordinatorLayout,
                    e.message.toString(),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }

        // Prepopulate the UI with an existing contact group details if this is an update flow
        contactGroupToUpdate?.let {
            binding.btnSave.setText(R.string.str_contact_group_update)
            // When recreated, the EditText restores whatever the user had typed
            if (null == savedInstanceState) {
                binding.txtGroupName.setText(it.name)
            }
        }

        setupRecyclerView()

        // The members can only be shown once the contacts are read; if they are not ready yet,
        // onContactsRead() shows them
        if (contactsApiInitialized) {
            showSelectedMembers()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(STATE_SELECTED_CONTACT_IDS, selectedContactIdStr)
    }

    private fun showSelectedMembers() {
        selectedContactInfoList = populateContactInfo()
        viewAdapter?.setList(selectedContactInfoList)
    }

    private fun populateContactInfo(): MutableList<ContactInfo> {
        val selectedContactIds = selectedContactIdStr.split(CONTACT_ID_SEPARATOR)
            .filter { it.isNotEmpty() }
        val tempList: MutableList<ContactInfo> = mutableListOf()
        selectedContactIds.forEach { contactId ->
            val contactInfo = contactsApi.getContactInfo(contactId)
            if (contactInfo != null){
                tempList.add(contactInfo)
            } else {
                requireContext().showShortToast("Contact not found")
            }
        }
        return tempList
    }

    private fun setupRecyclerView() {
        val recyclerView = binding.list
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

    private fun setupContactsApi() {
        val context = requireContext()
        val repo = ContactGroupRepositoryImpl.getInstance(
            dao = AppDatabase.getInstance(context).contactGroupDao()
        )
        val factory = RandomContactsApiGatewayFactory()
        val appPreferences = AppPreferences.getInstance(context)

        contactsApi = RandomContactApiGatewayImpl.getInstance(context, repo, factory, appPreferences)
        // This will call onContactsRead() once contacts data is ready
        contactsApi.initializeAsync(ContactInfoFilterOptions.of(true), this)
    }

    override fun onContactsRead() {
        contactsApiInitialized = true
        // Delivered after onViewCreated() when the contacts had not been read yet
        if (null != view) {
            showSelectedMembers()
        }
    }

    private fun validateContactGroup(): ContactGroup {
        if (TextUtils.isEmpty(binding.txtGroupName.text.toString())) {
            throw ContactGroupValidationException("Please enter group name")
        }

        val builder = convertToSelectedContactString()

        if (builder.isEmpty()) {
            throw ContactGroupValidationException("Please select contacts for this group")
        }

        return ContactGroup(
            name = binding.txtGroupName.text.toString(),
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