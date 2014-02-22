package com.yuanyu.soulmanager.model;

public class Project {

	private long mID;
	private String mName;
	private String mDescription; // Description
	
	// 对于各个属性的贡献值
	private int forceContribution; // Force contribution
	private int intelContribution; // Intelligence contribution
	private int volitContribution; // Volition contribution
	private int moneyContribution; // Money Contribution
	private int experContribution; // Experience Contribution
	private int happyContribution; // Happy Contribution
	
	private long mCreatedTime;
	private boolean mIsFinished;
	
	public Project(String s, long id, int f, int i, int v, int m, int e, int h, long time, boolean isFinished){
		mID = id;
		mName = s;
		forceContribution = f;
		intelContribution = i;
		volitContribution = v;
		moneyContribution = m;
		experContribution = e;
		happyContribution = h;
		mIsFinished = isFinished;
		mCreatedTime = time;
	}
	
	public void setDescription(String s){
		mDescription = s;
	}
	
	public long getID(){
		return mID;
	}
	
	public String getName(){
		return mName;
	}
	
	public String getDescription(){
		if(mDescription == null) {
			return "";
		}
		return mDescription;
	}
	
	public int getForceContribution(){
		return forceContribution;
	}
	
	public int getIntelligenceContribution(){
		return intelContribution;
	}
	
	public int getVolitionContribution(){
		return volitContribution;
	}
	
	public int getMoneyContribution(){
		return moneyContribution;
	}
	
	public int getExperienceContribution(){
		return experContribution;
	}
	
	public int getHappyContribution(){
		return happyContribution;
	}
	
	public long getCreatedTime() {
		return mCreatedTime;
	}
	
	public boolean isFinished() {
		return mIsFinished;
	}
	
	public void finishe() {
		mIsFinished = true;
	}
}
