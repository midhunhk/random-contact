package com.ae.apps.randomcontact.fragments

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
import com.ae.apps.randomcontact.contacts.RandomContactsApiGatewayFactory
import com.ae.apps.randomcontact.databinding.FragmentRandomContactBinding
import com.ae.apps.randomcontact.preferences.AppPreferences
import com.ae.apps.randomcontact.room.AppDatabase
import com.ae.apps.randomcontact.room.repositories.ContactGroupRepositoryImpl
import com.ae.apps.randomcontact.utils.PACKAGE_NAME_WHATSAPP
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

/**
 * A simple [Fragment] subclass.
 */
class RandomContactFragment : Fragment(R.layout.fragment_random_contact), ContactsDataConsumer {

    companion object {
        @Volatile
        private var INSTANCE: RandomContactFragment? = null

        fun getInstance(): RandomContactFragment =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: RandomContactFragment().also { INSTANCE = it }
            }
    }

    private lateinit var contactsApi: ContactsApiGateway
    private var currentContact: ContactInfo? = null
    private lateinit var binding: FragmentRandomContactBinding
    private lateinit var recyclerAdapter: ContactDetailsRecyclerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRandomContactBinding.bind(view)

        setupRecyclerView()

        setupViews()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setUpContactsApi()
    }

    private fun setUpContactsApi() {
        val context = requireActivity()
        val repo = ContactGroupRepositoryImpl.getInstance(
            AppDatabase.getInstance(context).contactGroupDao()
        )
        val factory = RandomContactsApiGatewayFactory()
        val appPreferences = AppPreferences.getInstance(context)
        contactsApi = RandomContactApiGatewayImpl.getInstance(context, repo, factory, appPreferences)

        // Initialize the random contact api
        contactsApi.initializeAsync(ContactInfoFilterOptions.of(true), this)
    }

    override fun onContactsRead() {
        showRandomContact()
    }

    private fun setupViews() {
        binding.btnRefresh.setOnClickListener {
            showRandomContact()
        }
        binding.btnAddressBook.setOnClickListener {
            Toast.makeText(requireContext(), "Opening contact in Contacts app", Toast.LENGTH_SHORT).show()
            showContactInAddressBook(requireActivity(), currentContact?.id)
        }
    }

    private fun setupRecyclerView() {
        var whatsAppInstalled = false
        if (null != requireActivity().packageManager) {
            whatsAppInstalled = CommonUtils.checkIfPackageIsInstalled(requireContext(), PACKAGE_NAME_WHATSAPP)
        }

        recyclerAdapter = ContactDetailsRecyclerAdapter(
            emptyList(),
            requireContext(),
            R.layout.contact_info_item,
            whatsAppInstalled
        )

        val recyclerView: RecyclerView = binding.list
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = recyclerAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.itemAnimator = DefaultItemAnimator()
    }

    private fun showRandomContact() {
        // Running the getRandomNumber() method in a background thread as we need to access the database
        doAsync {
            val randomContact: ContactInfo? = contactsApi.randomContact

            // Run the validation on the UI Thread
            uiThread {
                if (null == randomContact) {
                    Toast.makeText(
                        context, resources.getString(R.string.str_empty_contact_list),
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    displayContact(randomContact)
                }
            }
        }
    }

    private fun displayContact(contactInfo: ContactInfo) {
        binding.contactName.text = contactInfo.name
        binding.contactImage.setImageBitmap(contactInfo.picture)

        val phoneNumbersList = contactInfo.phoneNumbersList
        recyclerAdapter.setList(phoneNumbersList)

        currentContact = contactInfo
    }

}