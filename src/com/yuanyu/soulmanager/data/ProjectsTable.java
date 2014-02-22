package com.yuanyu.soulmanager.data;

import com.yuanyu.soulmanager.model.Project;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class ProjectsTable {
	
	public static final String TABLE_NAME = "projects";
	
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
		public static final String IS_FINISHED = "finish"; // 0 means false, 1 means true
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
                    "   " + Columns.IS_FINISHED + " INTEGER NOT NULL,\n" +
                    "   " + Columns.IS_DELETED + " INTEGER NOT NULL" +
                    ")";
	
	static void create(SQLiteDatabase db) {
        db.execSQL(CREATE_TBL_SQLITE);
    }
	
	static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Noting to do right now
    }
	
	public static ContentValues build(String name, String desc, int f, int i, int v, int m, int e, int h, long time, boolean isFinished, boolean isDeleted) {
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
        
        if(isFinished) {
        	cv.put(Columns.IS_FINISHED, 1);
        }
        else {
        	cv.put(Columns.IS_FINISHED, 0);
        }
        
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
	
	public static void finishProject(SQLiteDatabase db, Project p) {
		ContentValues cv = build(p.getName(), p.getDescription(), p.getForceContribution(), p.getIntelligenceContribution(),
				p.getVolitionContribution(), p.getMoneyContribution(), p.getExperienceContribution(),
				p.getHappyContribution(), p.getCreatedTime(), true, false);
		String where = Columns._ID + " = " + p.getID();
		db.update(TABLE_NAME, cv, where , null);
		p.finishe();
	}
	
	public static void markAsDeleted(SQLiteDatabase db, Project p) {
		ContentValues cv = build(p.getName(), p.getDescription(), p.getForceContribution(), p.getIntelligenceContribution(),
				p.getVolitionContribution(), p.getMoneyContribution(), p.getExperienceContribution(),
				p.getHappyContribution(), p.getCreatedTime(), p.isFinished(), true);
		String where = Columns._ID + " = " + p.getID();
		db.update(TABLE_NAME, cv, where , null);
	}
}
