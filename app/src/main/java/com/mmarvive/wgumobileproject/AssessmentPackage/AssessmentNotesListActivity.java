package com.mmarvive.wgumobileproject.assessmentpackage;

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
 * Class for the button used to create notes
 * */

public class AssessmentNotesListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

//    Constants
    private static final int ASSESSMENT_NOTE_EDITOR_ACTIVITY_CODE = 11111;
    private static final int ASSESSMENT_NOTE_VIEWER_ACTIVITY_CODE = 22222;

//    Variables
    private long assessmentId;
    private Uri assessmentUri;
    private CursorAdapter cursorAdapter;

//    Methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_note_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        assessmentUri = getIntent().getParcelableExtra(DatabaseProvider.ASSESSMENT_CONTENT_TYPES);
        assessmentId = Long.parseLong(Objects.requireNonNull(assessmentUri.getLastPathSegment()));
        bindAssessmentNoteList();
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AssessmentNotesListActivity.this, AssessmentNotesEditorActivity.class);
                intent.putExtra(DatabaseProvider.ASSESSMENT_CONTENT_TYPES, assessmentUri);
                startActivityForResult(intent, ASSESSMENT_NOTE_EDITOR_ACTIVITY_CODE);
            }
        });
        getLoaderManager().initLoader(0, null, this);
    }

    private void bindAssessmentNoteList() {
        String[] from = {DatabaseHelper.ASSESSMENT_NOTE_TEXT};
        int[] to = {R.id.textViewAssessmentNoteText};
        cursorAdapter = new SimpleCursorAdapter(this, R.layout.assessment_note_list_item, null, from, to, 0);
        ListView list = findViewById(R.id.assessmentNoteListView);
        list.setAdapter(cursorAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(AssessmentNotesListActivity.this, AssessmentNoteViewActivity.class);
                Uri uri = Uri.parse(DatabaseProvider.ASSESSMENT_NOTES_URI + "/" + id);
                intent.putExtra(DatabaseProvider.ASSESSMENT_NOTE_CONTENT_TYPE, uri);
                startActivityForResult(intent, ASSESSMENT_NOTE_VIEWER_ACTIVITY_CODE);
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, DatabaseProvider.ASSESSMENT_NOTES_URI, DatabaseHelper.ASSESSMENT_NOTES_COLUMNS, DatabaseHelper.ASSESSMENT_NOTE_ASSESSMENT_ID + " = " + this.assessmentId, null, null);
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
