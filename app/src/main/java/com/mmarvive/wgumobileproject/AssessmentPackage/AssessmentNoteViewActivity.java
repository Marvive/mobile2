package com.mmarvive.wgumobileproject.assessmentpackage;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mmarvive.wgumobileproject.CameraActivity;
import com.mmarvive.wgumobileproject.databasepackage.DatabaseManager;
import com.mmarvive.wgumobileproject.databasepackage.DatabaseProvider;
import com.mmarvive.wgumobileproject.imagepackage.ImageListActivity;
import com.mmarvive.wgumobileproject.R;

import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;

/**
 * Class to show activity on the assessment notes
 * */

public class AssessmentNoteViewActivity extends AppCompatActivity {

//    Constants
    private static final int ASSESSMENT_NOTE_EDITOR_ACTIVITY_CODE = 11111;
    private static final int CAMERA_ACTIVITY_CODE = 22222;

//    Variables
    private long assessmentNoteId;
    private Uri assessmentNoteUri;
    private TextView textViewAssessmentNoteText;

//    Methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_note_viewer);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        textViewAssessmentNoteText = findViewById(R.id.textViewAssessmentNoteText);
        assessmentNoteUri = getIntent().getParcelableExtra(DatabaseProvider.ASSESSMENT_NOTE_CONTENT_TYPE);
        if (assessmentNoteUri != null) {
            assessmentNoteId = Long.parseLong(Objects.requireNonNull(assessmentNoteUri.getLastPathSegment()));
            setTitle(getString(R.string.view_assessment_note));
            pullNote();
        }
    }

    private void pullNote() {
        AssessmentNote assessmentNote = DatabaseManager.getAssessmentNote(this, assessmentNoteId);
        textViewAssessmentNoteText.setText(assessmentNote.text);
        textViewAssessmentNoteText.setMovementMethod(new ScrollingMovementMethod());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            pullNote();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_assessment_note_viewer, menu);
        MenuItem item = menu.findItem(R.id.menu_item_share);
        ShareActionProvider shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        AssessmentNote assessmentNote = DatabaseManager.getAssessmentNote(this, assessmentNoteId);
        Assessment assessment = DatabaseManager.getAssessment(this, assessmentNote.assessmentId);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        String shareSubject = assessment.code + " " + assessment.name + ": Assessment Note";
        String shareBody = assessmentNote.text;
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubject);
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        shareActionProvider.setShareIntent(shareIntent);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_delete_assessment_note) {
            return deleteAssessmentNote();
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean deleteAssessmentNote() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int button) {
                if (button == DialogInterface.BUTTON_POSITIVE) {
                    DatabaseManager.deleteAssessmentNote(AssessmentNoteViewActivity.this, assessmentNoteId);
                    setResult(RESULT_OK);
                    finish();
                    Toast.makeText(AssessmentNoteViewActivity.this, getString(R.string.note_deleted), Toast.LENGTH_SHORT).show();
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.confirm_delete_note)
                .setPositiveButton(getString(android.R.string.yes), dialogClickListener)
                .setNegativeButton(getString(android.R.string.no), dialogClickListener)
                .show();
        return true;
    }

    private void addPicture() {
        Intent intent = new Intent(this, CameraActivity.class);
        intent.putExtra("PARENT_URI", assessmentNoteUri);
        startActivityForResult(intent, CAMERA_ACTIVITY_CODE);
    }

    public void handleEditNote(View view) {
        Intent intent = new Intent(this, AssessmentNotesEditorActivity.class);
        intent.putExtra(DatabaseProvider.ASSESSMENT_NOTE_CONTENT_TYPE, assessmentNoteUri);
        startActivityForResult(intent, ASSESSMENT_NOTE_EDITOR_ACTIVITY_CODE);
    }

    public void handleViewImages(View view) {
        Intent intent = new Intent(this, ImageListActivity.class);
        intent.putExtra("ParentUri", assessmentNoteUri);
        startActivityForResult(intent, 0);
    }

    public void handleAddPicture(View view) {
        addPicture();
    }
}
