package com.yuanyu.soulmanager.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class CacheDb {

    private static volatile CacheDb INSTANCE;
    public static CacheDb instance(Context context) {
        Context appContext = context.getApplicationContext();
        if (INSTANCE == null && appContext != null) {
            synchronized (CacheDb.class) {
                if (INSTANCE == null) {
                    INSTANCE = new CacheDb(appContext);
                }
            }
        }
        return INSTANCE;
    }

    private final CacheDbOpenHelper mOpenHelper;
    private SQLiteDatabase mDb;

    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    private final Context mAppContext;

    private CacheDb(Context appContext) {
        mAppContext = appContext;
        mOpenHelper = new CacheDbOpenHelper(appContext);
    }

    public ExecutorService getExecutor() {
        return mExecutor;
    }

    public Context getAppContext() {
        return mAppContext;
    }

    public SQLiteDatabase getDbBlocking() {
        // for thread safety: don't read mDb twice - could return null otherwise
        SQLiteDatabase db = mDb;
        return db != null ? db : (mDb = mOpenHelper.getWritableDatabase());
    }
}
