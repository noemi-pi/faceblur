package com.noemipi.faceblur;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

/** Provides methods to edit and format images. */

public class ImageHelper {

    /** Takes a String imagePath, and returns a Bitmap fitted to the dimensions of an ImageView.
     * If unable to resize, it returns a bitmap in its original size instead */

    public static Bitmap getBitmapSizedToView(String imagePath, ImageView imageView) {
        // Get the dimensions of the View
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();
        Bitmap bitmap;

        try {
            bitmap = getResizedBitmapFromPath(imagePath, targetW, targetH);
        } catch (IllegalArgumentException e) {
            //Invalid dimensions for ImageView. Returning bitmap in its original size instead.
            bitmap = BitmapFactory.decodeFile(imagePath);
        }

        return bitmap;
    }

    /** Takes a String imagePath, and returns a Bitmap fitted to the target dimensions targetW and targetH.
     * the bitmap's target width and height targetW and targetH must be strictly greater than 0 */

    public static Bitmap getResizedBitmapFromPath(String imagePath, int targetW, int targetH) throws IllegalArgumentException {
        if(targetW <= 0 || targetH <= 0) {
            throw(new IllegalArgumentException("invalid target dimensions: width or height is lesser or equal to zero"));
        }
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the view
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bmOptions);

        return bitmap;
    }

}
