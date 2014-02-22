package com.yuanyu.soulmanager;

public class Attributes {
	
	private static int FORCE = 0;
	private static int INTELLIGENCE = 0;
	private static int VOLITION = 0;
	private static int MONEY = 0;
	private static int EXPERIENCE = 0;
	private static int HAPPY = 0;
	
	public static void addForce(int value) {
		FORCE += value;
	}
	
	public static void addIntelligence(int value) {
		INTELLIGENCE += value;
	}
	
	public static void addVolition(int value) {
		VOLITION += value;
	}
	
	public static void addMoney(int value) {
		MONEY += value;
	}
	
	public static void addExperience(int value) {
		EXPERIENCE += value;
	}
	
	public static void addHappy(int value) {
		HAPPY += value;
	}
	
	public static int getForce() {
		return FORCE;
	}
	
	public static int getIntelligence() {
		return INTELLIGENCE;
	}
	
	public static int getVolition() {
		return VOLITION;
	}
	
	public static int getMoney() {
		return MONEY;
	}
	
	public static int getExperience() {
		return EXPERIENCE;
	}
	
	public static int getHappy() {
		return HAPPY;
	}
}
