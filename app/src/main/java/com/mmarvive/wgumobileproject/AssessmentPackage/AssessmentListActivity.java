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


public class AssessmentListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

//    Constants
    private static final int ASSESSMENT_VIEWER_ACTIVITY_CODE = 11111;
    private static final int ASSESSMENT_EDITOR_ACTIVITY_CODE = 22222;

//    Variables
    private CursorAdapter cursorAdapter;
    private long courseId;
    private Uri courseUri;

//    Methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        courseUri = getIntent().getParcelableExtra(DatabaseProvider.COURSE_CONTENT_TYPE);
        courseId = Long.parseLong(Objects.requireNonNull(courseUri.getLastPathSegment()));
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AssessmentListActivity.this, AssessmentEditorActivity.class);
                intent.putExtra(DatabaseProvider.COURSE_CONTENT_TYPE, courseUri);
                startActivityForResult(intent, ASSESSMENT_EDITOR_ACTIVITY_CODE);
            }
        });
        bindAssessmentList();
        getLoaderManager().initLoader(0, null, this);
    }

    protected void bindAssessmentList() {
        String[] from = {DatabaseHelper.ASSESSMENT_CODE, DatabaseHelper.ASSESSMENT_NAME, DatabaseHelper.ASSESSMENT_DATETIME};
        int[] to = {R.id.textViewAssessmentCode, R.id.textViewAssessmentName, R.id.textViewAssessmentDatetime};
        cursorAdapter = new SimpleCursorAdapter(this, R.layout.assessment_list_item, null, from, to, 0);
        ListView list = findViewById(R.id.assessmentListView);
        list.setAdapter(cursorAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(AssessmentListActivity.this, AssessmentViewActivity.class);
                Uri uri = Uri.parse(DatabaseProvider.ASSESSMENTS_URI + "/" + id);
                intent.putExtra(DatabaseProvider.ASSESSMENT_CONTENT_TYPES, uri);
                startActivityForResult(intent, ASSESSMENT_VIEWER_ACTIVITY_CODE);
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, DatabaseProvider.ASSESSMENTS_URI, DatabaseHelper.ASSESSMENTS_COLUMNS,
                DatabaseHelper.ASSESSMENT_COURSE_ID + " = " + this.courseId, null, null);
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
