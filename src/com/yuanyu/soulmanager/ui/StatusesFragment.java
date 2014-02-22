package com.yuanyu.soulmanager.ui;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yuanyu.soulmanager.R;
import com.yuanyu.soulmanager.data.CacheDb;
import com.yuanyu.soulmanager.data.FinishedTasksTable;
import com.yuanyu.soulmanager.model.LevelManager;
import com.yuanyu.soulmanager.model.SoulManager;
import com.yuanyu.soulmanager.model.StatusManager;
import com.yuanyu.soulmanager.ui.utils.FormattedTimeUtils;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class StatusesFragment extends Fragment implements SoulManager.OnAttributesChangedListener,
	SoulManager.OnNewTaskFinishedListener, SoulManager.OnUpdateFromInternetListener {
	
	private static final int LIST_MAX_NUMBER = 10;
	
	private static final int NAME_MAP_KEY = 0;
	private static final int TIME_MAP_KEY = 1;
	
	private TextView mLevelText;
	private TextView mProgressText;
	private ProgressBar mProgress;
	
	private TextView mForceText;
	private TextView mIntelligenceText;
	private TextView mVolitionText;
	private TextView mMoneyText;
	private TextView mExperienceText;
	private TextView mHappyText;
	
	private ListView mList;
	private List<Map<Integer, String>> mListData; // 0 --> Name, 1 --> Time
	private LastFinishedTasksAdapter mAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		
		mListData = Lists.newArrayList();
		mAdapter = new LastFinishedTasksAdapter(getActivity(), mListData);
		
		new LoadFromDatabaseTask().execute();
		
		SoulManager.getInstance().addOnAttributesChangedListener(this);
		SoulManager.getInstance().addOnNewTaskFinishedListener(this);
		SoulManager.getInstance().addOnUpdateFromInternetListener(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.status_fragment, container, false);
		
		mLevelText = (TextView) v.findViewById(R.id.status_fragment_level_text);
		mProgressText = (TextView) v.findViewById(R.id.status_fragment_progress_text);
		mProgress = (ProgressBar) v.findViewById(R.id.status_fragment_progress);
		
		mForceText = (TextView) v.findViewById(R.id.status_fragment_force_text);
		mIntelligenceText = (TextView) v.findViewById(R.id.status_fragment_intelligence_text);
		mVolitionText = (TextView) v.findViewById(R.id.status_fragment_volition_text);
		mMoneyText = (TextView) v.findViewById(R.id.status_fragment_money_text);
		mExperienceText = (TextView) v.findViewById(R.id.status_fragment_experience_text);
		mHappyText = (TextView) v.findViewById(R.id.status_fragment_happy_text);
				
		mList = (ListView) v.findViewById(R.id.status_fragment_last_tasks_list);
		mList.setAdapter(mAdapter);
		TextView emptyView = (TextView) v.findViewById(R.id.status_fragment_last_tasks_empty_text);
		mList.setEmptyView(emptyView);
		
		updateAttributes();
		
		return v;
	}
	
	private void updateAttributes() {
		StatusManager statusManager = StatusManager.INSTANCE;
		
		final LevelManager levelManager = LevelManager.INSTANCE;
		int experience = statusManager.getExperience();
		final int level = levelManager.getLevel(experience);
		final int progress = levelManager.getProgress(level, experience);
		mLevelText.setText("" + level);
		mProgressText.setText("" + progress + "/" + levelManager.getLevelGoal(level));
		
		// Must post a new runnable, otherwise setProgress will do not work.(caused by the bug of ProgressBar)
		mProgress.post(new Runnable(){
			@Override
			public void run() {
				mProgress.setMax(levelManager.getLevelGoal(level));
				mProgress.setProgress(progress);
			}
		});
		
		mForceText.setText("" + statusManager.getForce());
		mIntelligenceText.setText("" + statusManager.getIntelligence());
		mVolitionText.setText("" + statusManager.getVolition());
		mMoneyText.setText("" + statusManager.getMoney());
		mExperienceText.setText("" + statusManager.getExperience());
		mHappyText.setText("" + statusManager.getHappy());
	}
	
	private class LastFinishedTasksAdapter extends BaseAdapter {

		private List<Map<Integer, String>> mData;
		private LayoutInflater mInflater;
		
		public LastFinishedTasksAdapter(Context context, List<Map<Integer, String>> data) {
			mData = data;
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			if(mData.size() > LIST_MAX_NUMBER) {
				return LIST_MAX_NUMBER;
			}
				
			return mData.size();
		}

		@Override
		public Map<Integer, String> getItem(int position) {
			return mData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if(convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.status_last_tasks_list_item, null);
				holder.name = (TextView)convertView.findViewById(R.id.status_last_tasks_list_item_name);
				holder.time = (TextView)convertView.findViewById(R.id.status_last_tasks_list_item_time);
				convertView.setTag(holder);
			}
			else {
				holder = (ViewHolder)convertView.getTag();
			}
			
			holder.name.setText(mData.get(position).get(NAME_MAP_KEY));
			holder.time.setText(mData.get(position).get(TIME_MAP_KEY));
			
			return convertView;
		}
		
		class ViewHolder {
			public TextView name;
			public TextView time;
		}
	}
	
	private class LoadFromDatabaseTask extends AsyncTask<Void, Integer, List<Map<Integer, String>>> {

		@Override
		protected List<Map<Integer, String>> doInBackground(Void... params) {

			String[] columns = new String[] { FinishedTasksTable.Columns._ID,
					FinishedTasksTable.Columns.NAME, FinishedTasksTable.Columns.TIME };      
			String limit = "" + LIST_MAX_NUMBER;
			String orderBy = "ROWID DESC";
			Cursor cursor = CacheDb.instance(getActivity()).getDbBlocking()
					.query(FinishedTasksTable.TABLE_NAME, columns, null, null, null, null, orderBy, limit);
			
			List<Map<Integer, String>> list = Lists.newArrayList();
			cursor.moveToFirst();
            for(int i = 0; i < cursor.getCount(); i++){
            	String name = cursor.getString(1);
            	String time = FormattedTimeUtils.get(cursor.getLong(2));
            	
                Map<Integer, String> map = Maps.newHashMap();
                map.put(NAME_MAP_KEY, name);
                map.put(TIME_MAP_KEY, time);
                list.add(map);
                cursor.moveToNext();
            }
            cursor.close();
			
			return list;
		}

		@Override
		protected void onPostExecute(List<Map<Integer, String>> result) {
			mListData.clear();
			for(int i = 0; i < result.size(); i++) {
				mListData.add(result.get(i));
			}
			//mListData = result;
			mAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onNewTaskFinished(String time, String name) {
		Map<Integer, String> map = Maps.newHashMap();
		map.put(NAME_MAP_KEY, name);
		map.put(TIME_MAP_KEY, time);
		mListData.add(0, map);
		if(mListData.size() > LIST_MAX_NUMBER) {
			mListData.remove(mListData.size() - 1); // Remove last item
		}
		
		//SoulManager.getInstance().requestBackup(getActivity());
	}

	@Override
	public void onAttributesChanged() {
		updateAttributes();
		//SoulManager.getInstance().requestBackup(getActivity());
	}

	@Override
	public void onUpdateFromInternet() {
		updateAttributes();
	}
}
