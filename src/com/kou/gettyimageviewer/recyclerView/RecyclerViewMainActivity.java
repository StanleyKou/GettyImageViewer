package com.kou.gettyimageviewer.recyclerView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;

import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.kou.gettyimageviewer.R;
import com.kou.gettyimageviewer.model.ItemData;
import com.kou.gettyimageviewer.util.LogWrapper;
import com.squareup.picasso.Picasso;

public class RecyclerViewMainActivity extends Activity {

	private RecyclerView recyclerView;
	private GettyImageAdapter adapter;
	private LinearLayoutManager linearLayoutManager;
	private RecyclerViewPositionHelper recyclerViewPositionHelper;

	private Picasso picasso;
	private HashSet<Integer> imageReqSet = new HashSet<Integer>();
	private com.squareup.picasso.LruCache picassoLruCache;
	private RecyclerView.OnScrollListener onScrollListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recyclerview_main);

		// http://stackoverflow.com/questions/24449344/using-android-support-v7-widget-cardview-in-my-project-eclipse
		recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
		linearLayoutManager = new LinearLayoutManager(this);
		recyclerView.setLayoutManager(linearLayoutManager);
		recyclerView.setItemAnimator(new SlideInLeftAnimator());

		onScrollListener = new RecyclerView.OnScrollListener() {

			@Override
			public void onScrollStateChanged(RecyclerView recyclerview, int scrollState) {

				// if (scrollState == RecyclerView.SCROLL_STATE_IDLE) {
				// LogWrapper.d("TAG", "onScrollStateChanged  SCROLL_STATE_IDLE");
				// for (Integer i : imageReqSet) {
				// picasso.resumeTag(i);
				// }
				//
				// } else {
				// for (Integer i : imageReqSet) {
				// picasso.pauseTag(i);
				// }
				// }
			}

			@Override
			public void onScrolled(RecyclerView recyclerview, int dx, int dy) {

				int first = recyclerViewPositionHelper.findFirstVisibleItemPosition();
				int last = recyclerViewPositionHelper.findLastVisibleItemPosition();

				LogWrapper.d("TAG", "onScrolled  " + dx + " " + dy + " First: " + first + " Last: " + last);

				for (Integer i : imageReqSet) {
					if (first > i || i < last) {
						picasso.cancelTag(i);
					}
				}
			}
		};

		recyclerView.addOnScrollListener(onScrollListener);

		recyclerViewPositionHelper = RecyclerViewPositionHelper.createHelper(recyclerView);

		picassoLruCache = new com.squareup.picasso.LruCache(this);

		picasso = new Picasso.Builder(this) //
				.memoryCache(picassoLruCache) //
				.build();

		adapter = new GettyImageAdapter(RecyclerViewMainActivity.this, picasso, imageReqSet);
		recyclerView.setAdapter(adapter);

	}

	@Override
	protected void onResume() {
		super.onResume();

		DownloadHttpAsyncTask task = new DownloadHttpAsyncTask();
		// task.execute("http://www.gettyimagesgallery.com/collections/archive/baron.aspx");
		task.execute("http://www.gettyimagesgallery.com/collections/archive/slim-aarons.aspx");

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		picassoLruCache.clear();

	}

	// Wait all data
	public static String getResponseFromUrl(String url) throws ClientProtocolException, IOException {
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(url);
		HttpResponse response = httpclient.execute(httpget);
		String content = EntityUtils.toString(response.getEntity());
		return content;
	}

	public class DownloadHttpAsyncTask extends AsyncTask<String, Void, String> {

		String url;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			if (params != null) {
				for (String s : params) {
					url = s;
				}
			}
			// String resultSting = "";
			try {
				// resultSting = getResponseFromUrl(url);
				addData(url);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			// return resultSting;
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			// Document doc = Jsoup.parse(result);
			// // http://stackoverflow.com/questions/12948284/how-to-extract-specific-div-tags-from-get-response
			// // http://jsoup.org/cookbook/extracting-data/dom-navigation
			// Elements elements = doc.select("div.gallery-item-group");
			//
			// ArrayList<ItemData> itemsData = new ArrayList<ItemData>();
			//
			// for (Element e : elements) {
			// LogWrapper.d("TAG", "Element: " + e.nodeName());
			//
			// // http://stackoverflow.com/questions/4875064/jsoup-how-to-get-an-images-absolute-url
			// // http://stackoverflow.com/questions/10457415/extract-image-src-using-jsoup
			// // http://stackoverflow.com/questions/28669496/jsoup-extracting-innertext-from-anchor-tag
			// Element image = e.select("img").first();
			// String imgURL = image.attr("src");
			//
			// Element captionParent = e.select("p").first();
			// String caption = captionParent.text();
			//
			// ItemData data = new ItemData(caption, imgURL);
			// itemsData.add(data);
			// }
			//
			// // for (ItemData d : itemsData) {
			// // LogWrapper.d("DDD", d.getTitle());
			// // }
			// //
			// // for (ItemData d : itemsData) {
			// // LogWrapper.d("EEE", d.getImageUrl());
			// // }
			//
			// adapter = new GettyImageAdapter(RecyclerViewMainActivity.this, picasso, imageReqSet, itemsData);
			// recyclerView.setAdapter(adapter);

		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}
	}

	boolean isItemStarted = false;

	// Do not wait all data
	public void addData(String url) throws ClientProtocolException, IOException {
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(url);
		HttpResponse response = httpclient.execute(httpget);

		// String content = EntityUtils.toString(response.getEntity()); // first try: download all

		HttpEntity entity = response.getEntity();
		InputStream is = entity.getContent();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
		String line = null;

		StringBuilder sbItem = null;

		while ((line = reader.readLine()) != null) {
			// LogWrapper.d(TAG, line);

			if (line.contains("<!-- REPEATER ENDS -->")) {
				isItemStarted = false; // set flag, and quit

				Document doc = Jsoup.parse(sbItem.toString());
				Elements elements = doc.select("div.gallery-item-group");

				for (Element e : elements) {
					// LogWrapper.d(TAG, "Element: " + e.nodeName());
					Element image = e.select("img").first();
					String imgURL = image.attr("src");

					Element captionParent = e.select("p").first();
					String caption = captionParent.text();

					final ItemData data = new ItemData(caption, imgURL);

					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							adapter.addItem(data);
						}
					});
				}
				continue;
			}

			if (line.contains("<!-- REPEATER -->")) {
				isItemStarted = true; // set flag, and quit
				sbItem = new StringBuilder();
				continue;
			}

			if (isItemStarted == true) {
				sbItem.append(line);
			}
		}

		is.close();
	}
}