package com.belmedia.fakecallsandsms.activities;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.belmedia.fakecallsandsms.ExceptionHandler;
import com.belmedia.fakecallsandsms.R;
import com.belmedia.fakecallsandsms.Utils;
import com.crashlytics.android.Crashlytics;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by B.E.L on 05/07/2015.
 */
public class IncomeCallActivity extends AppCompatActivity implements View.OnClickListener {
    @Bind(R.id.image_view_contact) ImageView imageContact;
    @Bind(R.id.container_detail_text) LinearLayout containerContactTexts;
    @Bind(R.id.image_call) ImageView imageCall;
    @Bind(R.id.image_end) ImageView imageEnd;
    @Bind(R.id.text_view_incoming_call) TextView textIncomeCall;
    @Bind(R.id.text_view_name) TextView textViewContactName;
    @Bind(R.id.text_view_number) TextView textViewContactNumber;


    Handler handler;
    final int imageBtnMargin = 50;
    int screenWidth;
    MediaPlayer mp;
    private Uri imageUri;
    int RINGER_POSITION;
    private Vibrator vibrator;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_incoming_call);
        ButterKnife.bind(this);
        handler = new Handler();
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        try {
            mp = MediaPlayer.create(this, Settings.System.DEFAULT_RINGTONE_URI);
        }catch (IllegalStateException e){
            ExceptionHandler.handleException(e);
        }
        if (mp != null && ((AudioManager)getSystemService(Context.AUDIO_SERVICE)).getRingerMode() == AudioManager.RINGER_MODE_NORMAL){
            mp.setLooping(true);
            mp.start();
        }
        if (Utils.checkVibrationIsOn(this)) //vibrate on
            handler.post(viberation);
        IconListener iconListener = new IconListener();
        imageCall.setOnTouchListener(iconListener);
        imageEnd.setOnTouchListener(iconListener);

        imageUri = getIntent().getParcelableExtra(FakeCall.KEY_IMAGE_URI);
        screenWidth =  getWindowManager().getDefaultDisplay().getWidth();
        if (imageUri != null){
            imageContact.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);
        }

        String contactName = getIntent().getStringExtra(FakeCall.KEY_CONTACT_NAME);
        if (contactName != null)
            textViewContactName.setText(contactName);

        String contactNumber = getIntent().getStringExtra(FakeCall.KEY_CONTACT_NUMBER);
        if (contactNumber != null)
            textViewContactNumber.setText(contactNumber);
    }

    Runnable viberation = new Runnable() {
        long[] pattern = { 0, 1000, 1000 };

        @Override
        public void run() {
            vibrator.vibrate(pattern , -1);
            handler.postDelayed(this, 2000);
        }
    };

    @SuppressWarnings("deprecation")
    private ViewTreeObserver.OnGlobalLayoutListener layoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            Utils.loadImageFromUri(IncomeCallActivity.this, imageUri, imageContact);
            imageContact.getViewTreeObserver().removeGlobalOnLayoutListener(this);
        }
    };


    @Override
    protected void onStop() {
        super.onStop();
        endCall(0);
    }

    public void answerCall() {
        if (mp != null && mp.isPlaying()) {
            mp.stop();
            textIncomeCall.post(updateTimeOfCall);
        }
        handler.removeCallbacks(viberation);
        imageCall.setVisibility(View.GONE);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imageEnd.getLayoutParams();
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.rightMargin = screenWidth /2 - imageEnd.getWidth() / 2;
        imageEnd.setLayoutParams(params);
        imageEnd.setOnTouchListener(null);
        imageEnd.setOnClickListener(this);
        imageEnd.setImageResource(R.drawable.hung_up_btn_clear);

        Uri musicUri = getIntent().getParcelableExtra(FakeCall.KEY_MUSIC_URI);
        if (musicUri != null){
            String pathFromUri = getRealPathFromURI(this, musicUri);
            if (pathFromUri != null) {
                mp = new MediaPlayer();
                try {
                    mp.setDataSource(this, Uri.parse(pathFromUri));
                    mp.prepare();
                    AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                    audio.setStreamVolume(AudioManager.STREAM_MUSIC, 4, AudioManager.FLAG_ALLOW_RINGER_MODES);
                    mp.setLooping(true);
                    mp.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }


    @SuppressWarnings("deprecation")
    public void endCall(int delayToFinish) {
        try {
            if (mp != null && mp.isPlaying()) {
                mp.stop();
                mp.release();
            }
        } catch (IllegalStateException e){
            Crashlytics.logException(e);
        }
        handler.removeCallbacks(viberation);
        containerContactTexts.setBackgroundColor(getResources().getColor(R.color.red));
        textIncomeCall.setText(getString(R.string.end_call));
        textIncomeCall.postDelayed(finish, delayToFinish);
        textIncomeCall.removeCallbacks(updateTimeOfCall);
    }

    private Runnable updateTimeOfCall = new Runnable() {
        int currentTime;
        @Override
        public void run() {
            textIncomeCall.setText(Utils.milliSecondsToTimer(currentTime));
            currentTime++;
            textIncomeCall.postDelayed(this, 1000);
        }
    };

    private Runnable finish = new Runnable() {
        @Override
        public void run() {
            finish();
        }
    };

    @Override
    public void onClick(View v) {
        endCall(1300);
    }


    class IconListener implements View.OnTouchListener{
        boolean isCallIcon, triggerAnswer;
        int iconWidth, defaultMargin, endIconInitialPosition;
        RelativeLayout.LayoutParams params;
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    isCallIcon = (v.equals(imageCall));
                    if (!isCallIcon && endIconInitialPosition == 0) {
                        int[] position = new int[2];
                        v.getLocationOnScreen(position);
                        endIconInitialPosition = position[0] +  iconWidth;
                    }
                    params = (RelativeLayout.LayoutParams) v.getLayoutParams();
                    if (iconWidth == 0) {
                        iconWidth = v.getWidth();
                        defaultMargin = Utils.getDpInPixels(imageBtnMargin, v.getContext());
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    int x = (int)event.getRawX();
                    if (moveTheIcon(v, x)){
                        if (!triggerAnswer) {
                            triggerAnswer = true;
                            if (isCallIcon)
                                answerCall();
                            else
                                endCall(100);
                        }
                        return false;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (!triggerAnswer)
                        finalizeGesture(v);
                    break;
                case MotionEvent.ACTION_CANCEL:
                    finalizeGesture(v);
                    break;
            }
            return true;
        }

        private boolean moveTheIcon(@NonNull View icon,int x) {
            Log.d("TAG", Integer.toString(x));
            if (isCallIcon){
                if ( x >= screenWidth / 2)
                    return true;
                params.leftMargin =  x - iconWidth / 2;
            } else { // this is end call icon
                if ( x <= screenWidth / 2)
                    return true;
                params.rightMargin =  (endIconInitialPosition - x) + iconWidth;
            }
            icon.setLayoutParams(params);
            return false;
        }

        private void finalizeGesture(@NonNull View icon) {
            if (icon.equals(imageCall))
                params.leftMargin = defaultMargin;
            else
                params.rightMargin = defaultMargin;
            icon.setLayoutParams(params);
        }

    }

    private String getRealPathFromURI(Context context, Uri contentUri) {
        String[] projection = { MediaStore.Audio.Media.DATA };
        CursorLoader loader = new CursorLoader(context, contentUri, projection, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int songPath_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        if (cursor.moveToFirst())
            return cursor.getString(songPath_index);
        else
            return null;
    }



}
