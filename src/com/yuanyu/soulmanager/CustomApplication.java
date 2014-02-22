package com.yuanyu.soulmanager;

import com.yuanyu.soulmanager.model.StatusManager;

import android.app.Application;

public class CustomApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		StatusManager.INSTANCE.init(getApplicationContext());
	}

}
