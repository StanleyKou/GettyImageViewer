package com.kou.gettyimageviewer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.kou.gettyimageviewer.listView.ListViewMainActivity;
import com.kou.gettyimageviewer.recyclerView.RecyclerViewMainActivity;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	public void startRecyclerView(View view) {
		Intent intent = new Intent(this, RecyclerViewMainActivity.class);
		startActivity(intent);
	}

	public void startListView(View view) {
		Intent intent = new Intent(this, ListViewMainActivity.class);
		startActivity(intent);
	}

}