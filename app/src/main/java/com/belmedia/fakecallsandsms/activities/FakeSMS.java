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
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.belmedia.fakecallsandsms.R;
import com.belmedia.fakecallsandsms.ToggleButtonGroupTableLayout;
import com.belmedia.fakecallsandsms.Utils;
import com.belmedia.fakecallsandsms.sms.SmsReceiver;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FakeSMS extends AppCompatActivity implements View.OnClickListener {
    private static final int SELECT_PICTURE = 1;
    private static final int SELECT_CONTACT = 5;
    private final String TAG = getClass().getSimpleName();
    final int btnDpWidth = 100, btnDpwHeight = 100;

    public static final String KEY_CONTACT_NAME = "contactNameSMS", KEY_CONTACT_NUMBER = "contactNumberSMS"
            , KEY_CUSTOM = "extraCustomSMS", KEY_BODY_SMS = "bodySms", KEY_CONTACT_THUMBNAIL = "contactThumbnail"
            , KEY_LAST_TIME_CHOICE = "last_time_choice";


    @Bind(R.id.contact_picture_holder)
    ImageView btnAddContact;


    @Bind(R.id.editText_caller_name)
    EditText editTextCallerName;
    @Bind(R.id.editText_caller_number)
    EditText editTextCallerNumber;
    @Bind(R.id.editText_custom_time)
    EditText editTextCustomTime;
    @Bind(R.id.editText_body_sms)
    EditText editTextBodySms;
    @Bind(R.id.radio_group_time_picker)
    ToggleButtonGroupTableLayout radioGroupTimePicker;
    @Bind(R.id.spinner_custom)
    Spinner spinnerCustom;
    ContactData contactData;
    public static String defaultSmsApp;

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
        contactData.thumbnailUri = pref.getString(KEY_CONTACT_THUMBNAIL, "android.resource://" + getPackageName() + "/" + String.valueOf(R.drawable.celebs_unknown));
        radioGroupTimePicker.setCheckedRadioButtonId(pref.getInt(KEY_LAST_TIME_CHOICE, R.id.radioButton5sec));
        btnAddContact.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);


        editTextCallerName.setText(contactData.name);
        if (!contactData.number.equals(""))
            editTextCallerNumber.setText(contactData.number);
        editTextCustomTime.setText(pref.getString(KEY_CUSTOM, ""));
        editTextBodySms.append(pref.getString(KEY_BODY_SMS, ""));

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

    private ViewTreeObserver.OnGlobalLayoutListener layoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            Utils.loadImageFromUri(FakeSMS.this, Uri.parse(contactData.thumbnailUri), btnAddContact);
            btnAddContact.getViewTreeObserver().removeGlobalOnLayoutListener(this);
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        pref.edit().putString(KEY_CONTACT_NAME, editTextCallerName.getText().toString())
                .putString(KEY_CONTACT_NUMBER, editTextCallerNumber.getText().toString())
                .putString(KEY_CONTACT_THUMBNAIL, contactData.thumbnailUri)
                .putString(KEY_CUSTOM, editTextCustomTime.getText().toString())
                .putString(KEY_BODY_SMS, editTextBodySms.getText().toString())
                .putInt(KEY_LAST_TIME_CHOICE, radioGroupTimePicker.getCheckedRadioButtonId())
                .apply();
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
                        Bundle bundle = Utils.loadContactFromUri(getBaseContext(), selectedContactUri);
                        contactData.number = bundle.getString(Utils.KEY_NUMBER);
                        contactData.name = bundle.getString(Utils.KEY_NAME);
                        contactData.thumbnailUri = bundle.getString(Utils.KEY_THUMBNAIL);
                        editTextCallerName.setText(contactData.name);
                        editTextCallerNumber.setText(contactData.number);
                        if (contactData.thumbnailUri != null && !contactData.thumbnailUri.equals(""))
                            Utils.loadImageFromUri(getBaseContext(), Uri.parse(contactData.thumbnailUri), btnAddContact);
                        else {
                            btnAddContact.setImageResource(R.drawable.celebs_unknown);
                            btnAddContact.setBackgroundColor(getResources().getColor(R.color.gray));
                        }
                        editTextBodySms.setText(" ");
                    }
                    break;
                /*case 11:
                    final String myPackageName = getPackageName();
                    if (Telephony.Sms.getDefaultSmsPackage(this).equals(myPackageName)) {
                        callToSmsBroadcast();
                    }
                    break;*/
            }

        }
    }


    public void triggerSmsSend(View view) {
       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            boolean canWriteSms = false;
//
//            if(!SmsWriteOpUtil.isWriteEnabled(getApplicationContext())) {
//                canWriteSms = SmsWriteOpUtil.setWriteEnabled(getApplicationContext(), true);
//            }
//            Log.d("TAG", Boolean.toString(canWriteSms));
            //Get default sms app
            defaultSmsApp = Telephony.Sms.getDefaultSmsPackage(this);
            final String myPackageName = getPackageName();
            if (!defaultSmsApp.equals(myPackageName)) {

                //Change the default sms app to my app
                Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, getPackageName());
                startActivityForResult(intent, 11);
            } else {
                callToSmsBroadcast();
            }
        } else*/
            //callToSmsBroadcast();
        callToSmsBroadcast();

    }

    private void callToSmsBroadcast() {
        int id = radioGroupTimePicker.getCheckedRadioButtonId();
        int timeForTrigger = Utils.getTimeFromSpinner(id, editTextCustomTime, spinnerCustom, TAG);
        Intent smsBroadcastIntent = new Intent(this, SmsReceiver.class);
        smsBroadcastIntent.putExtra(KEY_CONTACT_NAME, editTextCallerName.getText().toString());
        smsBroadcastIntent.putExtra(KEY_CONTACT_NUMBER, editTextCallerNumber.getText().toString());
        smsBroadcastIntent.putExtra(KEY_CONTACT_THUMBNAIL, contactData.thumbnailUri);
        smsBroadcastIntent.putExtra(KEY_BODY_SMS, editTextBodySms.getText().toString());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, smsBroadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + timeForTrigger, pendingIntent);

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
