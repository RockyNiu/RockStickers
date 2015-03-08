package com.rockyniu.stickers.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Lei on 2015/2/15.
 */


public class BaseSQLiteHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "stickers.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_LINK = "link";
    public static final String TABLE_USER = "user";

    public static final String COLUMN_ID = "uuid";
    public static final String COLUMN_USER_ID = "userId";
    public static final String COLUMN_LINK = "link";
    public static final String COLUMN_TEXT = "text";
    public static final String COLUMN_LINKTYPE = "linkType";
    public static final String COLUMN_MODIFIED_TIME = "modifiedTime"; // last modified time
    public static final String COLUMN_DELETED = "deleted";

    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";

    private static final String LINK_DATABASE_CREATE = "create table "
            + TABLE_LINK + "("
            + COLUMN_ID + " text primary key, "
            + COLUMN_USER_ID + " text default '', "
            + COLUMN_LINK + " text default '', "
            + COLUMN_TEXT + " text default '', "
            + COLUMN_LINKTYPE + " integer default 0, "
            + COLUMN_MODIFIED_TIME + " integer default 0, "
            + COLUMN_DELETED + " integer default 0, "
            + " FOREIGN KEY (" + COLUMN_USER_ID + ") REFERENCES "
            + TABLE_USER + "(" + COLUMN_ID + ")" + " ON DELETE CASCADE ON UPDATE CASCADE"
            + ");";

    private static final String USER_DATABASE_CREATE = "create table "
            + TABLE_USER + "("
            + COLUMN_ID + " text primary key, "
            + COLUMN_USERNAME + " text default '', "
            + COLUMN_PASSWORD + " text default '', "
            + COLUMN_MODIFIED_TIME + " integer default 0, "
            + COLUMN_DELETED + " integer default 0"
            + ");";

    public BaseSQLiteHelper(Context context) {
        this(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public BaseSQLiteHelper(Context context, String name,
                            CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public BaseSQLiteHelper(Context context, String name, int version) {
        this(context, name, null, version);
    }

    public BaseSQLiteHelper(Context context, int version) {
        this(context, DATABASE_NAME, version);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        if (!database.isReadOnly()) {
            // Enable foreign key constraints
            database.execSQL("PRAGMA foreign_keys=ON;");
        }
        database.execSQL(LINK_DATABASE_CREATE);
        database.execSQL(USER_DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(BaseSQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LINK);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(db);
    }

}