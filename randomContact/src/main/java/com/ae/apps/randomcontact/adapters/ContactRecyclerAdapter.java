/*
 * Copyright 2016 Midhun Harikumar
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
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ae.apps.common.utils.MobileNetworkUtils;
import com.ae.apps.common.vo.PhoneNumberVo;
import com.ae.apps.randomcontact.R;
import com.ae.apps.randomcontact.utils.Utils;

import java.util.List;

/**
 * Adapter for RecyclerView to show contact details
 *
 * @author Midhun
 */
public class ContactRecyclerAdapter extends Adapter<ContactRecyclerAdapter.ViewHolder> {

    private Context mContext;
    private List<PhoneNumberVo> items;
    private int layoutResourceId;
    private String contactId;
    private boolean enableWhatsAppIntegration;

    public ContactRecyclerAdapter(List<PhoneNumberVo> items, int layoutResourceId, Context context) {
        super();
        this.layoutResourceId = layoutResourceId;
        setList(items);
        this.mContext = context;
    }

    public void setList(List<PhoneNumberVo> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int pos) {
        final PhoneNumberVo phoneNumberVo = items.get(pos);
        if (null != phoneNumberVo) {
            holder.txtContactNumber.setText(phoneNumberVo.getPhoneNumber());
            holder.txtPhoneType.setText(phoneNumberVo.getPhoneType());

            if(enableWhatsAppIntegration){
                holder.btnWhatsAppMessage.setVisibility(View.VISIBLE);
            } else {
                holder.btnWhatsAppMessage.setVisibility(View.GONE);
            }

            final String contactNo = phoneNumberVo.getPhoneNumber();
            holder.btnCall.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // call
                    MobileNetworkUtils.callContact(mContext, contactNo);
                }
            });

            holder.btnText.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // text
                    MobileNetworkUtils.textContact(mContext, contactNo);
                }
            });

            holder.btnWhatsAppMessage.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    Utils.sendWhatsAppMessage(mContext, contactNo);
                   //Utils.openWhatsAppContact(mContext, contactId);
                }
            });
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutResourceId, parent, false);
        return new ViewHolder(view);
    }

    public void setEnableWhatsAppIntegration(boolean enableWhatsAppIntegration) {
        this.enableWhatsAppIntegration = enableWhatsAppIntegration;
    }

    /**
     * @author midhun
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtContactNumber;
        TextView txtPhoneType;
        ImageButton btnCall;
        ImageButton btnText;
        ImageButton btnWhatsAppMessage;

        public ViewHolder(View itemView) {
            super(itemView);

            txtPhoneType = (TextView) itemView.findViewById(R.id.txtPhoneType);
            txtContactNumber = (TextView) itemView.findViewById(R.id.txtContactNumber);
            btnCall = (ImageButton) itemView.findViewById(R.id.btnCall);
            btnText = (ImageButton) itemView.findViewById(R.id.btnText);
            btnWhatsAppMessage = (ImageButton) itemView.findViewById(R.id.btnWhatsAppMessage);
        }

    }
}
