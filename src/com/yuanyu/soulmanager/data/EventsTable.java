package com.yuanyu.soulmanager.data;

import com.yuanyu.soulmanager.model.Event;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class EventsTable {
	
	public static final String TABLE_NAME = "events";
	
	public static final class Columns {
		public static final String _ID = "_id";
		public static final String NAME = "name";
		public static final String CREATE_TIME = "time";
		public static final String FINISHED_TIMES = "times";
		public static final String LAST_FINISHED_TIME = "last";
		public static final String IS_DELETED = "deleted"; // 0 means false, 1 means true
	}
	
	private static final String CREATE_TBL_SQLITE =
            "CREATE TABLE " + TABLE_NAME + " (\n" +
                    "   " + Columns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "   " + Columns.NAME + " TEXT NOT NULL,\n" +
                    "   " + Columns.CREATE_TIME + " INTEGER NOT NULL,\n" +
                    "   " + Columns.FINISHED_TIMES + " INTEGER NOT NULL,\n" +
                    "   " + Columns.LAST_FINISHED_TIME + " INTEGER,\n" +
                    "   " + Columns.IS_DELETED + " INTEGER NOT NULL" +
                    ")";
	
	static void create(SQLiteDatabase db) {
        db.execSQL(CREATE_TBL_SQLITE);
    }
	
	static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Noting to do right now
    }
	
	public static ContentValues build(String name, long time, int times, long lastTime, boolean isDeleted) {
        ContentValues cv = new ContentValues();
        cv.put(Columns.NAME, name);
        cv.put(Columns.CREATE_TIME, time);
        cv.put(Columns.FINISHED_TIMES, times);
        cv.put(Columns.LAST_FINISHED_TIME, lastTime);
        
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
	
	public static void recordEvent(SQLiteDatabase db, Event e, long time) {
		ContentValues cv = build(e.getName(), e.getCreatedTime(), e.getFinishedTimes() + 1, time, false);
		String where = Columns._ID + " = " + e.getID();
		db.update(TABLE_NAME, cv, where , null);
		e.record(time);
	}
	
	public static void markAsDeleted(SQLiteDatabase db, Event e) {
		ContentValues cv = build(e.getName(), e.getCreatedTime(), e.getFinishedTimes(), e.getLastFinishedTime(), true);
		String where = Columns._ID + " = " + e.getID();
		db.update(TABLE_NAME, cv, where , null);
	}
}
