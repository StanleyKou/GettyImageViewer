package com.kou.gettyimageviewer;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class GettyImageAdapter extends RecyclerView.Adapter<GettyImageAdapter.ViewHolder> {
	private ArrayList<ItemData> itemsData;
	private Picasso picasso;
	private com.squareup.picasso.LruCache picassoLruCache;

	public GettyImageAdapter(Context context, ArrayList<ItemData> itemsData) {
		this.itemsData = itemsData;

		picassoLruCache = new com.squareup.picasso.LruCache(context);

		picasso = new Picasso.Builder(context) //
				.memoryCache(picassoLruCache) //
				.build();

	}

	// Create new views (invoked by the layout manager)
	@Override
	public GettyImageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		// create a new view
		View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, null);

		// create ViewHolder
		ViewHolder viewHolder = new ViewHolder(itemLayoutView);
		return viewHolder;
	}

	// Replace the contents of a view (invoked by the layout manager)
	@Override
	public void onBindViewHolder(ViewHolder viewHolder, int position) {

		// - get data from your itemsData at this position
		// - replace the contents of the view with that itemsData

		// http://square.github.io/picasso/
		ItemData data = itemsData.get(position);

		viewHolder.txtViewTitle.setText(data.getTitle());

		// http://stackoverflow.com/questions/28426468/picasso-cannot-load-images-for-some-url-no-special-characters
		picasso.load(data.getImageUrl())//
				.config(Bitmap.Config.RGB_565)//
				.error(R.drawable.ic_launcher)//
				.fit().centerInside()//
				.into(viewHolder.imgViewIcon);

	}

	// inner class to hold a reference to each item of RecyclerView
	public static class ViewHolder extends RecyclerView.ViewHolder {

		public TextView txtViewTitle;
		public ImageView imgViewIcon;

		public ViewHolder(View itemLayoutView) {
			super(itemLayoutView);
			txtViewTitle = (TextView) itemLayoutView.findViewById(R.id.item_title);
			imgViewIcon = (ImageView) itemLayoutView.findViewById(R.id.item_icon);
		}
	}

	// Return the size of your itemsData (invoked by the layout manager)
	@Override
	public int getItemCount() {
		return itemsData.size();
	}

	public void clearCache() {
		picassoLruCache.clear();
	}
}