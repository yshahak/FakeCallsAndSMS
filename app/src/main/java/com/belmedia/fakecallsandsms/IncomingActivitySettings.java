package com.belmedia.fakecallsandsms;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

public class IncomingActivitySettings extends AppCompatActivity  {

    final int SecFactor = 1000, MinFactor = 1000*60;
    private static final int SELECT_PICTURE = 1, CROP_PIC_REQUEST_CODE = 2;
    final int contactDP = 300;
    Uri selectedImageUri;
    //ADDED

    @Bind(R.id.button_call) Button btnCall;
    @Bind(R.id.button_add_picture) Button btnAddPic;
    @Bind(R.id.button_add_voice) Button btnAddVoice;

    @Bind(R.id.image_contact)  ImageView imageContact;

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



    public void pickImage(View view) {
        Intent intent   = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        //startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
        startActivityForResult(intent, SELECT_PICTURE);
    }

    //UPDATED
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                selectedImageUri = data.getData();
                if(selectedImageUri !=null) {
                    imageContact.setVisibility(View.VISIBLE);

                    Picasso.with(this)
                            .load(selectedImageUri)
                            .resize(btnAddPic.getWidth(), btnAddPic.getHeight())
                            .centerCrop()
                            .into(imageContact);
                    btnAddPic.setVisibility(View.GONE);
                }else
                    System.out.println("selectedImagePath is null");
            }
        }
    }

    private void doCrop(Uri picUri, int width, int height) {
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(picUri, "image/*");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("outputX", width);
            cropIntent.putExtra("outputY", height);
            cropIntent.putExtra("return-data", true);
            startActivityForResult(cropIntent, CROP_PIC_REQUEST_CODE);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException e) {
            Bitmap bm = Utils.getBitmapFromUri(selectedImageUri, this, btnAddPic.getWidth(), btnAddPic.getHeight());
            setContactImage(bm);
            ExceptionHandler.handleException(e);
        }
    }

    private void setContactImage(Bitmap bitmap) {
        if (bitmap != null){
            imageContact.setImageBitmap(bitmap);
            imageContact.setVisibility(View.VISIBLE);
        } else {
            btnAddPic.setVisibility(View.VISIBLE);
            selectedImageUri = null;
        }
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
        if (selectedImageUri != null)
            incomeCallActivity.putExtra(IncomeCallActivity.EXTRA_IMAGE_URI, selectedImageUri);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,  incomeCallActivity, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1500, pendingIntent);

        //show the home screen
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }


}
