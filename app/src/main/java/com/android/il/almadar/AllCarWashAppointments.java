package com.android.il.almadar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.il.almadar.adapters.CarWashAdapter;
import com.android.il.almadar.core.Car;
import com.android.il.almadar.core.CarWash;
import com.android.il.almadar.core.RepairShop;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class AllCarWashAppointments extends AppCompatActivity {

    ListView list;
    List<CarWash> carsList;
    CarWashAdapter adapter;

    private void init(){
        list = (ListView) findViewById(R.id.listViewCars);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(AllCarWashAppointments.this, EditCarWashAppointmentActivity.class);
                intent.putExtra("object_id", carsList.get(position).getObject().getObjectId());
                startActivity(intent);

            }
        });
        carsList = new ArrayList<CarWash>();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Wash");
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.include("car");
        query.include("shop");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> scoreList, ParseException e) {
                if (e == null) {
                    Log.e("Cars", "Retrieved " + scoreList.size() + " cars");
                    for(ParseObject object: scoreList){
                        CarWash car = new CarWash();
                        car.setObject(object);
                        try {
                            Car car1 = new Car();
                            car1.setObject((ParseObject) object.get("car"));
                            car.setCar(car1.getObject());

                            car.setUser(ParseUser.getCurrentUser());
                            RepairShop shop = new RepairShop();
                            shop.setParseObject((ParseObject) object.get("shop"));
                            car.setRepairShop(shop.getParseObject());
                            carsList.add(car);
                        } catch (Exception e1){
                            e1.printStackTrace();
                        }
                    }
                    adapter = new CarWashAdapter(AllCarWashAppointments.this, R.layout.car_wash_list_item, carsList);
                    list.setAdapter(adapter);
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
        setContentView(R.layout.activity_all_car_wash_appointments);
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
