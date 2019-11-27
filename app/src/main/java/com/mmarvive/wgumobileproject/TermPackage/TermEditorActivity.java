package com.mmarvive.wgumobileproject.termpackage;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.mmarvive.wgumobileproject.databasepackage.DatabaseManager;
import com.mmarvive.wgumobileproject.databasepackage.DataProvider;
import com.mmarvive.wgumobileproject.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/**
 * Activity for Term Edit
 * */

public class TermEditorActivity extends AppCompatActivity implements View.OnClickListener {

    private String action;
    private Term term;

    private EditText termNameField;
    private EditText termStartDateField;
    private EditText termEndDateField;

    private DatePickerDialog termStartDateDialog;
    private DatePickerDialog termEndDateDialog;
    private SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_editor);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        termNameField = findViewById(R.id.termNameEditText);
        termStartDateField = findViewById(R.id.termStartDateEditText);
        termStartDateField.setInputType(InputType.TYPE_NULL);
        termEndDateField = findViewById(R.id.termEndDateEditText);
        termEndDateField.setInputType(InputType.TYPE_NULL);

        dateFormat = new SimpleDateFormat("MM-dd-yyyy", Locale.US);

        Intent intent = getIntent();
        Uri uri = intent.getParcelableExtra(DataProvider.TERM_CONTENT_TYPE);

        if (uri == null) {
            action = intent.ACTION_INSERT;
            setTitle(getString(R.string.add_new_term));
        } else {
            action = Intent.ACTION_EDIT;
            setTitle(getString(R.string.edit_term_title));
            long termId = Long.parseLong(Objects.requireNonNull(uri.getLastPathSegment()));
            term = DatabaseManager.getTerm(this, termId);
            fillTermForm(term);
        }
        datePickerSet();
    }

    private void fillTermForm(Term term) {
        termNameField.setText(term.name);
        termStartDateField.setText(term.start);
        termEndDateField.setText(term.end);
    }

    private void getTermFromForm() {
        term.name = termNameField.getText().toString().trim();
        term.start = termStartDateField.getText().toString().trim();
        term.end = termEndDateField.getText().toString().trim();
    }

// Utilizes datePicker to grab a proper date format
    private void datePickerSet() {
        termStartDateField.setOnClickListener(this);
        termEndDateField.setOnClickListener(this);

        Calendar calendar = Calendar.getInstance();
        termStartDateDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, month, dayOfMonth);
                termStartDateField.setText(dateFormat.format(newDate.getTime()));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        termEndDateDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, month, dayOfMonth);
                termEndDateField.setText(dateFormat.format(newDate.getTime()));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        termStartDateField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    termStartDateDialog.show();
                }
            }
        });
    }

//    Saves changes to Database

    public void saveTermChanges(View view) {
        if (action.equals(Intent.ACTION_INSERT)) {
            term = new Term();
            getTermFromForm();

            DatabaseManager.insertTerm(this, term.name, term.start, term.end, term.active);
            Toast.makeText(this, getString(R.string.term_saved), Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
        } else if (action.equals(Intent.ACTION_EDIT)) {
            getTermFromForm();
            term.saveChanges(this);
            Toast.makeText(this, getString(R.string.term_updated), Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
        }
        finish();
    }

    @Override
    public void onClick(View view) {
        if (view == termStartDateField) {
            termStartDateDialog.show();
        }
        if (view == termEndDateField) {
            termEndDateDialog.show();
        }
    }
}
