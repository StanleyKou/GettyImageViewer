package com.kou.gettyimageviewer.listView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

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
import android.util.Log;
import android.widget.ListView;

import com.kou.gettyimageviewer.R;
import com.kou.gettyimageviewer.model.ItemData;
import com.squareup.picasso.Picasso;

public class ListViewMainActivity extends Activity {

	private ListView listView;
	private CustomListAdapter adapter;

	private HashSet<Integer> imageReqSet = new HashSet<Integer>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_listview_main);
		listView = (ListView) findViewById(R.id.listView);

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

	// TODO: ���⿡�� �ϳ��� �������� �߰��ϴ� ���� ������ �ʿ䰡 ����.
	// http://stackoverflow.com/questions/22170470/how-to-get-a-web-page-content-in-android
	public static String getResponseFromUrl(String url) throws ClientProtocolException, IOException {
		HttpClient httpclient = new DefaultHttpClient(); // Create HTTP Client
		HttpGet httpget = new HttpGet(url); // Set the action you want to do
		HttpResponse response = httpclient.execute(httpget); // Executeit
		String content = EntityUtils.toString(response.getEntity());
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

			Document doc = Jsoup.parse(result);
			// http://stackoverflow.com/questions/12948284/how-to-extract-specific-div-tags-from-get-response
			// http://jsoup.org/cookbook/extracting-data/dom-navigation
			Elements elements = doc.select("div.gallery-item-group");

			ArrayList<ItemData> itemsData = new ArrayList<ItemData>();

			for (Element e : elements) {
				Log.d("TAG", "Element: " + e.nodeName());

				// http://stackoverflow.com/questions/4875064/jsoup-how-to-get-an-images-absolute-url
				// http://stackoverflow.com/questions/10457415/extract-image-src-using-jsoup
				// http://stackoverflow.com/questions/28669496/jsoup-extracting-innertext-from-anchor-tag
				Element image = e.select("img").first();
				String imgURL = image.attr("src");

				Element captionParent = e.select("p").first();
				String caption = captionParent.text();

				ItemData data = new ItemData(caption, imgURL);
				itemsData.add(data);
			}

			adapter = new CustomListAdapter(ListViewMainActivity.this, itemsData);
			listView.setAdapter(adapter);

		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

	}
}