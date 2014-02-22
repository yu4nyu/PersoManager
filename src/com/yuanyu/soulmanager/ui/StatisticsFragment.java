package com.yuanyu.soulmanager.ui;

import java.util.Calendar;
import java.util.Map;

import com.google.common.collect.Maps;
import com.yuanyu.soulmanager.R;
import com.yuanyu.soulmanager.data.CacheDb;
import com.yuanyu.soulmanager.data.FinishedTasksTable;
import com.yuanyu.soulmanager.data.RecordedEventsTable;
import com.yuanyu.soulmanager.ui.utils.FormattedTimeUtils;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class StatisticsFragment extends Fragment {
	
	TextView mContentText;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.statistics_fragment, container, false);
		
		mContentText = (TextView) v.findViewById(R.id.statistics_fragment_content);
		
		//String content = getSleepStatistics();
		String content = getTasksStatistics();
		mContentText.setText(content);
		
		return v;
	}
	
	private String getSleepStatistics() {
		String[] columns = new String[] { RecordedEventsTable.Columns.ID,
				RecordedEventsTable.Columns.NAME, RecordedEventsTable.Columns.TIME };
		String where = RecordedEventsTable.Columns.ID + " = 2 OR " +
				RecordedEventsTable.Columns.ID + " = 3";
		String orderBy = RecordedEventsTable.Columns.TIME;
		Cursor cursor = CacheDb.instance(getActivity()).getDbBlocking()
				.query(RecordedEventsTable.TABLE_NAME, columns, where, null, null, null, orderBy, null);

		cursor.moveToFirst();
		String result = "";
		long average = 0;
		int id;
		long getup = 0, sleep = 0;
		Calendar calendar = Calendar.getInstance();
        for(int i = 0; i < cursor.getCount(); i++){
        	id = cursor.getInt(cursor.getColumnIndex(RecordedEventsTable.Columns.ID));
        	if(id == 2) {
        		sleep = cursor.getLong(cursor.getColumnIndex(RecordedEventsTable.Columns.TIME));
        		calendar.setTimeInMillis(sleep);
        		if(calendar.get(Calendar.HOUR_OF_DAY) > 6 && calendar.get(Calendar.HOUR_OF_DAY) < 21) {
        			continue; // 忽略白天的睡觉
        		}
        		
        		result += "睡觉：" + calendar.get(Calendar.HOUR_OF_DAY) + "点" +
        				calendar.get(Calendar.MINUTE) + "分\n";
        	}
        	else if(id == 3) {
        		getup = cursor.getLong(cursor.getColumnIndex(RecordedEventsTable.Columns.TIME));
        		calendar.setTimeInMillis(getup);
        		if(calendar.get(Calendar.HOUR_OF_DAY) < 5 || calendar.get(Calendar.HOUR_OF_DAY) > 16) {
        			continue; // 忽略白天的睡觉
        		}
        		
        		result += "起床：" + calendar.get(Calendar.HOUR_OF_DAY) + "点" +
        				calendar.get(Calendar.MINUTE) + "分\n";
        	}
        	average += (getup - sleep);
        	cursor.moveToNext();
        }
        average /= (cursor.getCount()/2);
        cursor.close();
        
        result += "平均睡眠时间：" + average/1000/60/60 + "小时" + average/1000/60%60 + "分钟\n";
        return result;
	}
	
	private String getTasksStatistics() {
		long start = FormattedTimeUtils.getFirstMomentOfMonth();
		long end = FormattedTimeUtils.getLastMomentOfMonth();
		
		String[] columns = new String[] { FinishedTasksTable.Columns.TYPE,
				FinishedTasksTable.Columns.NAME, FinishedTasksTable.Columns.TIME };
		String where = FinishedTasksTable.Columns.TIME + " > " + start + " AND " +
				FinishedTasksTable.Columns.TIME + " < " + end;
		String orderBy = FinishedTasksTable.Columns.NAME;
		Cursor cursor = CacheDb.instance(getActivity()).getDbBlocking()
				.query(FinishedTasksTable.TABLE_NAME, columns, where, null, null, null, orderBy, null);
		
		Map<String, Integer> projects = Maps.newHashMap();
		Map<String, Integer> tasks = Maps.newHashMap();
		cursor.moveToFirst();
		int type;
		Integer count;
		String name;
		for(int i = 0; i < cursor.getCount(); i++) {
			type = cursor.getInt(cursor.getColumnIndex(FinishedTasksTable.Columns.TYPE));
			name = cursor.getString(cursor.getColumnIndex(FinishedTasksTable.Columns.NAME));
			switch(type) {
			case FinishedTasksTable.TYPE_PROJECT:
				count = projects.get(name);
				if(count == null) {
					projects.put(name, 1);
				}
				else {
					projects.put(name, count + 1);
				}
				break;
			case FinishedTasksTable.TYPE_TASK:
				count = tasks.get(name);
				if(count == null) {
					tasks.put(name, 1);
				}
				else {
					tasks.put(name, count + 1);
				}
				break;
			}
			cursor.moveToNext();
		}
		
		String result = "\n本月完成的项目：\n";
		if(projects.size() == 0) {
			result += "None\n";
		}
		for(Map.Entry<String, Integer> entry : projects.entrySet()) {
			result += entry.getKey() + " " + entry.getValue() + "次\n";
		}
		result += "\n本月完成的任务: \n";
		if(tasks.size() == 0) {
			result += "None\n";
		}
		for(Map.Entry<String, Integer> entry : tasks.entrySet()) {
			result += entry.getKey() + " " + entry.getValue() + "次\n";
		}
		
		return result;
	}
}
