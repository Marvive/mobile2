package com.mmarvive.wgumobileproject.coursepackage;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import com.mmarvive.wgumobileproject.databasepackage.DatabaseManager;
import com.mmarvive.wgumobileproject.databasepackage.DataProvider;
import com.mmarvive.wgumobileproject.DateUtility;
import com.mmarvive.wgumobileproject.R;

import java.util.Calendar;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/**
 * Class for Tracking Course editor activity
 * */

public class CourseEditorActivity extends AppCompatActivity implements View.OnClickListener {

    private String action;
    private Uri termUri;
    private Course course;

    private EditText etCourseName;
    private EditText etCourseStart;
    private EditText etCourseEnd;
    private EditText etCourseMentor;
    private EditText etCourseMentorPhone;
    private EditText etCourseMentorEmail;
    private DatePickerDialog courseStartDateDialog;
    private DatePickerDialog courseEndDateDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_editor);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        findViews();
        Intent intent = getIntent();
        Uri courseUri = intent.getParcelableExtra(DataProvider.COURSE_CONTENT_TYPE);
        termUri = intent.getParcelableExtra(DataProvider.TERM_CONTENT_TYPE);

        if (courseUri == null) {
            action = Intent.ACTION_INSERT;
            setTitle(getString(R.string.add_new_course));
        }
        else {
            action = Intent.ACTION_EDIT;
            setTitle(getString(R.string.edit_course_title));
            long classId = Long.parseLong(Objects.requireNonNull(courseUri.getLastPathSegment()));
            course = DatabaseManager.getCourse(this, classId);
            fillCourseForm(course);
        }
        setupDatePickers();
    }

    private void findViews() {
        etCourseName = findViewById(R.id.etCourseName);
        etCourseStart = findViewById(R.id.etCourseStart);
        etCourseEnd = findViewById(R.id.etCourseEnd);
        etCourseMentor = findViewById(R.id.etCourseMentor);
        etCourseMentorPhone = findViewById(R.id.etCourseMentorPhone);
        etCourseMentorEmail = findViewById(R.id.etCourseMentorEmail);
    }

    private void fillCourseForm(Course course) {
        etCourseName.setText(course.name);
        etCourseStart.setText(course.start);
        etCourseEnd.setText(course.end);
        etCourseMentor.setText(course.mentor);
        etCourseMentorPhone.setText(course.mentorPhone);
        etCourseMentorEmail.setText(course.mentorEmail);
    }

    private void setupDatePickers() {
        etCourseStart.setOnClickListener(this);
        etCourseEnd.setOnClickListener(this);

        Calendar calendar = Calendar.getInstance();
        courseStartDateDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, month, dayOfMonth);
                etCourseStart.setText(DateUtility.dateFormat.format(newDate.getTime()));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        courseEndDateDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, month, dayOfMonth);
                etCourseEnd.setText(DateUtility.dateFormat.format(newDate.getTime()));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        etCourseStart.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    courseStartDateDialog.show();
                }
            }
        });

        etCourseEnd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    courseEndDateDialog.show();
                }
            }
        });
    }

    public void saveCourseChanges(View view) {
        if (action.equals(Intent.ACTION_INSERT)) {
            long termId = Long.parseLong(Objects.requireNonNull(termUri.getLastPathSegment()));
            DatabaseManager.insertCourse(this, termId,
                    etCourseName.getText().toString().trim(),
                    etCourseStart.getText().toString().trim(),
                    etCourseEnd.getText().toString().trim(),
                    etCourseMentor.getText().toString().trim(),
                    etCourseMentorPhone.getText().toString().trim(),
                    etCourseMentorEmail.getText().toString().trim(),
                    CourseStatus.PLANNED);
        }
        else if (action.equals(Intent.ACTION_EDIT)) {
            course.name = etCourseName.getText().toString().trim();
            course.start = etCourseStart.getText().toString().trim();
            course.end = etCourseEnd.getText().toString().trim();
            course.mentor = etCourseMentor.getText().toString().trim();
            course.mentorPhone = etCourseMentorPhone.getText().toString().trim();
            course.mentorEmail = etCourseMentorEmail.getText().toString().trim();
            course.saveChanges(this);
            setResult(RESULT_OK);
        }
        finish();
    }

    @Override
    public void onClick(View view) {
        if (view == etCourseStart) {
            courseStartDateDialog.show();
        }
        if (view == etCourseEnd) {
            courseEndDateDialog.show();
        }
    }
}
