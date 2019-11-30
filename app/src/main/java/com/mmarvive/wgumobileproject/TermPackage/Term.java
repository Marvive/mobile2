package com.mmarvive.wgumobileproject.termpackage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.mmarvive.wgumobileproject.databasepackage.DatabaseHelper;
import com.mmarvive.wgumobileproject.databasepackage.DatabaseProvider;

/**
 * Term Base Class
 * */

public class Term {
    public long termId;
    public String name;
    public String start;
    public String end;
    public int active;

//    Pushes saved information to database
    public void saveChanges(Context context) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.TERM_NAME, name);
        values.put(DatabaseHelper.TERM_START, start);
        values.put(DatabaseHelper.TERM_END, end);
        values.put(DatabaseHelper.TERM_ACTIVE, active);
        context.getContentResolver().update(DatabaseProvider.TERMS_URI, values, DatabaseHelper.TERMS_TABLE_ID
                + " = " + termId, null);
    }

//    Grabs classes from database
    public long getClassCount(Context context) {
        Cursor cursor = context.getContentResolver().query(DatabaseProvider.COURSES_URI, DatabaseHelper.COURSES_COLUMNS,
                DatabaseHelper.COURSE_TERM_ID + " = " + this.termId, null, null);
        assert cursor != null;
        return cursor.getCount();
    }

//    Deactivates courses. Basically sets it as a true/false value 0 and 1
    public void deactivate(Context context) {
        this.active = 0;
        saveChanges(context);
    }

//    Activates courses. Basically sets it as a true/false value 0 and 1
    public void activate(Context context) {
        this.active = 1;
        saveChanges(context);
    }
}
