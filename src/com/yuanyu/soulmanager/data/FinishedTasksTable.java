package com.yuanyu.soulmanager.data;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class FinishedTasksTable {
	
	public static final String TABLE_NAME = "finishedtasks";
	
	public static final int TYPE_PROJECT = 0;
	public static final int TYPE_TASK = 1;
	
	public static final class Columns {
		public static final String _ID = "_id";
		public static final String TYPE = "type";
		public static final String ID = "id"; // The id in its own table
		public static final String NAME = "name";
		public static final String TIME = "time";
	}
	
	private static final String CREATE_TBL_SQLITE =
            "CREATE TABLE " + TABLE_NAME + " (\n" +
                    "   " + Columns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            		"   " + Columns.TYPE + " INTEGER NOT NULL,\n" +
                    "   " + Columns.ID + " INTEGER NOT NULL,\n" +
                    "   " + Columns.NAME + " TEXT NOT NULL,\n" +
                    "   " + Columns.TIME + " INTEGER NOT NULL" +
                    ")";
	
	static void create(SQLiteDatabase db) {
        db.execSQL(CREATE_TBL_SQLITE);
    }
	
	static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Noting to do right now
    }
	
	public static ContentValues build(int type, long id, String name, long time) {
        ContentValues cv = new ContentValues();
        cv.put(Columns.TYPE, type);
        cv.put(Columns.ID, id);
        cv.put(Columns.NAME, name);
        cv.put(Columns.TIME, time);
        return cv;
    }
	
	public static long insertBlocking(SQLiteDatabase db, ContentValues cv) {
        return db.insertWithOnConflict(TABLE_NAME, Columns._ID, cv, SQLiteDatabase.CONFLICT_IGNORE);
    }
}
