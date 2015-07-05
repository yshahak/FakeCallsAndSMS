package com.belmedia.fakecallsandsms;

import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by B.E.L on 05/07/2015.
 */
public class IncomeCallActivity extends AppCompatActivity {
    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_call);
        RingtoneManager ringtoneManager = new RingtoneManager(this);
        Uri uriRingtone = RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_RINGTONE);
        mp = MediaPlayer.create(this, Settings.System.DEFAULT_RINGTONE_URI);
        mp.setLooping(true);
        mp.start();

    }

    public void stopCall(View view) {
        mp.stop();
    }
}
