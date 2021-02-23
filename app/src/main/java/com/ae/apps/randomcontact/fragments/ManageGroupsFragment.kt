package com.ae.apps.randomcontact.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ae.apps.randomcontact.R
import com.ae.apps.randomcontact.adapters.ContactGroupRecyclerAdapter
import com.ae.apps.randomcontact.listeners.ContactGroupInteractionListener
import com.ae.apps.randomcontact.preferences.AppPreferences
import com.ae.apps.randomcontact.room.AppDatabase
import com.ae.apps.randomcontact.room.entities.ContactGroup
import com.ae.apps.randomcontact.room.repositories.ContactGroupRepository
import com.ae.apps.randomcontact.room.viewmodels.ContactGroupViewModel
import com.ae.apps.randomcontact.room.viewmodels.ContactGroupViewModelFactory
import com.ae.apps.randomcontact.utils.DEFAULT_CONTACT_GROUP
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class ManageGroupsFragment : Fragment(), ContactGroupInteractionListener {

    private var viewAdapter: ContactGroupRecyclerAdapter? = null
    private lateinit var allContactsRadio: RadioButton
    private lateinit var appPreferences: AppPreferences
    private lateinit var viewModel: ContactGroupViewModel

    companion object {
        /**
         * @return A new instance of fragment ManageGroupsFragment.
         */
        @JvmStatic
        fun newInstance() = ManageGroupsFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contactGroupRepository = ContactGroupRepository.getInstance(
            AppDatabase.getInstance(requireContext()).contactGroupDao()
        )
        val factory = ContactGroupViewModelFactory(contactGroupRepository)
        viewModel = ViewModelProviders.of(this, factory).get(ContactGroupViewModel::class.java)
        viewAdapter = ContactGroupRecyclerAdapter(this, Collections.emptyList())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_manage_groups, container, false)
        appPreferences = AppPreferences.getInstance(requireContext())


        initViews(view, appPreferences.selectedContactGroup()!!)
        setUpRecyclerView(view)

        viewModel.getAllContactGroups()
            .observe(viewLifecycleOwner, {
                kotlin.run {
                    viewAdapter?.setList(it!!)
                    val selectedContactGroup = appPreferences.selectedContactGroup()!!
                    viewAdapter?.setSelectedGroupId(selectedContactGroup)
                    checkIfDefaultContactGroupSelected(selectedContactGroup)
                }
            })

        return view
    }

    private fun setUpRecyclerView(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.list)
        recyclerView.adapter = viewAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.itemAnimator = DefaultItemAnimator()
    }

    private fun initViews(view: View, selectedContactGroup: String) {
        allContactsRadio = view.findViewById(R.id.radioAllContacts)

        allContactsRadio.setOnClickListener {
            viewAdapter?.setSelectedGroupId(DEFAULT_CONTACT_GROUP)
            viewAdapter?.notifyDataSetChanged()
            appPreferences.setSelectedContactGroup(DEFAULT_CONTACT_GROUP)
        }

        checkIfDefaultContactGroupSelected(selectedContactGroup)

        val createButton = view.findViewById<View>(R.id.btnAddGroup)
        createButton.setOnClickListener {
            val dialogFragment = AddContactGroupDialogFragment.newInstance(this)
            dialogFragment.show(childFragmentManager, "addContactGroupDialog")
        }
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
    }

    override fun selectContactGroup(contactGroup: ContactGroup) {
        allContactsRadio.isSelected = false
        allContactsRadio.isChecked = false
        appPreferences.setSelectedContactGroup(contactGroup.id.toString())
    }

    override fun editContactGroup(contactGroup: ContactGroup) {
        val dialogFragment = AddContactGroupDialogFragment.newInstance(this, contactGroup)
        dialogFragment.show(childFragmentManager, "addContactGroupDialog")
    }

    override fun deleteContactGroup(contactGroup: ContactGroup) {
        viewModel.deleteContactGroup(contactGroup)
    }

}