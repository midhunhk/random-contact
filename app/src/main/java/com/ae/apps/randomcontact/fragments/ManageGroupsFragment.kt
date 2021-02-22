package com.ae.apps.randomcontact.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.Fragment
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
import com.ae.apps.randomcontact.utils.DEFAULT_CONTACT_GROUP

/**
 * A simple [Fragment] subclass.
 */
class ManageGroupsFragment : Fragment(), ContactGroupInteractionListener {

    private var contactGroups: List<ContactGroup>? = null
    private var viewAdapter: ContactGroupRecyclerAdapter? = null
    private lateinit var allContactsRadio: RadioButton
    private lateinit var appPreferences: AppPreferences
    private lateinit var contactGroupRepository: ContactGroupRepository

    companion object {
        /**
         * @return A new instance of fragment ManageGroupsFragment.
         */
        @JvmStatic
        fun newInstance() = ManageGroupsFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_manage_groups, container, false)
        appPreferences = AppPreferences.getInstance(requireContext())
        contactGroupRepository = ContactGroupRepository.getInstance(
            AppDatabase.getInstance(requireContext()).contactGroupDao()
        )

        contactGroups = contactGroupRepository.getAllContactGroups()

        initViews(view, appPreferences.selectedContactGroup()!!)
        setUpRecyclerView(view, appPreferences.selectedContactGroup()!!)
        return view
    }

    private fun setUpRecyclerView(view: View, selectedContactGroup: String) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.list)
        if(null != recyclerView){
            viewAdapter = contactGroups?.let {
                ContactGroupRecyclerAdapter(it)
            }
            viewAdapter?.setSelectedGroupId(selectedContactGroup)
            recyclerView.adapter = viewAdapter
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.itemAnimator = DefaultItemAnimator()
        }
    }

    private fun initViews(view: View, selectedContactGroup: String){
        allContactsRadio = view.findViewById(R.id.radioAllContacts)

        allContactsRadio.setOnClickListener {
            viewAdapter?.setSelectedGroupId(DEFAULT_CONTACT_GROUP)
            viewAdapter?.notifyDataSetChanged()
            appPreferences.setSelectedContactGroup(DEFAULT_CONTACT_GROUP)
        }

        if(DEFAULT_CONTACT_GROUP == selectedContactGroup){
            allContactsRadio.isSelected = true
            allContactsRadio.isChecked = true
        }

        val createButton = view.findViewById<View>(R.id.btnAddGroup)
        createButton.setOnClickListener {
            val dialogFragment = AddContactGroupDialogFragment.newInstance(this)
            dialogFragment.show(parentFragmentManager, "addContactGroupDialog")
        }
    }

    override fun onContactGroupSelected(item: ContactGroup) {
        allContactsRadio.isSelected = false
        allContactsRadio.isChecked = false
        appPreferences.setSelectedContactGroup(item.id.toString())
    }

    override fun onContactGroupDeleted(item: ContactGroup) {
        contactGroupRepository.deleteContactGroup(item)
    }

    override fun onContactGroupAdded(contactGroup: ContactGroup) {
        contactGroupRepository.createContactGroup(contactGroup)
    }

    override fun onContactGroupUpdated(originalItem: ContactGroup, updatedItem:ContactGroup){
        contactGroupRepository.updateContactGroup(updatedItem)
    }

}