package com.kou.gettyimageviewer.listView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;

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
import android.util.Log;
import android.widget.ListView;

import com.kou.gettyimageviewer.R;
import com.kou.gettyimageviewer.model.ItemData;

public class ListViewMainActivity extends Activity {

	private final static String TAG = ListViewMainActivity.class.getSimpleName();

	private ListView listView;
	private CustomListAdapter adapter;

	private HashSet<Integer> imageReqSet = new HashSet<Integer>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_listview_main);
		listView = (ListView) findViewById(R.id.listView);
		adapter = new CustomListAdapter(ListViewMainActivity.this);
		listView.setAdapter(adapter);
	}

	@Override
	protected void onResume() {
		super.onResume();

		DownloadHttpAsyncTask task = new DownloadHttpAsyncTask();
		task.execute("http://www.gettyimagesgallery.com/collections/archive/slim-aarons.aspx");

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

	}

	boolean isItemStarted = false;

	// 	Do not wait all data
	public String getResponseFromUrl(String url) throws ClientProtocolException, IOException {
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(url);
		HttpResponse response = httpclient.execute(httpget);

		// String content = EntityUtils.toString(response.getEntity()); // first try: download all

		HttpEntity entity = response.getEntity();
		InputStream is = entity.getContent(); // Create an InputStream with the response
		BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
		StringBuilder sb = new StringBuilder();
		String line = null;

		StringBuilder sbItem = null;

		while ((line = reader.readLine()) != null) {
			// sb.append(line);
			Log.d(TAG, line);

			if (line.contains("<!-- REPEATER ENDS -->")) {
				isItemStarted = false; // set flag, and quit

				// <div class="gallery-item-group exitemrepeater">
				// <a href="/Picture-Library/Image.aspx?id=2263"><img src="/Images/Thumbnails/1336/133610.jpg" class="picture"/></a>
				// <div class="gallery-item-caption">
				// <p><a href="/Picture-Library/Image.aspx?id=2263">Bacall And Bogart</a></p>
				// </div>
				// </div>

				Document doc = Jsoup.parse(sbItem.toString());
				Elements elements = doc.select("div.gallery-item-group");

				// ArrayList<ItemData> itemsData = new ArrayList<ItemData>();

				for (Element e : elements) {
					// Log.d(TAG, "Element: " + e.nodeName());
					Element image = e.select("img").first();
					String imgURL = image.attr("src");

					Element captionParent = e.select("p").first();
					String caption = captionParent.text();

					final ItemData data = new ItemData(caption, imgURL);
					// itemsData.add(data);

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

		String content = sb.toString();

		is.close();

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
			String resultSting = "";
			try {
				resultSting = getResponseFromUrl(url);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return resultSting;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

//			Document doc = Jsoup.parse(result);
//			Elements elements = doc.select("div.gallery-item-group");
//
//			// ArrayList<ItemData> itemsData = new ArrayList<ItemData>();
//
//			for (Element e : elements) {
//				// Log.d(TAG, "Element: " + e.nodeName());
//				Element image = e.select("img").first();
//				String imgURL = image.attr("src");
//
//				Element captionParent = e.select("p").first();
//				String caption = captionParent.text();
//
//				final ItemData data = new ItemData(caption, imgURL);
//				// itemsData.add(data);
//
//				runOnUiThread(new Runnable() {
//
//					@Override
//					public void run() {
//						adapter.addItem(data);
//					}
//				});
//			}
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

	}
}