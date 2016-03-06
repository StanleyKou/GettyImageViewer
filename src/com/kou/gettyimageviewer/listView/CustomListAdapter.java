package com.kou.gettyimageviewer.listView;

import java.util.ArrayList;
import java.util.HashSet;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.kou.gettyimageviewer.R;
import com.kou.gettyimageviewer.model.ItemData;

public class CustomListAdapter extends BaseAdapter {
	private ArrayList<ItemData> listData = new ArrayList<ItemData>();
	private LayoutInflater layoutInflater;
	private Context context;
	private ListView listview;

	private final int cacheSize = 4 * 1024 * 1024; // 4MiB
	private LruCache<Integer, Bitmap> bitmapCache = new LruCache<Integer, Bitmap>(cacheSize);
	private HashSet<Integer> requestMap = new HashSet<>();

	public CustomListAdapter(Context context, ListView listview) {
		this.context = context;
		this.listview = listview;
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

	public void addItem(ItemData data) {
		listData.add(data);
		notifyDataSetChanged();
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
		holder.txtViewTitle.setText(newsItem.getTitle() + "");

		if (holder.imgViewIcon != null) {
			Bitmap bitmap = bitmapCache.get(position);
			if (bitmap != null) {
				holder.imgViewIcon.setImageBitmap(bitmap);
				requestMap.remove(position);
			} else {
				holder.imgViewIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.placeholder));

				if (requestMap.contains(position)) {
					// Just wait
				} else {
					requestMap.add(position);
					IimageDownloadResult result = new IimageDownloadResult() {

						@Override
						public void onImageDownloadComplete(ImageView imageView, int position) {

							int firstVisible = CustomListAdapter.this.listview.getFirstVisiblePosition();
							int lastVisible = CustomListAdapter.this.listview.getLastVisiblePosition();
							//
							Log.d("TAG", "Img Result Pos: " + position + " F:" + firstVisible + " L:" + lastVisible);

							if (firstVisible <= position && position <= lastVisible) {
								Bitmap bitmap = bitmapCache.get(position);
								if (bitmap != null) {
									imageView.setImageBitmap(bitmap);
								}
							}
						}
					};

					Log.d("TAG", "Img Request Pos: " + position);
					new ImageDownloaderTask(holder.imgViewIcon, position, bitmapCache, result).execute(newsItem.getImageUrl());
				}
			}
		}

		return convertView;
	}

	interface IimageDownloadResult {
		public void onImageDownloadComplete(ImageView imageView, int position);
	}

	static class ViewHolder {
		TextView txtViewTitle;
		ImageView imgViewIcon;
	}
}
