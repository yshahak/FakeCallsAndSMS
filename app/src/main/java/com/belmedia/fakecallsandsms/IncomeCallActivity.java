package com.belmedia.fakecallsandsms;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by B.E.L on 05/07/2015.
 */
public class IncomeCallActivity extends AppCompatActivity{
    @Bind(R.id.image_view_contact) ImageView imageContact;
    @Bind(R.id.container_detail_text) LinearLayout containerContactTexts;
    @Bind(R.id.image_call) ImageView imageCall;
    @Bind(R.id.image_end) ImageView imageEnd;
    @Bind(R.id.text_view_incoming_call) TextView textIncomeCall;

    public static final String EXTRA_IMAGE_URI = "imageUri";
    final int contactDPsize = 300, imageBtnMargin = 50;
    int screenWidth;
    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_call);
        ButterKnife.bind(this);
        IconListener iconListener = new IconListener();
        imageCall.setOnTouchListener(iconListener);
        imageEnd.setOnTouchListener(iconListener);

        Uri imageUri = getIntent().getParcelableExtra(EXTRA_IMAGE_URI);
        screenWidth =  getWindowManager().getDefaultDisplay().getWidth();
        if (imageUri != null){
            int height = Utils.getDpInPixels(contactDPsize, this);
            Picasso.with(this)
                    .load(imageUri)
                    .resize(screenWidth, height)
                    .centerCrop()
                    .into(imageContact);
        }
        mp = MediaPlayer.create(this, Settings.System.DEFAULT_RINGTONE_URI);
        mp.setLooping(true);
        mp.start();

    }


    public void answerCall() {
        if (mp.isPlaying()) {
            mp.stop();
            textIncomeCall.post(updateTimeOfCall);
        }
        Log.d("TAG", "once");
    }

    public void endCall() {
        mp.stop();
        containerContactTexts.setBackgroundColor(getResources().getColor(R.color.red));
        textIncomeCall.setText(getString(R.string.end_call));
        textIncomeCall.postDelayed(finish, 2000);
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



    class IconListener implements View.OnTouchListener{
        Boolean isCallIcon;
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
                    defaultMargin = Utils.getDpInPixels(imageBtnMargin, v.getContext());
                    params = (RelativeLayout.LayoutParams) v.getLayoutParams();
                    if (iconWidth == 0) {
                        iconWidth = v.getWidth();
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    int x = (int)event.getRawX();
                    if (moveTheIcon(v, x)){
                        if (isCallIcon)
                            answerCall();
                        else
                            endCall();
                        return false;
                    }
                    break;
                case MotionEvent.ACTION_UP:
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
            } else {
                if ( x <= screenWidth / 2)
                    return true;
                params.rightMargin =  (endIconInitialPosition - x) + iconWidth;
            }
            icon.setLayoutParams(params);
            return false;
        }

        private void finalizeGesture(@NonNull View icon) {
            params.leftMargin = defaultMargin;
            icon.setLayoutParams(params);
        }

    }



}
