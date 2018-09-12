package com.android.il.almadar.admin;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.il.almadar.R;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.HashMap;
import java.util.List;

public class EditTechActivity extends AppCompatActivity {

    EditText etFullName, etEmail, etPhone, etUsername;
    Button bSave, bResetPass, bDelete;
    ParseUser user;
    String id;
    private void getUser(){
        id = getIntent().getExtras().getString("user");

        ParseQuery<ParseUser> query = new ParseQuery<ParseUser>("_User");
        query.whereEqualTo("objectId", id);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                user = objects.get(0);

                // setting the values in the user interface
                if (user.get("full_name") != null)
                    etFullName.setText(user.get("full_name").toString());
                etEmail.setText(user.getEmail());
                etPhone.setText(user.get("phone").toString());
                etUsername.setText(user.getUsername());
            }
        });
    }

    // initialize all the variables
    private void init(){
        etFullName = (EditText) findViewById(R.id.editTextFullName);
        etEmail = (EditText) findViewById(R.id.editTextEmail);
        etPhone = (EditText) findViewById(R.id.editTextPhone);
        etUsername = (EditText) findViewById(R.id.editTextUsername);

        bSave = (Button) findViewById(R.id.buttonSaveChanges);
        bResetPass = (Button) findViewById(R.id.buttonResetPass);
        bDelete = (Button) findViewById(R.id.buttonDeleteTech);

        bDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder1 = new AlertDialog.Builder(EditTechActivity.this);
                builder1.setMessage("Are you sure that you want to delete the technician?");
                builder1.setCancelable(true);
                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                HashMap<String, String> params = new HashMap<String, String>();
                                params.put("userId", user.getObjectId());
                                ParseCloud.callFunctionInBackground("deleteUserWithId", params, new FunctionCallback<Float>() {
                                    @Override
                                    public void done(Float object, ParseException e) {
                                        if (e != null) {
                                            e.printStackTrace();
                                            Toast.makeText(EditTechActivity.this, "Sorry an error occurred, please try again later.", Toast.LENGTH_LONG).show();
                                        }
                                        else {
                                            Toast.makeText(EditTechActivity.this, "Technician deleted successfully", Toast.LENGTH_LONG).show();
                                            finish();
                                        }


                                    }
                                });

                            }
                        });

                builder1.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();


            }
        });
        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValid()){

                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("objectId", id);
                    params.put("username",  etUsername.getText().toString());
                    params.put("phone",  etPhone.getText().toString());
                    params.put("full_name", etFullName.getText().toString());
                    params.put("email", etEmail.getText().toString());
                    ParseCloud.callFunctionInBackground("modifyUser", params, new FunctionCallback<String>() {
                        @Override
                        public void done(String object, ParseException e) {
                            if (e != null)
                                e.printStackTrace();
                            Snackbar.make(findViewById(android.R.id.content), object, Snackbar.LENGTH_LONG).show();

                        }
                    });
                } else{

                }
            }
        });

        bResetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createChangePassDialog();
            }
        });

        getUser();
    }

    private boolean isValid(){
        if(TextUtils.isEmpty(etEmail.getText()) || TextUtils.isEmpty(etFullName.getText()) || TextUtils.isEmpty(etPhone.getText()) || TextUtils.isEmpty(etUsername.getText())){
            Snackbar.make(findViewById(android.R.id.content), "Please don't leave any empty fields", Snackbar.LENGTH_LONG).show();
            return false;
        } else{
            return true;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_tech);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        try {
            init();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
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

    private void createChangePassDialog(){
        LayoutInflater factory = LayoutInflater.from(this);
        final View dialogView = factory.inflate(
                R.layout.change_password_layout, null);
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setView(dialogView);
        final EditText etRetypePassword = (EditText) dialogView.findViewById(R.id.editTextOldPass);
        final EditText etNewPass =(EditText) dialogView.findViewById(R.id.editTextNewPass);
        dialog.setTitle("Reset User Password");
        dialog.setIcon(R.mipmap.ic_launcher);

        dialogView.findViewById(R.id.buttonSaveChanges).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //your business logic
                if (TextUtils.isEmpty(etRetypePassword.getText()) || TextUtils.isEmpty(etNewPass.getText())) {
                    Toast.makeText(EditTechActivity.this, "Please don't leave any empty fields", Toast.LENGTH_LONG).show();
                } else {
                    if (etRetypePassword.getText().toString().equals(etNewPass.getText().toString())) {
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("objectId", id);
                        params.put("password", etNewPass.getText().toString());
                        ParseCloud.callFunctionInBackground("changeUserPassword", params, new FunctionCallback<String>() {
                            @Override
                            public void done(String object, ParseException e) {
                                if (e != null)
                                    e.printStackTrace();
                                else
                                    dialog.dismiss();
                                Toast.makeText(EditTechActivity.this, object, Toast.LENGTH_LONG).show();

                            }
                        });
                    } else {
                        Toast.makeText(EditTechActivity.this, "The entered passwords don't match.", Toast.LENGTH_LONG).show();
                    }
                }

            }
        });


        dialog.show();
    }


}
