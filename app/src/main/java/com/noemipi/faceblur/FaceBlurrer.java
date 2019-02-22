package com.noemipi.faceblur;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Build;

import com.google.firebase.ml.vision.common.FirebaseVisionPoint;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceContour;

import java.util.List;

import androidx.annotation.RequiresApi;

/** Blurs faces in a picture. */
public class FaceBlurrer {
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)

    /** Blurs faces in a picture from a list of face detections.
     * Returns the resulting Bitmap. */

    public static Bitmap drawBlur(Context context, List<FirebaseVisionFace> faces, Bitmap bitmap)  {
        Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(mutableBitmap);

        if(faces.size() != 0) {
            Bitmap blurredImage = BlurBuilder.blur(context, bitmap);

            Bitmap output = Bitmap.createBitmap(blurredImage.getWidth(), blurredImage.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas outCanvas = new Canvas(output);

            for(FirebaseVisionFace face : faces) {
                final Paint paint = new Paint();
                final Rect rect = new Rect(0,0, blurredImage.getWidth(), blurredImage.getHeight());
                final int color = 0xff424242;
                List<FirebaseVisionPoint> faceOvalContour = face.getContour(FirebaseVisionFaceContour.FACE).getPoints();

                paint.setAntiAlias(true);
                outCanvas.drawARGB(0,0,0,0);
                paint.setColor(color);

                // Draw a Path representing the face oval
                outCanvas.drawPath(pathFromFaceContour(faceOvalContour, blurredImage.getWidth(), blurredImage.getHeight()), paint);
                //Use PorterDuffXfermode to get cutouts of the faces from the blurred image.
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
                outCanvas.drawBitmap(blurredImage, rect, rect, paint);
            }
            canvas.drawBitmap(output, 0, 0, new Paint());
            return mutableBitmap;
        }
        return mutableBitmap;
    }

    /** Creates and returns a closed Path from a list of points representing the contour of a face. */

    private static Path pathFromFaceContour(List<FirebaseVisionPoint> faceOvalContour, int imWidth, int imHeight) {
        Path path = new Path();

        path.moveTo(faceOvalContour.get(0).getX(), faceOvalContour.get(0).getY());

        for(int i = 1; i < faceOvalContour.size()-1; i++) {
            path.lineTo(faceOvalContour.get(i).getX(), faceOvalContour.get(i).getY());
        }
        path.close();

        return path;
    }
}
