package com.mmarvive.wgumobileproject;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

/**
 * Database Manager Class
 * */

public class DataManager {

//     Terms Section
    public static Uri insertTerm(Context context, String termName, String termStart, String termEnd, int termActive) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.TERM_NAME, termName);
        values.put(DBOpenHelper.TERM_START, termStart);
        values.put(DBOpenHelper.TERM_END, termEnd);
        values.put(DBOpenHelper.TERM_ACTIVE, termActive);
        return context.getContentResolver().insert(DataProvider.TERMS_URI, values);
    }

    public static Term getTerm(Context context, long termId) {
        Cursor cursor = context.getContentResolver().query(DataProvider.TERMS_URI, DBOpenHelper.TERMS_COLUMNS,
                DBOpenHelper.TERMS_TABLE_ID + " = " + termId, null, null);
        assert cursor != null;
        cursor.moveToFirst();
        String termName = cursor.getString(cursor.getColumnIndex(DBOpenHelper.TERM_NAME));
        String termStartDate = cursor.getString(cursor.getColumnIndex(DBOpenHelper.TERM_START));
        String termEndDate = cursor.getString(cursor.getColumnIndex(DBOpenHelper.TERM_END));
        int termActive = cursor.getInt(cursor.getColumnIndex(DBOpenHelper.TERM_ACTIVE));

        Term t = new Term();
        t.termId = termId;
        t.name = termName;
        t.start = termStartDate;
        t.end = termEndDate;
        t.active = termActive;
        return t;
    }

//     Courses Section
    public static Uri insertCourse(Context context, long termId, String courseName, String courseStart, String courseEnd,
                                   String courseMentor, String courseMentorPhone, String courseMentorEmail, CourseStatus status) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.COURSE_TERM_ID, termId);
        values.put(DBOpenHelper.COURSE_NAME, courseName);
        values.put(DBOpenHelper.COURSE_START, courseStart);
        values.put(DBOpenHelper.COURSE_END, courseEnd);
        values.put(DBOpenHelper.COURSE_MENTOR, courseMentor);
        values.put(DBOpenHelper.COURSE_MENTOR_PHONE, courseMentorPhone);
        values.put(DBOpenHelper.COURSE_MENTOR_EMAIL, courseMentorEmail);
        values.put(DBOpenHelper.COURSE_STATUS, status.toString());
        return context.getContentResolver().insert(DataProvider.COURSES_URI, values);
    }

    public static Course getCourse(Context context, long courseId) {
        Cursor cursor = context.getContentResolver().query(DataProvider.COURSES_URI, DBOpenHelper.COURSES_COLUMNS,
                DBOpenHelper.COURSES_TABLE_ID + " = " + courseId, null, null);
        assert cursor != null;
        cursor.moveToFirst();
        Long termId = cursor.getLong(cursor.getColumnIndex(DBOpenHelper.COURSE_TERM_ID));
        String courseName = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_NAME));
        String courseDescription = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_DESCRIPTION));
        String courseStart = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_START));
        String courseEnd = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_END));
        CourseStatus courseStatus = CourseStatus.valueOf(cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_STATUS)));
        String courseMentor = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_MENTOR));
        String courseMentorPhone = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_MENTOR_PHONE));
        String courseMentorEmail = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_MENTOR_EMAIL));
        int courseNotifications = (cursor.getInt(cursor.getColumnIndex(DBOpenHelper.COURSE_NOTIFICATIONS)));

        Course c = new Course();
        c.courseId = courseId;
        c.termId = termId;
        c.name = courseName;
        c.description = courseDescription;
        c.start = courseStart;
        c.end = courseEnd;
        c.status = courseStatus;
        c.mentor = courseMentor;
        c.mentorPhone = courseMentorPhone;
        c.mentorEmail = courseMentorEmail;
        c.notifications = courseNotifications;
        return c;
    }

    public static void deleteCourse(Context context, long courseId) {
        Cursor notesCursor = context.getContentResolver().query(DataProvider.COURSE_NOTES_URI,
                DBOpenHelper.COURSE_NOTES_COLUMNS, DBOpenHelper.COURSE_NOTE_COURSE_ID + " = " + courseId,
                null, null);
        assert notesCursor != null;
        while (notesCursor.moveToNext()) {
            deleteCourseNote(context, notesCursor.getLong(notesCursor.getColumnIndex(DBOpenHelper.COURSE_NOTES_TABLE_ID)));
        }
        context.getContentResolver().delete(DataProvider.COURSES_URI, DBOpenHelper.COURSES_TABLE_ID + " = "
                + courseId, null);
    }

//     Course Notes Section
    public static void insertCourseNote(Context context, long courseId, String text) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.COURSE_NOTE_COURSE_ID, courseId);
        values.put(DBOpenHelper.COURSE_NOTE_TEXT, text);
        context.getContentResolver().insert(DataProvider.COURSE_NOTES_URI, values);
    }

    public static CourseNote getCourseNote(Context context, long courseNoteId) {
        Cursor cursor = context.getContentResolver().query(DataProvider.COURSE_NOTES_URI, DBOpenHelper.COURSE_NOTES_COLUMNS,
                DBOpenHelper.COURSE_NOTES_TABLE_ID + " = " + courseNoteId, null, null);
        assert cursor != null;
        cursor.moveToFirst();
        long courseId = cursor.getLong(cursor.getColumnIndex(DBOpenHelper.COURSE_NOTE_COURSE_ID));
        String text = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_NOTE_TEXT));

        CourseNote c = new CourseNote();
        c.courseNoteId = courseNoteId;
        c.courseId = courseId;
        c.text = text;
        return c;
    }

    public static void deleteCourseNote(Context context, long courseNoteId) {
        context.getContentResolver().delete(DataProvider.COURSE_NOTES_URI, DBOpenHelper.COURSE_NOTES_TABLE_ID + " = " + courseNoteId, null);
    }

//     Assessments
    public static Uri insertAssessment(Context context, long courseId, String code, String name, String description, String datetime) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.ASSESSMENT_COURSE_ID, courseId);
        values.put(DBOpenHelper.ASSESSMENT_CODE, code);
        values.put(DBOpenHelper.ASSESSMENT_NAME, name);
        values.put(DBOpenHelper.ASSESSMENT_DESCRIPTION, description);
        values.put(DBOpenHelper.ASSESSMENT_DATETIME, datetime);
        return context.getContentResolver().insert(DataProvider.ASSESSMENTS_URI, values);
    }

    public static Assessment getAssessment(Context context, long assessmentId) {
        Cursor cursor = context.getContentResolver().query(DataProvider.ASSESSMENTS_URI, DBOpenHelper.ASSESSMENTS_COLUMNS,
                DBOpenHelper.ASSESSMENTS_TABLE_ID + " = " + assessmentId, null, null);
        assert cursor != null;
        cursor.moveToFirst();
        long courseId = cursor.getLong(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_COURSE_ID));
        String name = cursor.getString(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_NAME));
        String description = cursor.getString(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_DESCRIPTION));
        String code = cursor.getString(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_CODE));
        String datetime = cursor.getString(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_DATETIME));
        int notifications = cursor.getInt(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_NOTIFICATIONS));

        Assessment a = new Assessment();
        a.assessmentId = assessmentId;
        a.courseId = courseId;
        a.name = name;
        a.description = description;
        a.code = code;
        a.datetime = datetime;
        a.notifications = notifications;
        return a;
    }

    public static void deleteAssessment(Context context, long assessmentId) {
        Cursor notesCursor = context.getContentResolver().query(DataProvider.ASSESSMENT_NOTES_URI,
                DBOpenHelper.ASSESSMENT_NOTES_COLUMNS, DBOpenHelper.ASSESSMENT_NOTE_ASSESSMENT_ID + " = " +
                        assessmentId, null, null);
        assert notesCursor != null;
        while (notesCursor.moveToNext()) {
            deleteAssessmentNote(context, notesCursor.getLong(notesCursor.getColumnIndex(DBOpenHelper.ASSESSMENT_NOTES_TABLE_ID)));
        }
        context.getContentResolver().delete(DataProvider.ASSESSMENTS_URI, DBOpenHelper.ASSESSMENTS_TABLE_ID
                + " = " + assessmentId, null);
    }

//     Assessment Notes
    public static void insertAssessmentNote(Context context, long assessmentId, String text) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.ASSESSMENT_NOTE_ASSESSMENT_ID, assessmentId);
        values.put(DBOpenHelper.ASSESSMENT_NOTE_TEXT, text);
        context.getContentResolver().insert(DataProvider.ASSESSMENT_NOTES_URI, values);
    }

    public static AssessmentNote getAssessmentNote(Context context, long assessmentNoteId) {
        Cursor cursor = context.getContentResolver().query(DataProvider.ASSESSMENT_NOTES_URI,
                DBOpenHelper.ASSESSMENT_NOTES_COLUMNS, DBOpenHelper.ASSESSMENT_NOTES_TABLE_ID + " = "
                        + assessmentNoteId, null, null);
        assert cursor != null;
        cursor.moveToFirst();
        long assessmentId = cursor.getLong(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_NOTE_ASSESSMENT_ID));
        String text = cursor.getString(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_NOTE_TEXT));

        AssessmentNote a = new AssessmentNote();
        a.assessmentNoteId = assessmentNoteId;
        a.assessmentId = assessmentId;
        a.text = text;
        return a;
    }

    public static void deleteAssessmentNote(Context context, long assessmentNoteId) {
        context.getContentResolver().delete(DataProvider.ASSESSMENT_NOTES_URI, DBOpenHelper.ASSESSMENT_NOTES_TABLE_ID
                + " = " + assessmentNoteId, null);
    }

//     Images
    public static void insertImage(Context context, Uri parentUri, long timestamp) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.IMAGE_PARENT_URI, parentUri.toString());
        values.put(DBOpenHelper.IMAGE_TIMESTAMP, timestamp);
        context.getContentResolver().insert(DataProvider.IMAGES_URI, values);
    }

    public static Image getImage(Context context, long imageId) {
        @SuppressLint("Recycle") Cursor cursor = context.getContentResolver().query(DataProvider.IMAGES_URI, DBOpenHelper.IMAGES_COLUMNS, DBOpenHelper.IMAGES_TABLE_ID + " = " + imageId, null, null);
        assert cursor != null;
        cursor.moveToFirst();
        Uri parentUri = Uri.parse(cursor.getString(cursor.getColumnIndex(DBOpenHelper.IMAGE_PARENT_URI)));
        long timestamp = cursor.getLong(cursor.getColumnIndex(DBOpenHelper.IMAGE_TIMESTAMP));

        Image i = new Image();
        i.imageId = imageId;
        i.parentUri = parentUri;
        i.timestamp = timestamp;
        return i;
    }
}
