package com.kou.gettyimageviewer.recyclerView;

import java.util.ArrayList;
import java.util.HashSet;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kou.gettyimageviewer.R;
import com.kou.gettyimageviewer.model.ItemData;
import com.kou.gettyimageviewer.util.LogWrapper;
import com.squareup.picasso.Picasso;

public class GettyImageAdapter extends RecyclerView.Adapter<GettyImageAdapter.ViewHolder> {

	private final String TAG = GettyImageAdapter.class.getSimpleName();

	private ArrayList<ItemData> listData = new ArrayList<ItemData>();
	private Picasso picasso;
	private Context context;
	HashSet<Integer> imageReqSet;

	public GettyImageAdapter(Context context, Picasso picasso, HashSet<Integer> imageReqSet) {
		this.context = context;
		this.picasso = picasso;
		this.imageReqSet = imageReqSet;

	}

	public void addItem(ItemData data) {
		listData.add(data);
		notifyDataSetChanged();
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
	public void onBindViewHolder(ViewHolder viewHolder, final int position) {

		// - get data from your itemsData at this position
		// - replace the contents of the view with that itemsData

		// http://square.github.io/picasso/
		ItemData data = listData.get(position);
		viewHolder.txtViewTitle.setText(data.getTitle() + "");

		if (viewHolder.imgViewIcon.getTop() < 0) {

		}

		// For listview: picasso.cancelRequest(viewHolder.imgViewIcon);

		// http://stackoverflow.com/questions/28426468/picasso-cannot-load-images-for-some-url-no-special-characters
		picasso.load(data.getImageUrl())//
				.config(Bitmap.Config.RGB_565)//
				.error(R.drawable.ic_launcher)//
				.tag(position)//
				.fit().centerInside()//
				.into(viewHolder.imgViewIcon, new com.squareup.picasso.Callback() {
					@Override
					public void onSuccess() {
						imageReqSet.remove(position);
					}

					@Override
					public void onError() {
						LogWrapper.e(TAG, "onError image loading: " + position);
					}
				});

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
		return listData.size();
	}

}