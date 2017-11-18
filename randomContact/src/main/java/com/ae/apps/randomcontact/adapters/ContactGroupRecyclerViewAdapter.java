/*
 * Copyright 2017 Midhun Harikumar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ae.apps.randomcontact.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.ae.apps.randomcontact.R;
import com.ae.apps.randomcontact.data.ContactGroup;
import com.ae.apps.randomcontact.data.ContactGroupInteractionListener;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link ContactGroup} and makes a call to the
 * specified {@link ContactGroupInteractionListener}.
 */
public class ContactGroupRecyclerViewAdapter extends RecyclerView.Adapter<ContactGroupRecyclerViewAdapter.ViewHolder> {

    private final List<ContactGroup> mValues;
    private final ContactGroupInteractionListener mListener;

    private String mSelectedGroupId;
    private RadioButton mLastChecked;

    public ContactGroupRecyclerViewAdapter(List<ContactGroup> items, ContactGroupInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_group_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mGroupName.setText(mValues.get(position).getName());

        holder.mRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioButton radioButton = (RadioButton) v;
                if (radioButton.isChecked() && null != mListener) {
                    mListener.onContactGroupSelected(holder.mItem);
                }
                // To support only one radio button being selected a time,
                // we hold a reference to the last selected checkbox
                if (null != mLastChecked && mLastChecked != radioButton) {
                    mLastChecked.setChecked(false);
                }
                mLastChecked = radioButton;
            }
        });

        if (null != mSelectedGroupId && mSelectedGroupId.equals(holder.mItem.getId())) {
            holder.mRadio.setSelected(true);
            holder.mRadio.setChecked(true);
            mLastChecked = holder.mRadio;
        } else {
            holder.mRadio.setSelected(false);
            holder.mRadio.setChecked(false);
        }

        holder.mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onContactGroupDeleted(holder.mItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void setSelectedGroupId(String selectedGroupId) {
        this.mSelectedGroupId = selectedGroupId;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final RadioButton mRadio;
        final TextView mGroupName;
        final ImageButton mDeleteButton;
        ContactGroup mItem;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mRadio = (RadioButton) view.findViewById(R.id.radioSelected);
            mGroupName = (TextView) view.findViewById(R.id.content);
            mDeleteButton = (ImageButton) view.findViewById(R.id.btnDeleteContactGroup);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mGroupName.getText() + "'";
        }
    }
}
