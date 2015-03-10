package com.rockyniu.stickers.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.rockyniu.stickers.model.BaseData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Lei on 2015/2/16.
 */
public abstract class BaseDataSource<T extends BaseData> {

    private static final String TAG = "BaseDataSource";

    // Database fields
    public enum DeletedFlag {
        All(-1),
        UNDELETED(0),
        DELETED(1);
        private final int flag;

        private DeletedFlag(int flag) {
            this.flag = flag;
        }

        public int getFlag() {
            return flag;
        }
    }

    protected SQLiteDatabase database;
    protected BaseSQLiteHelper dbHelper;

    protected String columnId = BaseSQLiteHelper.COLUMN_ID;
    protected String columnModifiedTime = BaseSQLiteHelper.COLUMN_MODIFIED_TIME;
    protected String columnDeleted = BaseSQLiteHelper.COLUMN_DELETED;

    private String tableName;
    private String[] allColumns;


    protected BaseDataSource(Context context, String tableName, String[] allColumns) {
        dbHelper = new BaseSQLiteHelper(context);
        this.tableName = tableName;
        this.allColumns = allColumns;
    }

    protected BaseDataSource(Context context, int version, String tableName, String[] allColumns) {
        dbHelper = new BaseSQLiteHelper(context, version);
        this.tableName = tableName;
        this.allColumns = allColumns;
    }

    protected void openWritableDatabase() throws SQLException {
        database = dbHelper.getWritableDatabase();
//        database.execSQL("PRAGMA foreign_keys=ON;");
    }

    protected void openReadableDatabase() throws SQLException {
        database = dbHelper.getReadableDatabase();
    }

    protected void closeDatabase() {
        database.close();
    }

    public T getItemById(String id) {
        openWritableDatabase();
        Cursor cursor = database.query(tableName,
                allColumns, columnId + " = ? ",
                new String[]{id}, null, null, null);

        cursor.moveToFirst();
        if (cursor.isAfterLast())
            return null;
        T newItem = cursorToItem(cursor);

        closeDatabase();
        return newItem;
    }

    // remember to create an id for t:
    // t.setId(UUID.randomUUID().toString());
    public void insertItemWithId(T t) {
        Log.i(TAG, "enter method");
        if (t == null || t.getId() == null || dataIdExist(t.getId())) {
            throw new IllegalArgumentException();
        }
        ContentValues values = toValuesWithoutId(t);
        values.put(columnId, t.getId());
        openWritableDatabase();
        database.insert(tableName, null, values);
        closeDatabase();
    }

    public int updateItem(T t) {
        Log.i(TAG, "enter method");
        String id = t.getId();

        openWritableDatabase();
        ContentValues values = toValuesWithoutId(t);
        int rows = database.update(tableName, values,
                columnId + " = ?", new String[]{id});
        closeDatabase();
        return rows;

    }

    public boolean deleteItem(T t) {
        Log.i(TAG, "enter method");
        String id = t.getId();
        openWritableDatabase();
        int rows = database.delete(tableName,
                columnId + " = ?", new String[]{id});
        closeDatabase();
        return rows > 0 ? true : false;
    }

    // remember to set modifiedTime before use this method
    public boolean labelItemDeletedWithModifiedTime(T t) {
        Log.i(TAG, "enter method");
        String id = t.getId();

        openWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(columnDeleted, 1);
        values.put(columnModifiedTime, t.getModifiedTime());

        int rows = database.update(tableName, values,
                columnId + " = ?", new String[]{id});

        closeDatabase();
        return rows > 0 ? true : false;
    }

    public int changeItemId(String oldId, String newId) {
        Log.i(TAG, "enter method");
        openWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(columnId, newId);
        int rows = database.update(tableName, values,
                columnId + " = ?", new String[]{oldId});
        closeDatabase();
        return rows;
    }

    public List<T> getList(int linkType) {
        return getList(linkType, DeletedFlag.UNDELETED);
    }

    public List<T> getList(int linkType, DeletedFlag deleted) {
        List<T> list = new ArrayList<T>();

        // linkType filter
        String selection = BaseSQLiteHelper.COLUMN_LINKTYPE
                + " = ? ";
        String[] selectionArgs = new String[]{Integer.toString(linkType)};

        // Deleted filter
        if (deleted == DeletedFlag.UNDELETED || deleted == DeletedFlag.DELETED) {
            selection += " AND " + BaseSQLiteHelper.COLUMN_DELETED
                    + " = ? ";
            selectionArgs = Arrays.copyOf(selectionArgs,
                    selectionArgs.length + 1);
            selectionArgs[selectionArgs.length - 1] = Integer.toString(deleted.getFlag());
        }
        openReadableDatabase();
        Cursor cursor = database.query(true, tableName, allColumns, selection, selectionArgs, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            T item = cursorToItem(cursor);
            list.add(item);
            cursor.moveToNext();
        }
        // Make sure to close the cursor
        cursor.close();
        closeDatabase();
        return list;
    }

    protected boolean dataIdExist(String id) {
        openReadableDatabase();
        Cursor cursor = database.query(tableName,
                new String[]{columnId}, columnId + " = ? ",
                new String[]{id}, null, null, null);
        boolean exist = cursor.getCount() > 0 ? true : false;
        cursor.close();
        closeDatabase();
        return exist;
    }

    abstract ContentValues toValuesWithoutId(T t);

    abstract T cursorToItem(Cursor cursor);


}
