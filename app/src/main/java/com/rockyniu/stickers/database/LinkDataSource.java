package com.rockyniu.stickers.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.rockyniu.stickers.model.Link;

/**
 * Created by Lei on 2015/2/15.
 */

public class LinkDataSource extends BaseDataSource<Link> {

    private static final String TABLE_NAME = BaseSQLiteHelper.TABLE_LINK;
    private static final String[] ALL_COLUMNS = {BaseSQLiteHelper.COLUMN_ID,
            BaseSQLiteHelper.COLUMN_USER_ID,
            BaseSQLiteHelper.COLUMN_ADDRESS,
            BaseSQLiteHelper.COLUMN_TITLE,
            BaseSQLiteHelper.COLUMN_LINKTYPE,
            BaseSQLiteHelper.COLUMN_MODIFIED_TIME,
            BaseSQLiteHelper.COLUMN_DELETED};

    public LinkDataSource(Context context) {
        super(context, TABLE_NAME, ALL_COLUMNS);
    }

    public LinkDataSource(Context context, int version) {
        super(context, version, TABLE_NAME, ALL_COLUMNS);
    }

    @Override
    ContentValues toValuesWithoutId(Link link) {
        ContentValues values = new ContentValues();
//        values.put(com.rockyniu.mpgcalculator.database.BaseSQLiteHelper.COLUMN_ID, link.getId());
        values.put(BaseSQLiteHelper.COLUMN_USER_ID, link.getUserId());
        values.put(BaseSQLiteHelper.COLUMN_ADDRESS, link.getAddress());
        values.put(BaseSQLiteHelper.COLUMN_TITLE, link.getTitle());
        values.put(BaseSQLiteHelper.COLUMN_LINKTYPE, link.getLinkType());
        values.put(BaseSQLiteHelper.COLUMN_MODIFIED_TIME, link.getModifiedTime());
        values.put(BaseSQLiteHelper.COLUMN_DELETED, link.isDeleted());
        return values;
    }

    @Override
    Link cursorToItem(Cursor cursor) {
        Link link = new Link();
        link.setId(cursor.getString(cursor
                .getColumnIndex(BaseSQLiteHelper.COLUMN_ID)));
        link.setUserId(cursor.getString(cursor
                .getColumnIndex(BaseSQLiteHelper.COLUMN_USER_ID)));
        link.setAddress(cursor.getString(cursor
                .getColumnIndex(BaseSQLiteHelper.COLUMN_ADDRESS)));
        link.setTitle(cursor.getString(cursor
                .getColumnIndex(BaseSQLiteHelper.COLUMN_TITLE)));
        link.setLinkType(cursor.getInt(cursor
                .getColumnIndex(BaseSQLiteHelper.COLUMN_LINKTYPE)));
        link.setModifiedTime(cursor.getLong(cursor
                .getColumnIndex(BaseSQLiteHelper.COLUMN_MODIFIED_TIME)));
        link.setDeleted(cursor.getInt(cursor
                .getColumnIndex(BaseSQLiteHelper.COLUMN_DELETED)) == 1 ? true : false);
        return link;
    }
}