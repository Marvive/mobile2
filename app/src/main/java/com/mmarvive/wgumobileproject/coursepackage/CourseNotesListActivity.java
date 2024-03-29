package com.mmarvive.wgumobileproject.coursepackage;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mmarvive.wgumobileproject.databasepackage.DatabaseHelper;
import com.mmarvive.wgumobileproject.databasepackage.DatabaseProvider;
import com.mmarvive.wgumobileproject.R;

import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/**
 * Activity Class for course notes
 * */

public class CourseNotesListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int COURSE_NOTE_EDITOR_ACTIVITY_CODE = 11111;
    private static final int COURSE_NOTE_VIEWER_ACTIVITY_CODE = 22222;

    private long courseId;
    private Uri courseUri;
    private CursorAdapter cursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_note_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        courseUri = getIntent().getParcelableExtra(DatabaseProvider.COURSE_CONTENT_TYPE);
        courseId = Long.parseLong(Objects.requireNonNull(courseUri.getLastPathSegment()));
        bindCourseNoteList();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CourseNotesListActivity.this, CourseNotesEditScreenActivity.class);
                intent.putExtra(DatabaseProvider.COURSE_CONTENT_TYPE, courseUri);
                startActivityForResult(intent, COURSE_NOTE_EDITOR_ACTIVITY_CODE);
            }
        });
        getLoaderManager().initLoader(0, null, this);
    }

    private void bindCourseNoteList() {
        String[] from = {DatabaseHelper.COURSE_NOTE_TEXT};
        int[] to = {R.id.textViewCourseNoteText};
        cursorAdapter = new SimpleCursorAdapter(this, R.layout.course_note_list_item, null, from, to, 0);

        ListView list = findViewById(R.id.courseNoteListView);
        list.setAdapter(cursorAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CourseNotesListActivity.this, CourseNotesViewActivity.class);
                Uri uri = Uri.parse(DatabaseProvider.COURSE_NOTES_URI + "/" + id);
                intent.putExtra(DatabaseProvider.COURSE_NOTE_CONTENT_TYPE, uri);
                startActivityForResult(intent, COURSE_NOTE_VIEWER_ACTIVITY_CODE);
            }
        });
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, DatabaseProvider.COURSE_NOTES_URI, DatabaseHelper.COURSE_NOTES_COLUMNS, DatabaseHelper.COURSE_NOTE_COURSE_ID + " = " + this.courseId, null, null);
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
        restartLoader();
    }

    private void restartLoader() {
        getLoaderManager().restartLoader(0, null, this);
    }
}
