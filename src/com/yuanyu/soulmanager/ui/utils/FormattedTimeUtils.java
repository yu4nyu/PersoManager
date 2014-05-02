package com.yuanyu.soulmanager.ui.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class FormattedTimeUtils {
	
	static public String get(long time) {
		if(time == 0) return "";
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy MMM d H:m", Locale.getDefault());
		// TODO, check if Locale.getDefault() is safe
		Date date = new Date();
		
		date.setTime(time);
    	return dateFormat.format(date);
	}
	
	static public long getFirstMomentOfMonth() {
		Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTimeInMillis();
	}
	
	static public long getLastMomentOfMonth() {
		Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
		int lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		calendar.set(Calendar.DAY_OF_MONTH, lastDay);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		return calendar.getTimeInMillis();
	}
	
	/**
	 * @param year
	 * @param month 0-base index
	 * @return
	 */
	static public long getFirstMomentOfMonth(int year, int month) {
		Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTimeInMillis();
	}
	
	/**
	 * @param year
	 * @param month 0-based index
	 * @return
	 */
	static public long getLastMomentOfMonth(int year, int month) {
		Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month);
		int lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		calendar.set(Calendar.DAY_OF_MONTH, lastDay);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		return calendar.getTimeInMillis();
	}
}
