package com.yuanyu.soulmanager.model;

public enum LevelManager {
	
	INSTANCE;

	private static final int MIN_LEVEL = 1;
	private static final int FIRST_STEP = 50;
	private static final float SCALE_RATE = 1.2f;
	
	/**
	 * @return The experience goal for current level
	 */
	public int getLevelGoal(int currentLevel) {
		if(currentLevel < MIN_LEVEL) return FIRST_STEP;
		
		int goal = FIRST_STEP;
		for(int i = MIN_LEVEL; i < currentLevel; i++) {
			goal *= SCALE_RATE;
		}
		return goal;
	}
	
	public int getLevel(int experience) {
		int level = MIN_LEVEL;
		int totalGoal = getLevelGoal(level);
		while(experience >= totalGoal) {
			totalGoal += getLevelGoal(++level);
		}
		
		return level;
	}
	
	private int getTotalGoal(int level) {
		int goal = 0;
		for(int i = MIN_LEVEL; i <= level; i++) {
			goal += getLevelGoal(i);
		}
		return goal;
	}
	
	/**
	 * @return The progress for current level
	 */
	public int getProgress(int currentLevel, int experience) {
		if(currentLevel == MIN_LEVEL) {
			return experience;
		}
		return experience - getTotalGoal(currentLevel - 1);
	}
}
