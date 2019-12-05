package com.mmarvive.wgumobileproject.assessmentpackage;

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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mmarvive.wgumobileproject.Alarm;
import com.mmarvive.wgumobileproject.databasepackage.DatabaseManager;
import com.mmarvive.wgumobileproject.databasepackage.DatabaseProvider;
import com.mmarvive.wgumobileproject.DateUtility;
import com.mmarvive.wgumobileproject.R;

import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/**
 * Class to track activity on the AssessmentViewer
 * */

public class AssessmentViewActivity extends AppCompatActivity {

    private static final int ASSESSMENT_EDITOR_ACTIVITY_CODE = 11111;
    private static final int ASSESSMENT_NOTE_LIST_ACTIVITY_CODE = 22222;

    private long assessmentId;
    private Assessment assessment;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_viewer);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AssessmentViewActivity.this, AssessmentEditorActivity.class);
                Uri uri = Uri.parse(DatabaseProvider.ASSESSMENTS_URI + "/" + assessmentId);
                intent.putExtra(DatabaseProvider.ASSESSMENT_CONTENT_TYPES, uri);
                startActivityForResult(intent, ASSESSMENT_EDITOR_ACTIVITY_CODE);
            }
        });
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        loadAssessment();
    }

    private void loadAssessment() {
        Uri assessmentUri = getIntent().getParcelableExtra(DatabaseProvider.ASSESSMENT_CONTENT_TYPES);
        assessmentId = Long.parseLong(Objects.requireNonNull(assessmentUri.getLastPathSegment()));
        assessment = DatabaseManager.getAssessment(this, assessmentId);
        TextView textViewAssessmentTitle = findViewById(R.id.textViewAssessmentTitle);
        TextView textViewAssessmentDescription = findViewById(R.id.textViewAssessmentDescription);
        TextView textViewAssessmentDatetime = findViewById(R.id.textViewAssessmentDatetime);
        textViewAssessmentTitle.setText(assessment.code + ": " + assessment.name);
        textViewAssessmentDescription.setText(assessment.description);
        textViewAssessmentDatetime.setText(assessment.datetime);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            loadAssessment();
        }
    }

    public void openAssessmentNotesList(View view) {
        Intent intent = new Intent(AssessmentViewActivity.this, AssessmentNotesListActivity.class);
        Uri uri = Uri.parse(DatabaseProvider.ASSESSMENTS_URI + "/" + assessmentId);
        intent.putExtra(DatabaseProvider.ASSESSMENT_CONTENT_TYPES, uri);
        startActivityForResult(intent, ASSESSMENT_NOTE_LIST_ACTIVITY_CODE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_assessment_viewer, menu);
        this.menu = menu;
        showMenuOptions();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_delete_assessment:
                return deleteAssessment();
            case R.id.action_enable_notifications:
                return enableNotifications();
            case R.id.action_disable_notifications:
                return disableNotifications();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean deleteAssessment() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int button) {
                if (button == DialogInterface.BUTTON_POSITIVE) {
                    DatabaseManager.deleteAssessment(AssessmentViewActivity.this, assessmentId);
                    setResult(RESULT_OK);
                    finish();
                    Toast.makeText(AssessmentViewActivity.this, getString(R.string.assessment_deleted), Toast.LENGTH_SHORT).show();
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.confirm_delete_assessment)
                .setPositiveButton(getString(android.R.string.yes), dialogClickListener)
                .setNegativeButton(getString(android.R.string.no), dialogClickListener)
                .show();
        return true;
    }

    private boolean editAssessment() {
        Intent intent = new Intent(this, AssessmentEditorActivity.class);
        Uri uri = Uri.parse(DatabaseProvider.ASSESSMENTS_URI + "/" + assessment.courseId);
        intent.putExtra(DatabaseProvider.COURSE_CONTENT_TYPE, uri);
        startActivityForResult(intent, ASSESSMENT_EDITOR_ACTIVITY_CODE);
        return true;
    }




    private boolean enableNotifications() {
        final long millisecondMultiplier = 86400000;
        long now = DateUtility.todayLong();

        long assessmentDateTime = DateUtility.getDateTimestamp(assessment.datetime);

        Alarm.scheduleAssessmentAlarm(getApplicationContext(), (int) assessmentId, System.currentTimeMillis()
                + 1000, "Assessment is set for today", assessment.name + " takes will occur at " + assessment.datetime);
        if (now <= assessmentDateTime) {
            Alarm.scheduleAssessmentAlarm(getApplicationContext(), (int) assessmentId, assessmentDateTime, "Assessment is today.", assessment.name + " takes will occur at " + assessment.datetime);
        }

        if (now <= assessmentDateTime - 5 * millisecondMultiplier) {
            Alarm.scheduleAssessmentAlarm(getApplicationContext(), (int) assessmentId, assessmentDateTime - 5 * millisecondMultiplier, "Assessment set in five days.", assessment.name + " takes will occur at " + assessment.datetime);
        }

        if (now <= assessmentDateTime - 14 * millisecondMultiplier) {
            Alarm.scheduleAssessmentAlarm(getApplicationContext(), (int) assessmentId, assessmentDateTime - 14 * millisecondMultiplier, "Assessment set in two weeks.", assessment.name + " takes will occur at " + assessment.datetime);
        }

        assessment.notifications = 1;
        assessment.saveChanges(this);
        showMenuOptions();
        return true;
    }

    private boolean disableNotifications() {
        assessment.notifications = 0;
        assessment.saveChanges(this);
        showMenuOptions();
        return true;
    }

    private void showMenuOptions() {
        menu.findItem(R.id.action_enable_notifications).setVisible(true);
        menu.findItem(R.id.action_disable_notifications).setVisible(true);

        if (assessment.notifications == 1) {
            menu.findItem(R.id.action_enable_notifications).setVisible(false);
        } else {
            menu.findItem(R.id.action_disable_notifications).setVisible(false);
        }
    }
}
