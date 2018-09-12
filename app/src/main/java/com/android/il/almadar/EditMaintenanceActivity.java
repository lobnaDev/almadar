package com.android.il.almadar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.il.almadar.core.UserService;
import com.android.il.almadar.technician.BaseActivityMessage;
import com.android.il.almadar.technician.MessagingActivity;
import com.android.il.almadar.technician.SinchService;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.sinch.android.rtc.SinchError;

import java.util.List;

public class EditMaintenanceActivity extends BaseActivityMessage implements SinchService.StartFailedListener {


    private TextView tvDate, tvCarInfo, tvService, tvDesc, tvComment, tvTechInfo, tvStatus;
    private Button bCancel, bChat, bPhone;
    private ProgressDialog mSpinner;

    private UserService userService;

    //open a conversation with one person
    public void openConversation() {

        Intent intent = new Intent(getApplicationContext(), MessagingActivity.class);
        intent.putExtra("id", userService.getServiceUser().getObjectId());
        intent.putExtra("username", userService.getServiceUser().getUsername());
        startActivity(intent);
    }

    private String getIntentData(){
         return getIntent().getExtras().getString("id");
    }

    private void getUserService(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserService");
        query.whereEqualTo("objectId", getIntentData());
        query.include("Car");
        query.include("repair_shop");
        query.include("User");

        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> scoreList, ParseException e) {
                if (e == null) {
                    Log.e("Cars", "Retrieved " + scoreList.size() + " cars");
                    for (ParseObject object : scoreList) {
                        UserService service = new UserService();
                        try {
                            service.setObject(object);
                            service.setCar((ParseObject) object.get("Car"));
                            service.setRepairShop((ParseObject) object.get("repair_shop"));
                            if (object.get("Tech") != null)
                                service.setServiceTech((ParseUser) object.get("Tech"));

                            userService = service;
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }

                    tvCarInfo.setText(userService.getCar().getCarType() + " " + userService.getCar().getCarModel() + " " + userService.getCar().getCarYear());
                    if (userService.getServiceFinishDate() == null)
                        tvStatus.setText("not complete");
                    else
                        tvStatus.setText("complete");
                    try {
                        if (userService.getServiceTech() != null)
                            userService.getServiceTech().fetchIfNeeded();
                        tvTechInfo.setText("Technician name: " + userService.getServiceTech().getString("full_name") + ", Shop: " + userService.getRepairShop().getRepairShopName() + ", Shop Number: " + userService.getRepairShop().getRepairShopMobile());
                    } catch (Exception e1){
                        e1.printStackTrace();
                    }
                    tvComment.setText(userService.getServiceTechComment());
                    tvDate.setText(userService.getServiceDate().toString());
                    tvDesc.setText(userService.getServiceIssue());
                    tvService.setText(userService.getServiceName() + " : " + userService.getServiceTool() + " " + userService.getServiceSubTool());
                } else {
                    Log.e("Cars", "Error: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }
    /**
     * Initialize all widgets
     */
    private void init(){
        // textview
        tvDate = (TextView) findViewById(R.id.editMainTVDate);
        tvCarInfo = (TextView) findViewById(R.id.editMainTVCarInfo);
        tvService = (TextView) findViewById(R.id.editMainTVService);
        tvDesc = (TextView) findViewById(R.id.editMainTVDesc);
        tvComment = (TextView) findViewById(R.id.editMainTVComment);
        tvTechInfo = (TextView) findViewById(R.id.editMainTVTechn);
        tvStatus = (TextView) findViewById(R.id.editMainTVStatus);
        // button
        bCancel = (Button) findViewById(R.id.buttonCancelRequest);
        bChat = (Button) findViewById(R.id.buttonChat);
        bPhone = (Button) findViewById(R.id.buttonCallTech);
        getUserService();
        // onclick listeners
        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        bChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!getSinchServiceInterface().isStarted()) {
                    getSinchServiceInterface().startClient(ParseUser.getCurrentUser().getUsername());
                    showSpinner();
                } else {
                    openConversation();
                }
            }
        });

        bPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone =  userService.getServiceTech().getString("phone");
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + Uri.encode(phone.trim())));
                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(callIntent);
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_maintenance);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        init();
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

    @Override
    protected void onServiceConnected() {
        getSinchServiceInterface().setStartListener(this);
    }

    @Override
    protected void onPause() {
        if (mSpinner != null) {
            mSpinner.dismiss();
        }
        super.onPause();
    }

    @Override
    public void onStartFailed(SinchError error) {
        Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show();
        if (mSpinner != null) {
            mSpinner.dismiss();
        }
    }

    @Override
    public void onStarted() {
        openConversation();
    }
    private void showSpinner() {
        mSpinner = new ProgressDialog(this);
        mSpinner.setTitle("Logging in");
        mSpinner.setMessage("Please wait...");
        mSpinner.show();
    }
}
