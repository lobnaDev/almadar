package com.android.il.almadar;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RepairShopsMapActivity extends AppCompatActivity implements LocationListener, OnMapReadyCallback {

    private GoogleMap map;
    LocationManager locationManager; //This class provides access to the system location services
    Location location;
    Marker marker;
    List<ParseObject> repairList;
    HashMap<String, String> markerMap = new HashMap<String, String>();//?
    String service;

    ArrayList<String> list = new ArrayList<String>();
    private String getIntentData(){
        return getIntent().getExtras().getString("tool");
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
    private void getService(){
        service = getIntent().getExtras().getString("service");
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repair_shops_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        getService();
        map = mapFragment.getMap();
        map.setMyLocationEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);


        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                String id = markerMap.get(marker.getId());
                if(service.equals("maintenance")) {
                    Intent intent = new Intent(RepairShopsMapActivity.this, MaintenanceStep2Activity.class);
                    // adding the required data to the intent
                    intent.putExtra("object_id", id);
                    intent.putExtra("tool", getIntentData());
                    startActivity(intent);
                } else if(service.equals("wash")){
                    Intent intent = new Intent(RepairShopsMapActivity.this, CarWashActivity.class);
                    // adding the required data to the intent
                    intent.putExtra("object_id", id);
                    startActivity(intent);
                } else {
                    SendProblemDialog dialog  = new SendProblemDialog(RepairShopsMapActivity.this, id);
                    dialog.show();
                    dialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.mipmap.ic_launcher);

                }
            }
        });

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

        ParseQuery<ParseObject> query = ParseQuery.getQuery("RepairShop");

        if(service.equals("maintenance")) {
            list.add(getIntentData());

            query.whereContainsAll("services", list);
        } else if(service.equals("wash")){
            list = new ArrayList<>();
            list.add("wash");
            query.whereEqualTo("services", "wash");
        }

        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> scoreList, ParseException e) {
                if (e == null) {
                    map.clear();
                    Log.e("Repairshops", "Retrieved" + scoreList.size() + " shops");
                    repairList = scoreList;
                    for(ParseObject object : repairList){
                        LatLng latLng = new LatLng(object.getParseGeoPoint("location").getLatitude(), object.getParseGeoPoint("location").getLongitude());
                        Marker marker = map.addMarker(new MarkerOptions().position(latLng).title(object.getString("shop_name")).icon(BitmapDescriptorFactory.fromResource(R.drawable.pinpoint)).snippet("Shop mobile number: "+object.getString("shop_mobile")));
                        //map.clear();
                        markerMap.put(marker.getId(), object.getObjectId());
                    }
                } else {
                    Log.e("Repairshops", "Error: " + e.getMessage());
                }
            }
        });
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
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

    // initialize the map
    private void initMap(){
        locationUpdates();
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


    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        locationUpdates();
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
        this.map = googleMap;
        locationUpdates();
        initMap();
        if(location!=null) {
            LatLng sydney = new LatLng(location.getLatitude(), location.getLongitude());
            map.addMarker(new MarkerOptions().position(sydney).title("Marker").icon(BitmapDescriptorFactory.fromResource(R.drawable.pinpoint)));


        }

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
}
