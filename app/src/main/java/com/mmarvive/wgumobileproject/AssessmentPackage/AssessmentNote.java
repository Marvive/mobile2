package com.mmarvive.wgumobileproject.assessmentpackage;

import android.content.ContentValues;
import android.content.Context;

import com.mmarvive.wgumobileproject.databasepackage.DatabaseHelper;
import com.mmarvive.wgumobileproject.databasepackage.DataProvider;

/**
 * Class that gets created when you hit the add note button for the assessment
 * */

public class AssessmentNote {
    public long assessmentNoteId;
    public long assessmentId;
    public String text;

    public void saveChanges(Context context) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.ASSESSMENT_NOTE_ASSESSMENT_ID, assessmentId);
        values.put(DatabaseHelper.ASSESSMENT_NOTE_TEXT, text);
        context.getContentResolver().update(DataProvider.ASSESSMENT_NOTES_URI, values, DatabaseHelper.ASSESSMENT_NOTES_TABLE_ID
                + " = " + assessmentNoteId, null);
    }
}
