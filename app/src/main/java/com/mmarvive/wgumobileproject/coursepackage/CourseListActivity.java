package com.mmarvive.wgumobileproject.coursepackage;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mmarvive.wgumobileproject.databasepackage.DatabaseHelper;
import com.mmarvive.wgumobileproject.databasepackage.DatabaseProvider;
import com.mmarvive.wgumobileproject.R;

import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/**
 * Course List Activity
 * */
public class CourseListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int COURSE_VIEWER_ACTIVITY_CODE = 11111;
    private static final int COURSE_EDITOR_ACTIVITY_CODE = 22222;

    private long termId;
    private Uri termUri;
    private MySimpleCursorAdapter cursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CourseListActivity.this, CourseEditScreenActivity.class);
                intent.putExtra(DatabaseProvider.TERM_CONTENT_TYPE, termUri);
                startActivityForResult(intent, COURSE_EDITOR_ACTIVITY_CODE);
            }
        });

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        termUri = intent.getParcelableExtra(DatabaseProvider.TERM_CONTENT_TYPE);
        loadTermData();
        bindClassList();
        getLoaderManager().initLoader(0, null, this);
    }

    private void loadTermData() {
        if (termUri == null) {
            setResult(RESULT_CANCELED);
            finish();
        }
        else {
            termId = Long.parseLong(Objects.requireNonNull(termUri.getLastPathSegment()));
            setTitle(getString(R.string.courses));
        }
    }

    private void bindClassList() {
        String[] from = {DatabaseHelper.COURSE_NAME, DatabaseHelper.COURSE_START, DatabaseHelper.COURSE_END, DatabaseHelper.COURSE_STATUS};
        int[] to = {R.id.textViewCourseName, R.id.textViewCourseStartDate, R.id.textViewCourseEndDate, R.id.textViewCourseStatus};

        cursorAdapter = new MySimpleCursorAdapter(this, R.layout.course_list_item, null, from, to);

        ListView list = findViewById(android.R.id.list);
        list.setAdapter(cursorAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CourseListActivity.this, CourseViewActivity.class);
                Uri uri = Uri.parse(DatabaseProvider.COURSES_URI + "/" + id);
                intent.putExtra(DatabaseProvider.COURSE_CONTENT_TYPE, uri);
                startActivityForResult(intent, COURSE_VIEWER_ACTIVITY_CODE);
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, DatabaseProvider.COURSES_URI, DatabaseHelper.COURSES_COLUMNS, DatabaseHelper.COURSE_TERM_ID + " = " + this.termId, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loadTermData();
        restartLoader();
    }

    private void restartLoader() {
        getLoaderManager().restartLoader(0, null, this);
    }

    public class MySimpleCursorAdapter extends android.widget.SimpleCursorAdapter {

        public MySimpleCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
            super(context, layout, c, from, to);
        }

        @Override
        public void setViewText(TextView view, String text) {
            if (view.getId() == R.id.textViewCourseStatus) {
                String status = "";
                switch (text) {
                    case "PLANNED":
                        status = "Plan to Take";
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
                view.setText("Status: " + status);
            }
            else {
                view.setText(text);
            }
        }
    }
}
