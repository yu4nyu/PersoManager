package com.yuanyu.soulmanager.model;

public class Event {

	private long mID;
	private String mName;
	
	private long mCreatedTime;
	private int mFinishedTimes;
	private long mLastFinishedTime;
	
	public Event(String s, long id, long time, int finishedTimes, long lastTime){
		mID = id;
		mName = s;
		mCreatedTime = time;
		mFinishedTimes = finishedTimes;
		mLastFinishedTime = lastTime;
	}
	
	public long getID(){
		return mID;
	}
	
	public String getName(){
		return mName;
	}
	
	public long getCreatedTime() {
		return mCreatedTime;
	}
	
	public int getFinishedTimes() {
		return mFinishedTimes;
	}
	
	public long getLastFinishedTime() {
		return mLastFinishedTime;
	}
	
	public void record(long time) {
		mFinishedTimes++;
		mLastFinishedTime = time;
	}
}
