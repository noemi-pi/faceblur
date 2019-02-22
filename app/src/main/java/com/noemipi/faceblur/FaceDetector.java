package com.noemipi.faceblur;

import android.graphics.Bitmap;

import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;

import java.util.List;

public class FaceDetector {

    public List<FirebaseVisionFace> detect(Bitmap bitmap) {
        List<FirebaseVisionFace> faces = null;

        Task<List<FirebaseVisionFace>> faceDetectionTask = detectFaceFromBitmap(bitmap);

        while(!faceDetectionTask.isComplete()) {
            //
        }

        if(faceDetectionTask.isSuccessful()) {
            faces = faceDetectionTask.getResult();
        }

        return faces;
    }

    public Task<List<FirebaseVisionFace>> detectFaceFromBitmap(Bitmap bitmap) {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionFaceDetectorOptions options =
                new FirebaseVisionFaceDetectorOptions.Builder()
                        .setContourMode(FirebaseVisionFaceDetectorOptions.ALL_CONTOURS)
                        .build();

        return detectFaceTask(image, options);
    }


    public Task<List<FirebaseVisionFace>> detectFaceTask(FirebaseVisionImage image, FirebaseVisionFaceDetectorOptions options){
        FirebaseVisionFaceDetector detector = FirebaseVision.getInstance()
                .getVisionFaceDetector(options);

        Task<List<FirebaseVisionFace>> result = detector.detectInImage(image);

        return result;
    }
}
