package com.mmarvive.wgumobileproject.assessmentpackage;

import android.content.ContentValues;
import android.content.Context;

import com.mmarvive.wgumobileproject.databasepackage.DatabaseHelper;
import com.mmarvive.wgumobileproject.databasepackage.DatabaseProvider;


/**
 * Base Assessment Class Object
 * */

public class Assessment {
    public long assessmentId;
    public long courseId;
    public String code;
    public String name;
    public String description;
    public String datetime;
    public int notifications;

//    Saves changes to Database
    public void saveChanges(Context context) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.ASSESSMENT_COURSE_ID, courseId);
        values.put(DatabaseHelper.ASSESSMENT_CODE, code);
        values.put(DatabaseHelper.ASSESSMENT_NAME, name);
        values.put(DatabaseHelper.ASSESSMENT_DESCRIPTION, description);
        values.put(DatabaseHelper.ASSESSMENT_DATETIME, datetime);
        values.put(DatabaseHelper.ASSESSMENT_NOTIFICATIONS, notifications);
        context.getContentResolver().update(DatabaseProvider.ASSESSMENTS_URI, values, DatabaseHelper.ASSESSMENTS_TABLE_ID
                + " = " + assessmentId, null);
    }
}
