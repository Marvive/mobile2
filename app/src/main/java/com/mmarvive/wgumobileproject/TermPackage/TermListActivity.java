package com.mmarvive.wgumobileproject.termpackage;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.mmarvive.wgumobileproject.Alarm;
import com.mmarvive.wgumobileproject.coursepackage.CourseStatus;
import com.mmarvive.wgumobileproject.databasepackage.DatabaseHelper;
import com.mmarvive.wgumobileproject.databasepackage.DatabaseManager;
import com.mmarvive.wgumobileproject.databasepackage.DataProvider;
import com.mmarvive.wgumobileproject.R;

import java.util.GregorianCalendar;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/**
 * Activity for Term List
 * */

public class TermListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int TERM_EDITOR_ACTIVITY_CODE = 11111;
    public static final int TERM_VIEWER_ACTIVITY_CODE = 22222;

    private CursorAdapter cursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        String[] from = {DatabaseHelper.TERM_NAME, DatabaseHelper.TERM_START, DatabaseHelper.TERM_END};
        int[] to = {R.id.textViewTerm, R.id.textViewTermStartDate, R.id.textViewTermEndDate};

        cursorAdapter = new SimpleCursorAdapter(this, R.layout.term_list_item, null, from, to, 0);

        ListView list = findViewById(android.R.id.list);
        list.setAdapter(cursorAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(TermListActivity.this, TermViewerActivity.class);
                Uri uri = Uri.parse(DataProvider.TERMS_URI + "/" + id);
                intent.putExtra(DataProvider.TERM_CONTENT_TYPE, uri);
                startActivityForResult(intent, TERM_VIEWER_ACTIVITY_CODE);
            }
        });
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            restartLoader();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_create_sample:
                return generateSamples();
            case R.id.action_delete_all_terms:
                return deleteAllTerms();
            case R.id.action_generate_example_alarm:
                return createExampleAlarm();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

/*
* This is to generate sample classes that get pushed to the database then retrieved to show on screen
* */
    private boolean generateSamples() {
        Uri term1Uri = DatabaseManager.insertTerm(this, "Summer 2020", "01-01-2020", "2020-06-30", 1);
        Uri term2Uri = DatabaseManager.insertTerm(this, "Winter 2020", "07-01-2020", "12-31-2020", 0);
        Uri term3Uri = DatabaseManager.insertTerm(this, "Summer 2021", "01-01-2021", "06-30-2021", 0);
        Uri term4Uri = DatabaseManager.insertTerm(this, "Winter 2021", "07-01-2021", "12-31-2021", 0);
        Uri term5Uri = DatabaseManager.insertTerm(this, "Summer 2022", "01-01-2022", "06-30-2022", 0);
        Uri term6Uri = DatabaseManager.insertTerm(this, "Winter 2022", "07-01-2022", "12-31-2022", 0);

        Uri course1Uri = DatabaseManager.insertCourse(this, Long.parseLong(Objects.requireNonNull(term1Uri.getLastPathSegment())),
                getString(R.string.course_1), "01-01-2020", "02-01-2020",
                getString(R.string.mentor_1), "(510) 555-5555", getString(R.string.mentor_email_1),
                CourseStatus.IN_PROGRESS);

        DatabaseManager.insertCourse(this, Long.parseLong(term1Uri.getLastPathSegment()),
                getString(R.string.course_2), "02-01-2020", "03-01-2020",
                getString(R.string.mentor_2), "(808) 555-5555", getString(R.string.mentor_email_2),
                CourseStatus.PLANNED);

        DatabaseManager.insertCourse(this, Long.parseLong(term1Uri.getLastPathSegment()),
                getString(R.string.course_3), "03-01-2020", "06-30-2020",
                getString(R.string.mentor_3), "(911) 555-5555", getString(R.string.mentor_email_3),
                CourseStatus.DROPPED);

        DatabaseManager.insertCourseNote(this, Long.parseLong(Objects.requireNonNull(course1Uri.getLastPathSegment())),
                getString(R.string.short_example_note));

        DatabaseManager.insertCourseNote(this, Long.parseLong(course1Uri.getLastPathSegment()),
                getString(R.string.long_example_note));

        Uri ass1Uri = DatabaseManager.insertAssessment(this, Long.parseLong(course1Uri.getLastPathSegment()), "A001", getString(R.string.course_1),
                getString(R.string.assessment_description), "10-01-2020 2:30 PM");

        Uri ass2Uri = DatabaseManager.insertAssessment(this, Long.parseLong(course1Uri.getLastPathSegment()), "B002", getString(R.string.course_2),
                getString(R.string.assessment_description),  "10-01-2020 10:30 AM");

        DatabaseManager.insertAssessmentNote(this, Long.parseLong(Objects.requireNonNull(ass1Uri.getLastPathSegment())),
                getString(R.string.short_example_note));

        DatabaseManager.insertAssessmentNote(this, Long.parseLong(ass1Uri.getLastPathSegment()),
                getString(R.string.short_example_note));

        DatabaseManager.insertAssessmentNote(this, Long.parseLong(Objects.requireNonNull(ass2Uri.getLastPathSegment())),
                getString(R.string.short_example_note));

        DatabaseManager.insertAssessmentNote(this, Long.parseLong(ass2Uri.getLastPathSegment()),
                getString(R.string.short_example_note));

        DatabaseManager.insertAssessmentNote(this, Long.parseLong(ass2Uri.getLastPathSegment()),
                getString(R.string.short_example_note));

        restartLoader();
        return true;
    }

//        Takes you to new term screen
    public void openNewTermEditor(View view) {
        Intent intent = new Intent(this, TermEditorActivity.class);
        startActivityForResult(intent, TERM_EDITOR_ACTIVITY_CODE);
    }

    private boolean deleteAllTerms() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int button) {
                if (button == DialogInterface.BUTTON_POSITIVE) {
                    getContentResolver().delete(DataProvider.TERMS_URI, null, null);
                    getContentResolver().delete(DataProvider.COURSES_URI, null, null);
                    getContentResolver().delete(DataProvider.COURSE_NOTES_URI, null, null);
                    getContentResolver().delete(DataProvider.ASSESSMENTS_URI, null, null);
                    getContentResolver().delete(DataProvider.ASSESSMENT_NOTES_URI, null, null);
                    restartLoader();
                    Toast.makeText(TermListActivity.this, getString(R.string.all_terms_deleted), Toast.LENGTH_SHORT).show();
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.confirm_delete_all_terms))
                .setPositiveButton(getString(android.R.string.yes), dialogClickListener)
                .setNegativeButton(getString(android.R.string.no), dialogClickListener)
                .show();
        return true;
    }

    private boolean createExampleAlarm() {
//         Sets alarm for 5 seconds into the future
        Long time = new GregorianCalendar().getTimeInMillis() + 5000;

        Intent intent = new Intent(this, Alarm.class);
        intent.putExtra("title", getString(R.string.example_alarm));
        intent.putExtra("text", getString(R.string.example_alarm_message));

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        alarmManager.set(AlarmManager.RTC_WAKEUP, time, PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_ONE_SHOT));
        Toast.makeText(this, getString(R.string.test_alarm), Toast.LENGTH_SHORT).show();
        return true;
    }

    private void restartLoader() {
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, DataProvider.TERMS_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }
}
