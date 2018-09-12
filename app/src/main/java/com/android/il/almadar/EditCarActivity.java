package com.android.il.almadar;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.il.almadar.core.Car;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

public class EditCarActivity extends AppCompatActivity  implements LocationListener, OnMapReadyCallback {


    private EditText etCartype, etCarModel, etCarYear;
    private GoogleMap map;
    LocationManager locationManager;
    Location location;
    Button bSave;
    Marker marker;

    ParseObject parseObject;
    LatLng markerLocation;

    Car car;

    String carID;

    private void getCarId(){
        carID = getIntent().getExtras().getString("car_id");
    }
    private  void setValues(){
        car = new Car("Car");
        getCarId();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Car");
        query.getInBackground(carID, new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    car.setObject(object);
                    parseObject = object;
                    etCarModel.setText(car.getCarModel());
                    etCartype.setText(car.getCarType());
                    etCarYear.setText(car.getCarYear());
                   // etNumberPlate.setText(car.getNumberPlate());
                    try {
                        LatLng latLng = new LatLng(car.getLocation().getLatitude(), car.getLocation().getLongitude());
                        map.clear();
                        marker = map.addMarker(new MarkerOptions().position(latLng).title("Car Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.pinpoint)));
                        // marker.setPosition(latLng);
                    } catch(Exception e1){
                        e1.printStackTrace();
                    }

                } else {
                    // something went wrong
                }
            }
        });

    }

    // initialize all the variables
    private void init(){
        etCarModel = (EditText) findViewById(R.id.addCarETCarModel);
        etCartype = (EditText) findViewById(R.id.addCarEtType);
        etCarYear = (EditText) findViewById(R.id.addCarEtYear);

        bSave = (Button) findViewById(R.id.bSave);


        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()) {
                    car.setObject(parseObject);
                    car.setCarType(etCartype.getText().toString());
                    car.setCarModel(etCarModel.getText().toString());
                    String year = etCarYear.getText().toString();
                    car.setCarYear(year);
                   // car.setNumberPlate(numberPlate);
                   // car.setUser(ParseUser.getCurrentUser());
                    if (marker != null) {
                        ParseGeoPoint geoPoint = new ParseGeoPoint(marker.getPosition().latitude, marker.getPosition().longitude);
                        car.setLocation(geoPoint);
                    }


                    car.getObject().saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Toast.makeText(EditCarActivity.this, "Car updated successfully.", Toast.LENGTH_LONG)
                                        .show();
                            } else {
                                Toast.makeText(EditCarActivity.this, "Car wasn't updated, the number plate is not unique.", Toast.LENGTH_LONG)
                                        .show();
                                e.printStackTrace();
                            }
                        }
                    });
                } else{
                    Toast.makeText(EditCarActivity.this , "Please don't leave any empty fields.", Toast.LENGTH_LONG).show();
                }
            }
        });
        locationUpdates();
        setValues();
    }


    private void locationUpdates() {
        // Getting LocationManager object from System UserService LOCATION_SERVICE
        locationManager = (LocationManager) getSystemService(
                Context.LOCATION_SERVICE);

        // Creating a criteria object to retrieve provider
        Criteria criteria = new Criteria();

        // Getting the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);

        // Getting Current Location
        location = locationManager.getLastKnownLocation(provider);

        locationManager.requestLocationUpdates(provider, 50000, 0, this);

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
        setContentView(R.layout.activity_edit_car);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        map = mapFragment.getMap();
        map.setMyLocationEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);

        // setting the on-click listener of the map
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latlng) {
                // TODO Auto-generated method stub
                map.clear();
                marker = map.addMarker(new MarkerOptions()
                        .title("Car Location")
                        .position(latlng)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.pinpoint)))
                ;

                markerLocation = latlng;
            }
        });

        // map.setOnInfoWindowClickListener(this);
        map.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {

            @Override
            public boolean onMyLocationButtonClick() {
                // TODO Auto-generated method stub
                // Getting latitude of the current location
                double latitude = location.getLatitude();

                // Getting longitude of the current location
                double longitude = location.getLongitude();

                // Creating a LatLng object for the current location
                LatLng latLng = new LatLng(latitude, longitude);

                // Showing the current location in Google Map
                map.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                // Zoom in the Google Map
                map.animateCamera(CameraUpdateFactory.zoomTo(15));
                return true;
            }
        });



        init();

        if (location != null) {
            // Getting latitude of the current location
            double latitude = location.getLatitude();

            // Getting longitude of the current location
            double longitude = location.getLongitude();

            // Creating a LatLng object for the current location
            LatLng latLng = new LatLng(latitude, longitude);

            // Showing the current location in Google Map
            map.moveCamera(CameraUpdateFactory.newLatLng(latLng));

            // Zoom in the Google Map
            map.animateCamera(CameraUpdateFactory.zoomTo(15));
        }

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        // Add a marker in Sydney, Australia, and move the camera.
        this.map = googleMap;
        initMap();


    }

    // checking the validation
    private boolean validate(){
        if(TextUtils.isEmpty(etCarYear.getText().toString()) || TextUtils.isEmpty(etCarModel.getText().toString()) || TextUtils.isEmpty(etCarYear.getText().toString())){
            return false;
        } else{
            return true;
        }
    }
    // initialize the map
    private void initMap(){
        if(map!=null) {
            map.setMyLocationEnabled(true);
            map.getUiSettings().setZoomControlsEnabled(true);
            // map.setOnInfoWindowClickListener(this);
            map.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {

                @Override
                public boolean onMyLocationButtonClick() {
                    // TODO Auto-generated method stub
                    // Getting latitude of the current location
                    double latitude = location.getLatitude();

                    // Getting longitude of the current location
                    double longitude = location.getLongitude();

                    // Creating a LatLng object for the current location
                    LatLng latLng = new LatLng(latitude, longitude);

                    // Showing the current location in Google Map
                    map.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                    // Zoom in the Google Map
                    map.animateCamera(CameraUpdateFactory.zoomTo(15));
                    return true;
                }
            });
        }
    }
}
