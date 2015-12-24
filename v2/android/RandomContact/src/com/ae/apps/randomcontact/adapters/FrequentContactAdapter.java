package com.ae.apps.randomcontact.adapters;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ae.apps.common.vo.ContactVo;
import com.ae.apps.randomcontact.R;

/**
 * Adapter for RecyclerView to show contact details
 * 
 * @author Midhun
 *
 */
public class FrequentContactAdapter extends Adapter<FrequentContactAdapter.ViewHolder> {

	private Context			mContext;
	private List<ContactVo>	items;
	private int				layoutResourceId;
	Bitmap					defaultImage;

	public FrequentContactAdapter(List<ContactVo> items, int layoutResourceId, Context context) {
		super();
		this.layoutResourceId = layoutResourceId;
		setList(items);
		this.mContext = context;
		defaultImage = BitmapFactory.decodeResource(mContext.getResources(),
				com.ae.apps.aeappslibrary.R.drawable.profile_icon_5);
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
	public void onBindViewHolder(ViewHolder holder, int pos) {
		final ContactVo contactVo = items.get(pos);
		if (null != contactVo) {
			holder.contactNameText.setText(contactVo.getName());
			holder.contactCountText.setText(contactVo.getTimesContacted());

			// Try to get this contact's profile image
			Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(),
					contactVo.getMockProfileImageResource());
			if (bitmap == null) {
				bitmap = defaultImage;
			}
			holder.imgProfile.setImageBitmap(bitmap);
		}
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(layoutResourceId, parent, false);
		return new ViewHolder(view);
	}

	/**
	 * 
	 * @author midhun
	 *
	 */
	public static class ViewHolder extends RecyclerView.ViewHolder {
		ImageView	imgProfile;
		TextView	contactNameText;
		TextView	contactCountText;

		public ViewHolder(View itemView) {
			super(itemView);
			imgProfile = (ImageView) itemView.findViewById(R.id.userProfileImage);
			contactNameText = (TextView) itemView.findViewById(R.id.contactNameText);
			contactCountText = (TextView) itemView.findViewById(R.id.contactCountText);
		}

	}
}
