package com.belmedia.fakecallsandsms;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by B.E.L on 06/07/2015.
 */
public class Utils {

    public static Bitmap getBitmapFromUri(Uri selectedImageUri, Context context, int width, int height){
        Bitmap bm = null;
        try {
            InputStream inputStream;
            ContentResolver contentResolver = context.getContentResolver();
            inputStream = contentResolver.openInputStream(selectedImageUri);
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(inputStream, null, options);
            // Calculate inSampleSize
            options.inSampleSize = Utils.calculateInSampleSize(options, width, height);
            options.inJustDecodeBounds = false;
            inputStream = contentResolver.openInputStream(selectedImageUri);
            bm = BitmapFactory.decodeStream(inputStream, null, options);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bm;
    }

    public static Bitmap getBitmapFromBitmp(Bitmap bm, int newWidth, int newHeight){
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);
        // "RECREATE" THE NEW BITMAP
        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static int getDpInPixels(int dpValue, Context ctx) {
        if (ctx != null) {
            float d = ctx.getResources().getDisplayMetrics().density;
            return (int) (dpValue * d); // margin in pixels
        } else {
            return dpValue;
        }
    }

    /**
     * Function to convert milliseconds time to Timer Format
     * Hours:Minutes:Seconds
     * */
    public static String milliSecondsToTimer(int secondsReceives) {
        String finalTimerString = "";
        String secondsStringSeconds;
        String secondsStringMinutes;


        // Convert total duration into time
        int seconds = (secondsReceives % 60);
        int minutes = (secondsReceives / 60);

        // appending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsStringSeconds = "0" + seconds;
        } else {
            secondsStringSeconds = "" + seconds;
        }

        // appending 0 to minutes if it is one digit
        if (minutes < 10) {
            secondsStringMinutes = "0" + minutes;
        } else {
            secondsStringMinutes = "" + minutes;
        }

        finalTimerString = finalTimerString + secondsStringMinutes + ":" + secondsStringSeconds;
        // return timer string
        return finalTimerString;
    }

}
