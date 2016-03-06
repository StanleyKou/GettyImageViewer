package com.kou.gettyimageviewer.listView;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kou.gettyimageviewer.R;
import com.kou.gettyimageviewer.model.ItemData;

public class CustomListAdapter extends BaseAdapter {
	private ArrayList<ItemData> listData;
	private LayoutInflater layoutInflater;

	public CustomListAdapter(Context context, ArrayList<ItemData> listData) {
		this.listData = listData;
		layoutInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return listData.size();
	}

	@Override
	public Object getItem(int position) {
		return listData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.item_layout, null);
			holder = new ViewHolder();
			holder.txtViewTitle = (TextView) convertView.findViewById(R.id.item_title);
			holder.imgViewIcon = (ImageView) convertView.findViewById(R.id.item_icon);
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		ItemData newsItem = listData.get(position);
		holder.txtViewTitle.setText(newsItem.getTitle());

		if (holder.imgViewIcon != null) {
			new ImageDownloaderTask(holder.imgViewIcon).execute(newsItem.getImageUrl());
		}

		return convertView;
	}

	static class ViewHolder {
		TextView txtViewTitle;
		ImageView imgViewIcon;
	}
}
