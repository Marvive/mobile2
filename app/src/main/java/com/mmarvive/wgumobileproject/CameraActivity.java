package com.mmarvive.wgumobileproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import com.mmarvive.wgumobileproject.databasepackage.DatabaseManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

/**
 * Camera Handler
 * */
public class CameraActivity extends AppCompatActivity {

    protected boolean pictureTaken;
    protected static final String PICTURE_TAKEN = "photo_taken";
    protected String folder;
    protected String picturePath;
    private Uri parentUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        parentUri = getIntent().getParcelableExtra("PARENT_URI");
        folder = getExternalFilesDir(null) + "/term_tracker_images/";
        picturePath = folder + "tmpImage.jpg";
        takePicture();
    }

    protected void takePicture() {
        File folderFile = new File(folder);
        folderFile.mkdir();

        File file = new File(picturePath);
        Uri outputFileUri = FileProvider.getUriForFile(this, this.getApplicationContext()
                .getPackageName() + ".com.mmarvive.wgumobileproject.provider", file);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case 0:
                setResult(RESULT_CANCELED);
                finish();
            case -1:
                onPictureTaken();
                break;
        }
    }

    protected void onPictureTaken() {
        pictureTaken = true;
        long now = DateUtility.todayLongWithTime();
        File from = new File(picturePath);
        File to = new File(folder + now + ".jpg");
        from.renameTo(to);
        Bitmap src = BitmapFactory.decodeFile(folder + now + ".jpg");
        if (src == null) {
            setResult(RESULT_CANCELED);
            finish();
            return;
        }
        DatabaseManager.insertImage(this, parentUri, now);
        Bitmap thumb = ThumbnailUtils.extractThumbnail(src, (src.getWidth() / 5), (src.getHeight() / 5));
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(folder + now + "_thumb.png");
            thumb.compress(Bitmap.CompressFormat.PNG, 100, out);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (out != null) {
                    out.close();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        setResult(RESULT_OK);
        finish();
    }

    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(PICTURE_TAKEN, pictureTaken);
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState.getBoolean(PICTURE_TAKEN)) {
            onPictureTaken();
        }
    }
}
