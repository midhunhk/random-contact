/*
 * Copyright 2016-2018 Midhun Harikumar
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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ae.apps.common.managers.contact.AeContactManager;
import com.ae.apps.common.vo.ContactVo;
import com.ae.apps.randomcontact.R;

import java.util.List;

/**
 * Adapter for RecyclerView to show contact details
 *
 * @author Midhun
 */
public class FrequentContactAdapter extends Adapter<FrequentContactAdapter.ViewHolder> {

    private List<ContactVo> items;
    private int layoutResourceId;
    private Bitmap defaultImage;
    private AeContactManager mContactManager;

    public FrequentContactAdapter(List<ContactVo> items, int layoutResourceId, Context context,
                                  AeContactManager contactManager) {
        super();
        this.layoutResourceId = layoutResourceId;
        setList(items);
        defaultImage = BitmapFactory.decodeResource(context.getResources(),
                com.ae.apps.aeappslibrary.R.drawable.profile_icon_4);
        mContactManager = contactManager;
    }

    public void setList(List<ContactVo> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int pos) {
        final ContactVo contactVo = items.get(pos);
        if (null != contactVo) {
            holder.contactNameText.setText(contactVo.getName());
            holder.contactCountText.setText(contactVo.getTimesContacted());

            // Try to get this contact's profile image
            Bitmap profileImage = mContactManager.getContactPhoto(contactVo.getId(), defaultImage);
            holder.imgProfile.setImageBitmap(profileImage);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutResourceId, parent, false);
        return new ViewHolder(view);
    }

    /**
     * @author midhun
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProfile;
        TextView contactNameText;
        TextView contactCountText;

        ViewHolder(View itemView) {
            super(itemView);
            imgProfile = itemView.findViewById(R.id.userProfileImage);
            contactNameText = itemView.findViewById(R.id.contactNameText);
            contactCountText = itemView.findViewById(R.id.contactCountText);
        }

    }
}
