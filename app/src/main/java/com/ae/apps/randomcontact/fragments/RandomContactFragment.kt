package com.ae.apps.randomcontact.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ae.apps.lib.api.contacts.ContactsApiGateway
import com.ae.apps.lib.api.contacts.types.ContactInfoFilterOptions
import com.ae.apps.lib.api.contacts.types.ContactsDataConsumer
import com.ae.apps.lib.common.models.ContactInfo
import com.ae.apps.lib.common.utils.CommonUtils
import com.ae.apps.lib.common.utils.ContactUtils.showContactInAddressBook
import com.ae.apps.randomcontact.R
import com.ae.apps.randomcontact.adapters.ContactDetailsRecyclerAdapter
import com.ae.apps.randomcontact.contacts.RandomContactApiGatewayImpl
import com.ae.apps.randomcontact.databinding.FragmentRandomContactBinding
import com.ae.apps.randomcontact.room.AppDatabase
import com.ae.apps.randomcontact.room.repositories.ContactGroupRepository
import com.ae.apps.randomcontact.utils.PACKAGE_NAME_WHATSAPP


/**
 * A simple [Fragment] subclass.
 */
class RandomContactFragment : Fragment(R.layout.fragment_random_contact), ContactsDataConsumer {

    companion object {
        private lateinit var contactsApi: ContactsApiGateway
        @Volatile private var INSTANCE: RandomContactFragment? = null

        fun newInstance(context: Context): RandomContactFragment =
            INSTANCE ?: synchronized(this){
                contactsApi = RandomContactApiGatewayImpl.getInstance(context)
                INSTANCE ?: RandomContactFragment().also { INSTANCE = it }
            }
    }

    private var currentContact:ContactInfo? = null
    private var binding: FragmentRandomContactBinding? = null
    private lateinit var recyclerAdapter: ContactDetailsRecyclerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRandomContactBinding.bind(view)

        setupRecyclerView()

        setupViews()

        // Initialize the random contact api
        contactsApi.initializeAsync(ContactInfoFilterOptions.of(true), this)
    }
    override fun onContactsRead() {
        showRandomContact()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun setupViews(){
        binding?.btnRefresh?.setOnClickListener {
            showRandomContact()
        }
        binding?.btnAddressBook?.setOnClickListener {
            Toast.makeText(requireContext(), "Opening contact in Contacts app", Toast.LENGTH_SHORT).show();
            showContactInAddressBook(requireActivity(), currentContact?.id);
        }
    }

    private fun setupRecyclerView(){
        var whatsAppInstalled: Boolean = false;
        if (null != requireActivity().packageManager) {
            whatsAppInstalled = CommonUtils.isPackageInstalled(PACKAGE_NAME_WHATSAPP, context)
        }

        recyclerAdapter = ContactDetailsRecyclerAdapter(
            emptyList(),
            requireContext(),
            R.layout.contact_info_item,
            whatsAppInstalled
        )

        val recyclerView:RecyclerView =  binding?.list!!
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = recyclerAdapter
        val linearLayoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
    }

    private fun showRandomContact(){
        val randomContact:ContactInfo? = contactsApi.randomContact
        if(null == randomContact){
            Toast.makeText(
                context,
                resources.getString(R.string.str_empty_contact_list),
                Toast.LENGTH_LONG
            ).show();
        } else {
            displayContact(randomContact)
        }
    }

    private fun displayContact(contactInfo: ContactInfo) {
        binding?.contactName?.text  = contactInfo.name
        binding?.contactImage?.setImageBitmap(contactInfo.picture)
        binding?.timesContactedCount?.text = contactInfo.timesContacted

        val phoneNumbersList = contactInfo.phoneNumbersList
        recyclerAdapter.setList(phoneNumbersList)

        currentContact = contactInfo
    }

}