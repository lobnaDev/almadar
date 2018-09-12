package com.android.il.almadar.technician;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.il.almadar.R;
import com.android.il.almadar.core.UserService;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.sinch.android.rtc.SinchError;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.List;


public class EditProblemActivity extends BaseActivityMessage implements View.OnClickListener, SinchService.StartFailedListener {


    private Button bSave, bCall, bChat, bLocation;
    private TextView tvCarInfo, tvProblemDesc, tvUserComment;
    private CheckBox checkBox;
    private EditText etComment;
    private ProgressDialog mSpinner;
    private UserService userService;
    private ProgressDialog progressDialog;
    private BroadcastReceiver receiver = null;

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
                    for(ParseObject object: scoreList){
                        UserService service = new UserService();
                        try {
                            service.setObject(object);
                            service.setCar((ParseObject) object.get("Car"));
                            service.setRepairShop((ParseObject) object.get("repair_shop"));
                            if(object.get("Tech")!=null)
                                service.setServiceTech((ParseUser) object.get("Tech"));

                            userService = service;
                        } catch (Exception e1){
                            e1.printStackTrace();
                        }
                    }

                    tvCarInfo.setText(userService.getCar().getCarType() + " "+userService.getCar().getCarModel() + " "+ userService.getCar().getCarYear());
                    if(userService.getServiceFinishDate()==null)
                        checkBox.setChecked(false);
                    else
                        checkBox.setChecked(true);
                    tvUserComment.setText(userService.getServiceIssue());
                    String tow;
                    if(userService.getServiceTowCar()){
                        tow = "The car needs to be towed/lifted";
                    } else{
                        tow="The car doesn't need to be towed/lifted";
                    }
                    if(userService.getServiceTechComment()!=null){
                        etComment.setText(userService.getServiceTechComment());
                    }
                    tvProblemDesc.setText("Date request made: "+userService.getServiceDate()+ "\n"+tow+"\n"+userService.getServiceName() + " : "+userService.getServiceTool() + " "+userService.getServiceSubTool());
                } else {
                    Log.e("Cars", "Error: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

 /*   @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    } */
    private void init(){
        // buttons
  /*      android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
*/
        bSave = (Button) findViewById(R.id.buttonSave);
        bCall = (Button) findViewById(R.id.buttonCall);
        bChat = (Button) findViewById(R.id.buttonChat);
        bLocation = (Button) findViewById(R.id.buttonCarLocation);
        // textview
        tvCarInfo = (TextView) findViewById(R.id.editProblemTVCarInfo);
        tvProblemDesc = (TextView) findViewById(R.id.editProblemTVDesc);
        tvUserComment = (TextView) findViewById(R.id.editProblemTVUserComment);
        // checkbox
        checkBox = (CheckBox) findViewById(R.id.checkBoxIssueSolved);
        // edit text
        etComment = (EditText) findViewById(R.id.editTextTechComment);

        // set the onclick listeners
        bSave.setOnClickListener(this);
        bCall.setOnClickListener(this);
        bChat.setOnClickListener(this);
        bLocation.setOnClickListener(this);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_problem);

            getIntentData();
        init();
        getUserService();


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonSave:
                userService.setServiceTechComment(etComment.getText().toString());
                if(checkBox.isChecked())
                    userService.setServiceFinishDate(Calendar.getInstance().getTime());
                userService.getObject().saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(EditProblemActivity.this, "Service updated successfully.", Toast.LENGTH_LONG)
                                    .show();

                            String channelName = "service_"+userService.getObject().getObjectId()+"_admin";
                            Log.e("ChannelName", channelName);
                            String channelName2 = "service_"+userService.getObject().getObjectId()+"_user";
                            JSONObject data = new JSONObject();
                            try {
                                data.put("data", "com.parse.Data");
                                data.put("from", ParseUser.getCurrentUser().getEmail());
                                data.put("title", "New Urgent Problem submitted");
                                data.put("message", userService.getServiceIssue());
                                data.put("object_id", userService.getObject().getObjectId());
                                data.put("type", "user");
                            }catch (Exception e1){
                                e1.printStackTrace();
                                return;
                            }


                            ParsePush parsePush = new ParsePush();
                            parsePush.setData(data);

                            ParseQuery<ParseInstallation> parseQuery = ParseQuery.getQuery(ParseInstallation.class);
                            if(userService.getServiceUser()!=null) {
                                parseQuery.whereEqualTo("email", userService.getServiceUser().getEmail());
                                parsePush.setQuery(parseQuery);
                                parsePush.sendInBackground();
                            }
                        } else {
                            Toast.makeText(EditProblemActivity.this, "Service wasn't updated an error occured please try again later.", Toast.LENGTH_LONG)
                                    .show();
                            e.printStackTrace();
                        }
                    }
                });
                break;
            case R.id.buttonCall:

                String phone =  userService.getServiceUser().getString("phone");
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + Uri.encode(phone.trim())));
                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(callIntent);

                break;
            case R.id.buttonChat:
                if (!getSinchServiceInterface().isStarted()) {
                    getSinchServiceInterface().startClient(ParseUser.getCurrentUser().getUsername());
                    showSpinner();
                } else {
                    openConversation();
                }
                break;
            case R.id.buttonCarLocation:
                Intent intent = new Intent(EditProblemActivity.this, ViewLocationActivity.class);
                intent.putExtra("lat", userService.getCar().getLocation().getLatitude());
                intent.putExtra("long", userService.getCar().getLocation().getLongitude());
                startActivity(intent);
                break;
        }
    }

    //open a conversation with one person
    public void openConversation() {

        Intent intent = new Intent(getApplicationContext(), MessagingActivity.class);
        intent.putExtra("id", userService.getServiceUser().getObjectId());
        intent.putExtra("username", userService.getServiceUser().getUsername());
        startActivity(intent);

    }

    //show a loading spinner while the sinch client starts
    private void showSpinner2() {

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        Log.e("EditProblem", "Inside Spinner before receiver");
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.e("EditProblem", "Inside Spinner inside receiver");

                Boolean success = intent.getBooleanExtra("success", false);
                progressDialog.dismiss();
                if (!success) {
                    Toast.makeText(getApplicationContext(), "Messaging service failed to start", Toast.LENGTH_LONG).show();
                }
            }
        };

        Log.e("EditProblem", "Inside Spinner after receiver");

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("com.android.il.almadar.technician.EditProblemActivity"));
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
}
