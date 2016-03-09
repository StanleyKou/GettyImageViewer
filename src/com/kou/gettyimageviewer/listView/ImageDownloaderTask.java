package com.kou.gettyimageviewer.listView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpStatus;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.AsyncTask;
import android.util.LruCache;
import android.widget.ImageView;

import com.kou.gettyimageviewer.listView.CustomListAdapter.IimageDownloadResult;
import com.kou.gettyimageviewer.util.LogWrapper;
import com.kou.gettyimageviewer.util.Util;

/**
 * Originally from Nilanchal <br/>
 * Bitmap cache added
 */
class ImageDownloaderTask extends AsyncTask<String, Void, Bitmap> {

	private final WeakReference<ImageView> imageViewReference;
	private Context context;
	private int position = -1;
	private LruCache<Integer, Bitmap> bitmapCache;
	private IimageDownloadResult result;

	public ImageDownloaderTask(Context context, ImageView imageView, int position, LruCache<Integer, Bitmap> bitmapCache, IimageDownloadResult result) {
		imageViewReference = new WeakReference<ImageView>(imageView);
		this.context = context;
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
				// Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
				Bitmap bitmap = createScaledBitmapFromStream(inputStream, 128, 128);
				return bitmap;
			}
		} catch (Exception e) {
			urlConnection.disconnect();
			LogWrapper.w("ImageDownloader", "Error downloading image from " + url);
		} finally {
			if (urlConnection != null) {
				urlConnection.disconnect();
			}
		}
		return null;
	}

	// http://stackoverflow.com/questions/7051025/how-do-i-scale-a-streaming-bitmap-in-place-without-reading-the-whole-image-first
	protected Bitmap createScaledBitmapFromStream(InputStream s, int minimumDesiredBitmapWidth, int minimumDesiredBitmapHeight) {

		final BufferedInputStream is = new BufferedInputStream(s, 32 * 1024);
		try {
			final Options decodeBitmapOptions = new Options();
			// For further memory savings, you may want to consider using this option
			decodeBitmapOptions.inPreferredConfig = Config.RGB_565; // Uses 2-bytes instead of default 4 per pixel

			if (minimumDesiredBitmapWidth > 0 && minimumDesiredBitmapHeight > 0) {
				final Options decodeBoundsOptions = new Options();
				decodeBoundsOptions.inJustDecodeBounds = true;
				is.mark(32 * 1024); // 32k is probably overkill, but 8k is insufficient for some jpgs
				BitmapFactory.decodeStream(is, null, decodeBoundsOptions);
				is.reset();

				final int originalWidth = decodeBoundsOptions.outWidth;
				final int originalHeight = decodeBoundsOptions.outHeight;

				// inSampleSize prefers multiples of 2, but we prefer to prioritize memory savings
				decodeBitmapOptions.inSampleSize = Math.max(1, Math.min(originalWidth / minimumDesiredBitmapWidth, originalHeight / minimumDesiredBitmapHeight));

			}

			return BitmapFactory.decodeStream(is, null, decodeBitmapOptions);

		} catch (IOException e) {
			throw new RuntimeException(e); // this shouldn't happen
		} finally {
			try {
				is.close();
			} catch (IOException ignored) {
			}
		}

	}
}