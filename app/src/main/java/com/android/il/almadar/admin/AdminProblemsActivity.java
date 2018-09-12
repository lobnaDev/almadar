package com.android.il.almadar.admin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.il.almadar.R;
import com.android.il.almadar.adapters.MaintenanceAdapter;
import com.android.il.almadar.core.RepairShop;
import com.android.il.almadar.core.UserService;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class AdminProblemsActivity extends AppCompatActivity {

    private ListView lvMaintenanceHistory;
    private ArrayList<UserService> list = new ArrayList<UserService>();
    private MaintenanceAdapter adapter;

    String issue;


    private String getService(){
        issue = getIntent().getExtras().getString("service");
        return issue;
    }

    private  void init(){
        getService();
        lvMaintenanceHistory = (ListView) findViewById(R.id.listViewCars);
        TextView tvTitle = (TextView) findViewById(R.id.textViewPro);
        tvTitle.setText("Submitted " + issue.toUpperCase());
        lvMaintenanceHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserService service = list.get(position);
                Intent intent = new Intent(AdminProblemsActivity.this, EditNewProblemActivity.class);
                intent.putExtra("id", service.getObject().getObjectId());
                startActivity(intent);
            }
        });
    }

    ParseUser user;
    RepairShop shop;

    private void getCurrentUser(){
        ParseQuery<ParseUser> query = ParseQuery.getQuery("_User");
        query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.include("repair_shop");
        shop = new RepairShop();
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                user = objects.get(0);
                shop.setParseObject((ParseObject) objects.get(0).get("repair_shop"));
                getListItems(shop);
            }
        });


    }
    private void getListItems(RepairShop shop){

        ParseQuery<ParseObject> query2 = ParseQuery.getQuery("RepairShop");
//        query2.whereEqualTo("object_id", ((ParseObject)ParseUser.getCurrentUser().get("repair_shop")).getObjectId());
        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserService");
        query.whereEqualTo("Service", issue);
        query.whereEqualTo("repair_shop", shop.getParseObject());
        //       shop.setParseObject((ParseObject) ParseUser.getCurrentUser().get("repair_shop"));
        query.include("Car");
        query.include("repair_shop");
        query.orderByDescending("createdAt");
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
                                    list.add(service);
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                }

                    }
                    adapter = new MaintenanceAdapter(AdminProblemsActivity.this, R.layout.car_wash_list_item, list);
                    lvMaintenanceHistory.setAdapter(adapter);
                } else {
                    Log.e("Cars", "Error: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_my_car);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        try {
            init();
            getCurrentUser();
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
