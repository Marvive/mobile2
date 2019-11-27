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
                return createSampleData();
            case R.id.action_delete_all_terms:
                return deleteAllTerms();
            case R.id.action_create_test_alarm:
                return createTestAlarm();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean createSampleData() {
        Uri term1Uri = DatabaseManager.insertTerm(this, "Spring 2020", "2020-01-01", "2020-06-30", 1);
        Uri term2Uri = DatabaseManager.insertTerm(this, "Fall 2020", "2020-07-01", "2020-12-31", 0);
        Uri term3Uri = DatabaseManager.insertTerm(this, "Spring 2021", "2021-01-01", "2021-06-30", 0);
        Uri term4Uri = DatabaseManager.insertTerm(this, "Fall 2021", "2021-07-01", "2021-12-31", 0);
        Uri term5Uri = DatabaseManager.insertTerm(this, "Spring 2022", "2022-01-01", "2022-06-30", 0);
        Uri term6Uri = DatabaseManager.insertTerm(this, "Fall 2022", "2022-07-01", "2022-12-31", 0);

        Uri course1Uri = DatabaseManager.insertCourse(this, Long.parseLong(Objects.requireNonNull(term1Uri.getLastPathSegment())),
                "C196: Mobile Application Development", "2020-01-01", "2020-02-01",
                "Nolan Townshend", "(510) 555-5555", "nolan.townshend@wgu.edu",
                CourseStatus.IN_PROGRESS);

        DatabaseManager.insertCourse(this, Long.parseLong(term1Uri.getLastPathSegment()),
                "C193: Client-Server Application Development", "2020-02-01", "2020-03-01",
                "Bruce Banner", "(808) 555-5555", "bruce.banner@wgu.edu",
                CourseStatus.PLANNED);

        DatabaseManager.insertCourse(this, Long.parseLong(term1Uri.getLastPathSegment()),
                "C195: Software II - Advanced Java Concepts", "2020-03-01", "2020-06-30",
                "Luke Skywalker", "(911) 555-5555", "luke.skywalker@wgu.edu",
                CourseStatus.DROPPED);

        DatabaseManager.insertCourseNote(this, Long.parseLong(Objects.requireNonNull(course1Uri.getLastPathSegment())),
                getString(R.string.short_example_note));

        DatabaseManager.insertCourseNote(this, Long.parseLong(course1Uri.getLastPathSegment()),
                getString(R.string.long_example_note));

        Uri ass1Uri = DatabaseManager.insertAssessment(this, Long.parseLong(course1Uri.getLastPathSegment()), "CLP1", "Mobile Application Development",
                "@string/long_test_note", "2020-10-01 2:30 PM");

        Uri ass2Uri = DatabaseManager.insertAssessment(this, Long.parseLong(course1Uri.getLastPathSegment()), "ABC3", "Second Assessment, although this one has a name that won't fit on the grid",
                "Assessment Description",  "2020-10-01 10:30 AM");

        DatabaseManager.insertAssessmentNote(this, Long.parseLong(Objects.requireNonNull(ass1Uri.getLastPathSegment())),
                "Assessment #1 Note #1");

        DatabaseManager.insertAssessmentNote(this, Long.parseLong(ass1Uri.getLastPathSegment()),
                "Assessment #1 Note #2");

        DatabaseManager.insertAssessmentNote(this, Long.parseLong(Objects.requireNonNull(ass2Uri.getLastPathSegment())),
                "Assessment #2 Note #1");

        DatabaseManager.insertAssessmentNote(this, Long.parseLong(ass2Uri.getLastPathSegment()),
                "Assessment #2 Note #2");

        DatabaseManager.insertAssessmentNote(this, Long.parseLong(ass2Uri.getLastPathSegment()),
                "Assessment #2 Note #3");

        restartLoader();
        return true;
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

    private boolean createTestAlarm() {
//         Sets alarm for 5 seconds into the future
        Long time = new GregorianCalendar().getTimeInMillis() + 5000;

        Intent intent = new Intent(this, Alarm.class);
        intent.putExtra("title", "Test Alarm");
        intent.putExtra("text", "This is a test alarm.");

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        alarmManager.set(AlarmManager.RTC_WAKEUP, time, PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_ONE_SHOT));
        Toast.makeText(this, getString(R.string.test_alarm), Toast.LENGTH_SHORT).show();
        return true;
    }

    public void openNewTermEditor(View view) {
        Intent intent = new Intent(this, TermEditorActivity.class);
        startActivityForResult(intent, TERM_EDITOR_ACTIVITY_CODE);
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
