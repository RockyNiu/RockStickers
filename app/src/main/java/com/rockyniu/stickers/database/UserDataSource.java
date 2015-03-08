package com.rockyniu.stickers.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.rockyniu.stickers.model.User;

/**
 * Created by Lei on 2015/2/16.
 */
public class UserDataSource extends BaseDataSource<User> {
    private static final String TABLE_NAME = BaseSQLiteHelper.TABLE_USER;
    private static final String[] ALL_COLUMNS = {BaseSQLiteHelper.COLUMN_ID,
            BaseSQLiteHelper.COLUMN_USERNAME, BaseSQLiteHelper.COLUMN_PASSWORD,
            BaseSQLiteHelper.COLUMN_MODIFIED_TIME, BaseSQLiteHelper.COLUMN_DELETED};

    public UserDataSource(Context context) {
        super(context, TABLE_NAME, ALL_COLUMNS);
    }

    public UserDataSource(Context context, int version) {
        super(context, version, TABLE_NAME, ALL_COLUMNS);
    }

    @Override
    ContentValues toValuesWithoutId(User user) {
        ContentValues values = new ContentValues();
//        values.put(com.rockyniu.mpgcalculator.database.BaseSQLiteHelper.COLUMN_ID, user.getId());
        values.put(BaseSQLiteHelper.COLUMN_USERNAME, user.getUserName());
        values.put(BaseSQLiteHelper.COLUMN_PASSWORD, user.getPassword());
        values.put(BaseSQLiteHelper.COLUMN_MODIFIED_TIME, user.getModifiedTime());
        values.put(BaseSQLiteHelper.COLUMN_DELETED, user.isDeleted());
        return values;
    }

    @Override
    User cursorToItem(Cursor cursor) {
        User user = new User();
        user.setId(cursor.getString(cursor
                .getColumnIndex(BaseSQLiteHelper.COLUMN_ID)));
        user.setUserName(cursor.getString(cursor
                .getColumnIndex(BaseSQLiteHelper.COLUMN_USERNAME)));
        user.setPassword(cursor.getString(cursor
                .getColumnIndex(BaseSQLiteHelper.COLUMN_PASSWORD)));
        user.setModifiedTime(cursor.getLong(cursor
                .getColumnIndex(BaseSQLiteHelper.COLUMN_MODIFIED_TIME)));
        user.setDeleted(cursor.getInt(cursor
                .getColumnIndex(BaseSQLiteHelper.COLUMN_DELETED)) == 1 ? true : false);
        return user;
    }
}
