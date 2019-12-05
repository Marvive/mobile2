package com.mmarvive.wgumobileproject.coursepackage;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mmarvive.wgumobileproject.Alarm;
import com.mmarvive.wgumobileproject.assessmentpackage.AssessmentListActivity;
import com.mmarvive.wgumobileproject.databasepackage.DatabaseManager;
import com.mmarvive.wgumobileproject.databasepackage.DatabaseProvider;
import com.mmarvive.wgumobileproject.DateUtility;
import com.mmarvive.wgumobileproject.R;

import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import static com.mmarvive.wgumobileproject.DateUtility.millisecondMultiplier;

/**
 * Activity for Course Viewer
 * */

public class CourseViewActivity extends AppCompatActivity {

//      Constants...
    private static final int COURSE_NOTE_LIST_ACTIVITY_CODE = 11111;
    private static final int ASSESSMENT_LIST_ACTIVITY_CODE = 22222;
    private static final int COURSE_EDITOR_ACTIVITY_CODE = 33333;

//    Variables
    private Menu menu;
    private long courseId;
    private Course course;

//    Classes
    private TextView textViewCourseName;
    private TextView textViewStartDate;
    private TextView textViewEndDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_viewer);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        Uri courseUri = intent.getParcelableExtra(DatabaseProvider.COURSE_CONTENT_TYPE);
        courseId = Long.parseLong(Objects.requireNonNull(courseUri.getLastPathSegment()));
        course = DatabaseManager.getCourse(this, courseId);

        setStatusLabel();
        findElements();
    }

//    Changes status string
    private void setStatusLabel() {
        TextView textViewStatus = findViewById(R.id.textViewStatus);
        String status = "";
        switch (course.status.toString()) {
            case "PLANNED":
                status = "Planned to Take";
                break;
            case "IN_PROGRESS":
                status = "In Progress";
                break;
            case "COMPLETED":
                status = "Completed";
                break;
            case "DROPPED":
                status = "Dropped";
                break;
        }
        textViewStatus.setText("Status: " + status);
    }

    private void findElements() {
        textViewCourseName = findViewById(R.id.textViewCourseName);
        textViewCourseName.setText(course.name);
        textViewStartDate = findViewById(R.id.textViewCourseStart);
        textViewStartDate.setText(course.start);
        textViewEndDate = findViewById(R.id.textViewCourseEnd);
        textViewEndDate.setText(course.end);
    }

    private void updateElements() {
        course = DatabaseManager.getCourse(this, courseId);
        textViewCourseName.setText(course.name);
        textViewStartDate.setText(course.start);
        textViewEndDate.setText(course.end);
    }

    public void openClassNotesList(View view) {
        Intent intent = new Intent(CourseViewActivity.this, CourseNotesListActivity.class);
        Uri uri = Uri.parse(DatabaseProvider.COURSES_URI + "/" + courseId);
        intent.putExtra(DatabaseProvider.COURSE_CONTENT_TYPE, uri);
        startActivityForResult(intent, COURSE_NOTE_LIST_ACTIVITY_CODE);
    }

    public void openAssessments(View view) {
        Intent intent = new Intent(CourseViewActivity.this, AssessmentListActivity.class);
        Uri uri = Uri.parse(DatabaseProvider.COURSES_URI + "/" + courseId);
        intent.putExtra(DatabaseProvider.COURSE_CONTENT_TYPE, uri);
        startActivityForResult(intent, ASSESSMENT_LIST_ACTIVITY_CODE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_course_viewer, menu);
        this.menu = menu;
        showMenuOptions();
        return true;
    }

    private void showMenuOptions() {
        menu.findItem(R.id.action_enable_notifications).setVisible(true);
        menu.findItem(R.id.action_disable_notifications).setVisible(true);

        if (course.notifications == 1) {
            menu.findItem(R.id.action_enable_notifications).setVisible(false);
        } else {
            menu.findItem(R.id.action_disable_notifications).setVisible(false);
        }

        if (course.status == null) {
            course.status = CourseStatus.PLANNED;
            course.saveChanges(this);
        }

        switch (course.status.toString()) {
            case "PLANNED":
                menu.findItem(R.id.action_drop_course).setVisible(false);
                menu.findItem(R.id.action_start_course).setVisible(true);
                menu.findItem(R.id.action_mark_course_completed).setVisible(false);
                break;
            case "IN_PROGRESS":
                menu.findItem(R.id.action_drop_course).setVisible(true);
                menu.findItem(R.id.action_start_course).setVisible(false);
                menu.findItem(R.id.action_mark_course_completed).setVisible(true);
                break;
            case "COMPLETED":
            case "DROPPED":
                menu.findItem(R.id.action_drop_course).setVisible(false);
                menu.findItem(R.id.action_start_course).setVisible(false);
                menu.findItem(R.id.action_mark_course_completed).setVisible(false);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_edit_course:
                return editCourse();
            case R.id.action_delete_course:
                return deleteCourse();
            case R.id.action_enable_notifications:
                return enableNotifications();
            case R.id.action_disable_notifications:
                return disableNotifications();
            case R.id.action_drop_course:
                return dropCourse();
            case R.id.action_start_course:
                return startCourse();
            case R.id.action_mark_course_completed:
                return markCourseCompleted();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

//    Takes you to course editor
    private boolean editCourse() {
        Intent intent = new Intent(this, CourseEditScreenActivity.class);
        Uri uri = Uri.parse(DatabaseProvider.COURSES_URI + "/" + course.courseId);
        intent.putExtra(DatabaseProvider.COURSE_CONTENT_TYPE, uri);
        startActivityForResult(intent, COURSE_EDITOR_ACTIVITY_CODE);
        return true;
    }

//    Deletes Course
    private boolean deleteCourse() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int button) {
                if (button == DialogInterface.BUTTON_POSITIVE) {
                    DatabaseManager.deleteCourse(CourseViewActivity.this, courseId);
                    setResult(RESULT_OK);
                    finish();
                    Toast.makeText(CourseViewActivity.this, getString(R.string.course_deleted), Toast.LENGTH_SHORT).show();
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.confirm_delete_course)
                .setPositiveButton(getString(android.R.string.yes), dialogClickListener)
                .setNegativeButton(getString(android.R.string.no), dialogClickListener)
                .show();
        return true;
    }

//    Creates alarms/notifications. Had to cast it back to ints due to id requiring a long
/*    Math: x * hoursInADay * minutesInAnHour * secondsInAMinute * millisecondsInASecond
*       When enableNotifications is called, checks to see if "now" is earlier than the course.start time
*       If it's earlier, then it will create a notification for several different dates
* */
    private boolean enableNotifications() {
        long now = DateUtility.todayLong();
        long courseStart = DateUtility.getDateTimestamp(course.start);
        long courseEnd = DateUtility.getDateTimestamp(course.end);

        if (now <= courseStart) {
            Alarm.scheduleCourseAlarm(getApplicationContext(), (int) courseId, courseStart,
                    "Course starts today!", course.name + " begins on " + course.start);
//            System.out.println("Course Starts Today!");
        }

        if (now <= courseStart - 3 * DateUtility.millisecondMultiplier) {
            Alarm.scheduleCourseAlarm(getApplicationContext(), (int) courseId, courseStart,
                    "Course starts in three days!", course.name + " begins on " + course.start);
        }

        if (now <= courseStart - 21 * DateUtility.millisecondMultiplier) {
            Alarm.scheduleCourseAlarm(getApplicationContext(), (int) courseId, courseStart,
                    "Course starts in three weeks!", course.name + " begins on " + course.start);
        }

        if (now <= courseEnd) {
            Alarm.scheduleCourseAlarm(getApplicationContext(), (int) courseId, courseEnd,
                    "Course Ends Today!", course.name + " ends on " + course.end);
        }

        if (now <= courseEnd - 3 * millisecondMultiplier) {
            Alarm.scheduleCourseAlarm(getApplicationContext(), (int) courseId, courseEnd,
                    "Course ends in three days!", course.name + " ends on " + course.end);
        }

        if (now <= courseEnd - 21 * millisecondMultiplier) {
            Alarm.scheduleCourseAlarm(getApplicationContext(), (int) courseId, courseEnd,
                    "Course ends in three weeks!", course.name + " ends on " + course.end);
        }

        course.notifications = 1;
        course.saveChanges(this);
        showMenuOptions();
        return true;
    }

//    Disables notifications
    private boolean disableNotifications() {
        course.notifications = 0;
        course.saveChanges(this);
        showMenuOptions();
        return true;
    }

//    Sets status of course to DROPPED
    private boolean dropCourse() {
        course.status = CourseStatus.DROPPED;
        course.saveChanges(this);
        setStatusLabel();
        showMenuOptions();
        return true;
    }
//    Sets status of course to IN_PROGRESS
    private boolean startCourse() {
        course.status = CourseStatus.IN_PROGRESS;
        course.saveChanges(this);
        setStatusLabel();
        showMenuOptions();
        return true;
    }

//    Sets status of course to COMPLETED
    private boolean markCourseCompleted() {
        course.status = CourseStatus.COMPLETED;
        course.saveChanges(this);
        setStatusLabel();
        showMenuOptions();
        return true;
    }

//    updates the status if confirmed
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            updateElements();
        }
    }
}
