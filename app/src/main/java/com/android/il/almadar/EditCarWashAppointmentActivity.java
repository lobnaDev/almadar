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
import android.widget.TextView;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class EditCarWashAppointmentActivity extends AppCompatActivity {

    private Spinner spinner;
    private EditText etInfo;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private Button bSubmit;
    private String carWashID;
    private TextView tvRepairshop;
    private ArrayAdapter<String> carsAdapter;
    private HashMap<String, String> CAR_ARRAY;
    private String selectedCar;
    private List<Car> carsList;
    private Car car;
    private RepairShop shop;
    private String[] array1 = {"Car1", "Car2"};
    private CarWash wash = new CarWash();

    /**
     * Get the car wash information
     */
    private void getCarWash(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Wash");
        query.whereEqualTo("objectId", carWashID);
        query.include("car");
        query.include("shop");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    wash = new CarWash();
                    wash.setObject(objects.get(0));
                    Car car = new Car();
                    car.setObject((ParseObject) wash.getObject().get("car"));
                    wash.setCar(car.getObject());

                    car.setUser(ParseUser.getCurrentUser());
                    RepairShop shop = new RepairShop();
                    shop.setParseObject((ParseObject) objects.get(0).get("shop"));
                    wash.setRepairShop(shop.getParseObject());
                    // shop.setParseObject( wash.getRepairShop());
                    tvRepairshop.setText("Repair Shop: " + shop.getRepairShopName());
                    Log.e("Car wash info", wash.getInfo());
                    Log.e("Car", wash.getCar().getCarType());
                    Log.e("Repair", shop.getRepairShopName() + "");


                    Date date = wash.getAppointmentDate();
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date);

                    datePicker.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), new DatePicker.OnDateChangedListener() {
                        @Override
                        public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        }
                    });
                    timePicker.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY));
                    timePicker.setCurrentMinute(cal.get(Calendar.MINUTE));
                    etInfo.setText(wash.getInfo());
                    int i = 0;
                    for (Car car1 : carsList) {
                        if (car1.getObject().getObjectId().equals(car.getObject().getObjectId())) {
                            spinner.setSelection(i);
                            Log.e("Here", "car is valid");
                        }
                        Log.e("Here", "car is not valid");

                        i++;
                    }

                } else {
                    // something went wrong
                }

            }
        });
    }

    private  void setSpinnerAdapters() {

        // setting the  adapter
        carsAdapter = new ArrayAdapter<String>(EditCarWashAppointmentActivity.this, android.R.layout.simple_spinner_item, array1);
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
        carWashID = getIntent().getExtras().getString("object_id");
    }
    // initialize all the widgets
    private void init(){
        spinner = (Spinner) findViewById(R.id.spinnerCarWash);
        etInfo = (EditText) findViewById(R.id.washEtInfo);
        datePicker = (DatePicker) findViewById(R.id.datePicker);
        timePicker = (TimePicker) findViewById(R.id.timePicker);
        bSubmit = (Button) findViewById(R.id.washBSubmit);
        tvRepairshop = (TextView) findViewById(R.id.textViewRepair);
        getIntentData();
       // getRepairShop();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
        datePicker.setMinDate(calendar.getTimeInMillis());
        bSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar calendar1 = Calendar.getInstance();
                calendar1.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), timePicker.getCurrentHour(), timePicker.getCurrentMinute());
                // setting the values
                wash.setAppointmentDate(calendar1.getTime());
                wash.setApproval(false);
                wash.setCar(car.getObject());
                wash.setInfo(etInfo.getText().toString());
                wash.setUser(ParseUser.getCurrentUser());

                // saving the changes
                wash.getObject().saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e==null){
                            Toast.makeText(EditCarWashAppointmentActivity.this, "Car wash appointment request was updated successfully.", Toast.LENGTH_SHORT).show();
                            finish();
                        } else{
                            Toast.makeText(EditCarWashAppointmentActivity.this, "Sorry an error occurred, try again later", Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.activity_edit_car_wash_appointment);
        init();
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        getCarWash();
    }
}
