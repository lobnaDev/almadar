package com.android.il.almadar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.il.almadar.core.Car;
import com.android.il.almadar.core.CarWash;
import com.android.il.almadar.core.RepairShop;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class CarWashActivity extends AppCompatActivity {


    private Spinner spinner;
    private EditText etInfo;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private Button bSubmit;
    private String repairID;

    private ArrayAdapter<String> carsAdapter;
    private HashMap<String, String> CAR_ARRAY;
    private String selectedCar;
    private List<Car> carsList;
    private Car car;
    private RepairShop shop;
    private String[] array1 = {"Car1", "Car2"};


    // get repair shop
    private void getRepairShop(){
        getIntentData();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("RepairShop");
        query.getInBackground(repairID, new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    shop = new RepairShop();
                    // object will be your shop
                    shop.setParseObject(object);

                } else {
                    // something went wrong
                }
            }
        });
    }
    private  void setSpinnerAdapters() {

        // setting the  adapter
        carsAdapter = new ArrayAdapter<String>(CarWashActivity.this, android.R.layout.simple_spinner_item, array1);
        carsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(carsAdapter);
        // setting the on item selected listener
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub

                selectedCar = CAR_ARRAY.get(parent.getSelectedItem().toString());
                Log.e("Selected Car", selectedCar);
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Car");
                query.getInBackground(selectedCar, new GetCallback<ParseObject>() {
                    public void done(ParseObject object, ParseException e) {
                        if (e == null) {
                            car = new Car("Car");
                            // object will be your car
                            car.setObject(object);
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
    }


    // get the cars list
    private void getCarList(){
        carsList = new ArrayList<Car>() ;
        CAR_ARRAY = new HashMap<String, String>();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Car");
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> scoreList, ParseException e) {
                if (e == null) {
                    Log.e("Cars", "Retrieved " + scoreList.size() + " cars");
                    array1 = new String[scoreList.size()];
                    for (int i = 0; i < scoreList.size(); i++) {
                        Car car = new Car("Car");
                        car.setObject(scoreList.get(i));
                        CAR_ARRAY.put(car.getCarType() + " : " + car.getCarModel(), car.getObject().getObjectId());
                        array1[i] = car.getCarType() + " : " + car.getCarModel();
                        carsList.add(car);
                    }

                    setSpinnerAdapters();
                } else {
                    Log.e("Cars", "Error: " + e.getMessage());
                }
            }
        });
    }
    // get intent data
    private void getIntentData(){
        repairID = getIntent().getExtras().getString("object_id");
    }
    // initialize all the widgets
    private void init(){
        spinner = (Spinner) findViewById(R.id.spinnerCarWash);
        etInfo = (EditText) findViewById(R.id.washEtInfo);
        datePicker = (DatePicker) findViewById(R.id.datePicker);
        timePicker = (TimePicker) findViewById(R.id.timePicker);
        bSubmit = (Button) findViewById(R.id.washBSubmit);
        getIntentData();
        getRepairShop();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
        datePicker.setMinDate(calendar.getTimeInMillis());



        bSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CarWash wash = new CarWash();

                Calendar calendar1 = Calendar.getInstance();
                calendar1.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), timePicker.getCurrentHour(), timePicker.getCurrentMinute());
                // setting the values
                wash.setAppointmentDate(calendar1.getTime());
                wash.setApproval(false);
                wash.setCar(car.getObject());
                wash.setRepairShop(shop.getParseObject());
                wash.setInfo(etInfo.getText().toString());
                wash.setUser(ParseUser.getCurrentUser());

                // saving the changes
                wash.getObject().saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e==null){
                            Toast.makeText(CarWashActivity.this, "Car wash appointment request sent to the repair shop, they'll review it shortly", Toast.LENGTH_SHORT).show();
                            finish();
                        } else{
                            Toast.makeText(CarWashActivity.this, "Sorry an error occurred", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        getCarList();

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_wash);
        init();
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }
}
