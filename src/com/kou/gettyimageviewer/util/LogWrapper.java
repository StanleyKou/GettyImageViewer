package com.kou.gettyimageviewer.util;

import android.util.Log;

import com.kou.gettyimageviewer.BuildConfig;

public class LogWrapper {
	private LogWrapper() {
	}

	public static final String TIME_TAG = "TIME";
	public static final String TAG = "GettyImageViewer";

	public static int logLevel = Log.ERROR;

	static {
		if (BuildConfig.DEBUG) {
			logLevel = Log.ERROR;
		} else {
			logLevel = Log.ASSERT;
		}
	}

	public static void setLogLevel(int logLevel) {
		if (logLevel <= Log.ERROR && logLevel <= 0) {
			LogWrapper.logLevel = logLevel;
		}
	}

	public static void e(String tag, String message, Throwable tr) {
		if (LogWrapper.logLevel <= Log.ERROR) {

			Log.e(tag, getMessageWithCaller(message), tr);
		}
	}

	public static void e(String tag, String message) {
		if (LogWrapper.logLevel <= Log.ERROR) {

			Log.e(tag, getMessageWithCaller(message));
		}
	}

	public static void w(String tag, String message) {
		if (LogWrapper.logLevel <= Log.WARN) {
			Log.w(tag, getMessageWithCaller(message));
		}
	}

	public static void i(String tag, String message, Throwable tr) {
		if (LogWrapper.logLevel <= Log.INFO) {
			Log.i(tag, getMessageWithCaller(message), tr);
		}
	}

	public static void i(String tag, String message) {
		if (LogWrapper.logLevel <= Log.INFO) {
			Log.i(tag, getMessageWithCaller(message));
		}
	}

	public static void d(String tag, String message) {
		if (LogWrapper.logLevel <= Log.DEBUG) {
			Log.d(tag, getMessageWithCaller(message));
		}
	}

	public static void d(String tag, String message, Object... params) {
		if (LogWrapper.logLevel <= Log.DEBUG) {
			if (params != null && params.length > 0) {
				message = String.format(message, params);
			}
			Log.d(tag, getMessageWithCaller(message));
		}
	}

	public static void v(String tag, String message) {
		if (LogWrapper.logLevel <= Log.VERBOSE) {
			Log.v(tag, getMessageWithCaller(message));
		}
	}

	private static String getMessageWithCaller(String message) {

		Exception exception = new Exception();

		if (exception.getStackTrace() != null && exception.getStackTrace().length >= 2) {
			StackTraceElement callerElement = exception.getStackTrace()[2];
			return new StringBuilder("(").append(callerElement.getFileName()).append(" ").append(callerElement.getLineNumber()).append(") ").append(message).toString();
		} else {
			return message;
		}
	}
}
