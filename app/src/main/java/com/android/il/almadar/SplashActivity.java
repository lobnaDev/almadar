package com.android.il.almadar;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;

import com.android.il.almadar.admin.AdminMainActivity;
import com.android.il.almadar.technician.MessageService;
import com.android.il.almadar.technician.TechMainActivity;
import com.android.il.almadar.utils.ParseUtils;
import com.android.il.almadar.utils.PrefManager;
import com.parse.ParseUser;

public class SplashActivity extends AppCompatActivity {
    Intent intent;
    Intent serviceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        serviceIntent = new Intent(getApplicationContext(), MessageService.class);
        Thread timer= new Thread(){
            @Override
            public void run() {
                try {
                    sleep(5000);
                    setProgressBarIndeterminateVisibility(true);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    final ParseUser user = ParseUser.getCurrentUser();
//                    setProgressBarIndeterminateVisibility(false);
                    if (user != null) {
                        PrefManager pref = new PrefManager(getApplicationContext());
                        String email = pref.getEmail();

                        if (email != null) {
                            ParseUtils.subscribeWithEmail(pref.getEmail());
                        }else{
                            Log.e("SPLASH", "Email is null. Not subscribing to parse!");
                        }

                        if (user.get("role").equals("user")) {
                                intent = new Intent(SplashActivity.this, CarSelectorActivity.class);
                                Snackbar.make(findViewById(android.R.id.content), "Welcome back "+user.getUsername(), Snackbar.LENGTH_LONG)
                                        .show();
                                if (intent != null) {
                                    startActivity(intent);
                                }
                                // finish();
                            } else if (user.get("role").equals("tech")) {
                                intent = new Intent(SplashActivity.this, TechMainActivity.class);
                                Snackbar.make(findViewById(android.R.id.content), "Welcome back "+user.getUsername(), Snackbar.LENGTH_LONG)
                                        .show();
                                if (intent != null) {
                                    startActivity(intent);
                                }
                                //  finish();
                            } else if (user.get("role").equals("admin")) {

                                intent = new Intent(SplashActivity.this, AdminMainActivity.class);
                                Snackbar.make(findViewById(android.R.id.content), "Welcome back "+user.getUsername(), Snackbar.LENGTH_LONG)
                                        .show();
                                if (intent != null) {
                                    startActivity(intent);
                                }
                            }

                    } else{
                        intent = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                }
            }
        };

        timer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
