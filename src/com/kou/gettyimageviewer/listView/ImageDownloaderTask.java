package com.kou.gettyimageviewer.listView;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpStatus;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import com.kou.gettyimageviewer.R;
import com.kou.gettyimageviewer.listView.CustomListAdapter.IimageDownloadResult;

/**
 * Originally from Nilanchal <br/>
 * Bitmap cache added
 */
class ImageDownloaderTask extends AsyncTask<String, Void, Bitmap> {

	private final WeakReference<ImageView> imageViewReference;
	private int position = -1;
	private LruCache<Integer, Bitmap> bitmapCache;
	private IimageDownloadResult result;

	public ImageDownloaderTask(ImageView imageView, int position, LruCache<Integer, Bitmap> bitmapCache, IimageDownloadResult result) {
		imageViewReference = new WeakReference<ImageView>(imageView);
		this.position = position;
		this.bitmapCache = bitmapCache;
		this.result = result;
	}

	@Override
	protected Bitmap doInBackground(String... params) {
		return downloadBitmap(params[0]);
	}

	@Override
	protected void onPostExecute(Bitmap bitmap) {
		if (isCancelled()) {
			bitmap = null;
		}

		bitmapCache.put(position, bitmap);

		if (imageViewReference != null) {
			ImageView imageView = imageViewReference.get();
			if (imageView != null && bitmap != null) {
				result.onImageDownloadComplete(imageView, position);
			}
		}
	}

	private Bitmap downloadBitmap(String url) {
		HttpURLConnection urlConnection = null;
		try {
			URL uri = new URL(url);
			urlConnection = (HttpURLConnection) uri.openConnection();

			int statusCode = urlConnection.getResponseCode();
			if (statusCode != HttpStatus.SC_OK) {
				return null;
			}

			InputStream inputStream = urlConnection.getInputStream();
			if (inputStream != null) {
				Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
				return bitmap;
			}
		} catch (Exception e) {
			urlConnection.disconnect();
			Log.w("ImageDownloader", "Error downloading image from " + url);
		} finally {
			if (urlConnection != null) {
				urlConnection.disconnect();
			}
		}
		return null;
	}
}