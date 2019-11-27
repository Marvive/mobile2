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

    private EditText editTextCourseName;
    private EditText editTextCourseStart;
    private EditText editTextCourseEnd;
    private EditText editTextCourseMentor;
    private EditText editTextCourseMentorPhone;
    private EditText editTextCourseMentorEmail;
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
        } else {
            action = Intent.ACTION_EDIT;
            setTitle(getString(R.string.edit_course_title));
            long classId = Long.parseLong(Objects.requireNonNull(courseUri.getLastPathSegment()));
            course = DatabaseManager.geditTextCourse(this, classId);
            fillCourseForm(course);
        }
        datePickerSet();
    }

    private void findViews() {
        editTextCourseName = findViewById(R.id.editTextCourseName);
        editTextCourseStart = findViewById(R.id.editTextCourseStart);
        editTextCourseEnd = findViewById(R.id.editTextCourseEnd);
        editTextCourseMentor = findViewById(R.id.editTextCourseMentor);
        editTextCourseMentorPhone = findViewById(R.id.editTextCourseMentorPhone);
        editTextCourseMentorEmail = findViewById(R.id.editTextCourseMentorEmail);
    }

    private void fillCourseForm(Course course) {
        editTextCourseName.setText(course.name);
        editTextCourseStart.setText(course.start);
        editTextCourseEnd.setText(course.end);
        editTextCourseMentor.setText(course.mentor);
        editTextCourseMentorPhone.setText(course.mentorPhone);
        editTextCourseMentorEmail.setText(course.mentorEmail);
    }

    private void datePickerSet() {
        editTextCourseStart.setOnClickListener(this);
        editTextCourseEnd.setOnClickListener(this);

        Calendar calendar = Calendar.getInstance();
        courseStartDateDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, month, dayOfMonth);
                editTextCourseStart.setText(DateUtility.dateFormat.format(newDate.getTime()));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        courseEndDateDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, month, dayOfMonth);
                editTextCourseEnd.setText(DateUtility.dateFormat.format(newDate.getTime()));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        editTextCourseStart.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    courseStartDateDialog.show();
                }
            }
        });

        editTextCourseEnd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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
                    editTextCourseName.getText().toString().trim(),
                    editTextCourseStart.getText().toString().trim(),
                    editTextCourseEnd.getText().toString().trim(),
                    editTextCourseMentor.getText().toString().trim(),
                    editTextCourseMentorPhone.getText().toString().trim(),
                    editTextCourseMentorEmail.getText().toString().trim(),
                    CourseStatus.PLANNED);
        }
        else if (action.equals(Intent.ACTION_EDIT)) {
            course.name = editTextCourseName.getText().toString().trim();
            course.start = editTextCourseStart.getText().toString().trim();
            course.end = editTextCourseEnd.getText().toString().trim();
            course.mentor = editTextCourseMentor.getText().toString().trim();
            course.mentorPhone = editTextCourseMentorPhone.getText().toString().trim();
            course.mentorEmail = editTextCourseMentorEmail.getText().toString().trim();
            course.saveChanges(this);
            setResult(RESULT_OK);
        }
        finish();
    }

    @Override
    public void onClick(View view) {
        if (view == editTextCourseStart) {
            courseStartDateDialog.show();
        } else if (view == editTextCourseEnd) {
            courseEndDateDialog.show();
        }
    }
}
