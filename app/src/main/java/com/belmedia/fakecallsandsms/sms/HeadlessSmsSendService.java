package com.belmedia.fakecallsandsms.sms;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by yshahak on 26/08/2015.
 */
public class HeadlessSmsSendService extends IntentService {
    public HeadlessSmsSendService() {
        super(HeadlessSmsSendService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}