package com.belmedia.fakecallsandsms;

import android.app.Application;

import com.flurry.android.FlurryAgent;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;


/**
 * Created by yshahak on 07/01/2015.
 */
public class MyApplication extends Application {
    //Tracker tracker;
    String TAG = getClass().getSimpleName();

    private final String Flurry_id = "4Q49G8PJHXTK5F7C4HFM";



    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        // configure Flurry
        FlurryAgent.setLogEnabled(false);

        // init Flurry
        FlurryAgent.init(this, Flurry_id);
    }

}