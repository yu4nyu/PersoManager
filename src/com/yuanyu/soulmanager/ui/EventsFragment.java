package com.yuanyu.soulmanager.ui;

import java.util.List;

import com.google.common.collect.Lists;
import com.yuanyu.soulmanager.R;
import com.yuanyu.soulmanager.data.CacheDb;
import com.yuanyu.soulmanager.data.EventsTable;
import com.yuanyu.soulmanager.data.RecordedEventsTable;
import com.yuanyu.soulmanager.model.Event;
import com.yuanyu.soulmanager.model.SoulManager;
import com.yuanyu.soulmanager.ui.utils.FormattedTimeUtils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemLongClickListener;

public class EventsFragment extends ListFragment implements SoulManager.OnUpdateFromInternetListener {

	private final static String CONFIRM_DIALOG_POSITION = "position";
	private final static String CONFIRM_DIALOG_NAME = "name";
	private final static int MSG_UPDATE = 0;
	
	private List<Event> mEventList = Lists.newArrayList();
	private EventAdapter mAdapter;
	
	@SuppressLint("HandlerLeak")
	private Handler mUpdateHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case MSG_UPDATE:
				mAdapter.notifyDataSetChanged();
				//SoulManager.getInstance().requestBackup(getActivity());
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);

		setHasOptionsMenu(true);
		setRetainInstance(true);

		mAdapter = new EventAdapter(getActivity(), mEventList);
		setListAdapter(mAdapter);
		
		new LoadFromDatabaseTask().execute();
		
		SoulManager.getInstance().addOnUpdateFromInternetListener(this);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.event_fragment, container, false);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.project, menu);
		
		getListView().setOnItemLongClickListener(new OnItemLongClickListener(){
			@Override
			public boolean onItemLongClick(AdapterView<?> list, View view, int position, long id) {
				DeleteConfirmDialogFragment confirmDialog = new DeleteConfirmDialogFragment();
				Bundle bundle = new Bundle();
				bundle.putInt(CONFIRM_DIALOG_POSITION, position);
				bundle.putString(CONFIRM_DIALOG_NAME, mEventList.get(position).getName());
				confirmDialog.setArguments(bundle);
				confirmDialog.show(getFragmentManager(), "delete");
				return false;
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.project_action_add) {
			AddEventDialog dialog = new AddEventDialog(getActivity(), getActivity().getLayoutInflater());
			dialog.show();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		RecordConfirmDialogFragment confirmDialog = new RecordConfirmDialogFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(CONFIRM_DIALOG_POSITION, position);
		bundle.putString(CONFIRM_DIALOG_NAME, mEventList.get(position).getName());
		confirmDialog.setArguments(bundle);
		confirmDialog.show(getFragmentManager(), "record");
	}
	
	private void recordEvent(int position) {
		final Event event = mEventList.get(position);
		final long time = System.currentTimeMillis();
		final ContentValues cv = RecordedEventsTable.build(event.getID(), event.getName(), time);
		final CacheDb cacheDb = CacheDb.instance(getActivity());
		cacheDb.getExecutor().execute(new Runnable(){
			@Override
			public void run() {
				RecordedEventsTable.insertBlocking(cacheDb.getDbBlocking(), cv);
				EventsTable.recordEvent(cacheDb.getDbBlocking(), event, time);
				mUpdateHandler.sendEmptyMessage(MSG_UPDATE);
			}
		});
	}
	
	private void deleteEvent(int position) {
		final Event event = mEventList.get(position);
		final CacheDb cacheDb = CacheDb.instance(getActivity());
		cacheDb.getExecutor().execute(new Runnable(){
			@Override
			public void run() {
				EventsTable.markAsDeleted(cacheDb.getDbBlocking(), event);
			}
		});
		mEventList.remove(position);
		mAdapter.notifyDataSetChanged();
	}

	class EventAdapter extends BaseAdapter {

		private List<Event> mData;
		private LayoutInflater mInflater;
		private Context mContext;

		public EventAdapter(Context context, List<Event> list) {
			mInflater = getActivity().getLayoutInflater();
			mData = list;
			mContext = context;
		}

		@Override
		public int getCount() {
			return mData.size();
		}

		@Override
		public Event getItem(int position) {
			return mData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = new ViewHolder();

			if(convertView == null) {
				convertView = mInflater.inflate(R.layout.event_list_item, null);
				viewHolder.name = (TextView) convertView.findViewById(R.id.event_list_item_name_text);
				viewHolder.times = (TextView) convertView.findViewById(R.id.event_list_item_times_text);
				viewHolder.lastTime = (TextView) convertView.findViewById(R.id.event_list_item_last_time_text);
				convertView.setTag(viewHolder);
			}
			else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			viewHolder.name.setText(mData.get(position).getName());
			viewHolder.times.setText(String.format(mContext.getString(R.string.event_recorded_times), mData.get(position).getFinishedTimes()));
			viewHolder.lastTime.setText(mContext.getString(R.string.event_last_time) + " " + FormattedTimeUtils.get(mData.get(position).getLastFinishedTime()));

			return convertView;
		}

		class ViewHolder {
			public TextView name;
			public TextView times;
			public TextView lastTime;
		}
	}
	
	@SuppressLint("ValidFragment")
	private class RecordConfirmDialogFragment extends DialogFragment {
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			setRetainInstance(true);
			
			final int position = getArguments().getInt(CONFIRM_DIALOG_POSITION);
			String name = getArguments().getString(CONFIRM_DIALOG_NAME);

			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(R.string.confirm)
			.setMessage(String.format(getActivity().getString(R.string.event_record_confirm_dialog_text), name))
			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					(EventsFragment.this).recordEvent(position);
				}
			})
			.setNegativeButton(android.R.string.no, null);
			
			return builder.create();
		}
	}
	
	@SuppressLint("ValidFragment")
	private class DeleteConfirmDialogFragment extends DialogFragment {
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			setRetainInstance(true);
			
			final int position = getArguments().getInt(CONFIRM_DIALOG_POSITION);
			String name = getArguments().getString(CONFIRM_DIALOG_NAME);

			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(R.string.confirm)
			.setMessage(String.format(getActivity().getString(R.string.event_delete_confirm_dialog_text), name))
			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					(EventsFragment.this).deleteEvent(position);
				}
			})
			.setNegativeButton(android.R.string.no, null);
			
			return builder.create();
		}
	}
	
	private class AddEventDialog {
		
		private EditText mNameEdit;
		private AlertDialog mDialog;
		private Resources mResources;
		
		private AddEventDialog(Context context, LayoutInflater inflater) {
			mResources = context.getResources();
			
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle(R.string.add_an_event);
			builder.setPositiveButton(mResources.getString(R.string.ok), null);
			builder.setNegativeButton(mResources.getString(R.string.cancel), null);
			mNameEdit = new EditText(context);
			builder.setView(mNameEdit);
			mDialog = builder.create();
		}
		
		private void show() {
			mDialog.show();
			mDialog.getButton(Dialog.BUTTON_POSITIVE).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					if(view.getId() == android.R.id.button1) { // Positive button of add dialog
						final String name = mNameEdit.getText().toString();
						if(name.equals("") || name.trim().equals("")) { // Name must not be empty
							mNameEdit.setError(mResources.getString(R.string.error_not_empty));
							return;
						}
						
						// Add to database
						final long time = System.currentTimeMillis();
						final ContentValues cv = EventsTable.build(name, time, 0, 0, false);
						final CacheDb cacheDb = CacheDb.instance(getActivity());
						cacheDb.getExecutor().execute(new Runnable(){
							@Override
							public void run() {
								long id = EventsTable.insertBlocking(cacheDb.getDbBlocking(), cv);
								
								Event event = new Event(name, id, time, 0, 0);
								mEventList.add(event);
								mUpdateHandler.sendEmptyMessage(MSG_UPDATE);
							}
						});
						
						mDialog.dismiss();
					}
				}
			});
		}
	}
	
	private class LoadFromDatabaseTask extends AsyncTask<Void, Integer, List<Event>> {

		@Override
		protected List<Event> doInBackground(Void... params) {

			String[] columns = new String[] { EventsTable.Columns._ID, EventsTable.Columns.NAME,
					EventsTable.Columns.CREATE_TIME, EventsTable.Columns.FINISHED_TIMES,
					EventsTable.Columns.LAST_FINISHED_TIME, EventsTable.Columns.IS_DELETED };      
			
			Cursor cursor = CacheDb.instance(getActivity()).getDbBlocking()
					.query(EventsTable.TABLE_NAME, columns, null, null, null, null, null, null);
			
			List<Event> list = Lists.newArrayList();
			cursor.moveToFirst();
            for(int i = 0; i < cursor.getCount(); i++){
            	if(cursor.getInt(cursor.getColumnIndex(EventsTable.Columns.IS_DELETED)) == 0) { // Not be deleted
	                Event event = new Event(cursor.getString(1), cursor.getLong(0),
	                		cursor.getLong(2), cursor.getInt(3),cursor.getLong(4));
	                list.add(event);
            	}
                cursor.moveToNext();
            }
            cursor.close();
			
			return list;
		}

		@Override
		protected void onPostExecute(List<Event> result) {
			mEventList.clear();
			mEventList.addAll(result);
			mAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onUpdateFromInternet() {
		new LoadFromDatabaseTask().execute();
	}
}
