
package com.yuanyu.soulmanager.data;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

public abstract class SQLiteOpenHelperCompat extends SQLiteOpenHelper {

    public SQLiteOpenHelperCompat(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public SQLiteOpenHelperCompat(Context context, String name, CursorFactory factory, int version,
            DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            onConfigureCompat(db);
        } // else onConfigure is calling it
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public final void onConfigure(SQLiteDatabase db) {
        // this is not called < Jelly Bean, illegal super call does not crash therefore
        super.onConfigure(db);
        onConfigureCompat(db);
    }

    /**
     * replacement for onConfigure, either called as first thing in onOpen or
     * redirected from onConfigure for >= JellyBean
     */
    protected void onConfigureCompat(SQLiteDatabase db) {}

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    protected static void enableForeignKeyConstraints (SQLiteDatabase db) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            db.execSQL("PRAGMA foreign_keys=ON");
        } else {
            db.setForeignKeyConstraintsEnabled(true);
        }
    }
}
