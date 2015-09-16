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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
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

public class FakeCall extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = getClass().getSimpleName();
    //final int  btnDpWidth = 100, btnDpwHeight = 100;

    private static final int SELECT_PICTURE = 1, CROP_PIC_REQUEST_CODE = 2,
            SELECT_MUSIC = 3, SELECT_RECORD_SOUND = 4, SELECT_CONTACT = 5; /*SELECT_CELEB = 6*/;
    public static final String KEY_IMAGE_URI = "imageUriCall", KEY_MUSIC_URI = "musicUriCall"
            , KEY_CONTACT_NAME = "contactNameCall", KEY_CONTACT_NUMBER = "contactNumberCall"
            , KEY_CUSTOM = "extraCustomCall", KEY_LAST_TIME_CHOICE = "last_time_choice";
    Uri selectedImageUri, selectedMusicUri;
    //ADDED

    @Bind(R.id.contact_picture_holder)
    ImageView photoHolder;
    @Bind(R.id.contact_picture_btn)
    ImageView btnAddPhoto;

    @Bind(R.id.button_add_voice)
    TextView btnAddVoice;


    @Bind(R.id.editText_caller_name)
    EditText editTextCallerName;
    @Bind(R.id.editText_caller_number)
    EditText editTextCallerNumber;
    @Bind(R.id.editText_custom_time)
    EditText editTextCustomTime;

    @Bind(R.id.radio_group_time_picker)
    ToggleButtonGroupTableLayout radioGroupTimePicker;
    @Bind(R.id.spinner_custom)
    Spinner spinnerCustom;
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fake_call);
        ButterKnife.bind(this);

        btnAddVoice.setOnClickListener(this);
        btnAddPhoto.setOnClickListener(this);
        //findViewById(R.id.button_add_celeb).setOnClickListener(this);
        findViewById(R.id.button_add_contact).setOnClickListener(this);

        pref = PreferenceManager.getDefaultSharedPreferences(this);
        editTextCallerName.setText(pref.getString(KEY_CONTACT_NAME, ""));
        editTextCallerNumber.setText(pref.getString(KEY_CONTACT_NUMBER, ""));
        editTextCustomTime.setText(pref.getString(KEY_CUSTOM, ""));
        String savedImageUri = pref.getString(KEY_IMAGE_URI, "");
        if (!"".equals(savedImageUri)) {
            selectedImageUri = Uri.parse(savedImageUri);
            photoHolder.getViewTreeObserver().addOnGlobalLayoutListener(layoutListenerInageHolder);
        }
        radioGroupTimePicker.getViewTreeObserver().addOnGlobalLayoutListener(layoutListenerSpinner);

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

    private ViewTreeObserver.OnGlobalLayoutListener layoutListenerInageHolder = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            Utils.loadImageFromUri(FakeCall.this, selectedImageUri, photoHolder);
            photoHolder.getViewTreeObserver().removeGlobalOnLayoutListener(this);
        }
    };

    private ViewTreeObserver.OnGlobalLayoutListener layoutListenerSpinner = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            radioGroupTimePicker.setCheckedRadioButtonId(pref.getInt(KEY_LAST_TIME_CHOICE, R.id.radioButton5sec));
            radioGroupTimePicker.getViewTreeObserver().removeGlobalOnLayoutListener(this);
        }
    };


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
                .putString(KEY_CUSTOM, editTextCustomTime.getText().toString())
                .putString(KEY_IMAGE_URI, imageUri)
                .putString(KEY_MUSIC_URI, musicUri)
                .putInt(KEY_LAST_TIME_CHOICE, radioGroupTimePicker.getCheckedRadioButtonId())
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
        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case SELECT_PICTURE:
                    selectedImageUri = data.getData();
                    if (selectedImageUri != null) {
                        if (photoHolder.getWidth() == 0)
                            photoHolder.getViewTreeObserver().addOnGlobalLayoutListener(layoutListenerInageHolder);
                        else
                            Utils.loadImageFromUri(getBaseContext(), selectedImageUri, photoHolder);
                    } else
                        System.out.println("selectedImagePath is null");
                    break;

                case SELECT_MUSIC:
                    selectedMusicUri = data.getData();
                    if (selectedMusicUri != null) {
                        loadMusicFromUri();
                    }
                    break;

                case SELECT_RECORD_SOUND:
                    selectedMusicUri = data.getData();
                    //btnAddVoice.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.musical_note), null, null);
                    if (selectedMusicUri != null) {
                        loadMusicFromUri();
                    }
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
                            if (photoHolder.getWidth() == 0)
                                photoHolder.getViewTreeObserver().addOnGlobalLayoutListener(layoutListenerInageHolder);
                            else
                                Utils.loadImageFromUri(getBaseContext(), selectedImageUri, photoHolder);
                        } else {
                            selectedImageUri = null;
                        }
                    }
                    break;
               /* case SELECT_CELEB:
                    int drawAble_id = data.getIntExtra(PickCelebActivity.KEY_CELEB_RESULT, R.drawable.celebs_obama);
                    String packageName = getPackageName();
                    selectedImageUri = Uri.parse("android.resource://" + packageName + "/" + drawAble_id);
                    Utils.loadImageFromUri(getBaseContext(), selectedImageUri, photoHolder);
                    switch (drawAble_id) {
                        case R.drawable.celebs_obama:
                            editTextCallerName.setText("USA President");
                            editTextCallerNumber.setText("201-925-0000");
                            break;
                        case R.drawable.celebs_kim:
                            editTextCallerName.setText("Kim");
                            editTextCallerNumber.setText("201-546-6577");
                            break;
                        case R.drawable.celebs_bye:
                            editTextCallerName.setText("beyonce");
                            editTextCallerNumber.setText("201-126-6657");
                            break;
                        case R.drawable.celebs_cat:
                            editTextCallerName.setText("Kitty Cat");
                            editTextCallerNumber.setText("0000");
                            break;
                    }
                    break;*/

            }
        }
    }

    private void loadMusicFromUri() {
        btnAddVoice.setVisibility(View.GONE);
        btnAddVoice = (TextView) findViewById(R.id.button_exist_voice);
        btnAddVoice.setVisibility(View.VISIBLE);
        btnAddVoice.setOnClickListener(this);
        String title = getSongNameFromURI(this, selectedMusicUri);
        if (title != null)
            btnAddVoice.setText(title);
    }

    private void removeMusicFromUri() {
        btnAddVoice.setVisibility(View.GONE);
        btnAddVoice = (TextView) findViewById(R.id.button_add_voice);
        btnAddVoice.setVisibility(View.VISIBLE);
        btnAddVoice.setOnClickListener(this);
    }


    public void triggerCallAlarm(View view) {
        //PreferenceManager.getDefaultSharedPreferences(this);

        int id = radioGroupTimePicker.getCheckedRadioButtonId();
        int timeForTrigger = Utils.getTimeFromSpinner(id, editTextCustomTime, spinnerCustom, TAG);

        Intent incomeCallActivity = new Intent(this, IncomeCallActivity.class);
        if (selectedImageUri != null)
            incomeCallActivity.putExtra(KEY_IMAGE_URI, selectedImageUri);
        if (selectedMusicUri != null)
            incomeCallActivity.putExtra(KEY_MUSIC_URI, selectedMusicUri);
        String contactName = editTextCallerName.getText().toString();
        if (!contactName.equals(""))
            incomeCallActivity.putExtra(KEY_CONTACT_NAME, contactName);
        String contactNumber = editTextCallerNumber.getText().toString();
        if (!contactNumber.equals(""))
            incomeCallActivity.putExtra(KEY_CONTACT_NUMBER, contactNumber);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, incomeCallActivity, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + timeForTrigger, pendingIntent);

        //show the home screen
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }


    public void startVoiceDialog() {
        CharSequence[] items =  {"Record new voice", "Select from library", "Clear"};
        new AlertDialog.Builder(this)
                .setTitle("Choose source of voice")
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 2:
                                selectedMusicUri = null;
                                pref.edit().putString(KEY_MUSIC_URI, "").apply();
                                removeMusicFromUri();
                                break;
                            case 1:
                                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                                try {
                                    startActivityForResult(i, SELECT_MUSIC);
                                } catch (ActivityNotFoundException e) {
                                    ExceptionHandler.handleException(e);
                                    Toast.makeText(getBaseContext(), "Your device miss built in app for select music", Toast.LENGTH_LONG).show();
                                }
                                break;
                            case 0:
                                Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
                                try {
                                    startActivityForResult(intent, SELECT_RECORD_SOUND);
                                } catch (ActivityNotFoundException e) {
                                    ExceptionHandler.handleException(e);
                                    Toast.makeText(getBaseContext(), "Your device miss built in recorder", Toast.LENGTH_LONG).show();
                                }
                                break;
                        }
                    }
                }).create().show();

    }


    private String getSongNameFromURI(Context context, Uri contentUri) {
        String[] projection = {MediaStore.Audio.Media.TITLE};
        CursorLoader loader = new CursorLoader(context, contentUri, projection, null, null, null);
        Cursor cursor = loader.loadInBackground();
        if (cursor == null)
            return "record";
        int songTitle_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
        if (cursor.moveToFirst())
            return cursor.getString(songTitle_index);
        else
            return "record";
    }

    @Override
    public void onClick(View v) {
        if (v.equals(btnAddPhoto))
            Utils.pickImage(this, SELECT_PICTURE);
        else if (v.equals(btnAddVoice))
            startVoiceDialog();
       /* else if (v.getId() == R.id.button_add_celeb)
            pickFromCeleb();*/
        else if (v.getId() == R.id.button_add_contact)
            pickFromContact();

    }


    public void pickFromContact() {
        Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
        pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE); // Show user only contacts w/ phone numbers
        try {
            startActivityForResult(pickContactIntent, SELECT_CONTACT);
        } catch (ActivityNotFoundException e){
            Toast.makeText(this, "No contact app to handle this", Toast.LENGTH_LONG).show();
        }

    }


   /* public void pickFromCeleb() {
        Intent pickContactIntent = new Intent(this, PickCelebActivity.class);
        startActivityForResult(pickContactIntent, SELECT_CELEB);
    }*/
}
