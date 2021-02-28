package com.ae.apps.randomcontact.fragments

import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ae.apps.lib.custom.views.EmptyRecyclerView
import com.ae.apps.randomcontact.R
import com.ae.apps.randomcontact.adapters.ContactGroupRecyclerAdapter
import com.ae.apps.randomcontact.listeners.ContactGroupInteractionListener
import com.ae.apps.randomcontact.preferences.AppPreferences
import com.ae.apps.randomcontact.room.AppDatabase
import com.ae.apps.randomcontact.room.entities.ContactGroup
import com.ae.apps.randomcontact.room.repositories.ContactGroupRepositoryImpl
import com.ae.apps.randomcontact.room.viewmodels.ContactGroupViewModel
import com.ae.apps.randomcontact.room.viewmodels.ContactGroupViewModelFactory
import com.ae.apps.randomcontact.utils.DEFAULT_CONTACT_GROUP
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * A simple [Fragment] subclass.
 */
class ManageGroupsFragment : Fragment(R.layout.fragment_manage_groups),
    ContactGroupInteractionListener {

    private var viewAdapter: ContactGroupRecyclerAdapter? = null
    private lateinit var allContactsRadio: RadioButton
    private lateinit var appPreferences: AppPreferences
    private lateinit var viewModel: ContactGroupViewModel
    private lateinit var emptyView: View

    companion object {
        /**
         * @return A new instance of fragment ManageGroupsFragment.
         */
        @JvmStatic
        fun newInstance() = ManageGroupsFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewAdapter = ContactGroupRecyclerAdapter(this)
        appPreferences = AppPreferences.getInstance(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view, appPreferences.selectedContactGroup())
        setUpRecyclerView(view)
        val contactGroupDao = AppDatabase.getInstance(requireContext()).contactGroupDao()
        val repository = ContactGroupRepositoryImpl.getInstance(contactGroupDao)
        val viewModelFactory = ContactGroupViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(ContactGroupViewModel::class.java)
        viewModel.getAllContactGroups()
            .observe(viewLifecycleOwner, { contactGroups ->
                kotlin.run {
                    viewAdapter?.setList(contactGroups)
                    val selectedContactGroup = appPreferences.selectedContactGroup()
                    viewAdapter?.setSelectedGroupId(selectedContactGroup)
                    checkIfDefaultContactGroupSelected(selectedContactGroup)
                }
            })
    }

    private fun initViews(view: View, selectedContactGroup: String) {
        allContactsRadio = view.findViewById(R.id.radioAllContacts)

        allContactsRadio.setOnClickListener {
            viewAdapter?.setSelectedGroupId(DEFAULT_CONTACT_GROUP)
            viewAdapter?.notifyDataSetChanged()
            appPreferences.setSelectedContactGroup(DEFAULT_CONTACT_GROUP)
        }

        emptyView = view.findViewById(R.id.empty_view)

        checkIfDefaultContactGroupSelected(selectedContactGroup)

        val createButton = view.findViewById<View>(R.id.btnAddGroup)
        createButton.setOnClickListener {
            launchContactGroupDialog()
        }

        val createGroupAlt = view.findViewById<View>(R.id.btnAddGroupEmptyView)
        createGroupAlt.setOnClickListener {
            launchContactGroupDialog()
        }
    }

    private fun setUpRecyclerView(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.list) as EmptyRecyclerView
        recyclerView.adapter = viewAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.setEmptyView(emptyView)
    }

    private fun checkIfDefaultContactGroupSelected(selectedContactGroup: String) {
        if (DEFAULT_CONTACT_GROUP == selectedContactGroup) {
            allContactsRadio.isSelected = true
            allContactsRadio.isChecked = true
        }
    }

    override fun onContactGroupAdded(contactGroup: ContactGroup) {
        viewModel.createContactGroup(contactGroup)
    }

    override fun onContactGroupUpdated(originalItem: ContactGroup, updatedItem: ContactGroup) {
        viewModel.updateContactGroup(updatedItem)
        //viewAdapter?.notifyDataSetChanged()
    }

    override fun selectContactGroup(contactGroup: ContactGroup) {
        allContactsRadio.isSelected = false
        allContactsRadio.isChecked = false
        appPreferences.setSelectedContactGroup(contactGroup.id.toString())
    }

    override fun editContactGroup(contactGroup: ContactGroup) {
        launchContactGroupDialog(contactGroup)
    }

    private fun launchContactGroupDialog(contactGroup: ContactGroup? = null) {
        val dialogFragment = AddContactGroupDialogFragment.newInstance(this, contactGroup)
        dialogFragment.show(childFragmentManager, "addContactGroupDialog")
    }

    override fun deleteContactGroup(contactGroup: ContactGroup) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.str_contact_group_delete_title))
            .setNegativeButton(resources.getString(R.string.str_dialog_cancel)) { dialog, _ ->
                // Respond to negative button press
                dialog.dismiss()
            }
            .setPositiveButton(resources.getString(R.string.str_dialog_delete)) { dialog, _ ->
                // Respond to positive button press
                processContactGroupRemoval(contactGroup)
                dialog.dismiss()
            }
            .show()
    }

    private fun processContactGroupRemoval(item: ContactGroup) {
        // Remove the contact group item from the database
        viewModel.deleteContactGroup(item)

        // Make the All Contacts radio checked if the deleted item was previously selected
        if (appPreferences.selectedContactGroup() == (item.id.toString())) {
            checkIfDefaultContactGroupSelected(DEFAULT_CONTACT_GROUP)
            appPreferences.setSelectedContactGroup(DEFAULT_CONTACT_GROUP)
        }
    }

}