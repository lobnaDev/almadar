package com.android.il.almadar;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.ParseRole;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class NewAccountActivity extends AppCompatActivity {
    EditText userName, mobile, email, password;
    Button newAccount;
    ParseRole role;

    private void init() {
        // widgets
        userName = (EditText) findViewById(R.id.usernameText);
        mobile = (EditText) findViewById(R.id.mobileText);
        email = (EditText) findViewById(R.id.emailText);
        password = (EditText) findViewById(R.id.passwordText);
        newAccount = (Button) findViewById(R.id.creatacctbutton);

        // on click listener

        newAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValid() && passwordValid()) {
                    final ParseUser user = new ParseUser();
                    user.setUsername(userName.getText().toString());
                    user.setPassword(password.getText().toString());
                    user.setEmail(email.getText().toString());
                    user.put("role", "admin");
                    // other fields can be set just like with ParseObject
                    user.put("phone", mobile.getText().toString());
                    user.signUpInBackground(new SignUpCallback() {
                        public void done(ParseException e) {
                            if (e == null) {
                                Snackbar.make(findViewById(android.R.id.content), "Account successfully created, just ensure that you activate it by clicking on the link sent to your email.", Snackbar.LENGTH_LONG)
                                        .show();
                                Intent intent = new Intent(NewAccountActivity.this, CarSelectorActivity.class);
                                startActivity(intent);
                                finish();
                            } else

                            {
                                Snackbar.make(findViewById(android.R.id.content), "Sorry an error occurred try again later.", Snackbar.LENGTH_LONG)
                                        .show();
                                Snackbar.make(findViewById(android.R.id.content), e.getMessage(), Snackbar.LENGTH_LONG)
                                        .show();
                                e.printStackTrace();

                            }

                        }
                    });
                } else {
                    Snackbar.make(findViewById(android.R.id.content), "Please ensure that the data is valid", Snackbar.LENGTH_LONG)
                            .show();
                }
            }
        });
    }

    private boolean passwordValid() {
        if (password.getText().length() < 8) {
            password.setError("The password should be greater than 8 characters");
            return false;
        } else
            return true;
    }

    private boolean isValid() {
        if (TextUtils.isEmpty(userName.getText()) || TextUtils.isEmpty(email.getText())
                || TextUtils.isEmpty(password.getText()) || TextUtils.isEmpty(mobile.getText())) {
            return false;
        } else
            return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_account);
        init();
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
