package com.tele.udpplayer.utils;

import android.util.Log;

public class LogUtils {
	// 开关
	private static final boolean DEBUG = true;
	private static final String TAG = "udpplayer";

	public static void d(String text) {
		if (DEBUG) {
			Log.d(TAG, text);
		}
	}

	public static void d(String tag, String text) {
		if (DEBUG) {
			Log.d(tag, text);
		}
	}

	public static void i(String text) {
		if (DEBUG) {
			Log.i(TAG, text);
		}
	}

	public static void i(String tag, String text) {
		if (DEBUG) {
			Log.i(tag, text);
		}
	}

	public static void w(String text) {
		if (DEBUG) {
			Log.w(TAG, text);
		}
	}

	public static void w(String tag, String text) {
		if (DEBUG) {
			Log.w(tag, text);
		}
	}

	public static void e(String text) {
		if (DEBUG) {
			Log.e(TAG, text);
		}
	}

	public static void e(String tag, String text) {
		if (DEBUG) {
			Log.e(tag, text);
		}
	}
}
