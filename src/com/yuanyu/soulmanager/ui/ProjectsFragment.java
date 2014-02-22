package com.yuanyu.soulmanager.ui;

import java.util.List;

import com.google.common.collect.Lists;
import com.yuanyu.soulmanager.R;
import com.yuanyu.soulmanager.data.CacheDb;
import com.yuanyu.soulmanager.data.FinishedTasksTable;
import com.yuanyu.soulmanager.data.ProjectsTable;
import com.yuanyu.soulmanager.model.Project;
import com.yuanyu.soulmanager.model.SoulManager;
import com.yuanyu.soulmanager.model.StatusManager;
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
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;

public class ProjectsFragment extends ListFragment implements SoulManager.OnUpdateFromInternetListener {

	private final static String CONFIRM_DIALOG_POSITION = "position";
	private final static String CONFIRM_DIALOG_NAME = "name";
	private final static int MSG_UPDATE = 0;
	
	private List<Project> mProjectList = Lists.newArrayList();
	private ProjectAdapter mAdapter;
	
	@SuppressLint("HandlerLeak")
	private Handler mUpdateHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case MSG_UPDATE:
				mAdapter.notifyDataSetChanged();
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setRetainInstance(true);
		setHasOptionsMenu(true);

		mAdapter = new ProjectAdapter(getActivity(), mProjectList);
		setListAdapter(mAdapter);
		
		new LoadFromDatabaseTask().execute();
		
		SoulManager.getInstance().addOnUpdateFromInternetListener(this);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.project_fragment, container, false);
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
				bundle.putString(CONFIRM_DIALOG_NAME, mProjectList.get(position).getName());
				confirmDialog.setArguments(bundle);
				confirmDialog.show(getFragmentManager(), "delete");
				return false;
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.project_action_add) {
			AddProjectDialog dialog = new AddProjectDialog(getActivity(), getActivity().getLayoutInflater());
			dialog.show();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		FinishConfirmDialogFragment confirmDialog = new FinishConfirmDialogFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(CONFIRM_DIALOG_POSITION, position);
		bundle.putString(CONFIRM_DIALOG_NAME, mProjectList.get(position).getName());
		confirmDialog.setArguments(bundle);
		confirmDialog.show(getFragmentManager(), "finish");
	}
	
	private void finishProject(int position) {
		final Project project = mProjectList.get(position);
		long time = System.currentTimeMillis();
		final ContentValues cv = FinishedTasksTable.build(FinishedTasksTable.TYPE_PROJECT,
				project.getID(), project.getName(), time);
		final CacheDb cacheDb = CacheDb.instance(getActivity());
		cacheDb.getExecutor().execute(new Runnable(){
			@Override
			public void run() {
				FinishedTasksTable.insertBlocking(cacheDb.getDbBlocking(), cv);
				ProjectsTable.finishProject(cacheDb.getDbBlocking(), project);
				mUpdateHandler.sendEmptyMessage(MSG_UPDATE);
			}
		});
		String formattedTime = FormattedTimeUtils.get(time);
		SoulManager.getInstance().notifyOnNewTaskFinishedListener(formattedTime, project.getName());
		
		StatusManager statusManager = StatusManager.INSTANCE;
		statusManager.addForce(project.getForceContribution());
		statusManager.addIntelligence(project.getIntelligenceContribution());
		statusManager.addVolition(project.getVolitionContribution());
		statusManager.addMoney(project.getMoneyContribution());
		statusManager.addExperience(project.getExperienceContribution());
		statusManager.addHappy(project.getHappyContribution());
		SoulManager.getInstance().notifyOnAttributesChangedListener();
	}
	
	private void deleteProject(int position) {
		final Project project = mProjectList.get(position);
		final CacheDb cacheDb = CacheDb.instance(getActivity());
		cacheDb.getExecutor().execute(new Runnable(){
			@Override
			public void run() {
				ProjectsTable.markAsDeleted(cacheDb.getDbBlocking(), project);
			}
		});
		mProjectList.remove(position);
		mAdapter.notifyDataSetChanged();
	}

	class ProjectAdapter extends BaseAdapter {

		private List<Project> mData;
		private LayoutInflater mInflater;

		public ProjectAdapter(Context context, List<Project> list) {
			mInflater = getActivity().getLayoutInflater();
			mData = list;
		}

		@Override
		public int getCount() {
			return mData.size();
		}

		@Override
		public Project getItem(int position) {
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
				convertView = mInflater.inflate(R.layout.project_list_item, null);
				viewHolder.name = (TextView) convertView.findViewById(R.id.project_list_item_name_text);
				viewHolder.force = (TextView) convertView.findViewById(R.id.project_list_item_force_text);
				viewHolder.intelligence = (TextView) convertView.findViewById(R.id.project_list_item_intelligence_text);
				viewHolder.volition = (TextView) convertView.findViewById(R.id.project_list_item_volition_text);
				viewHolder.money = (TextView) convertView.findViewById(R.id.project_list_item_money_text);
				viewHolder.experience = (TextView) convertView.findViewById(R.id.project_list_item_experience_text);
				viewHolder.happy = (TextView) convertView.findViewById(R.id.project_list_item_happy_text);
				convertView.setTag(viewHolder);
			}
			else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			viewHolder.name.setText(mData.get(position).getName());
			viewHolder.force.setText("" + mData.get(position).getForceContribution());
			viewHolder.intelligence.setText("" + mData.get(position).getIntelligenceContribution());
			viewHolder.volition.setText("" + mData.get(position).getVolitionContribution());
			viewHolder.money.setText("" + mData.get(position).getMoneyContribution());
			viewHolder.experience.setText("" + mData.get(position).getExperienceContribution());
			viewHolder.happy.setText("" + mData.get(position).getHappyContribution());
			
			if(mData.get(position).isFinished()) {
				// Disable click event on this item
				convertView.setClickable(true); // Use true, but not false
				convertView.setFocusable(true); // Use true, but not false
				convertView.setAlpha(0.3f);
			}

			return convertView;
		}

		class ViewHolder {
			public TextView name;
			public TextView force;
			public TextView intelligence;
			public TextView volition;
			public TextView money;
			public TextView experience;
			public TextView happy;
		}
	}
	
	@SuppressLint("ValidFragment")
	public class FinishConfirmDialogFragment extends DialogFragment {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			setRetainInstance(true);
			
			final int position = getArguments().getInt(CONFIRM_DIALOG_POSITION);
			String name = getArguments().getString(CONFIRM_DIALOG_NAME);	
			
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());	
			builder.setTitle(R.string.confirm)
			.setMessage(String.format(getActivity().getString(R.string.project_finish_confirm_dialog_text), name))
			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					(ProjectsFragment.this).finishProject(position);
				}
			})
			.setNegativeButton(android.R.string.no, null);
			
			return builder.create();
		}
	}
	
	@SuppressLint("ValidFragment")
	public class DeleteConfirmDialogFragment extends DialogFragment {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			setRetainInstance(true);
			
			final int position = getArguments().getInt(CONFIRM_DIALOG_POSITION);
			String name = getArguments().getString(CONFIRM_DIALOG_NAME);	
			
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());	
			builder.setTitle(R.string.confirm)
			.setMessage(String.format(getActivity().getString(R.string.project_delete_confirm_dialog_text), name))
			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					(ProjectsFragment.this).deleteProject(position);
				}
			})
			.setNegativeButton(android.R.string.no, null);
			
			return builder.create();
		}
	}
	
	private class AddProjectDialog {
		private EditText mNameEdit;
		private EditText mDescriptionEdit;

		private NumberPicker mForcePicker;
		private NumberPicker mIntelligencePicker;
		private NumberPicker mVolitionPicker;
		private NumberPicker mMoneyPicker;
		private NumberPicker mExperiencePicker;
		private NumberPicker mHappyPicker;
		
		private AlertDialog mDialog;
		private Resources mResources;
		
		private AddProjectDialog(Context context, LayoutInflater inflater) {
			mResources = context.getResources();
			
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setPositiveButton(mResources.getString(R.string.ok), null);
			builder.setNegativeButton(mResources.getString(R.string.cancel), null);
			mDialog = builder.create();

			View v = inflater.inflate(R.layout.project_add_dialog, null);
			mForcePicker = (NumberPicker) v.findViewById(R.id.project_add_dialog_force_value);
			mIntelligencePicker = (NumberPicker) v.findViewById(R.id.project_add_dialog_intelligence_value);
			mVolitionPicker = (NumberPicker) v.findViewById(R.id.project_add_dialog_volition_value);
			mMoneyPicker = (NumberPicker) v.findViewById(R.id.project_add_dialog_money_value);
			mExperiencePicker = (NumberPicker) v.findViewById(R.id.project_add_dialog_experience_value);
			mHappyPicker = (NumberPicker) v.findViewById(R.id.project_add_dialog_happy_value);
			
			mForcePicker.setMaxValue(99);
			mIntelligencePicker.setMaxValue(99);
			mVolitionPicker.setMaxValue(99);
			mMoneyPicker.setMaxValue(99);
			mExperiencePicker.setMaxValue(99);
			mHappyPicker.setMaxValue(99);
			mDialog.setView(v);

			mNameEdit = (EditText) v.findViewById(R.id.projet_add_dialog_name_edit);
			mDescriptionEdit = (EditText) v.findViewById(R.id.projet_add_dialog_desciption_edit);
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
						
						final String description = mDescriptionEdit.getText().toString();
						final int force = mForcePicker.getValue();
						final int intelligence = mIntelligencePicker.getValue();
						final int volition = mVolitionPicker.getValue();
						final int money = mMoneyPicker.getValue();
						final int experience = mExperiencePicker.getValue();
						final int happy = mHappyPicker.getValue();
						
						// Add to database
						final long time = System.currentTimeMillis();
						final ContentValues cv = ProjectsTable.build(name, description, force, intelligence,
								volition, money, experience, happy, time, false, false);
						final CacheDb cacheDb = CacheDb.instance(getActivity());
						cacheDb.getExecutor().execute(new Runnable(){
							@Override
							public void run() {
								long id = ProjectsTable.insertBlocking(cacheDb.getDbBlocking(), cv);
								
								Project project = new Project(name, id, force, intelligence, volition, money, experience, happy, time, false);
								project.setDescription(description);
								mProjectList.add(project);
								mUpdateHandler.sendEmptyMessage(MSG_UPDATE);
							}
						});
						
						mDialog.dismiss();
					}
				}
			});
		}
	}
	
	private class LoadFromDatabaseTask extends AsyncTask<Void, Integer, List<Project>> {

		@Override
		protected List<Project> doInBackground(Void... params) {

			String[] columns = new String[] { ProjectsTable.Columns._ID, ProjectsTable.Columns.NAME,
					ProjectsTable.Columns.DESCRIPTION, ProjectsTable.Columns.FORCE,
					ProjectsTable.Columns.INTELLIGENCE, ProjectsTable.Columns.VOLITION,
					ProjectsTable.Columns.MONEY, ProjectsTable.Columns.EXPERIENCE,
					ProjectsTable.Columns.HAPPY, ProjectsTable.Columns.CREATE_TIME,
					ProjectsTable.Columns.IS_FINISHED, ProjectsTable.Columns.IS_DELETED };      
			
			Cursor cursor = CacheDb.instance(getActivity()).getDbBlocking()
					.query(ProjectsTable.TABLE_NAME, columns, null, null, null, null, null, null);
			
			List<Project> list = Lists.newArrayList();
			cursor.moveToFirst();
			boolean isFinished;
            for(int i = 0; i < cursor.getCount(); i++){
            	if(cursor.getInt(cursor.getColumnIndex(ProjectsTable.Columns.IS_DELETED)) == 0) { // Not be deleted
            		isFinished = cursor.getInt(10) == 1 ? true : false;
                    Project project = new Project(cursor.getString(1), cursor.getLong(0),
                    		cursor.getInt(3), cursor.getInt(4),cursor.getInt(5),
                    		cursor.getInt(6), cursor.getInt(7), cursor.getInt(8), cursor.getLong(9), isFinished);
                    list.add(project);
            	}
                cursor.moveToNext();
            }
            cursor.close();
			
			return list;
		}

		@Override
		protected void onPostExecute(List<Project> result) {
			mProjectList.clear();
			mProjectList.addAll(result);
			mAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onUpdateFromInternet() {
		new LoadFromDatabaseTask().execute();
	}
}
