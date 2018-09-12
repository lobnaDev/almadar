package com.android.il.almadar.admin;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.android.il.almadar.R;
import com.android.il.almadar.core.RepairShop;
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
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;




public class EditRepairShopFragment extends AppCompatActivity implements LocationListener, OnMapReadyCallback, CompoundButton.OnCheckedChangeListener {


    Button bSubmit;
    EditText etShopName, etMobilePhone;
    CheckBox checkBoxWheels, checkBoxAc, checkBoxEngine, checkBoxBreak, checkBoxWash;

    List<String> newServicesList;
    ParseUser user;
    RepairShop shop;

    private GoogleMap map;
    LocationManager locationManager;
    Location location;
    Marker marker;

    LatLng markerLocation;
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

    private void getRepairShop(){
        shop = new RepairShop();
        user = ParseUser.getCurrentUser();
        ParseQuery<ParseUser> query = new ParseQuery<ParseUser>("_User");
        query.whereEqualTo("objectId", user.getObjectId());
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                user = objects.get(0);
                shop.setParseObject((ParseObject) user.get("repair_shop"));
                etMobilePhone.setText(shop.getRepairShopMobile());
                etShopName.setText(shop.getRepairShopName());
                LatLng position = new LatLng(shop.getRepairShopLocation().getLatitude(), shop.getRepairShopLocation().getLongitude());
                map.addMarker(new MarkerOptions().position(position).title("Repair Shop Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.pinpoint)));
                List<String> list = shop.getRepairShopServices();
                for (String item : list) {
                    switch (item) {
                        case "ac":
                            checkBoxAc.setChecked(true);
                            break;
                        case "wheel":
                            checkBoxWheels.setChecked(true);
                            break;
                        case "breakers":
                            checkBoxBreak.setChecked(true);
                            break;
                        case "engine":
                            checkBoxEngine.setChecked(true);
                            break;
                        case "wash":
                            checkBoxWash.setChecked(true);
                            break;
                    }
                }


            }
        });
    }

    /**
     * Initialize all the variables
     */
    private void init(){
        newServicesList = new ArrayList<String>() ;
        bSubmit = (Button) findViewById(R.id.buttonSubmit);
        etShopName = (EditText) findViewById(R.id.editTextShop);
        etMobilePhone = (EditText) findViewById(R.id.editTextShopPhone);
        checkBoxAc = (CheckBox) findViewById(R.id.checkBoxAc);
        checkBoxBreak = (CheckBox) findViewById(R.id.checkBoxBreakers);
        checkBoxEngine = (CheckBox) findViewById(R.id.checkBoxEngine);
        checkBoxWash = (CheckBox) findViewById(R.id.checkBoxWash);
        checkBoxWheels = (CheckBox) findViewById(R.id.checkBoxWheels);

        bSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBoxEngine.isChecked()){
                    newServicesList.add("engine");
                }

                if(checkBoxAc.isChecked()){
                    newServicesList.add("ac");
                }

                if(checkBoxWheels.isChecked()){
                    newServicesList.add("wheel");
                }

                if(checkBoxWash.isChecked()){
                    newServicesList.add("wash");
                }

                if(checkBoxBreak.isChecked())
                    newServicesList.add("breaker");

                shop.setRepairShopLocation(new ParseGeoPoint(markerLocation.latitude, markerLocation.longitude));
                shop.setRepairShop(etShopName.getText().toString());
                shop.setRepairShopMobile(etMobilePhone.getText().toString());
                shop.setRepairShopServices(newServicesList);
                try {
                    shop.getParseObject().saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Snackbar.make(findViewById(android.R.id.content), "Repair shop updated successfully", Snackbar.LENGTH_LONG).show();
                                finish();
                            } else
                                e.printStackTrace();
                        }
                    });
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
        checkBoxAc.setOnCheckedChangeListener(this);
        checkBoxWash.setOnCheckedChangeListener(this);
        checkBoxEngine.setOnCheckedChangeListener(this);
        checkBoxBreak.setOnCheckedChangeListener(this);
        checkBoxWheels.setOnCheckedChangeListener(this);


        getRepairShop();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_repair_shop);
        init();
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
                        .title("Repair Shop Location")
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

        locationUpdates();
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
        if(location!=null) {
            LatLng sydney = new LatLng(location.getLatitude(), location.getLongitude());
        }

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.checkBoxAc:
                if(isChecked)
                    Toast.makeText(EditRepairShopFragment.this, "Checked the ac", Toast.LENGTH_LONG).show();
                break;
            case R.id.checkBoxBreakers:
                if(isChecked)
                    Toast.makeText(EditRepairShopFragment.this, "Checked the ac", Toast.LENGTH_LONG).show();
                break;
        }
    }
}
