package com.tele.udpplayer.utils;

import java.sql.Date;
import java.text.SimpleDateFormat;

public class TimeUtils {
	
	//格式化时间
	public static String getTime(){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm yyyy-MM-dd");
		Date date = new Date(System.currentTimeMillis());
		String timeStr = simpleDateFormat.format(date);
		return timeStr;
	}

}
