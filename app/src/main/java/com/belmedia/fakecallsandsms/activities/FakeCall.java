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
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.belmedia.fakecallsandsms.ExceptionHandler;
import com.belmedia.fakecallsandsms.R;
import com.belmedia.fakecallsandsms.ToggleButtonGroupTableLayout;
import com.belmedia.fakecallsandsms.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FakeCall extends AppCompatActivity implements View.OnClickListener{

    private final String TAG = getClass().getSimpleName();
    final int  btnDpWidth = 100, btnDpwHeight = 100;

    private static final int SELECT_PICTURE = 1,  CROP_PIC_REQUEST_CODE = 2,
            SELECT_MUSIC = 3, SELECT_RECORD_SOUND = 4, SELECT_CONTACT = 5, SELECT_CELEB = 6;
    public static final String KEY_IMAGE_URI = "imageUriCall", KEY_MUSIC_URI = "musicUriCall"
            ,KEY_CONTACT_NAME = "contactNameCall", KEY_CONTACT_NUMBER = "contactNumberCall", KEY_CUSTOM = "extraCustomCall";
    Uri selectedImageUri, selectedMusicUri;
    //ADDED

    @Bind(R.id.contact_picture_holder) ImageView photoHolder;
    @Bind(R.id.contact_picture_btn) ImageView btnAddPhoto;

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
        btnAddPhoto.setOnClickListener(this);
        findViewById(R.id.button_add_celeb).setOnClickListener(this);
        findViewById(R.id.button_add_contact).setOnClickListener(this);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        editTextCallerName.setText(pref.getString(KEY_CONTACT_NAME, ""));
        editTextCallerNumber.setText(pref.getString(KEY_CONTACT_NUMBER, ""));
        editTextCustomTime.setText(pref.getString(KEY_CUSTOM, ""));
        String savedImageUri = pref.getString(KEY_IMAGE_URI, "");
        if (!"".equals(savedImageUri)) {
            selectedImageUri = Uri.parse(savedImageUri);
            Utils.loadImageFromUri(this, btnDpWidth, btnDpwHeight, selectedImageUri, photoHolder);
        }
        String savedMusicUri = pref.getString(KEY_MUSIC_URI, "");
        if (!"".equals(savedMusicUri)) {
            selectedMusicUri = Uri.parse(savedMusicUri);
            loadMusicFromUri();
        }
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
    protected void onStop() {
        super.onStop();
        String imageUri = "";
        if (selectedImageUri != null)
            imageUri = selectedImageUri.toString();
        String musicUri = "";
        if (selectedMusicUri != null)
            musicUri = selectedMusicUri.toString();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        pref.edit().putString(KEY_CONTACT_NAME, editTextCallerName.getText().toString())
                .putString(KEY_CONTACT_NUMBER, editTextCallerNumber.getText().toString())
                .putString(KEY_CUSTOM,editTextCustomTime.getText().toString())
                .putString(KEY_IMAGE_URI, imageUri)
                .putString(KEY_MUSIC_URI, musicUri)
                .apply();
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
                        Utils.loadImageFromUri(getBaseContext(), btnDpWidth, btnDpwHeight, selectedImageUri, photoHolder);
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
                    //btnAddVoice.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.musical_note), null, null);
                    String titleRecord = "My Record";
                    btnAddVoice.setText(titleRecord);
                    break;

                case SELECT_CONTACT:
                    Uri selectedContactUri = data.getData();
                    if (selectedContactUri != null) {
                        Bundle bundle = Utils.loadContactFromUri(getBaseContext(), selectedContactUri);
                        editTextCallerName.setText(bundle.getString(Utils.KEY_NAME));
                        editTextCallerNumber.setText(bundle.getString(Utils.KEY_NUMBER));
                        String thumbnail = bundle.getString(Utils.KEY_THUMBNAIL);
                        if (thumbnail != null) {
                            selectedImageUri = Uri.parse(thumbnail);
                            Utils.loadImageFromUri(getBaseContext(), btnDpWidth, btnDpwHeight, selectedImageUri, photoHolder);
                        } else {
                            selectedImageUri = null;
                        }
                    }
                    break;
                case SELECT_CELEB:
                    int drawAble_id = data.getIntExtra(PickCelebActivity.KEY_CELEB_RESULT, R.drawable.celeb_obama);
                    String packageName = getPackageName();
                    selectedImageUri = Uri.parse("android.resource://" + packageName +"/" + drawAble_id);
                    Utils.loadImageFromUri(getBaseContext(), btnDpWidth, btnDpwHeight, selectedImageUri, photoHolder);
                    switch (drawAble_id){
                        case R.drawable.celeb_obama:
                            editTextCallerName.setText("Barack Obama");
                            editTextCallerNumber.setText("058-699669");
                            break;
                        case R.drawable.celeb_golda:
                            editTextCallerName.setText("גולדה מאיר");
                            editTextCallerNumber.setText("052-296569");
                            break;
                        case R.drawable.celeb_hawking:
                            editTextCallerName.setText("Stephen Hawking");
                            editTextCallerNumber.setText("050-934015");
                            break;
                    }
                    break;

            }
        }
    }

    private void loadMusicFromUri() {
       /* btnAddVoice.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.musical_note), null, null);
        String title = getSongNameFromURI(this, selectedMusicUri);
        if (title != null)
            btnAddVoice.setText(title);*/
    }




    public void triggerCallAlarm(View view) {
        PreferenceManager.getDefaultSharedPreferences(this);

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
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + timeForTrigger, pendingIntent);

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
                                    Toast.makeText(getBaseContext(), "Your device miss built in recorder", Toast.LENGTH_LONG).show();
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
        if (v.equals(btnAddPhoto))
            Utils.pickImage(this, SELECT_PICTURE);
        else if (v.equals(btnAddVoice))
            startVoiceDialog();
        else if (v.getId() == R.id.button_add_celeb)
            pickFromCeleb();
        else if (v.getId() == R.id.button_add_contact)
            pickFromContact();

    }


    public void pickFromContact() {
        Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
        pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE); // Show user only contacts w/ phone numbers
        startActivityForResult(pickContactIntent, SELECT_CONTACT);

    }


    public void pickFromCeleb() {
        Intent pickContactIntent = new Intent(this, PickCelebActivity.class);
        startActivityForResult(pickContactIntent, SELECT_CELEB);
    }
}
