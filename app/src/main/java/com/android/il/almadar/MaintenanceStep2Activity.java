package com.android.il.almadar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.il.almadar.core.Car;
import com.android.il.almadar.core.RepairShop;
import com.android.il.almadar.core.UserService;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MaintenanceStep2Activity extends AppCompatActivity {

    private EditText etDescription;
    private Spinner spinCar, spinService;
    private Button bSave;
    private String id;
    private ArrayAdapter<String> carsAdapter;
    private HashMap<String, String> CAR_ARRAY;
    private String selectedCar, selectedTool;
    private List<Car> carsList;
    private Car car;
    private ArrayAdapter<String> toolAdapter;
    private String[] toolsArray;
    private String serviceString;
    private RepairShop shop;
    private CheckBox checkbox;
    String[] array1 = {"Car1", "Car2"};
    ParseUser admin;
    String channelName;
    // setting the value of the tool spinner
    private void getTools(){
        switch (serviceString){
            case "engine":
                toolsArray = new String[] {"Engine Air Breaker", "Cooling System", "Battery", "Oil-Oil Filter", "Fuel Filter", "Spark Plug", "Belts", "Other"};
                break;
            case "ac":
                toolsArray = new String[] {"A/C Compressor", "Condenser", "Blower Motor", "Relays", "Cooling Fan", "Other"};
                break;
            case "wheels":
                toolsArray = new String[] {"Flat Tire", "Tire Pressure", "Tire Rotation", "Tire Balancing", "Other"};
                break;
            case "breakers":
                toolsArray = new String[] {"Front Break - Pad", "Rear Break - Pad", "Other"};
                break;
        }
    }


    private  void setSpinnerAdapters(){

        // setting the  adapter
        carsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, array1);
        carsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinCar.setAdapter(carsAdapter);
        // setting the on item selected listener
        spinCar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

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

        getTools();
        // setting the  adapter
        toolAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, toolsArray);
        toolAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinService.setAdapter(toolAdapter);
        // setting the on item selected listener
        spinService.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub

                selectedTool = parent.getSelectedItem().toString();

            }


            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
                //selectedGender = parent.getSelectedItem().toString();
            }
        });

    }
    private void getIntentData(){
        id = getIntent().getExtras().getString("object_id");
        serviceString = getIntent().getExtras().getString("tool");
        ParseQuery<ParseObject> query = ParseQuery.getQuery("RepairShop");
        query.getInBackground(id, new GetCallback<ParseObject>() {
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

        Log.d("Maintenance", id + " tool: " + serviceString +" "+ selectedTool);
    }

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
                    for(int i=0; i<scoreList.size(); i++){
                        Car car = new Car("Car");
                        car.setObject(scoreList.get(i));
                        CAR_ARRAY.put(  car.getCarType() +" : " + car.getCarModel(), car.getObject().getObjectId());
                        array1[i] = car.getCarType() +" : " + car.getCarModel();
                        carsList.add(car);
                    }

                    setSpinnerAdapters();
                } else {
                    Log.e("Cars", "Error: " + e.getMessage());
                }
            }
        });
    }
    private  void init(){
        bSave = (Button) findViewById(R.id.bMaintenanceSubmit);
        etDescription = (EditText) findViewById(R.id.etMaintenanceIssue);
        spinCar = (Spinner) findViewById(R.id.spinnerCars);
        spinService = (Spinner) findViewById(R.id.spinnerMaintenance);
        checkbox = (CheckBox) findViewById(R.id.checkBoxPull);
        getIntentData();

        // setting the on click listener
        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // setting the user service information
                final UserService service = new UserService();
                service.setRepairShop(shop.getParseObject());
                service.setServiceIssue(etDescription.getText().toString());
                service.setServiceName("maintenance");
                service.setCar(car.getObject());
                Date date = Calendar.getInstance().getTime();
                service.setServiceDate(date);
                service.setServiceTool(serviceString);
                service.setServiceSubTool(selectedTool);
                service.setServiceSubTowCar(checkbox.isChecked());
                service.setServiceUser(ParseUser.getCurrentUser());


                // saving the changes
                service.getObject().saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(MaintenanceStep2Activity.this, "Maintenance request sent to the repair shop, they'll review it shortly", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(MaintenanceStep2Activity.this, "Sorry an error occurred", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                });

            }
        });

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance_step2);
        // initialize all the variables
        init();
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
}
