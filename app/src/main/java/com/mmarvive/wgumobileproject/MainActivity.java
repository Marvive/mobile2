package com.mmarvive.wgumobileproject;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.mmarvive.wgumobileproject.databasepackage.DatabaseProvider;
import com.mmarvive.wgumobileproject.databasepackage.DatabaseHelper;
import com.mmarvive.wgumobileproject.termpackage.TermListActivity;
import com.mmarvive.wgumobileproject.termpackage.TermViewActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/**
 * Startup Main
 * */

public class MainActivity extends AppCompatActivity {

    private static final int TERM_VIEWER_ACTIVITY_CODE = 11111;
    private static final int TERM_LIST_ACTIVITY_CODE = 22222;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void openCurrentTerm(View view) {
        Cursor c = getContentResolver().query(DatabaseProvider.TERMS_URI, null, DatabaseHelper.TERM_ACTIVE
                + " =1", null, null);
        assert c != null;
        while (c.moveToNext()) {
            Intent intent = new Intent(this, TermViewActivity.class);
            long id = c.getLong(c.getColumnIndex(DatabaseHelper.TERMS_TABLE_ID));
            Uri uri = Uri.parse(DatabaseProvider.TERMS_URI + "/" + id);
            intent.putExtra(DatabaseProvider.TERM_CONTENT_TYPE, uri);
            startActivityForResult(intent, TERM_VIEWER_ACTIVITY_CODE);
            return;
        }
        Toast.makeText(this, getString(R.string.no_active_term_set),
                Toast.LENGTH_SHORT).show();
    }

    public void openTermList(View view) {
        Intent intent = new Intent(this, TermListActivity.class);
        startActivityForResult(intent, TERM_LIST_ACTIVITY_CODE);
    }
}
