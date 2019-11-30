package com.mmarvive.wgumobileproject.coursepackage;

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
 * Activity for Course Notes View
 * */

public class CourseNotesViewActivity extends AppCompatActivity {

    private static final int COURSE_NOTE_EDITOR_ACTIVITY_CODE = 11111;
    private static final int CAMERA_ACTIVITY_CODE = 22222;

    private long courseNoteId;
    private Uri courseNoteUri;
    private TextView textViewCourseNoteText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_note_viewer);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        textViewCourseNoteText = findViewById(R.id.textViewCourseNoteText);
        courseNoteUri = getIntent().getParcelableExtra(DatabaseProvider.COURSE_NOTE_CONTENT_TYPE);

        if (courseNoteUri != null) {
            courseNoteId = Long.parseLong(Objects.requireNonNull(courseNoteUri.getLastPathSegment()));
            setTitle(getString(R.string.view_course_note));
            pullNote();
        }
    }

//    Loads note into view
    private void pullNote() {
        CourseNote courseNote = DatabaseManager.getCourseNote(this, courseNoteId);
        textViewCourseNoteText.setText(courseNote.text);
        textViewCourseNoteText.setMovementMethod(new ScrollingMovementMethod());
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
        getMenuInflater().inflate(R.menu.menu_course_note_viewer, menu);
        MenuItem item = menu.findItem(R.id.menu_item_share);
        ShareActionProvider shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        CourseNote courseNote = DatabaseManager.getCourseNote(this, courseNoteId);
        Course course = DatabaseManager.getCourse(this, courseNote.courseId);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        String shareSubject = course.name + ": Course Note";
        String shareBody = courseNote.text;
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubject);
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        shareActionProvider.setShareIntent(shareIntent);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_delete_course_note:
                return deleteCourseNote();
            case R.id.action_add_photo:
                return addPicture();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

//    Removes note from view and database
    private boolean deleteCourseNote() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int button) {
                if (button == DialogInterface.BUTTON_POSITIVE) {
                    DatabaseManager.deleteCourseNote(CourseNotesViewActivity.this, courseNoteId);
                    setResult(RESULT_OK);
                    finish();
                    Toast.makeText(CourseNotesViewActivity.this, getString(R.string.note_deleted), Toast.LENGTH_SHORT).show();
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

//    Adds photo to note
    private boolean addPicture() {
        Intent intent = new Intent(this, CameraActivity.class);
        intent.putExtra("PARENT_URI", courseNoteUri);
        startActivityForResult(intent, CAMERA_ACTIVITY_CODE);
        return true;
    }

    public void handleEditNote(View view) {
        Intent intent = new Intent(this, CourseNotesEditScreenActivity.class);
        intent.putExtra(DatabaseProvider.COURSE_NOTE_CONTENT_TYPE, courseNoteUri);
        startActivityForResult(intent, COURSE_NOTE_EDITOR_ACTIVITY_CODE);
    }

    public void handleViewImages(View view) {
        Intent intent = new Intent(this, ImageListActivity.class);
        intent.putExtra("ParentUri", courseNoteUri);
        startActivityForResult(intent, 0);
    }

    public void handleAddPicture(View view) {
        addPicture();
    }
}
