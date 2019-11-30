package com.mmarvive.wgumobileproject.termpackage;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mmarvive.wgumobileproject.coursepackage.CourseListActivity;
import com.mmarvive.wgumobileproject.databasepackage.DatabaseHelper;
import com.mmarvive.wgumobileproject.databasepackage.DatabaseManager;
import com.mmarvive.wgumobileproject.databasepackage.DatabaseProvider;
import com.mmarvive.wgumobileproject.R;

import java.util.ArrayList;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/**
 * Activity for Term View
 * */

public class TermViewActivity extends AppCompatActivity {

//    Constants for Class
    private static final int TERM_EDITOR_ACTIVITY_CODE = 11111;
    private static final int COURSE_LIST_ACTIVITY_CODE = 22222;

//    Private Variables
    private Uri termUri;
    private Term term;
    private TextView textView_title;
    private TextView textView_start;
    private TextView textView_end;
    private Menu menu;

    private long termId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_viewer);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        termUri = intent.getParcelableExtra(DatabaseProvider.TERM_CONTENT_TYPE);
        findElements();
        loadTermData();
    }

    private void findElements() {
        textView_title = findViewById(R.id.textViewTermViewTermTitle);
        textView_start = findViewById(R.id.textViewTermViewStartDate);
        textView_end = findViewById(R.id.textViewTermViewEndDate);
    }

    private void loadTermData() {
        if (termUri == null) {
            setResult(RESULT_CANCELED);
            finish();
        } else {
            termId = Long.parseLong(Objects.requireNonNull(termUri.getLastPathSegment()));
            term = DatabaseManager.getTerm(this, termId);

            setTitle(getString(R.string.view_term));
            textView_title.setText(term.name);
            textView_start.setText(term.start);
            textView_end.setText(term.end);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_term_viewer, menu);
        this.menu = menu;
        showMenuOptions();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_mark_term_active:
                return markTermActive();
            case R.id.action_edit_term:
                Intent intent = new Intent(this, TermEditScreenActivity.class);
                Uri uri = Uri.parse(DatabaseProvider.TERMS_URI + "/" + term.termId);
                intent.putExtra(DatabaseProvider.TERM_CONTENT_TYPE, uri);
                startActivityForResult(intent, TERM_EDITOR_ACTIVITY_CODE);
                break;
            case R.id.action_delete_term:
                return deleteTerm();
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

//    Changes the active value

    private boolean markTermActive() {
        Cursor cursor = getContentResolver().query(DatabaseProvider.TERMS_URI, null, null, null, null);
        ArrayList<Term> termList = new ArrayList<>();
        assert cursor != null;
        while (cursor.moveToNext()) {
            termList.add(DatabaseManager.getTerm(this, cursor.getLong(cursor.getColumnIndex(DatabaseHelper.TERMS_TABLE_ID))));
        }

        for (Term term : termList) {
            term.deactivate(this);
        }

        this.term.activate(this);
        showMenuOptions();

        Toast.makeText(TermViewActivity.this, getString(R.string.term_marked_active), Toast.LENGTH_SHORT).show();
        return true;
    }

//    Completely deletes the term unless there are courses present inside of it

    private boolean deleteTerm() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int button) {
                if (button == DialogInterface.BUTTON_POSITIVE) {
                    long classCount = term.getClassCount(TermViewActivity.this);
                    if (classCount == 0) {
                        getContentResolver().delete(DatabaseProvider.TERMS_URI, DatabaseHelper.TERMS_TABLE_ID + " = " + termId, null);

                        Toast.makeText(TermViewActivity.this, getString(R.string.term_deleted), Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Toast.makeText(TermViewActivity.this, getString(R.string.need_to_remove_courses), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.confirm_delete_term)
                .setPositiveButton(getString(android.R.string.yes), dialogClickListener)
                .setNegativeButton(getString(android.R.string.no), dialogClickListener)
                .show();
        return true;
    }

    public void showMenuOptions() {
        if (term.active == 1) {
            menu.findItem(R.id.action_mark_term_active).setVisible(false);
        }
    }

    public void openClassList(View view) {
        Intent intent = new Intent(this, CourseListActivity.class);
        intent.putExtra(DatabaseProvider.TERM_CONTENT_TYPE, termUri);
        startActivityForResult(intent, COURSE_LIST_ACTIVITY_CODE);
    }
}
