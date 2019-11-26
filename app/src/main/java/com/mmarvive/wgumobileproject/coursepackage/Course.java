package com.mmarvive.wgumobileproject.coursepackage;

import android.content.ContentValues;
import android.content.Context;

import com.mmarvive.wgumobileproject.databasepackage.DatabaseHelper;
import com.mmarvive.wgumobileproject.databasepackage.DataProvider;


/**
 * Base Course Class
 * */

public class Course {
    public long courseId;
    public long termId;
    public String name;
    public String description;
    public String start;
    public String end;
    public CourseStatus status;
    public String mentor;
    public String mentorPhone;
    public String mentorEmail;
    public int notifications;

    public void saveChanges(Context context) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COURSE_TERM_ID, termId);
        values.put(DatabaseHelper.COURSE_NAME, name);
        values.put(DatabaseHelper.COURSE_DESCRIPTION, description);
        values.put(DatabaseHelper.COURSE_START, start);
        values.put(DatabaseHelper.COURSE_END, end);
        values.put(DatabaseHelper.COURSE_STATUS, status.toString());
        values.put(DatabaseHelper.COURSE_MENTOR, mentor);
        values.put(DatabaseHelper.COURSE_MENTOR_PHONE, mentorPhone);
        values.put(DatabaseHelper.COURSE_MENTOR_EMAIL, mentorEmail);
        values.put(DatabaseHelper.COURSE_NOTIFICATIONS, notifications);
        context.getContentResolver().update(DataProvider.COURSES_URI, values, DatabaseHelper.COURSES_TABLE_ID
                + " = " + courseId, null);
    }
}
