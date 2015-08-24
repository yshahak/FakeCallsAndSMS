package com.belmedia.fakecallsandsms.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.belmedia.fakecallsandsms.R;
import com.belmedia.fakecallsandsms.SmsReceiver;
import com.belmedia.fakecallsandsms.ToggleButtonGroupTableLayout;
import com.belmedia.fakecallsandsms.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FakeSMS extends AppCompatActivity implements View.OnClickListener {
    private static final int SELECT_PICTURE = 1;
    private static final int SELECT_CONTACT = 5;
    private final String TAG = getClass().getSimpleName();
    final int  btnDpWidth = 100, btnDpwHeight = 100;

    public static final String KEY_CONTACT_NAME = "contactNameSMS", KEY_CONTACT_NUMBER = "contactNumberSMS"
            , KEY_CUSTOM = "extraCustomSMS", KEY_BODY_SMS = "bodySms", KEY_CONTACT_THUMBNAIL = "contactThumbnail";


    @Bind(R.id.contact_picture_holder) ImageView btnAddContact;


    @Bind(R.id.editText_caller_name)  EditText editTextCallerName;
    @Bind(R.id.editText_caller_number) EditText editTextCallerNumber;
    @Bind(R.id.editText_custom_time) EditText editTextCustomTime;
    @Bind(R.id.editText_body_sms) EditText editTextBodySms;
    @Bind(R.id.radio_group_time_picker)
    ToggleButtonGroupTableLayout radioGroupTimePicker;
    @Bind(R.id.spinner_custom)
    Spinner spinnerCustom;
    ContactData contactData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fake_sms);
        ButterKnife.bind(this);
        btnAddContact.setOnClickListener(this);
        contactData = new ContactData();

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        contactData.name = pref.getString(KEY_CONTACT_NAME, "");
        contactData.number = pref.getString(KEY_CONTACT_NUMBER, "");
        contactData.thumbnailUri = pref.getString(KEY_CONTACT_THUMBNAIL, "android.resource://" + getPackageName() +"/" + String.valueOf(R.drawable.anonymous));
        Utils.loadImageFromUri(this, btnDpWidth, btnDpwHeight, Uri.parse(contactData.thumbnailUri), btnAddContact);
        editTextCallerName.setText(contactData.name);
        if (!contactData.number.equals(""))
            editTextCallerNumber.setText(contactData.number);
        editTextCustomTime.setText(pref.getString(KEY_CUSTOM, ""));
        editTextBodySms.setText(pref.getString(KEY_BODY_SMS, ""));
        editTextCustomTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                findViewById(R.id.radioButtonCustom).performClick();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        pref.edit().putString(KEY_CONTACT_NAME, contactData.name)
                .putString(KEY_CONTACT_NUMBER, contactData.number)
                .putString(KEY_CONTACT_THUMBNAIL, contactData.thumbnailUri)
                .putString(KEY_CUSTOM, editTextCustomTime.getText().toString())
                .putString(KEY_BODY_SMS, editTextBodySms.getText().toString())
                .commit();
    }

    /*private void sendSms() {
        ContentValues values = new ContentValues();
        values.put("address", contactData.number);
        values.put("body", editTextBodySms.getText().toString());
        getContentResolver().insert(Uri.parse("content://sms/inbox"), values);
        getContentResolver().notifyChange(Uri.parse("content://sms/inbox"), null);
        buildSmsNotification(contactData.name, editTextBodySms.getText().toString());
    }*/

    /*private void buildSmsNotification(String title, String message){
        Intent launchIntent;
        Uri uriSound= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(this);
            launchIntent = new Intent(Intent.ACTION_SEND);
            launchIntent.setType("text/plain");
            if (defaultSmsPackageName != null) {
                launchIntent.setPackage(defaultSmsPackageName);
            }
        } else {
            String SMS_MIME_TYPE = "vnd.android-dir/mms-sms";
            launchIntent = new Intent(Intent.ACTION_MAIN);
            launchIntent.setType(SMS_MIME_TYPE);
        }
        Bitmap bitmap = null;
        if (!contactData.thumbnailUri.equals("") ) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(contactData.thumbnailUri));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.anonymous);
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        if (bitmap != null) {
            mBuilder.setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setLargeIcon(bitmap)
                    .setSmallIcon(android.R.drawable.stat_notify_chat)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setSound(uriSound);
        } else {
            mBuilder.setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setSmallIcon(android.R.drawable.stat_notify_chat)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setSound(uriSound);
        }
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);
        mBuilder.setContentIntent(resultPendingIntent);
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).notify(1, mBuilder.build());
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_fake_sm, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        Utils.pickContact(this, SELECT_CONTACT);
    }

    @SuppressWarnings("deprecation")
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case SELECT_CONTACT:
                    Uri selectedContactUri = data.getData();
                    if (selectedContactUri != null) {
                        Bundle bundle = Utils.loadContactFromUri(getBaseContext(),  selectedContactUri);
                        contactData.number = bundle.getString(Utils.KEY_NUMBER);
                        contactData.name = bundle.getString(Utils.KEY_NAME);
                        contactData.thumbnailUri = bundle.getString(Utils.KEY_THUMBNAIL);
                        editTextCallerName.setText(contactData.name);
                        editTextCallerNumber.setText(contactData.number);
                        if (contactData.thumbnailUri != null && !contactData.thumbnailUri.equals(""))
                            Utils.loadImageFromUri(getBaseContext(), btnDpWidth, btnDpwHeight, Uri.parse(contactData.thumbnailUri), btnAddContact);
                        else {
                            btnAddContact.setImageResource(R.drawable.anonymous);
                            btnAddContact.setBackgroundColor(getResources().getColor(R.color.gray));
                        }
                    }
                    break;
            }

        }
    }



    public void triggerSmsSend(View view) {
        int id = radioGroupTimePicker.getCheckedRadioButtonId();
        int timeForTrigger = Utils.getTimeFromSpinner(id, editTextCustomTime, spinnerCustom, TAG);

        Intent smsBroadcastIntent = new Intent(this, SmsReceiver.class);
        smsBroadcastIntent.putExtra(KEY_CONTACT_NAME, contactData.name);
        smsBroadcastIntent.putExtra(KEY_CONTACT_NUMBER, contactData.number);
        smsBroadcastIntent.putExtra(KEY_CONTACT_THUMBNAIL, contactData.thumbnailUri);
        smsBroadcastIntent.putExtra(KEY_BODY_SMS, editTextBodySms.getText().toString());


        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, smsBroadcastIntent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + timeForTrigger, pendingIntent);

        /*view.postDelayed(new Runnable() {
            @Override
            public void run() {
                //sendSms();
            }
        }, timeForTrigger);*/

        //show home screen
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    public class ContactData {
        public String name;
        public String number;
        public String thumbnailUri;
    }
}
