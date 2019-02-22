package com.noemipi.faceblur;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileManager {
    private static final int REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE = 2;

    /** Creates and returns a new image File. If file creation fails, returns null */

    private static File createImageFile(Activity activity) throws IOException {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Permission not granted, initiate request.
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE);
        } else if (isExternalStorageWritable()){
            // Create an image file name.
            String imageFileName = createImageFileName();
            // Gets the directory for the application's album
            File storageDir = getPublicAlbumStorageDir(activity
                    .getResources()
                    .getString(R.string.app_name));
            // Create the image File
            File image = File.createTempFile(
                    imageFileName,
                    ".png",
                    storageDir
            );

            return image;
        }

        return null;
    }

    /** Creates and returns a new image File. If file creation fails,
     * displays a Toast with an error message and returns null */

    public static File getNewImageFile(Activity activity){
        File imageFile = null;
        try {
            imageFile = createImageFile(activity);
        } catch (IOException e) {
            // Error occurred while creating the file.
            String toastMessage = "File creation failed. " + e.getMessage();
            Toast.makeText(activity, toastMessage, Toast.LENGTH_LONG).show();
        }

        return imageFile;
    }

    /** Gets the directory for a specific album in the user's public pictures directory.
     * If the album does not exist, create it.*/

    public static File getPublicAlbumStorageDir(String albumName) {
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);

        return file;
    }

    /** Creates a unique image file name. */

    private static String createImageFileName() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        return imageFileName;
    }


    /* Checks if external storage is available for read and write */

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }

        return false;
    }

    /* Save Bitmap image to a File */

    public static void saveImage(Context context, Bitmap bitmap, File file) {
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            OutputStreamWriter outputStreamWriter= new OutputStreamWriter(outputStream);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.close();
            scanToMediaScanner(context, file);
        } catch (IOException e) {
            String errorMessage = "failed to save file " + file.getAbsolutePath() + " " + e.getMessage();
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    /** Scans file to MediaScanner. */

    public static void scanToMediaScanner(Context  context, File file) {
        Intent mediaIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaIntent.setData(Uri.fromFile(file));
        context.sendBroadcast(mediaIntent);
    }

}
