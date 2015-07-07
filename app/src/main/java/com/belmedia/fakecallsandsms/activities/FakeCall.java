package com.belmedia.fakecallsandsms.activities;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.belmedia.fakecallsandsms.ExceptionHandler;
import com.belmedia.fakecallsandsms.R;
import com.belmedia.fakecallsandsms.ToggleButtonGroupTableLayout;
import com.belmedia.fakecallsandsms.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FakeCall extends AppCompatActivity  implements View.OnClickListener{

    private final String TAG = getClass().getSimpleName();
    final int  btnDp = 130;

    private static final int SELECT_PICTURE = 1,  CROP_PIC_REQUEST_CODE = 2, SELECT_MUSIC = 3, SELECT_RECORD_SOUND = 4;
    public static final String KEY_IMAGE_URI = "imageUriCall", KEY_MUSIC_URI = "musicUriCall"
            ,KEY_CONTACT_NAME = "contactNameCall", KEY_CONTACT_NUMBER = "contactNumberCall", KEY_CUSTOM = "extraCustomCall";
    Uri selectedImageUri, selectedMusicUri;
    //ADDED

    @Bind(R.id.button_call) Button btnCall;
    @Bind(R.id.button_add_picture) TextView btnAddPic;
    @Bind(R.id.button_add_voice) TextView btnAddVoice;


    @Bind(R.id.editText_caller_name) EditText editTextCallerName;
    @Bind(R.id.editText_caller_number) EditText editTextCallerNumber;
    @Bind(R.id.editText_custom_time) EditText editTextCustomTime;

    @Bind(R.id.radio_group_time_picker)
    ToggleButtonGroupTableLayout radioGroupTimePicker;
    @Bind(R.id.spinner_custom) Spinner spinnerCustom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fake_call);
        ButterKnife.bind(this);
        btnAddVoice.setOnClickListener(this);
        btnAddPic.setOnClickListener(this);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        editTextCallerName.setText(pref.getString(KEY_CONTACT_NAME, ""));
        editTextCallerNumber.setText(pref.getString(KEY_CONTACT_NUMBER, ""));
        editTextCustomTime.setText(pref.getString(KEY_CUSTOM, ""));

        String savedImageUri = pref.getString(KEY_IMAGE_URI, "");
        if (!"".equals(savedImageUri)) {
            selectedImageUri = Uri.parse(savedImageUri);
            Utils.loadImageFromUri(this, btnDp, selectedImageUri, btnAddPic, TAG);
        }
        String savedMusicUri = pref.getString(KEY_MUSIC_URI, "");
        if (!"".equals(savedMusicUri)) {
            selectedMusicUri = Uri.parse(savedMusicUri);
            loadMusicFromUri();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        pref.edit().putString(KEY_CONTACT_NAME, editTextCallerName.getText().toString())
                .putString(KEY_CONTACT_NUMBER, editTextCallerNumber.getText().toString())
                .putString(KEY_CUSTOM,editTextCustomTime.getText().toString())
                .putString(KEY_IMAGE_URI, selectedImageUri.toString())
                .putString(KEY_MUSIC_URI, selectedMusicUri.toString())
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_incoming_activity_settings, menu);
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



    @SuppressWarnings("deprecation")
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case SELECT_PICTURE:
                    selectedImageUri = data.getData();
                    if (selectedImageUri != null) {
                        Utils.loadImageFromUri(getParent(), btnDp, selectedImageUri, btnAddPic, TAG);
                    } else
                        System.out.println("selectedImagePath is null");
                    break;

                case SELECT_MUSIC:
                    selectedMusicUri = data.getData();
                    if (selectedImageUri != null) {
                        loadMusicFromUri();
                    }
                    break;

                case SELECT_RECORD_SOUND:
                    selectedMusicUri = data.getData();
                    btnAddVoice.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.musical_note), null, null);
                    String titleRecord = "My Record";
                    btnAddVoice.setText(titleRecord);
                    break;
                }

        }
    }

    private void loadMusicFromUri() {
        btnAddVoice.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.musical_note), null, null);
        String title = getSongNameFromURI(this, selectedMusicUri);
        if (title != null)
            btnAddVoice.setText(title);
    }




    public void triggerCallAlarm(View view) {
        int id = radioGroupTimePicker.getCheckedRadioButtonId();
        int timeForTrigger = Utils.getTimeFromSpinner(id, editTextCustomTime, spinnerCustom, TAG);

        Intent incomeCallActivity = new Intent(this, IncomeCallActivity.class);
        if (selectedImageUri != null)
            incomeCallActivity.putExtra(KEY_IMAGE_URI, selectedImageUri);
        if (selectedMusicUri != null)
            incomeCallActivity.putExtra(KEY_MUSIC_URI, selectedMusicUri);
        String contactName =  editTextCallerName.getText().toString();
        if (!contactName.equals(""))
            incomeCallActivity.putExtra(KEY_CONTACT_NAME, contactName);
        String contactNumber =  editTextCallerNumber.getText().toString();
        if (!contactNumber.equals(""))
            incomeCallActivity.putExtra(KEY_CONTACT_NUMBER, contactNumber);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,  incomeCallActivity, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1500, pendingIntent);

        //show the home screen
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }


    public void startVoiceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Choose source of voice")
                .setPositiveButton("From device media", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(i, SELECT_MUSIC);
                    }
                })
                .setNegativeButton("Record new voice", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
                                try {
                                    startActivityForResult(intent, SELECT_RECORD_SOUND);
                                } catch (ActivityNotFoundException e) {
                                    ExceptionHandler.handleException(e);
                                    Toast.makeText(getParent(), "Your device miss built in recorder", Toast.LENGTH_LONG).show();
                                }
                            }
                        }

                ).

                    create()

                    .

                    show();
                }



    private String getSongNameFromURI(Context context, Uri contentUri) {
        String[] projection = { MediaStore.Audio.Media.TITLE };
        CursorLoader loader = new CursorLoader(context, contentUri, projection, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int songTitle_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
        cursor.moveToFirst();
        Log.d("TAG", cursor.getString(songTitle_index));
        return cursor.getString(songTitle_index);
    }

    @Override
    public void onClick(View v) {
        if (v.equals(btnAddPic))
            Utils.pickImage(this, SELECT_PICTURE);
        else if (v.equals(btnAddVoice))
            startVoiceDialog();
    }


}