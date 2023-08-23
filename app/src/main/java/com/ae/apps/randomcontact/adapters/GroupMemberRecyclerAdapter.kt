package com.ae.apps.randomcontact.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.ae.apps.lib.common.models.ContactInfo
import com.ae.apps.randomcontact.R
import com.ae.apps.randomcontact.listeners.GroupMemberInteractionListener

class GroupMemberRecyclerAdapter(
    private var listener: GroupMemberInteractionListener,
    private var items: List<ContactInfo>
) : RecyclerView.Adapter<GroupMemberRecyclerAdapter.ViewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun setList(items: List<ContactInfo>) {
        this.items = items;
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.group_member_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.item = items[position]
        holder.txtMemberName.text = holder.item!!.name
        holder.btnRemoveMember.setOnClickListener {
            val contactId = holder.item!!.id
            listener.onGroupMemberRemoved(contactId)
        }
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        var item: ContactInfo? = null
        val txtMemberName: TextView = item.findViewById(R.id.txtMemberName)
        val btnRemoveMember: View = item.findViewById(R.id.btnRemoveMember)
    }
}