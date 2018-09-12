package com.android.il.almadar.admin;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.il.almadar.R;
import com.android.il.almadar.core.RepairShop;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.HashMap;
import java.util.List;

public class AddNewTechActivity extends AppCompatActivity {

    EditText etFullName, etEmail, etPhone, etUsername, etPassword;
    Button bSave;
    ParseUser user;
    String id;
    RepairShop shop;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void getCurrentUser(){
        ParseQuery<ParseUser> query = ParseQuery.getQuery("_User");
        query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.include("repair_shop");
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                user = objects.get(0);
                shop = new RepairShop();
               shop.setParseObject( ((ParseObject) objects.get(0).get("repair_shop")));
            }
        });


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_tech);
        getCurrentUser();
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        init();
    }

    // initialize all the variables
    private void init(){
        etFullName = (EditText) findViewById(R.id.editTextFullName);
        etEmail = (EditText) findViewById(R.id.editTextEmail);
        etPhone = (EditText) findViewById(R.id.editTextPhone);
        etUsername = (EditText) findViewById(R.id.editTextUsername);
        etPassword = (EditText) findViewById(R.id.editTextPassword);

        bSave = (Button) findViewById(R.id.buttonSaveChanges);
        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValid()) {

                    HashMap<String, Object> params = new HashMap<String, Object>();
                    params.put("username", etUsername.getText().toString());
                    params.put("phone", etPhone.getText().toString());
                    params.put("full_name", etFullName.getText().toString());
                    params.put("email", etEmail.getText().toString());
                    params.put("password", etPassword.getText().toString());
                    params.put("repair", shop.getParseObject().getObjectId());
                    ParseCloud.callFunctionInBackground("AddNewUser", params, new FunctionCallback<String>() {
                        @Override
                        public void done(String object, ParseException e) {
                            if (e != null)
                                e.printStackTrace();
                            Snackbar.make(findViewById(android.R.id.content), object, Snackbar.LENGTH_LONG).show();

                        }
                    });
                } else {

                }
            }
        });



    }
    private boolean isValid(){
        if(TextUtils.isEmpty(etEmail.getText()) || TextUtils.isEmpty(etPassword.getText()) || TextUtils.isEmpty(etFullName.getText()) || TextUtils.isEmpty(etPhone.getText()) || TextUtils.isEmpty(etUsername.getText())){
            Snackbar.make(findViewById(android.R.id.content), "Please don't leave any empty fields", Snackbar.LENGTH_LONG).show();
            return false;
        } else{
            return true;
        }
    }
}
