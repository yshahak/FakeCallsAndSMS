package com.belmedia.fakecallsandsms.sms;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.belmedia.fakecallsandsms.R;
import com.belmedia.fakecallsandsms.Utils;
import com.belmedia.fakecallsandsms.activities.ChatActivity;
import com.belmedia.fakecallsandsms.activities.FakeSMS;

/**
 * Created by B.E.L on 09/07/2015.
 */
public class SmsReceiver extends BroadcastReceiver {

    String number, name, bodyText, thumbNail;
    public static final String SMS_EXTRA_NAME = "pdus";
    public static final String SMS_URI = "content://sms";

    public static final String ADDRESS = "address";
    public static final String PERSON = "person";
    public static final String DATE = "date";
    public static final String READ = "read";
    public static final String STATUS = "status";
    public static final String TYPE = "type";
    public static final String BODY = "body";
    public static final String SEEN = "seen";

    public static final int MESSAGE_TYPE_INBOX = 1;
    public static final int MESSAGE_TYPE_SENT = 2;

    public static final int MESSAGE_IS_NOT_READ = 0;
    public static final int MESSAGE_IS_READ = 1;

    public static final int MESSAGE_IS_NOT_SEEN = 0;
    public static final int MESSAGE_IS_SEEN = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        number = intent.getStringExtra(FakeSMS.KEY_CONTACT_NUMBER);
        bodyText = intent.getStringExtra(FakeSMS.KEY_BODY_SMS);
        thumbNail = intent.getStringExtra(FakeSMS.KEY_CONTACT_THUMBNAIL);
        name = intent.getStringExtra(FakeSMS.KEY_CONTACT_NAME);

       /* ContentValues values = new ContentValues();
        values.put(ADDRESS, number);
        values.put(BODY, bodyText);
        values.put(DATE, System.currentTimeMillis());
        //values.put( STATUS, sms.getStatus() );

        values.put( TYPE, MESSAGE_TYPE_INBOX );
        values.put( SEEN, MESSAGE_IS_NOT_SEEN );
        values.put( READ, MESSAGE_IS_NOT_READ );*/
        Intent launchIntent = new Intent(context, ChatActivity.class);
        launchIntent.putExtra(FakeSMS.KEY_CONTACT_NUMBER, number);
        launchIntent.putExtra(FakeSMS.KEY_CONTACT_THUMBNAIL, thumbNail);
        launchIntent.putExtra(FakeSMS.KEY_CONTACT_NAME, name);
        launchIntent.putExtra(FakeSMS.KEY_BODY_SMS, bodyText);

        buildSmsNotification(context, launchIntent);


       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            context.getContentResolver().insert(Telephony.Sms.Inbox.CONTENT_URI, values);
            context.getContentResolver().notifyChange(Telephony.Sms.Inbox.CONTENT_URI, null);
        }
        else {
           *//* Uri  u = Uri.parse("content://sms/inbox");
            ContentProviderClient P = context.getContentResolver()
                    .acquireContentProviderClient(u);
            try {
                Uri InsertedMessage = P.insert(u, values);
                Log.d("TAG", InsertedMessage.toString());
            } catch (RemoteException e) {
                e.printStackTrace();
            }*//*
            context.getContentResolver().insert(Uri.parse("content://sms/inbox"), values);
            //context.getContentResolver().notifyChange(Uri.parse("content://sms/inbox"), null);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //Change my sms app to the last default sms
            Intent defaultSms = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
            defaultSms.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, FakeSMS.defaultSmsApp);

            context.getApplicationContext().startActivity(defaultSms);
        }*/

    }

    private void buildSmsNotification(Context context, Intent intent){
       Uri uriSound= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
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
        }*/
        int height, width;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            Resources res = context.getResources();
            height = (int) res.getDimension(android.R.dimen.notification_large_icon_height);
            width = (int) res.getDimension(android.R.dimen.notification_large_icon_width);
        } else {
            height = Utils.getDpInPixels(48, context);
            width = height;
        }
        Bitmap bitmap;
        if (thumbNail == null || thumbNail.equals("") ) {
            bitmap = Utils.decodeSampledBitmapFromResource(context.getResources(), R.drawable.celebs_unknown, width, height);
        } else {
            bitmap = Utils.getBitmapFromUri(Uri.parse(thumbNail), context.getApplicationContext(), width, height);
           /* try {
                bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(thumbNail));

            } catch (IOException e) {
                e.printStackTrace();
            }*/
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
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(1, mBuilder.build());
    }

/*
    private static void sendSms(Context context, String sender, String body) {
        byte [] pdu = null ;
        byte [] scBytes = PhoneNumberUtils
                .networkPortionToCalledPartyBCD("0000000000");
        byte [] senderBytes = PhoneNumberUtils.networkPortionToCalledPartyBCD(sender);
        int lsmcs = scBytes.length;
        byte [] dateBytes = new byte [ 7 ];
        Calendar calendar = new GregorianCalendar();
        dateBytes[ 0 ] = reverseByte((byte) (calendar.get(Calendar.YEAR)));
        dateBytes[ 1 ] = reverseByte((byte) (calendar.get(Calendar.MONTH) + 1));
        dateBytes[ 2 ] = reverseByte((byte) (calendar.get(Calendar.DAY_OF_MONTH)));
        dateBytes[ 3 ] = reverseByte((byte) (calendar.get(Calendar.HOUR_OF_DAY)));
        dateBytes[ 4 ] = reverseByte(( byte ) (calendar.get(Calendar.MINUTE)));
        dateBytes[ 5 ] = reverseByte(( byte ) (calendar.get(Calendar.SECOND)));
        dateBytes[ 6 ] = reverseByte(( byte ) ((calendar.get(Calendar.ZONE_OFFSET) + calendar
                .get(Calendar.DST_OFFSET)) / ( 60 * 1000 * 15 )));
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            bo.write(lsmcs);
            bo.write(scBytes);
            bo.write( 0x04 );
            bo.write(( byte ) sender.length());
            bo.write(senderBytes);
            bo.write( 0x00 );
            bo.write( 0x00 );  // encoding: 0 for default 7bit
            bo.write(dateBytes);
            try {
                byte[] bodybytes  = GsmAlphabet.stringToGsm7BitPacked(body);
                bo.write(bodybytes);
            } catch(Exception e) {}

            pdu = bo.toByteArray();
        } catch (IOException e) {
        }

        Intent intent = new Intent();
        intent.setClassName("com.android.mms",
                "com.android.mms.transaction.SmsReceiverService");
        intent.setAction("android.provider.Telephony.SMS_RECEIVED");
        intent.putExtra( "pdus" , new Object[] { pdu });
        context.startService(intent);
    }
*/

    /*private static byte reverseByte( byte b) {
        return ( byte ) ((b & 0xF0 ) >> 4 | (b & 0x0F ) << 4 );
    }

    void AddMessage(Context context,  String Address, String Message)
    {

        *//*Uri  u = Uri.parse("content://sms/inbox");

        if (u != null)
        {

            Cursor mCursor = context.getContentResolver().query(u, null, null,
                    null, null);

            ContentProviderClient P = context.getContentResolver()
                    .acquireContentProviderClient(u);

            ContentValues v = new ContentValues();
            v.put("body", Message);
            v.put("read", false);
            v.put("seen", true);
            v.put("address", Address);
            v.put("date", Sent.toMillis(false));
            v.put("date_sent", Sent.toMillis(false));

            try
            {
                int Thread = -1;
                Uri InsertedMessage = P.insert(u, v);
                Log.d("InsertedURI", InsertedMessage.toString());
                Cursor InsertedMessageCursor = context.getContentResolver().query(
                        InsertedMessage, null, null, null, null);
                if (InsertedMessageCursor.moveToFirst())
                {
                    Thread = InsertedMessageCursor.getInt(InsertedMessageCursor
                            .getColumnIndex("thread_id"));
                    Uri MessageThread = Uri.parse("content://mms-sms/threadID/" + Thread);
                    Log.d("MessageThread", MessageThread.toString());
                    Intent V = new Intent(Intent.ACTION_VIEW);
                    V.setData(MessageThread);
                    context.startActivity(V);
                }

            } catch (RemoteException e)
            {

                e.printStackTrace();
            }

        }*//*

    }
*/
}
