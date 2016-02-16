package com.kou.gettyimageviewer;

import java.util.ArrayList;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class GettyImageAdapter extends RecyclerView.Adapter<GettyImageAdapter.ViewHolder> {
	private ArrayList<ItemData> itemsData;

	public GettyImageAdapter(ArrayList<ItemData> itemsData) {
		this.itemsData = itemsData;
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
		viewHolder.txtViewTitle.setText(itemsData.get(position).getTitle());
		viewHolder.imgViewIcon.setImageResource(itemsData.get(position).getImageUrl());

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
}