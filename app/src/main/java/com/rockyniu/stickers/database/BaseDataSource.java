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
        ALL(-1),
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
        T newItem = null;
        Cursor cursor = null;
        try {
            cursor = database.query(tableName,
                    allColumns, columnId + " = ? ",
                    new String[]{id}, null, null, null);

            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                newItem = cursorToItem(cursor);
            }
        } catch (SQLException e) {
            Log.e(TAG, "Fail to retrieve item", e);
            throw e;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            closeDatabase();
        }
        return newItem;
    }

    // remember to create an id for t:
    // t.setId(UUID.randomUUID().toString());
    public void insertItemWithId(T t) {
        if (t == null || t.getId() == null || dataIdExist(t.getId())) {
            throw new IllegalArgumentException();
        }
        try {
            ContentValues values = toValuesWithoutId(t);
            values.put(columnId, t.getId());
            openWritableDatabase();
            database.insert(tableName, null, values);
        } catch (SQLException e) {
            Log.e(TAG, "Fail to insert item", e);
            throw e;
        } finally {
            closeDatabase();
        }
    }

    public int updateItem(T t) {
        String id = t.getId();
        int rows;
        openWritableDatabase();

        try {
            ContentValues values = toValuesWithoutId(t);
            rows = database.update(tableName, values,
                    columnId + " = ?", new String[]{id});
        } catch (SQLException e) {
            Log.e(TAG, "Fail to update item", e);
            throw e;
        } finally {
            closeDatabase();
        }
        return rows;

    }

    public boolean deleteItem(T t) {
        String id = t.getId();
        openWritableDatabase();

        int rows;
        try {
            rows = database.delete(tableName,
                    columnId + " = ?", new String[]{id});
        } catch (SQLException e) {
            Log.e(TAG, "Fail to delete item", e);
            throw e;
        } finally {
            closeDatabase();
        }
        return rows > 0 ? true : false;
    }

    // remember to set modifiedTime before use this method
    public boolean labelItemDeletedWithModifiedTime(T t) {
        String id = t.getId();
        openWritableDatabase();

        int rows;
        try {
            ContentValues values = new ContentValues();
            values.put(columnDeleted, 1);
            values.put(columnModifiedTime, t.getModifiedTime());

            rows = database.update(tableName, values,
                    columnId + " = ?", new String[]{id});
        } catch (SQLException e) {
            Log.e(TAG, "Fail to label item deleted", e);
            throw e;
        } finally {
            closeDatabase();
        }
        return rows > 0 ? true : false;
    }

    public int changeItemId(String oldId, String newId) {
        openWritableDatabase();
        int rows;

        try {
            ContentValues values = new ContentValues();
            values.put(columnId, newId);
            rows = database.update(tableName, values,
                    columnId + " = ?", new String[]{oldId});
        } catch (SQLException e) {
            Log.e(TAG, "Fail to change item id", e);
            throw e;
        } finally {
            closeDatabase();
        }
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
        Cursor cursor = null;
        try {
            cursor = database.query(true, tableName, allColumns, selection, selectionArgs, null, null, null, null);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                T item = cursorToItem(cursor);
                list.add(item);
                cursor.moveToNext();
            }
        } catch (SQLException e) {
            Log.e(TAG, "Fail to retrieve item list", e);
            throw e;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            closeDatabase();
        }
        return list;
    }

    protected boolean dataIdExist(String id) {
        openReadableDatabase();
        Cursor cursor = null;
        boolean exist;
        try {
            cursor = database.query(tableName,
                    new String[]{columnId}, columnId + " = ? ",
                    new String[]{id}, null, null, null);
            exist = cursor.getCount() > 0 ? true : false;
        } catch (SQLException e) {
            Log.e(TAG, "Fail to know whether data exist", e);
            throw e;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            closeDatabase();
        }
        return exist;
    }

    abstract ContentValues toValuesWithoutId(T t);

    abstract T cursorToItem(Cursor cursor);


}
