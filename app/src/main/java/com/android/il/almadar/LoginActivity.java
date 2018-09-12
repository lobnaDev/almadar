package com.android.il.almadar;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.il.almadar.admin.AdminMainActivity;
import com.android.il.almadar.technician.BaseActivityMessage;
import com.android.il.almadar.technician.TechMainActivity;
import com.android.il.almadar.utils.PrefManager;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends BaseActivityMessage {

    private Button bLogin, bRegister;
    private EditText etUsername, etPassword;
    Intent intent;

    PrefManager pref ;
    private void init() {
        pref = new PrefManager(getApplicationContext());
        bLogin = (Button) findViewById(R.id.loginButton);
        bRegister = (Button) findViewById(R.id.registerButton);
        etUsername = (EditText) findViewById(R.id.usernameText);
        etPassword = (EditText) findViewById(R.id.passwordText);
        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(LoginActivity.this, CarSelectorActivity.class);
                startActivity(intent);
            }
        });
    bRegister.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(LoginActivity.this, NewAccountActivity.class);
            startActivity(intent);

        }
    });
    }
    @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_login);
                init();
            }}

//            @Override
//            public void onClick(View v) {
//                intent = new Intent(LoginActivity.this, CarSelectorActivity.class);
//                startActivity(intent);
//            };
//            @Override
//            protected void onCreate(Bundle savedInstanceState) {
//                super.onCreate(savedInstanceState);
//                setContentView(R.layout.activity_login);
//                init();
//            }
//
//                ParseUser.logInInBackground(etUsername.getText().toString(), etPassword.getText().toString(), new LogInCallback() {
//                    public void done(ParseUser user, ParseException e) {
//                        if (user != null) {
//
//                            if (e == null) {
//
//                                try {
//                                    pref.createLoginSession(user.getEmail());
//
//                                }catch (Exception e1){
//                                    e1.printStackTrace();
//                                }
//
//                                if (user.get("role").equals("user")) {
//                                    intent = new Intent(LoginActivity.this, CarSelectorActivity.class);
//                                    Snackbar.make(findViewById(android.R.id.content), "Login was successful", Snackbar.LENGTH_LONG)
//                                            .show();
//                                    if (intent != null) {
//                                        startActivity(intent);
//                                    }
//                                    // finish();
//                                } else if (user.get("role").equals("tech")) {
//                                    intent = new Intent(LoginActivity.this, TechMainActivity.class);
//                                    Snackbar.make(findViewById(android.R.id.content), "Login was successful", Snackbar.LENGTH_LONG)
//                                            .show();
//                                    if (intent != null) {
//                                        startActivity(intent);
//                                    }
//                                    //  finish();
//                                } else if (user.get("role").equals("admin")) {
//
//                                    intent = new Intent(LoginActivity.this, AdminMainActivity.class);
//                                    Snackbar.make(findViewById(android.R.id.content), "Login was successful", Snackbar.LENGTH_LONG)
//                                            .show();
//                                    if (intent != null) {
//                                        startActivity(intent);
//                                    }
//                                }
//                                finish();
//                            }
//                        } else {
//                            e.printStackTrace();
//                            Snackbar.make(findViewById(android.R.id.content), "Sorry an error occurred try again later", Snackbar.LENGTH_LONG)
//                                    .show();
//                        }
//                    }
//
//                });
//            }
//        });

//        bRegister.setOnClickListener(new View.OnClickListener(){
//        @Override
//        public void onClick (View v){
//        Intent intent = new Intent(LoginActivity.this, NewAccountActivity.class);
//        startActivity(intent);
//    }
//    });


    // on create method

