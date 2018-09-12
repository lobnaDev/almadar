package com.android.il.almadar;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
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

public class SendProblemDialog extends Dialog {

    private EditText etDescription;
    private Button bSave, bCancel;
    private CheckBox checkBox;
    private Activity activity;

    private String id;
    private Spinner spinner;
    private ArrayAdapter<String> carsAdapter;
    private HashMap<String, String> CAR_ARRAY;
    private String selectedCar;
    private List<Car> carsList;
    private Car car;
    private RepairShop shop;
    ParseUser admin;

    String[] array1 = {"Car1", "Car2"};


    // constructors


    public SendProblemDialog(Activity activity, String repairShopID){
        super(activity);
        this.activity = activity;
        this.id = repairShopID;
        getRepairShop();
    }

    // get repair shop
    private void getRepairShop(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("RepairShop");
        query.getInBackground(id, new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    shop = new RepairShop();
                    // object will be your shop
                    shop.setParseObject(object);
                    ParseQuery<ParseUser> query = new ParseQuery<ParseUser>("_User");
                    query.whereEqualTo("repair_shop", shop.getParseObject());
                    query.whereEqualTo("role", "admin");
                    query.findInBackground(new FindCallback<ParseUser>() {
                        @Override
                        public void done(List<ParseUser> objects, ParseException e) {
                            if (objects.size() > 0)
                                admin = objects.get(0);
                            if (e != null)
                                e.printStackTrace();
                        }
                    });

                } else {
                    // something went wrong
                }
            }
        });
    }
    private  void setSpinnerAdapters() {

        // setting the  adapter
        carsAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, array1);
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
        // initialize all variable
    private void init(){

        // widgets initialization
        etDescription = (EditText) findViewById(R.id.etProblemDesc);
        checkBox = (CheckBox) findViewById(R.id.checkBoxPull);
        bSave = (Button) findViewById(R.id.bMaintenanceSubmit);
        bCancel = (Button) findViewById(R.id.bMaintenanceCancel);
        spinner = (Spinner) findViewById(R.id.spinner);
        getCarList();

        // cancel button
        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        // save button
        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // setting the user service information
               final  UserService service = new UserService();
                service.setRepairShop(shop.getParseObject());
                service.setServiceIssue(etDescription.getText().toString());
                service.setServiceName("problem");
                service.setCar(car.getObject());
                Date date = Calendar.getInstance().getTime();
                service.setServiceDate(date);
                service.setServiceSubTowCar(checkBox.isChecked());
                service.setServiceUser(ParseUser.getCurrentUser());

                // saving the changes
                service.getObject().saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e==null){
                            Toast.makeText(activity, "Problem request sent to the repair shop, they'll review it shortly", Toast.LENGTH_SHORT).show();
                            dismiss();

                           String channelName = "service_"+service.getObject().getObjectId()+"_admin";
                            Log.e("ChannelName", channelName);
                            String channelName2 = "service_"+service.getObject().getObjectId()+"_user";
                            JSONObject data = new JSONObject();
                            try {
                                data.put("data", "com.parse.Data");
                                data.put("from", ParseUser.getCurrentUser().getEmail());
                                data.put("title", "New Problem submitted");
                                data.put("message", service.getServiceIssue());
                                data.put("object_id", service.getObject().getObjectId());
                                data.put("type", "admin");
                            }catch (Exception e1){
                                e1.printStackTrace();
                                return;
                            }


                            ParsePush parsePush = new ParsePush();
                            parsePush.setData(data);

                            ParseQuery<ParseInstallation> parseQuery = ParseQuery.getQuery(ParseInstallation.class);
                            if(admin!=null) {
                                parseQuery.whereEqualTo("email", admin.getEmail());
                                parsePush.setQuery(parseQuery);
                                parsePush.sendInBackground();
                            }
                        } else{
                            Toast.makeText(activity, "Sorry an error occurred", Toast.LENGTH_SHORT).show();

                            e.printStackTrace();
                        }
                    }
                });
            }
        });

       // setSpinnerAdapters();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_LEFT_ICON);
        setTitle("Send a problem");

        setContentView(R.layout.activity_send_problem);
        init();

    }
}
