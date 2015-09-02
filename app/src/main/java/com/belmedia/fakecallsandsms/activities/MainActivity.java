package com.belmedia.fakecallsandsms.activities;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.belmedia.fakecallsandsms.R;
import com.flurry.android.FlurryAgent;
import com.ironsource.mobilcore.CallbackResponse;
import com.ironsource.mobilcore.MobileCore;
import com.startapp.android.publish.Ad;
import com.startapp.android.publish.AdEventListener;
import com.startapp.android.publish.StartAppAd;
import com.startapp.android.publish.StartAppSDK;

public class MainActivity extends AppCompatActivity  implements AdEventListener {

    private final String StartApp_id = "207720152";
    private final String MOBILE_CORE_TAG = "7HI5CZPBF7JBBSG0ZDZOZPKDEL9VF";

    private StartAppAd startAppAd = new StartAppAd(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        boolean isDebuggable =  ( 0 != ( getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE ) );

        super.onCreate(savedInstanceState);
        StartAppSDK.init(this, StartApp_id, true);
        MobileCore.init(this, MOBILE_CORE_TAG, MobileCore.LOG_TYPE.PRODUCTION, MobileCore.AD_UNITS.INTERSTITIAL);
        if (savedInstanceState == null && !isDebuggable) {
            MobileCore.showInterstitial(this, new CallbackResponse() {
                @Override
                public void onConfirmation(TYPE type) {
                    if (type != TYPE.INTERSTITIAL_NOT_READY) {
                        startAppAd.showAd();
                        startAppAd.loadAd();
                    }
                }
            });
        }

        //Utils.getSmsConversation(this);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onStart(){
        super.onStart();
        FlurryAgent.onStartSession(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        startAppAd.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        startAppAd.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        FlurryAgent.onEndSession(this);
    }

    @Override
    public void onBackPressed() {
        startAppAd.onBackPressed();
        super.onBackPressed();
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void startActivity(View view) {
        int id = view.getId();
        Intent intent = null;
        switch (id){
            case R.id.button_call:
                intent = new Intent(this, FakeCall.class);
                break;
            case R.id.button_SMS:
                intent = new Intent(this, FakeSMS.class);
                break;
        }
        if (intent != null)
            startActivity(intent);
    }

    @Override
    public void onReceiveAd(Ad ad) {

    }

    @Override
    public void onFailedToReceiveAd(Ad ad) {

    }
}
