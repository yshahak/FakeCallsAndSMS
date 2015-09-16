package com.belmedia.fakecallsandsms;

import com.crashlytics.android.Crashlytics;

/**
 * Created by B.E.L on 12/01/2015.
 */
public class ExceptionHandler {
    public static boolean enableReporting = true;

    public static void handleException(Exception e) {
        if (enableReporting) {
            try {
                Crashlytics.logException(e);
            } catch (Exception ex) {
            } finally {
                e.printStackTrace();
            }
        } else
            e.printStackTrace();

    }

    public static void handleException(OutOfMemoryError error) {
        error.printStackTrace();
    }
}

