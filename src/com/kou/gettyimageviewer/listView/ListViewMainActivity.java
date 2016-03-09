package com.kou.gettyimageviewer.listView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;

import com.kou.gettyimageviewer.R;
import com.kou.gettyimageviewer.model.ItemData;
import com.kou.gettyimageviewer.util.LogWrapper;

public class ListViewMainActivity extends Activity {
	private static final String TAG = ListViewMainActivity.class.getSimpleName();
	private ListView listView;
	private CustomListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_listview_main);
		listView = (ListView) findViewById(R.id.listView);
		adapter = new CustomListAdapter(ListViewMainActivity.this, listView);
		listView.setAdapter(adapter);

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

	}

	public class DownloadHttpAsyncTask extends AsyncTask<String, Void, Void> {

		String url;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(String... params) {
			if (params != null) {
				for (String s : params) {
					url = s;
				}
			}
			try {
				addData(url);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				LogWrapper.e(TAG, "DownloadHttpAsyncTask ClientProtocolException");
			} catch (IOException e) {
				e.printStackTrace();
				LogWrapper.e(TAG, "DownloadHttpAsyncTask IOException");
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
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