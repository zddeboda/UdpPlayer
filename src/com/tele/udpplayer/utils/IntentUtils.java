package com.tele.udpplayer.utils;

import android.content.Context;
import android.content.Intent;

public class IntentUtils {

	public static void startActivity(Context mContext, Class cls) {
		mContext.startActivity(new Intent(mContext, cls));
	}

}