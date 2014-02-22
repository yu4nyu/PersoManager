package com.yuanyu.soulmanager.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxPath;
import com.yuanyu.soulmanager.R;
import com.yuanyu.soulmanager.model.SoulManager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class BackupFragment extends Fragment {

	private static final String appKey = "qgpifaeu1whjm65";
	private static final String appSecret = "rt5ej1wv3e9bxao";

	private static final int REQUEST_LINK_TO_DBX = 0;

	private Button mLinkButton;
	private Button mBackupButton;
	private Button mRecoveryButton;
	private DbxAccountManager mDbxAcctMgr;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.backup_fragment, null);
		mLinkButton = (Button)view.findViewById(R.id.backup_fragment_dropbox_button);
		mLinkButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) {
				linkToDropbox();
			}
		});
		mBackupButton = (Button)view.findViewById(R.id.backup_fragment_backup_button);
		mBackupButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) {
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setTitle(R.string.confirm)
				.setMessage(R.string.confirm_backup_to_dropbox)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						backupToDropbox();
					}
				})
				.setNegativeButton(android.R.string.cancel, null);
				builder.show();
			}
		});
		mRecoveryButton = (Button)view.findViewById(R.id.backup_fragment_recovery_button);
		mRecoveryButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) {
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setTitle(R.string.confirm)
				.setMessage(R.string.confirm_recovery_from_dropbox)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						recoveryFromDropbox();
					}
				})
				.setNegativeButton(android.R.string.cancel, null);
				builder.show();
			}
		});

		mDbxAcctMgr = DbxAccountManager.getInstance(getActivity().getApplicationContext(), appKey, appSecret);

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mDbxAcctMgr.hasLinkedAccount()) {
			showLinkedView();
		} else {
			showUnlinkedView();
		}
	}

	private void showLinkedView() {
		mLinkButton.setVisibility(View.GONE);
		mBackupButton.setVisibility(View.VISIBLE);
		mRecoveryButton.setVisibility(View.VISIBLE);
	}

	private void showUnlinkedView() {
		mLinkButton.setVisibility(View.VISIBLE);
		mBackupButton.setVisibility(View.GONE);
		mRecoveryButton.setVisibility(View.GONE);
	}

	private void linkToDropbox() {
		mDbxAcctMgr.startLink(getActivity(), REQUEST_LINK_TO_DBX);
	}

	private void backupToDropbox() {
		try {
			final String DATABASE_FILE_NAME = "cache.db";
			final String PREFS_FILE_NAME = "status.xml";
			DbxPath databasePath = new DbxPath(DbxPath.ROOT, DATABASE_FILE_NAME);
			DbxPath prefsPath = new DbxPath(DbxPath.ROOT, PREFS_FILE_NAME);

			// Create DbxFileSystem for synchronized file access.
			DbxFileSystem dbxFs = DbxFileSystem.forAccount(mDbxAcctMgr.getLinkedAccount());

			// Get or create file on Dropbox
			DbxFile databaseFile = null;
			if (!dbxFs.exists(databasePath)) {
				databaseFile = dbxFs.create(databasePath);
			}
			else {
				databaseFile = dbxFs.open(databasePath);
			}
			DbxFile prefsFile = null;
			if (!dbxFs.exists(prefsPath)) {
				prefsFile = dbxFs.create(prefsPath);
			}
			else {
				prefsFile = dbxFs.open(prefsPath);
			}

			// Write
			File database = getActivity().getDatabasePath(DATABASE_FILE_NAME);
			String sharedPrefsPath = getActivity().getFilesDir().getPath();
			sharedPrefsPath = sharedPrefsPath.replace("files", "");
			sharedPrefsPath += "shared_prefs/" + PREFS_FILE_NAME;
			File prefs = new File(sharedPrefsPath);
			try {
				databaseFile.writeFromExistingFile(database, true);
				prefsFile.writeFromExistingFile(prefs, true);
			}
			finally {
				databaseFile.close();
				prefsFile.close();
			}
			Toast.makeText(getActivity(), R.string.backup_succeeded, Toast.LENGTH_SHORT).show();
		} catch (IOException e) {
			Toast.makeText(getActivity(), R.string.backup_failed, Toast.LENGTH_SHORT).show();
		}
	}

	private void recoveryFromDropbox() {
		final String DATABASE_FILE_NAME = "cache.db";
		final String PREFS_FILE_NAME = "status.xml";
		DbxPath databasePath = new DbxPath(DbxPath.ROOT, DATABASE_FILE_NAME);
		DbxPath prefsPath = new DbxPath(DbxPath.ROOT, PREFS_FILE_NAME);

		// Get files on device
		File database = getActivity().getDatabasePath(DATABASE_FILE_NAME);
		String sharedPrefsPath = getActivity().getFilesDir().getPath();
		sharedPrefsPath = sharedPrefsPath.replace("files", "");
		sharedPrefsPath += "shared_prefs/" + PREFS_FILE_NAME;
		File prefs = new File(sharedPrefsPath);

		DbxFile databaseFile = null;
		DbxFile prefsFile = null;
		try {
			// Create DbxFileSystem for synchronized file access.
			DbxFileSystem dbxFs = DbxFileSystem.forAccount(mDbxAcctMgr.getLinkedAccount());
			
			// Get files on Dropbox
			if (dbxFs.exists(databasePath)) {
				databaseFile = dbxFs.open(databasePath);
			}
			else {
				throw new IOException();
			}

			if (dbxFs.exists(prefsPath)) {
				prefsFile = dbxFs.open(prefsPath);
			}
			else {
				throw new IOException();
			}

			boolean allFileOpened = (databaseFile != null && prefsFile != null);
			if(allFileOpened) {
				// Delete origin file
				if(!getActivity().deleteDatabase(DATABASE_FILE_NAME)) { // Use database.delete() may doesn't work
					throw new IOException();
				}
				// Create new file
				FileInputStream input = databaseFile.getReadStream();
				FileOutputStream output = new FileOutputStream(database);

				// Write
				byte[] buffer = new byte[1024];
				int read;
				while ((read = input.read(buffer)) != -1) {
					output.write(buffer, 0, read);
				}
				input.close();
				input = null;
				output.flush();
				output.close();
				output = null;
			}

			if(allFileOpened) {
				// Delete origin file
				if(!prefs.delete()) {
					throw new IOException();
				}
				// Create new file
				FileInputStream input = prefsFile.getReadStream();
				FileOutputStream output = new FileOutputStream(prefs);

				// Write
				byte[] buffer = new byte[1024];
				int read;
				while ((read = input.read(buffer)) != -1) {
					output.write(buffer, 0, read);
				}
				input.close();
				input = null;
				output.flush();
				output.close();
				output = null;
			}
			
			if(allFileOpened) {
				//Toast.makeText(getActivity(), R.string.recovery_succeeded, Toast.LENGTH_SHORT).show();
				//StatusManager.INSTANCE.init(getActivity());
				// TODO, does not work now, delete it or make it work
				SoulManager.getInstance().notifyOnUpdateFromInternetListener();
				
				// Show restart dialog
				showRestartDialog();
			}
		}
		catch (DbxException e) {
			Toast.makeText(getActivity(), R.string.recovery_failed, Toast.LENGTH_SHORT).show();
		}
		catch (IOException e) {
			Toast.makeText(getActivity(), R.string.recovery_failed, Toast.LENGTH_SHORT).show();
		}
		finally {
			if(databaseFile != null) {
				databaseFile.close();
			}
			if(prefsFile != null) {
				prefsFile.close();
			}
		}
	}
	
	private void showRestartDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.restart_dialog_title)
		.setMessage(R.string.restart_dialog_message)
		.setIcon(android.R.drawable.ic_dialog_info)
		.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				System.exit(0);
			}
		});
		builder.show();
	}
}
