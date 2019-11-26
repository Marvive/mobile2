package com.mmarvive.wgumobileproject.coursepackage;

import android.content.ContentValues;
import android.content.Context;

import com.mmarvive.wgumobileproject.databasepackage.DatabaseHelper;
import com.mmarvive.wgumobileproject.databasepackage.DataProvider;

/**
 * Base Course Note Class
 * */

public class CourseNote {
    public long courseNoteId;
    public long courseId;
    public String text;

    public void saveChanges(Context context) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COURSE_NOTE_COURSE_ID, courseId);
        values.put(DatabaseHelper.COURSE_NOTE_TEXT, text);
        context.getContentResolver().update(DataProvider.COURSE_NOTES_URI, values, DatabaseHelper.COURSE_NOTES_TABLE_ID
                + " = " + courseNoteId, null);
    }
}
