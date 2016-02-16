package com.kou.gettyimageviewer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class MainActivity extends Activity {

	private RecyclerView recyclerView;
	private GettyImageAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// http://stackoverflow.com/questions/24449344/using-android-support-v7-widget-cardview-in-my-project-eclipse
		recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

	}

	@Override
	protected void onResume() {
		super.onResume();

		DownloadHttpAsyncTask task = new DownloadHttpAsyncTask();
		task.execute("http://www.gettyimagesgallery.com/collections/archive/slim-aarons.aspx");

		recyclerView.setLayoutManager(new LinearLayoutManager(this));
		recyclerView.setItemAnimator(new DefaultItemAnimator());

	}

	// http://stackoverflow.com/questions/22170470/how-to-get-a-web-page-content-in-android
	public static String getResponseFromUrl(String url) throws ClientProtocolException, IOException {
		HttpClient httpclient = new DefaultHttpClient(); // Create HTTP Client
		HttpGet httpget = new HttpGet(url); // Set the action you want to do
		HttpResponse response = httpclient.execute(httpget); // Executeit

		// http://stackoverflow.com/questions/12948284/how-to-extract-specific-div-tags-from-get-response
		String content = EntityUtils.toString(response.getEntity());
		Document doc = Jsoup.parse(content);
		Elements ele = doc.select("div.gallery-wrap");

		// HttpEntity entity = response.getEntity();
		// InputStream is = entity.getContent(); // Create an InputStream with the response
		// BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
		// StringBuilder sb = new StringBuilder();
		// String line = null;
		// while ((line = reader.readLine()) != null)
		// sb.append(line);
		//
		// String resString = sb.toString();
		//
		// is.close();
		return "abc";
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

			// http://stackoverflow.com/questions/12948284/how-to-extract-specific-div-tags-from-get-response

			ArrayList<ItemData> itemsData = new ArrayList<ItemData>();
			itemsData.add(new ItemData("Help", R.drawable.ic_launcher));
			itemsData.add(new ItemData("Favorite", R.drawable.ic_launcher));
			itemsData.add(new ItemData("Like", R.drawable.ic_launcher));
			itemsData.add(new ItemData("Rating", R.drawable.ic_launcher));

			adapter = new GettyImageAdapter(itemsData);
			recyclerView.setAdapter(adapter);

		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

	}
}