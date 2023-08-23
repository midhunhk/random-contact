package com.ae.apps.randomcontact.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ae.apps.lib.common.models.PhoneNumberInfo
import com.ae.apps.lib.common.utils.MobileNetworkUtils
import com.ae.apps.randomcontact.R
import com.ae.apps.randomcontact.utils.sendWhatsAppMessage

/**
 *
 */
class ContactDetailsRecyclerAdapter(
    private var items: List<PhoneNumberInfo>,
    private var context: Context,
    private val layoutResourceId: Int,
    private val enableWhatsAppIntegration: Boolean = false
):
    RecyclerView.Adapter<ContactDetailsRecyclerAdapter.ViewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun setList(items: List<PhoneNumberInfo>) {
        this.items = items
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layoutResourceId, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val phoneNumberInfo = items[position]
        val contactNo = phoneNumberInfo.phoneNumber

        holder.contactNumber.text = contactNo
        holder.contactPhoneType.text = phoneNumberInfo.phoneType

        if(enableWhatsAppIntegration){
            holder.btnWhatsAppMessage.visibility = View.VISIBLE
        } else {
            holder.btnWhatsAppMessage.visibility = View.GONE
        }

        holder.btnCall.setOnClickListener {
            MobileNetworkUtils.dialContact(context, contactNo)
        }
        holder.btnText.setOnClickListener {
            MobileNetworkUtils.textContact(context, contactNo)
        }
        holder.btnWhatsAppMessage.setOnClickListener {
            sendWhatsAppMessage(context, contactNo)
        }
    }

    class ViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        val contactNumber: TextView = item.findViewById(R.id.txtContactNumber)
        val contactPhoneType: TextView = item.findViewById(R.id.txtPhoneType)
        val btnCall:View = item.findViewById(R.id.btnCall)
        val btnText:View = item.findViewById(R.id.btnText)
        val btnWhatsAppMessage:View = item.findViewById(R.id.btnWhatsAppMessage)
    }
}