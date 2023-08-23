package com.ae.apps.randomcontact.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView
import com.ae.apps.randomcontact.R
import com.ae.apps.randomcontact.listeners.ContactGroupInteractionListener
import com.ae.apps.randomcontact.room.entities.ContactGroup
import java.util.*

public class ContactGroupRecyclerAdapter(
    private val listener: ContactGroupInteractionListener,
    private var items: List<ContactGroup> = Collections.emptyList()
) :
    RecyclerView.Adapter<ContactGroupRecyclerAdapter.ViewHolder>() {

    private var selectedGroupId: String? = null
    private var lastChecked: RadioButton? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setList(items: List<ContactGroup>) {
        this.items = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.contact_group_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.item = items[position]
        holder.radioButton.text = holder.item!!.name

        holder.radioButton.setOnClickListener {
            val radioButton: RadioButton = it as RadioButton
            if (lastChecked != null && lastChecked != radioButton) {
                lastChecked?.isChecked = false
            }
            lastChecked = radioButton

            listener.selectContactGroup(holder.item!!)
        }

        holder.editButton.setOnClickListener {
            listener.editContactGroup(holder.item!!)
        }

        holder.deleteButton.setOnClickListener {
            listener.deleteContactGroup(holder.item!!)
        }

        if (null != selectedGroupId && selectedGroupId!! == holder.item!!.id.toString()) {
            holder.radioButton.isSelected = true
            holder.radioButton.isChecked = true
            lastChecked = holder.radioButton
        } else {
            holder.radioButton.isSelected = false
            holder.radioButton.isChecked = false
        }
    }

    fun setSelectedGroupId(selectedGroupId: String) {
        this.selectedGroupId = selectedGroupId
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        val radioButton: RadioButton = view.findViewById(R.id.radioSelected)
        val deleteButton: ImageButton = view.findViewById(R.id.btnDeleteContactGroup)
        val editButton: ImageButton = view.findViewById(R.id.btnEditContactGroup)
        var item: ContactGroup? = null

        override fun toString(): String {
            return super.toString() + " '" + radioButton.text + "'"
        }
    }

}