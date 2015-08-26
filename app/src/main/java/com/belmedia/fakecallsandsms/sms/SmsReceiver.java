package com.belmedia.fakecallsandsms.sms;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Telephony;
import android.support.v4.app.NotificationCompat;

import com.belmedia.fakecallsandsms.R;
import com.belmedia.fakecallsandsms.activities.FakeSMS;

import java.io.IOException;

/**
 * Created by B.E.L on 09/07/2015.
 */
public class SmsReceiver extends BroadcastReceiver {

    String number,name,  bodyText, thumbNail;

    @Override
    public void onReceive(Context context, Intent intent) {

        number = intent.getStringExtra(FakeSMS.KEY_CONTACT_NUMBER);
        bodyText = intent.getStringExtra(FakeSMS.KEY_BODY_SMS);
        thumbNail = intent.getStringExtra(FakeSMS.KEY_CONTACT_THUMBNAIL);
        name = intent.getStringExtra(FakeSMS.KEY_CONTACT_NAME);

        ContentValues values = new ContentValues();
        values.put("address", number);
        values.put("body", bodyText);
        values.put("date", System.currentTimeMillis());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            context.getContentResolver().insert(Telephony.Sms.Inbox.CONTENT_URI, values);
            context.getContentResolver().notifyChange(Telephony.Sms.Inbox.CONTENT_URI, null);
        }
        else {
            context.getContentResolver().insert(Uri.parse("content://sms/inbox"), values);
            context.getContentResolver().notifyChange(Uri.parse("content://sms/inbox"), null);
        }
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //Change my sms app to the last default sms
            Intent defaultSms = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
            intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, FakeSMS.defaultSmsApp);
            context.getApplicationContext().startActivity(defaultSms);
        }
*/
        buildSmsNotification(context);

    }

    private void buildSmsNotification(Context context){
        Intent launchIntent;
        Uri uriSound= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(context);
            String defaultSmsPackageName = FakeSMS.defaultSmsApp;

            if (defaultSmsPackageName != null) {
                launchIntent = context.getPackageManager().getLaunchIntentForPackage(defaultSmsPackageName);
            } else {
                String SMS_MIME_TYPE = "vnd.android-dir/mms-sms";
                launchIntent = new Intent(Intent.ACTION_MAIN);
                launchIntent.setType(SMS_MIME_TYPE);
            }
        } else {
            String SMS_MIME_TYPE = "vnd.android-dir/mms-sms";
            launchIntent = new Intent(Intent.ACTION_MAIN);
            launchIntent.setType(SMS_MIME_TYPE);
        }
        Bitmap bitmap = null;
        if (thumbNail == null || thumbNail.equals("") ) {
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.celebs_unknown);
        } else {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(thumbNail));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        if (bitmap != null) {
            mBuilder.setContentTitle(name)
                    .setContentText(bodyText)
                    .setAutoCancel(true)
                    .setLargeIcon(bitmap)
                    .setSmallIcon(android.R.drawable.stat_notify_chat)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setSound(uriSound);
        } else {
            mBuilder.setContentTitle(name)
                    .setContentText(bodyText)
                    .setAutoCancel(true)
                    .setSmallIcon(android.R.drawable.stat_notify_chat)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setSound(uriSound);
        }
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, launchIntent, 0);
        mBuilder.setContentIntent(resultPendingIntent);
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(1, mBuilder.build());
    }
}