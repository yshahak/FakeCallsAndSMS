package com.belmedia.fakecallsandsms;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.belmedia.fakecallsandsms.activities.FakeSMS;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by B.E.L on 06/07/2015.
 */
public class Utils {

    final static int SecFactor = 1000, MinFactor = 1000*60;

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

    public static void loadImageFromUri(final Context context, int btnDp, Uri selectedImageUri, final TextView btn, final String TAG) {
        Target loadTarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                btn.setText(" ");
                BitmapDrawable bitmapDrawable = new BitmapDrawable(context.getResources(), bitmap);
                btn.setBackgroundDrawable(bitmapDrawable);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                Log.d(TAG, "failed");
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        int dimen = getDpInPixels(btnDp, context);
        Picasso.with(context)
                .load(selectedImageUri)
                .resize(dimen, dimen)
                .centerCrop()
                .into(loadTarget);
    }

    private void doCrop(Activity ctx, Uri picUri, int width, int height, int CROP_PIC_REQUEST_CODE) {
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(picUri, "image/*");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("outputX", width);
            cropIntent.putExtra("outputY", height);
            cropIntent.putExtra("return-data", true);
            ctx.startActivityForResult(cropIntent, CROP_PIC_REQUEST_CODE);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException e) {
            ExceptionHandler.handleException(e);
        }
    }

    public static  void pickImage(Activity activity, int SELECT_PICTURE) {
        Intent intent   = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activity.startActivityForResult(intent, SELECT_PICTURE);
    }



    public static int getTimeFromSpinner(int id, EditText editTextCustomTime, Spinner spinnerCustom, String TAG) {
        int timeForTrigger;
        switch (id){
            case R.id.radioButton10sec:
                timeForTrigger = 10 * SecFactor;
                break;
            case R.id.radioButton30sec:
                timeForTrigger = 30 * SecFactor;
                break;
            case R.id.radioButton5min:
                timeForTrigger = 5 * MinFactor;
                break;
            case R.id.radioButton15min:
                timeForTrigger = 15 * MinFactor;
                break;
            case R.id.radioButtonCustom:
                String customTime =  editTextCustomTime.getText().toString();
                if (customTime.equals(""))
                    timeForTrigger = 20 * SecFactor;
                else {
                    boolean spinnerIsInSeconds = (spinnerCustom.getSelectedItemPosition() == 0);
                    int value = Integer.valueOf(customTime);
                    Log.d(TAG, Integer.toString(value));
                    if (spinnerIsInSeconds)
                        timeForTrigger = value * SecFactor;
                    else
                        timeForTrigger = value * MinFactor;
                }

            default:
                timeForTrigger = 10 * SecFactor;
        }
        return timeForTrigger;
    }

    public static void pickContact(Activity activity, int selectContact) {
        Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
        pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE); // Show user only contacts w/ phone numbers
        activity.startActivityForResult(pickContactIntent, selectContact);

    }

    public static void loadContactFromUri(FakeSMS.ContactData data,@NonNull Context context, Uri selectedContactUri) {
        // Get the URI that points to the selected contact
        // We only need the NUMBER column, because there will be only one row in the result
        String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};

        // Perform the query on the contact to get the NUMBER column
        // We don't need a selection or sort order (there's only one result for the given URI)
        // CAUTION: The query() method should be called from a separate thread to avoid blocking
        // your app's UI thread. (For simplicity of the sample, this code doesn't do that.)
        // Consider using CursorLoader to perform the query.
        Cursor cursor = context.getContentResolver()
                .query(selectedContactUri, projection, null, null, null);
        cursor.moveToFirst();

        // Retrieve the phone number from the NUMBER column
        int columnNumber = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
        String number = cursor.getString(columnNumber);
        number = number.replaceAll("[^0-9]+", "");
        data.number = Long.valueOf(number);
        int columnName = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
        data.name = cursor.getString(columnName);
        cursor.close();
    }


}
