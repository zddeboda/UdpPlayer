package com.tele.udpplayer.utils;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;


public class BroadcastUtils {
	// 发送普通广播
	public static void sendBro(Context mContext, String action) {
		mContext.sendBroadcast(new Intent(action));
	}

	// 发送单数据广播
	public static void sendBro(Context mContext, String action, String key,
			String text) {
		Intent intent = new Intent(action);
		intent.putExtra(key, text);
		mContext.sendBroadcast(intent);
	}

	// 发送单个int数据
	public static void sendBroInt(Context mContext, String action, String key,
			int value) {
		Intent intent = new Intent(action);
		intent.putExtra(key, value);
		mContext.sendBroadcast(intent);
	}
		

}
