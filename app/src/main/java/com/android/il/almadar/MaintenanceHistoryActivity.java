package com.android.il.almadar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.il.almadar.adapters.MaintenanceAdapter;
import com.android.il.almadar.core.UserService;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class MaintenanceHistoryActivity extends AppCompatActivity {


    String service;
    private ListView lvMaintenanceHistory;
    private ArrayList<UserService> list = new ArrayList<UserService>();
    private MaintenanceAdapter adapter;

    // initialize all the variables
    private void init(){
        lvMaintenanceHistory = (ListView) findViewById(R.id.listViewMaintenance);
        lvMaintenanceHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(service.equals("problem")){
                    Intent intent = new Intent(MaintenanceHistoryActivity.this, EditMaintenanceActivity.class);
                    UserService service = list.get(position);
                    intent.putExtra("id", service.getObject().getObjectId());
                    startActivity(intent);
                } else{
                    Intent intent = new Intent(MaintenanceHistoryActivity.this, EditMaintenanceActivity.class);
                    UserService service = list.get(position);
                    intent.putExtra("id", service.getObject().getObjectId());
                    startActivity(intent);
                }
            }
        });

    }

    private void getIntentData(){
        service = getIntent().getExtras().getString("service");
    }

    private void getListItems(){
        getIntentData();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserService");
        query.whereEqualTo("User", ParseUser.getCurrentUser());
        query.whereEqualTo("Service",service );
        query.include("Car");
        query.include("repair_shop");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> scoreList, ParseException e) {
                if (e == null) {
                    Log.e("Cars", "Retrieved " + scoreList.size() + " cars");
                    for(ParseObject object: scoreList){
                        UserService service = new UserService();
                        try {
                            service.setObject(object);
                            service.setCar((ParseObject) object.get("Car"));
                            service.setRepairShop((ParseObject)object.get("repair_shop"));
                            list.add(service);
                        } catch (Exception e1){
                            e1.printStackTrace();
                        }
                    }
                    adapter = new MaintenanceAdapter(MaintenanceHistoryActivity.this, R.layout.car_wash_list_item, list);
                    lvMaintenanceHistory.setAdapter(adapter);
                } else {
                    Log.e("Cars", "Error: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    // on create method
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance_history);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        init();
        getListItems();

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
