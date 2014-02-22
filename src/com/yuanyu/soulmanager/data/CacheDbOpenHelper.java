package com.yuanyu.soulmanager.data;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

class CacheDbOpenHelper extends SQLiteOpenHelperCompat {

    private static final String DB_NAME = "cache.db";
    private static final int DB_VERSION = 1;

    public CacheDbOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        ProjectsTable.create(db);
        TasksTable.create(db);
        EventsTable.create(db);
        FinishedTasksTable.create(db);
        RecordedEventsTable.create(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        ProjectsTable.onUpgrade(db, oldVersion, newVersion);
        TasksTable.onUpgrade(db, oldVersion, newVersion);
        EventsTable.onUpgrade(db, oldVersion, newVersion);
        FinishedTasksTable.onUpgrade(db, oldVersion, newVersion);
        RecordedEventsTable.onUpgrade(db, oldVersion, newVersion);
    }

    @Override
    public void onConfigureCompat(SQLiteDatabase db) {
        db.enableWriteAheadLogging();
        SQLiteOpenHelperCompat.enableForeignKeyConstraints(db);
    }

    static String dropTable(String tableName) {
        return "DROP TABLE IF EXISTS " + tableName;
    }

    static String createSingleColumnIdx(String tableName, String columnName) {
        return "CREATE INDEX " + tableName + "_" + columnName + "_idx ON " + tableName + " (" + columnName + ")";
    }

    static String createMultiColumnIdx(String tableName, String... columnNames) {
        StringBuilder sb = new StringBuilder("CREATE INDEX ");
        sb.append(tableName).append('_');
        appendWithDelimitter(sb, "_", columnNames);
        sb.append("_idx ON ").append(tableName).append(" (");
        appendWithDelimitter(sb, ", ", columnNames);
        sb.append(")");
        return sb.toString();
    }

    private static void appendWithDelimitter(StringBuilder sb, String delimitter, String... strings) {
        boolean prependDelimitter = false;
        for (String string : strings) {
            if (prependDelimitter) {
                sb.append(delimitter);
            } else {
                // first iteration only
                prependDelimitter = true;
            }
            sb.append(string);
        }
    }
}
