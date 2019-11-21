package com.mmarvive.wgumobileproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

/**
 * Term Base Class
 * */

public class Term {
    public long termId;
    public String name;
    public String start;
    public String end;
    public int active;

    public void saveChanges(Context context) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.TERM_NAME, name);
        values.put(DBOpenHelper.TERM_START, start);
        values.put(DBOpenHelper.TERM_END, end);
        values.put(DBOpenHelper.TERM_ACTIVE, active);
        context.getContentResolver().update(DataProvider.TERMS_URI, values, DBOpenHelper.TERMS_TABLE_ID
                + " = " + termId, null);
    }

    public long getClassCount(Context context) {
        Cursor cursor = context.getContentResolver().query(DataProvider.COURSES_URI, DBOpenHelper.COURSES_COLUMNS,
                DBOpenHelper.COURSE_TERM_ID + " = " + this.termId, null, null);
        return cursor.getCount();
    }

    public void deactivate(Context context) {
        this.active = 0;
        saveChanges(context);
    }

    public void activate(Context context) {
        this.active = 1;
        saveChanges(context);
    }
}
