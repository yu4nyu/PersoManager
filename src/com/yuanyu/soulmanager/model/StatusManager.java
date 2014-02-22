package com.yuanyu.soulmanager.model;

import android.content.Context;
import android.content.SharedPreferences;

public enum StatusManager {
	
	INSTANCE;
	
	public final static String STATUS_PREFERENCE_FILE_KEY = "status";
	
	private final static String SAVED_FORCE = "force";
	private final static String SAVED_INTELLIGENCE = "intelligence";
	private final static String SAVED_VOLITION = "volition";
	private final static String SAVED_MONEY = "money";
	private final static String SAVED_EXPERIENCE = "experience";
	private final static String SAVED_HAPPY = "happy";
	
	private final int DEFAULT_VALUE = -1;
	
	private int mForce;
	private int mIntelligence;
	private int mVolition;
	private int mMoney;
	private int mExperience;
	private int mHappy;
	
	private SharedPreferences.Editor mEditor;
	
	public void init(Context context) {
		SharedPreferences sharedPref = context.getSharedPreferences(
				STATUS_PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
		mEditor = sharedPref.edit();
				
		mForce = sharedPref.getInt(SAVED_FORCE, DEFAULT_VALUE);
		if(mForce == DEFAULT_VALUE) {
			writeIfNotExist(SAVED_FORCE);
			mForce = 0;
		}
		mIntelligence = sharedPref.getInt(SAVED_INTELLIGENCE, DEFAULT_VALUE);
		if(mIntelligence == DEFAULT_VALUE) {
			writeIfNotExist(SAVED_INTELLIGENCE);
			mIntelligence = 0;
		}
		mVolition = sharedPref.getInt(SAVED_VOLITION, DEFAULT_VALUE);
		if(mVolition == DEFAULT_VALUE) {
			writeIfNotExist(SAVED_VOLITION);
			mVolition = 0;
		}
		mMoney = sharedPref.getInt(SAVED_MONEY, DEFAULT_VALUE);
		if(mMoney == DEFAULT_VALUE) {
			writeIfNotExist(SAVED_MONEY);
			mMoney = 0;
		}
		mExperience = sharedPref.getInt(SAVED_EXPERIENCE, DEFAULT_VALUE);
		if(mExperience == DEFAULT_VALUE) {
			writeIfNotExist(SAVED_EXPERIENCE);
			mExperience = 0;
		}
		mHappy = sharedPref.getInt(SAVED_HAPPY, DEFAULT_VALUE);
		if(mHappy == DEFAULT_VALUE) {
			writeIfNotExist(SAVED_HAPPY);
			mHappy = 0;
		}
	}
	
	private void writeIfNotExist(String key) {
		mEditor.putInt(key, 0);
		mEditor.commit();
	}

	public int getForce() {
		return mForce;
	}
	
	public void addForce(int n) {
		mForce += n;
		mEditor.putInt(SAVED_FORCE, mForce);
		mEditor.commit();
	}

	public int getIntelligence() {
		return mIntelligence;
	}
	
	public void addIntelligence(int n) {
		mIntelligence += n;
		mEditor.putInt(SAVED_INTELLIGENCE, mIntelligence);
		mEditor.commit();
	}

	public int getVolition() {
		return mVolition;
	}
	
	public void addVolition(int n) {
		mVolition += n;
		mEditor.putInt(SAVED_VOLITION, mVolition);
		mEditor.commit();
	}

	public int getMoney() {
		return mMoney;
	}
	
	public void addMoney(int n) {
		mMoney += n;
		mEditor.putInt(SAVED_MONEY, mMoney);
		mEditor.commit();
	}

	public int getExperience() {
		return mExperience;
	}
	
	public void addExperience(int n) {
		mExperience += n;
		mEditor.putInt(SAVED_EXPERIENCE, mExperience);
		mEditor.commit();
	}
	
	public int getHappy() {
		return mHappy;
	}
	
	public void addHappy(int n) {
		mHappy += n;
		mEditor.putInt(SAVED_HAPPY, mHappy);
		mEditor.commit();
	}
}
