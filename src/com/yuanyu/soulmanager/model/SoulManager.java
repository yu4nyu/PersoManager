package com.yuanyu.soulmanager.model;

import java.util.List;

import com.google.common.collect.Lists;

public class SoulManager {

	private static SoulManager INSTANCE = new SoulManager();

	public static SoulManager getInstance() {
		return INSTANCE;
	}

	private static List<OnAttributesChangedListener> mOnAttributesChangedListenerList = Lists.newArrayList();
	private static List<OnNewTaskFinishedListener> mOnNewTaskFinishedListenerList = Lists.newArrayList();
	private static List<OnUpdateFromInternetListener> mOnUpdateFromInternetListenerList = Lists.newArrayList();

	public interface OnAttributesChangedListener {
		void onAttributesChanged();
	}

	public interface OnNewTaskFinishedListener {
		void onNewTaskFinished(String time, String name);
	}
	
	public interface OnUpdateFromInternetListener {
		void onUpdateFromInternet();
	}

	public void addOnAttributesChangedListener(OnAttributesChangedListener listener) {
		mOnAttributesChangedListenerList.add(listener);
	}

	public void removemOnAttributesChangedListenerList(OnAttributesChangedListener listener) {
		mOnAttributesChangedListenerList.remove(listener);
	}

	public void addOnNewTaskFinishedListener(OnNewTaskFinishedListener listener) {
		mOnNewTaskFinishedListenerList.add(listener);
	}

	public void removeOnNewTaskFinishedListener(OnNewTaskFinishedListener listener) {
		mOnNewTaskFinishedListenerList.remove(listener);
	}
	
	public void addOnUpdateFromInternetListener(OnUpdateFromInternetListener listener) {
		mOnUpdateFromInternetListenerList.add(listener);
	}

	public void removeOnUpdateFromInternetListener(OnUpdateFromInternetListener listener) {
		mOnUpdateFromInternetListenerList.remove(listener);
	}

	public void notifyOnAttributesChangedListener() {
		for(OnAttributesChangedListener listener : mOnAttributesChangedListenerList) {
			listener.onAttributesChanged();
		}
	}

	public void notifyOnNewTaskFinishedListener(String time, String name) {
		for(OnNewTaskFinishedListener listener : mOnNewTaskFinishedListenerList) {
			listener.onNewTaskFinished(time, name);
		}
	}
	
	public void notifyOnUpdateFromInternetListener() {
		for(OnUpdateFromInternetListener listener : mOnUpdateFromInternetListenerList) {
			listener.onUpdateFromInternet();
		}
	}
}
