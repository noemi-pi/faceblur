package com.noemipi.faceblur;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.FirebaseApp;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private String mCurrentPhotoPath;
    private static final int REQUEST_TAKE_PHOTO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
    }

    /** Takes a picture, saves it to a file, then launch another Activity. */

    public void takePicture(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there is a camera activity to handle the intent.
        if(takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            photoFile = FileManager.getNewImageFile(this);

            // If the file was successfully created, take a picture and go to the next Activity.
            if(photoFile != null) {
                // Make the file visible to other apps.
                FileManager.scanToMediaScanner(this, photoFile);
                // Save necessary information to the intent and take picture.
                mCurrentPhotoPath = photoFile.getAbsolutePath();
                Uri photoURI = Uri.fromFile(photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    /** Launches the FaceBlurActivity and passes it the relevant intent data
     * if the picture was successfully captured. */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            Intent intent = new Intent(this, FaceBlurActivity.class);
            intent.putExtra("mCurrentPhotoPath", mCurrentPhotoPath);
            startActivity(intent);
        }
    }
}
