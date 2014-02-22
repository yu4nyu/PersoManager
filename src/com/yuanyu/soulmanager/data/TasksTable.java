package com.yuanyu.soulmanager.data;

import com.yuanyu.soulmanager.model.Task;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class TasksTable {
	
	public static final String TABLE_NAME = "tasks";
	
	public static final class Columns {
		public static final String _ID = "_id";
		public static final String NAME = "name";
		public static final String DESCRIPTION = "description";
		public static final String FORCE = "force";
		public static final String INTELLIGENCE = "intelligence";
		public static final String VOLITION = "volition";
		public static final String MONEY = "money";
		public static final String EXPERIENCE = "experience";
		public static final String HAPPY = "happy";
		public static final String CREATE_TIME = "time";
		public static final String FINISHED_TIMES = "times";
		public static final String IS_DELETED = "deleted"; // 0 means false, 1 means true
	}
	
	private static final String CREATE_TBL_SQLITE =
            "CREATE TABLE " + TABLE_NAME + " (\n" +
                    "   " + Columns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "   " + Columns.NAME + " TEXT NOT NULL,\n" +
                    "   " + Columns.DESCRIPTION + " TEXT,\n" +
                    "   " + Columns.FORCE + " INTEGER NOT NULL,\n" +
                    "   " + Columns.INTELLIGENCE + " INTEGER NOT NULL,\n" +
                    "   " + Columns.VOLITION + " INTEGER NOT NULL,\n" +
                    "   " + Columns.MONEY + " INTEGER NOT NULL,\n" +
                    "   " + Columns.EXPERIENCE + " INTEGER NOT NULL,\n" +
                    "   " + Columns.HAPPY + " INTEGER NOT NULL,\n" +
                    "   " + Columns.CREATE_TIME + " INTEGER NOT NULL,\n" +
                    "   " + Columns.FINISHED_TIMES + " INTEGER NOT NULL,\n" +
                    "   " + Columns.IS_DELETED + " INTEGER NOT NULL" +
                    ")";
	
	static void create(SQLiteDatabase db) {
        db.execSQL(CREATE_TBL_SQLITE);
    }
	
	static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Noting to do right now
    }
	
	public static ContentValues build(String name, String desc, int f, int i, int v, int m, int e, int h, long time, int times, boolean isDeleted) {
        ContentValues cv = new ContentValues();
        cv.put(Columns.NAME, name);
        cv.put(Columns.DESCRIPTION, desc);
        cv.put(Columns.FORCE, f);
        cv.put(Columns.INTELLIGENCE, i);
        cv.put(Columns.VOLITION, v);
        cv.put(Columns.MONEY, m);
        cv.put(Columns.EXPERIENCE, e);
        cv.put(Columns.HAPPY, h);
        cv.put(Columns.CREATE_TIME, time);
        cv.put(Columns.FINISHED_TIMES, times);
        
        if(isDeleted) {
        	cv.put(Columns.IS_DELETED, 1);
        }
        else {
        	cv.put(Columns.IS_DELETED, 0);
        }
        
        return cv;
    }
	
	public static long insertBlocking(SQLiteDatabase db, ContentValues cv) {
        return db.insertWithOnConflict(TABLE_NAME, Columns._ID, cv, SQLiteDatabase.CONFLICT_IGNORE);
    }
	
	public static void finishTask(SQLiteDatabase db, Task t) {
		ContentValues cv = build(t.getName(), t.getDescription(), t.getForceContribution(), t.getIntelligenceContribution(),
				t.getVolitionContribution(), t.getMoneyContribution(), t.getExperienceContribution(),
				t.getHappyContribution(), t.getCreatedTime(), t.getFinishedTimes() + 1, false);
		String where = Columns._ID + " = " + t.getID();
		db.update(TABLE_NAME, cv, where , null);
		t.finish();
	}
	
	public static void markAsDeleted(SQLiteDatabase db, Task t) {
		ContentValues cv = build(t.getName(), t.getDescription(), t.getForceContribution(), t.getIntelligenceContribution(),
				t.getVolitionContribution(), t.getMoneyContribution(), t.getExperienceContribution(),
				t.getHappyContribution(), t.getCreatedTime(), t.getFinishedTimes(), true);
		String where = Columns._ID + " = " + t.getID();
		db.update(TABLE_NAME, cv, where , null);
	}
}
