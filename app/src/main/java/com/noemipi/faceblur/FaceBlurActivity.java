package com.noemipi.faceblur;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;

import java.io.File;
import java.util.List;

import androidx.annotation.RequiresApi;

public class FaceBlurActivity extends AppCompatActivity {
    private Bitmap mSourceImage;

    /**Detects faces in the picture passed by intent, and displays and saves the picture with the faces blurred
     * if the detection was successful.*/

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_blur);

        Intent intent = getIntent();
        final ImageView imageView = findViewById(R.id.imageView);
        final String currentPhotoPath = intent.getStringExtra("mCurrentPhotoPath");

        imageView.post(new Runnable() {
            /** Detects faces in sourceImage and display an output image where the faces are blurred.
             * Saves the output image. */

            @Override
            public void run() {
                mSourceImage = ImageHelper.getBitmapSizedToView(currentPhotoPath, imageView);
                Bitmap waitingImage = BlurBuilder.blur(FaceBlurActivity.this, mSourceImage);

                // Set a temporary Bitmap in imageView while waiting for the detection to complete.
                imageView.setImageBitmap(waitingImage);

                // Create a face detection task.
                FaceDetector faceDetector = new FaceDetector();
                Task<List<FirebaseVisionFace>> faceDetectionTask = faceDetector.detectFaceFromBitmap(mSourceImage);

                // On successful detection, blurs face in image, display the resulting image and save it.
                faceDetectionTask.addOnSuccessListener(
                        new OnSuccessListener<List<FirebaseVisionFace>>() {
                            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
                            @Override
                            public void onSuccess(List<FirebaseVisionFace> faces) {
                                // Task completed successfully.
                                blurDetectedFaces(faces, imageView, mSourceImage);
                            }
                        });

                // On failure with an exception, set the original image in imageView and display an error message.
                faceDetectionTask.addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                String toastMessage = "Face detection failed. " + e.getMessage();
                                Toast.makeText(FaceBlurActivity.this, toastMessage, Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
    }

    /** Takes a list of FirebaseVisionFace and an imageview,
     * Blurs the faces, display the resulting image and save it .*/

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void blurDetectedFaces(List<FirebaseVisionFace> faces, ImageView imageView, Bitmap sourceImage) {
        // If faces were successfully detected, blur them.
        if (faces != null) {
            Bitmap blurredFace = FaceBlurrer.drawBlur(FaceBlurActivity.this, faces, sourceImage);

            imageView.setImageBitmap(blurredFace);

            // Save the output image.
            File imageFile = FileManager.getNewImageFile(FaceBlurActivity.this);

            if (imageFile != null) {
                FileManager.saveImage(FaceBlurActivity.this, blurredFace, imageFile);
            }
        }
    }

}
