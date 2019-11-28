package com.mmarvive.wgumobileproject.assessmentpackage;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.mmarvive.wgumobileproject.databasepackage.DatabaseManager;
import com.mmarvive.wgumobileproject.DateUtility;
import com.mmarvive.wgumobileproject.R;
import com.mmarvive.wgumobileproject.databasepackage.DataProvider;

import java.util.Calendar;
import java.util.Objects;
import java.util.TimeZone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/**
 * Inherits AppCompatActivity and implements the View.OnClickListener Interface
 * */

public class AssessmentEditorActivity extends AppCompatActivity implements View.OnClickListener {

    private Assessment assessment;
    private long courseId;

    private EditText editTextAssessmentCode;
    private EditText editTextAssessmentName;
    private EditText editTextAssessmentDescription;
    private EditText editTextAssessmentDatetime;

    private DatePickerDialog assessmentDateDialog;
    private TimePickerDialog assessmentTimeDialog;
    private String action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println(getIntent().getParcelableExtra(DataProvider.ASSESSMENT_CONTENT_TYPES));
        System.out.println("Editor");
        setContentView(R.layout.activity_assessment_editor);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        findViews();

        Uri assessmentUri = getIntent().getParcelableExtra(DataProvider.ASSESSMENT_CONTENT_TYPES);
//        AssessmentUri is null always
        if (assessmentUri == null) {

            setTitle(getString(R.string.new_assessment));
            action = Intent.ACTION_INSERT;
            Uri courseUri = getIntent().getParcelableExtra(DataProvider.COURSE_CONTENT_TYPE);
            courseId = Long.parseLong(Objects.requireNonNull(courseUri.getLastPathSegment()));
            assessment = new Assessment();
        } else {
            setTitle(getString(R.string.edit_assessment));
            action = Intent.ACTION_EDIT;
            long assessmentId = Long.parseLong(Objects.requireNonNull(assessmentUri.getLastPathSegment()));
            assessment = DatabaseManager.getAssessment(this, assessmentId);
            courseId = assessment.courseId;
            fillAssessmentForm(assessment);
        }
        setupDateAndTimePickers();
    }

    private void findViews() {
        editTextAssessmentCode = findViewById(R.id.editTextAssessmentCode);
        editTextAssessmentName = findViewById(R.id.editTextAssessmentName);
        editTextAssessmentDescription = findViewById(R.id.editTextAssessmentDescription);
        editTextAssessmentDatetime = findViewById(R.id.editTextAssessmentDatetime);
    }

    private void fillAssessmentForm(Assessment assessment) {
//        if (assessment != null) {
            editTextAssessmentCode.setText(assessment.code);
            editTextAssessmentName.setText(assessment.name);
            editTextAssessmentDescription.setText(assessment.description);
            editTextAssessmentDatetime.setText(assessment.datetime);
//        }
    }

    private void setupDateAndTimePickers() {
        editTextAssessmentDatetime.setOnClickListener(this);

        Calendar calendar = Calendar.getInstance();
        assessmentDateDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar calendar2 = Calendar.getInstance();
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, month, dayOfMonth);
                editTextAssessmentDatetime.setText(DateUtility.dateFormat.format(newDate.getTime()));
                assessmentTimeDialog = new TimePickerDialog(AssessmentEditorActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//                       Changes format from military time to regular
                        String AM_PM;
                        if (hourOfDay < 12) {
                            AM_PM = "AM";
                        } else {
                            AM_PM = "PM";
                        }
                        if (hourOfDay > 12) {
                            hourOfDay = hourOfDay - 12;
                        } else if (hourOfDay == 0) {
                            hourOfDay = 12;
                        }
                        String minuteString = Integer.toString(minute);
                        if (minute < 10) {
                            minuteString = "0" + minuteString;
                        }
                        String datetime = editTextAssessmentDatetime.getText().toString() + " " + hourOfDay + ":" + minuteString
                                + " " + AM_PM + " " + TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT);
                        editTextAssessmentDatetime.setText(datetime);
                    }
                }, calendar2.get(Calendar.HOUR_OF_DAY), calendar2.get(Calendar.MINUTE), false);
                assessmentTimeDialog.show();
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        editTextAssessmentDatetime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    assessmentDateDialog.show();
                }
            }
        });
    }

//    Saves changes to database

    public void saveAssessmentChanges(View view) {
        getValuesFromFields();
        switch (action) {
            case Intent.ACTION_INSERT:
                DatabaseManager.insertAssessment(this, courseId, assessment.code, assessment.name, assessment.description,
                        assessment.datetime);
                setResult(RESULT_OK);
                finish();
                break;
            case Intent.ACTION_EDIT:
                assessment.saveChanges(this);
                setResult(RESULT_OK);
                finish();
                break;
            default:
                throw new UnsupportedOperationException();
        }
    }

    private void getValuesFromFields() {
        assessment.code = editTextAssessmentCode.getText().toString().trim();
        assessment.name = editTextAssessmentName.getText().toString().trim();
        assessment.description = editTextAssessmentDescription.getText().toString().trim();
        assessment.datetime = editTextAssessmentDatetime.getText().toString().trim();
    }

    @Override
    public void onClick(View view) {
        if (view == editTextAssessmentDatetime) {
            assessmentDateDialog.show();
        }
    }
}
