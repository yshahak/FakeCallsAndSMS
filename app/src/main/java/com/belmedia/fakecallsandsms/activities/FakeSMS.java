package com.belmedia.fakecallsandsms.activities;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Telephony;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.belmedia.fakecallsandsms.R;
import com.belmedia.fakecallsandsms.ToggleButtonGroupTableLayout;
import com.belmedia.fakecallsandsms.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FakeSMS extends AppCompatActivity implements View.OnClickListener {
    private static final int SELECT_PICTURE = 1;
    private static final int SELECT_CONTACT = 5;
    private final String TAG = getClass().getSimpleName();
    final int  btnDp = 130;

    public static final String KEY_CONTACT_NAME = "contactNameSMS", KEY_CONTACT_NUMBER = "contactNumberSMS"
            , KEY_CUSTOM = "extraCustomSMS", KEY_BODY_SMS = "bodySms";

    @Bind(R.id.button_call)
    Button btnCall;
    @Bind(R.id.button_add_picture) TextView btnAddPic;


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
        btnAddPic.setOnClickListener(this);
        contactData = new ContactData();

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        contactData.name = pref.getString(KEY_CONTACT_NAME, "");
        contactData.number = pref.getLong(KEY_CONTACT_NUMBER, -1);
        editTextCallerName.setText(contactData.name);
        if (contactData.number != -1)
            editTextCallerNumber.setText(Long.toString(contactData.number));
        editTextCustomTime.setText(pref.getString(KEY_CUSTOM, ""));
        editTextBodySms.setText(pref.getString(KEY_BODY_SMS, ""));


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        pref.edit().putString(KEY_CONTACT_NAME, contactData.name)
                .putLong(KEY_CONTACT_NUMBER, contactData.number)
                .putString(KEY_CUSTOM, editTextCustomTime.getText().toString())
                .putString(KEY_BODY_SMS, editTextBodySms.getText().toString())
                .commit();
    }

    private void sendSms() {
        ContentValues values = new ContentValues();
        values.put("address", contactData.number);
        values.put("body", editTextBodySms.getText().toString());
        getContentResolver().insert(Uri.parse("content://sms/inbox"), values);
        getContentResolver().notifyChange(Uri.parse("content://sms/inbox"), null);
        buildSmsNotification(Long.toString(contactData.number), editTextBodySms.getText().toString());
    }

    private void buildSmsNotification(String title, String message){
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

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSmallIcon(android.R.drawable.stat_notify_chat)
                .setPriority(Notification.PRIORITY_HIGH)
                .setSound(uriSound);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);
        mBuilder.setContentIntent(resultPendingIntent);
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).notify(1, mBuilder.build());
    }

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
                        Utils.loadContactFromUri(contactData , getBaseContext(),  selectedContactUri);
                        editTextCallerName.setText(contactData.name);
                        editTextCallerNumber.setText(Long.toString(contactData.number));
                    }
                    break;
            }

        }
    }



    public void triggerSmsSend(View view) {
        int id = radioGroupTimePicker.getCheckedRadioButtonId();
        int timeForTrigger = Utils.getTimeFromSpinner(id, editTextCustomTime, spinnerCustom, TAG);
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                sendSms();
            }
        }, 5000);

        //show home screen
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    public class ContactData {
        public String name;
        public long number;
    }
}
