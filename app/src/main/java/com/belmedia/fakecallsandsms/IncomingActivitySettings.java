package com.belmedia.fakecallsandsms;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import butterknife.Bind;
import butterknife.ButterKnife;

public class IncomingActivitySettings extends AppCompatActivity {

    final int SecFactor = 1000, MinFactor = 1000*60;


    @Bind(R.id.button_call) Button btnCall;
    @Bind(R.id.button_add_picture) Button btnAddPic;
    @Bind(R.id.button_add_voice) Button btnAddVoice;

    @Bind(R.id.editText_caller_name) EditText editTextCallerName;
    @Bind(R.id.editText_caller_number) EditText editTextCallerNumber;
    @Bind(R.id.editText_custom_time) EditText editTextCustomTime;

    @Bind(R.id.radio_group_time_picker) RadioGroup radioGroupTimePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_activity_settings);
        ButterKnife.bind(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_incoming_activity_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void triggerCallAlarm(View view) {
        int id = radioGroupTimePicker.getCheckedRadioButtonId();
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
            default:
                timeForTrigger = 10 * SecFactor;

        }

        Intent incomeCallActivity = new Intent(this, IncomeCallActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,  incomeCallActivity, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + timeForTrigger, pendingIntent);
        finish();
    }
}
