package com.android.il.almadar;

import android.app.Application;
import android.content.Intent;

import com.android.il.almadar.technician.MessageService;
import com.android.il.almadar.utils.ParseUtils;
import com.parse.Parse;
import com.parse.ParseInstallation;


public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
     //   Parse.initialize(this);
        // register with parse
        ParseUtils.registerParse(this);
        //  PushService.setDefaultPushCallback(this, MainActivity.class);
       // ParseUser.enableRevocableSessionInBackground();
    }

    public static void subscribeWithEmail(String email) {
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();

        installation.put("email", email);

        installation.saveInBackground();
    }

    @Override
    public void onTerminate() {
        stopService(new Intent(this, MessageService.class));
        super.onTerminate();
    }
}
