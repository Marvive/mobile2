package com.mmarvive.wgumobileproject.imagepackage;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.mmarvive.wgumobileproject.databasepackage.DatabaseHelper;
import com.mmarvive.wgumobileproject.databasepackage.DatabaseManager;
import com.mmarvive.wgumobileproject.databasepackage.DatabaseProvider;
import com.mmarvive.wgumobileproject.DateUtility;
import com.mmarvive.wgumobileproject.R;

import java.io.File;
import java.util.Date;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

/**
 * Activity manager for images
 * */

public class ImageListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private CursorAdapter cursorAdapter;
    private Uri parentUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        parentUri = getIntent().getParcelableExtra("ParentUri");
        bindImagesList();
        getLoaderManager().initLoader(0, null, this);
    }

    private void bindImagesList() {
        String[] from = {DatabaseHelper.IMAGE_TIMESTAMP, DatabaseHelper.IMAGE_TIMESTAMP};
        int[] to = {R.id.imageView, R.id.imageText};
        cursorAdapter = new MySimpleCursorAdapter(this, R.layout.image_list_item, null, from, to);
        ListView list = findViewById(R.id.lvImages);
        list.setAdapter(cursorAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Image image = DatabaseManager.getImage(ImageListActivity.this, id);
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                File file = new File(getExternalFilesDir(null) + "/term_tracker_images" + image.timestamp + ".jpg");
                intent.setDataAndType(FileProvider.getUriForFile(ImageListActivity.this, ImageListActivity.this
                        .getApplicationContext().getPackageName() + ".com.mmarvive.wgumobileproject.provider", file), "image/*");
                Toast.makeText(ImageListActivity.this, FileProvider.getUriForFile(ImageListActivity.this, ImageListActivity.this.getApplicationContext().getPackageName() + ".com.mmarvive.wgumobileproject.provider", file).toString(), Toast.LENGTH_LONG).show();
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        restartLoader();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, DatabaseProvider.IMAGES_URI, DatabaseHelper.IMAGES_COLUMNS,
                DatabaseHelper.IMAGE_PARENT_URI + " = " + "'" + parentUri + "'", null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }

    private void restartLoader() {
        getLoaderManager().restartLoader(0, null, this);
    }

    public class MySimpleCursorAdapter extends SimpleCursorAdapter {

        MySimpleCursorAdapter(Context context, int layout, Cursor cursor, String[] from, int[] to) {
            super(context, layout, cursor, from, to);
        }

        @Override
        public void setViewText(TextView textView, String text) {
            long timestamp = Long.parseLong(text);
            Date date = new Date(timestamp);
            textView.setText("Taken: " + DateUtility.dateTimeFormat.format(date));
        }

        @Override
        public void setViewImage(ImageView imageView, String id) {
            String path = getExternalFilesDir(null) + "/term_tracker_images/" + id + "_thumb.png";
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            imageView.setImageBitmap(bitmap);
        }
    }
}
