package com.android.il.almadar.admin;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.il.almadar.R;
import com.android.il.almadar.core.UserService;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EditNewProblemActivity extends AppCompatActivity {

    private TextView tvCarInfo, tvProblemDesc, tvUserComment;
    private Button bSave;
    private Spinner spinner;
    String[] array1 = {"Car1", "Car2"};
    private ArrayList<ParseUser> techList;
    private HashMap<String, String> TECH_ARRAY;
    ParseUser tech;
    String selectedTech;
    private UserService userService;
    String shop;
    private Button bDelete;
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
                    Log.e("Service", "Retrieved " + scoreList.size() + " cars");
                    for(ParseObject object: scoreList){
                        UserService service = new UserService();
                        try {
                            service.setObject(object);
                            service.setCar((ParseObject) object.get("Car"));
                            service.setRepairShop((ParseObject) object.get("repair_shop"));
                            if(object.get("Tech")!=null)
                                service.setServiceTech((ParseUser) object.get("Tech"));

                            userService = service;
                            shop = userService.getRepairShop().getParseObject().getObjectId();
                            getShopList(shop);
                        } catch (Exception e1){
                            e1.printStackTrace();
                        }
                    }
                    tvCarInfo.setText(userService.getCar().getCarType() + " "+userService.getCar().getCarModel() + " "+ userService.getCar().getCarYear());
                    try {
                        tvUserComment.setText("Username: " + userService.getServiceUser().getUsername() + ", comment: " + userService.getServiceIssue());

                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                    String tow;
                    if(userService.getServiceTowCar()){
                        tow = "The car needs to be towed/lifted";
                    } else{
                        tow="The car doesn't need to be towed/lifted";
                    }
                    tvProblemDesc.setText("Date request made: " + userService.getServiceDate() + "\n" + tow + "\n" + userService.getServiceName() + " : " + userService.getServiceTool() + " " + userService.getServiceSubTool());


                } else {
                    Log.e("Cars", "Error: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }


    private void init(){
        // textview
        tvCarInfo = (TextView) findViewById(R.id.editProblemTVCarInfo);
        tvProblemDesc = (TextView) findViewById(R.id.editProblemTVDesc);
        tvUserComment = (TextView) findViewById(R.id.editProblemTVUserComment);
        bSave = (Button) findViewById(R.id.buttonTechnician);
        spinner = (Spinner) findViewById(R.id.spinnerTech);
        bDelete = (Button) findViewById(R.id.buttonDelete);
        bDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder1 = new AlertDialog.Builder(EditNewProblemActivity.this);
                builder1.setMessage("Are you sure you want to delete the problem?");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                userService.getObject().deleteInBackground(new DeleteCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            Toast.makeText(EditNewProblemActivity.this, "Request deleted successfully", Toast.LENGTH_LONG).show();
                                            finish();
                                        } else
                                            Toast.makeText(EditNewProblemActivity.this, "Request wasn't deleted, please try again later", Toast.LENGTH_LONG).show();
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
                userService.setServiceTech(tech);
                userService.getObject().saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(EditNewProblemActivity.this, "Service updated successfully.", Toast.LENGTH_LONG)
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
                                data.put("type", "tech");
                            }catch (Exception e1){
                                e1.printStackTrace();
                                return;
                            }


                            ParsePush parsePush = new ParsePush();
                            parsePush.setData(data);

                            ParseQuery<ParseInstallation> parseQuery = ParseQuery.getQuery(ParseInstallation.class);
                            if(userService.getServiceTech()!=null) {
                                parseQuery.whereEqualTo("email", userService.getServiceTech().getEmail());
                                parsePush.setQuery(parseQuery);
                                parsePush.sendInBackground();
                            }
                        } else {
                            Toast.makeText(EditNewProblemActivity.this, "Service wasn't updated an error occurred please try again later.", Toast.LENGTH_LONG)
                                    .show();
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        

    }
    
    private void setSpinnerAdapters(){
        // setting the  adapter
        ArrayAdapter<String> carsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, array1);
        carsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(carsAdapter);
        // setting the on item selected listener
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub

                selectedTech = TECH_ARRAY.get(parent.getSelectedItem().toString());
                Log.e("Selected Car", selectedTech);
                ParseQuery<ParseUser> query = ParseQuery.getQuery("_User");
                query.getInBackground(selectedTech, new GetCallback<ParseUser>() {
                    public void done(ParseUser object, ParseException e) {
                        if (e == null) {
                            tech = object;
                            // object will be your car
                        } else {
                            // something went wrong
                        }
                    }
                });

            }


            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
                //selectedGender = parent.getSelectedItem().toString();
            }
        });
        if(userService.getServiceTech()!=null){
            int i = 0;
            for (ParseUser car1 : techList) {
                if (car1.getObjectId().equals(userService.getServiceTech().getObjectId())) {
                    spinner.setSelection(i);
                    Log.e("Here", "tech is valid");
                    break;
                }
                Log.e("Here", "tech is not valid");

                i++;
            }
        }
    }

    private void getShopList(String shop){
        techList = new ArrayList<ParseUser>() ;
        TECH_ARRAY = new HashMap<String, String>();
        ParseQuery<ParseUser> query = ParseQuery.getQuery("_User");
        query.whereEqualTo("role", "tech");
        query.whereEqualTo("repair_shop", userService.getRepairShop().getParseObject());
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> scoreList, ParseException e) {
                if (e == null) {
                    Log.e("Users", "Retrieved " + scoreList.size() + " users");
                    array1 = new String[scoreList.size()];
                    for (int i = 0; i < scoreList.size(); i++) {
                        ParseUser tech = scoreList.get(i);
                        TECH_ARRAY.put(tech.get("full_name").toString(), tech.getObjectId());
                        array1[i] = tech.get("full_name").toString();
                        techList.add(tech);
                    }

                    setSpinnerAdapters();
                } else {
                    Log.e("Cars", "Error: " + e.getMessage());
                }
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_new_problem);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        try {
            init();
            getUserService();
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
}
