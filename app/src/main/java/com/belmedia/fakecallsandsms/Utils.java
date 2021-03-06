package com.belmedia.fakecallsandsms;

import android.app.Activity;
import android.app.Application;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.belmedia.fakecallsandsms.sms.ChatMessage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

/**
 * Created by B.E.L on 06/07/2015.
 */
public class Utils {

    final static int SecFactor = 1000, MinFactor = 1000*60;
    public final static String KEY_NAME = "name", KEY_NUMBER = "number", KEY_THUMBNAIL = "thumbnail";

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

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight){
        Bitmap bm = null;
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static Bitmap getBitmapFromBitmap(Bitmap bm, int newWidth, int newHeight){
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

    @SuppressWarnings("deprecation")
    public static void loadImageFromUri(@NonNull final Context context,@NonNull final Uri selectedImageUri,@NonNull final ImageView btn) {
        int width = btn.getWidth(), height = btn.getHeight();
        Picasso.with(context)
                .load(selectedImageUri)
                .resize(width, height)
                .centerCrop()
                .into(btn);
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
            case R.id.radioButton5sec:
                timeForTrigger = 5 * SecFactor;
                break;
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
            case R.id.radioButton30min:
                timeForTrigger = 30 * MinFactor;
                break;
            case R.id.radioButtonCustom:
                String customTime =  editTextCustomTime.getText().toString();
                if (customTime.equals(""))
                    timeForTrigger = 20 * SecFactor;
                else {
                    boolean spinnerIsInSeconds = (spinnerCustom.getSelectedItemPosition() == 0);
                    int value;
                    try{
                        value = Integer.valueOf(customTime);
                    } catch (NumberFormatException e){
                        value = 10;
                    }
                    if (spinnerIsInSeconds)
                        timeForTrigger = value * SecFactor;
                    else
                        timeForTrigger = value * MinFactor;
                }
                break;
            default:
                timeForTrigger = 10 * SecFactor;
        }
        return timeForTrigger;
    }

    public static void pickContact(Activity activity, int selectContact) {
        Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
        pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE); // Show user only contacts w/ phone numbers
        try {
            activity.startActivityForResult(pickContactIntent, selectContact);
        }catch (ActivityNotFoundException e){
            ExceptionHandler.handleException(e);
            Toast.makeText(activity, "No Contact App to handle this", Toast.LENGTH_LONG).show();
        }
    }

    public static Bundle loadContactFromUri(@NonNull Context context, Uri selectedContactUri) {
        // Get the URI that points to the selected contact
        // We only need the NUMBER column, because there will be only one row in the result
        Bundle bundle = new Bundle();
        String[] projection;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            projection  = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.PHOTO_URI};
        else
            projection  = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};

        // Perform the query on the contact to get the NUMBER column
        // We don't need a selection or sort order (there's only one result for the given URI)
        // CAUTION: The query() method should be called from a separate thread to avoid blocking
        // your app's UI thread. (For simplicity of the sample, this code doesn't do that.)
        // Consider using CursorLoader to perform the query.
        Cursor cursor = context.getContentResolver()
                .query(selectedContactUri, projection, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {

            // Retrieve the phone number from the NUMBER column
            int columnNumber = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            String number = cursor.getString(columnNumber);
            //number = number.replaceAll("[^0-9]+", "");
            number = number.replaceAll("\\D+", "");

            bundle.putString(KEY_NUMBER, number);
            int columnName = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            String thumbnail = "";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                int columnThumbnailUri = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI);
                thumbnail = cursor.getString(columnThumbnailUri);
                if (thumbnail == null)
                    thumbnail = "android.resource://" + context.getPackageName() + "/" + String.valueOf(R.drawable.celebs_unknown);
            } else {
                thumbnail = "android.resource://" + context.getPackageName() + "/" + String.valueOf(R.drawable.celebs_unknown);
            }
            bundle.putString(KEY_THUMBNAIL, thumbnail);
            bundle.putString(KEY_NAME, cursor.getString(columnName));
            cursor.close();
        }
        return bundle;
    }


    public static void getSmsConversation(Activity context) {
        Uri mSmsinboxQueryUri = Uri.parse("content://sms/inbox");
        String[] projection = new String[] { "_id", "thread_id", "address", "person", "date","body", "type" };
        Cursor cursor = context.getContentResolver().query(mSmsinboxQueryUri,null , null, null, null);
        if (cursor == null)
            return;
        //context.startManagingCursor(cursor);
        String[] columns = new String[] { "address", "person", "date", "body","type" };
        if (cursor.moveToFirst()) {
            int count = cursor.getCount();
            String result;
            String[] columnNames = cursor.getColumnNames();
            while (cursor.moveToNext()){
                for (String field : columnNames){
                    result =  cursor.getString(cursor.getColumnIndex(field));
                    if (result != null)
                        Log.d("TAG", result);
                }
                /*String address = cursor.getString(cursor.getColumnIndex(columns[0]));
                String name = cursor.getString(cursor.getColumnIndex(columns[1]));
                String date = cursor.getString(cursor.getColumnIndex(columns[2]));
                String msg = cursor.getString(cursor.getColumnIndex(columns[3]));
                String type = cursor.getString(cursor.getColumnIndex(columns[4]));*/
            }
        }
    }

    /**
     * @param app app context
     * @param list array to save
     */
    public static void save(final Application app, final ArrayList<ChatMessage> list){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (list != null) {
                    try {
                        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(app).edit();
                        int count = 0;
                        String number = "0";
                        for (ChatMessage chatMessage : list){
                            if (count == 0)
                                number = chatMessage.getSenderNumber();
                            Gson gson = new GsonBuilder().create();
                            String array = gson.toJson(chatMessage);
                            edit.putString(number + Integer.toString(count), array);
                            count++;
                        }
                        Gson gson = new GsonBuilder().create();
                        String array = gson.toJson(list);
                        edit.putString(number, array);
                        edit.putInt(number + "size", count).apply();
                    } catch (ConcurrentModificationException e){
                        ExceptionHandler.handleException(e);
                    }
                }
            }
        }).start();
    }

    public static void getNumberHistory(Application application, String number, ArrayList<ChatMessage> chatHistory) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(application);
        int size = pref.getInt(number + "size", 0);
        if (size == 0)
            return;
        for (int i = 0 ; i < size; i++) {
            Gson gson = new Gson();
            Type type = new TypeToken<ChatMessage>() {}.getType();
            String array = pref.getString(number + Integer.toString(i), "null");
            ChatMessage message = gson.fromJson(array, type);
            chatHistory.add(message);
        }
    }

    public static boolean checkVibrationIsOn(Context context){
        boolean status = false;
        AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        if(am.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE){
            status = true;
        } else if (1 == Settings.System.getInt(context.getContentResolver(), "vibrate_when_ringing", 0)) //vibrate on
            status = true;
        return status;
    }

    public static boolean checkRingerIsOn(Context context){
        AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        return am.getRingerMode() == AudioManager.RINGER_MODE_NORMAL;
    }



    public static String getLastDate(ArrayList<ChatMessage> chatHistory) {
        String date;
        for (int i = chatHistory.size() - 1; i >= 0 ; i--){
            date = chatHistory.get(i).getDate();
            if (date != null)
                return date;
        }
        return null;
    }
}
