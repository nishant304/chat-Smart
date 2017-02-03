package com.smart.rchat.smart.application;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.smart.rchat.smart.util.AppUtil;

/**
 * Created by nishant on 02.02.17.
 */

public class SmartApp extends Application implements Application.ActivityLifecycleCallbacks {

    private Handler handler;

    private Runnable userLeft = new Runnable() {
        @Override
        public void run() {
            if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
                FirebaseDatabase.getInstance().getReference().child("Users").
                        child(FirebaseAuth.getInstance().getCurrentUser().getUid()).
                        child("status").setValue("last seen at " + AppUtil.getCurrentTime());
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(this);
        handler = new Handler();
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            handler.removeCallbacks(userLeft);
    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {
            handler.postDelayed(userLeft,1000);
    }

    @Override
    public void onActivityResumed(Activity activity) {
        handler.removeCallbacks(userLeft);
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        handler.removeCallbacks(userLeft);
    }

    @Override
    public void onActivityStopped(Activity activity) {

    }
}
