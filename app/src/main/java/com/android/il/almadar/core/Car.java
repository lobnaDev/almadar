package com.android.il.almadar.core;

import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;


public class Car {

    ParseObject object;

    public Car(){
        object = new ParseObject("Car");
    }
    public ParseObject getObject() {
        return object;
    }

    public void setObject(ParseObject object) {
        this.object = object;
    }

    public Car(String name){
       object = new ParseObject(name);
    }
    public String getCarType() {
        return object.getString("car_name");
    }

    public void setCarType(String carType) {
        object.put("car_name", carType);
    }

    public String getCarYear() {
        return object.getString("year");
    }

    public void setCarYear(String carYear) {
        object.put("year", carYear);
    }

    public String getCarModel() {
        return object.getString("car_model");
    }

    public void setCarModel(String carModel) {
        object.put("car_model", carModel);
    }


    public ParseUser getUser() {
        return (ParseUser) object.get("user");
    }

    public void setUser(ParseUser user) {
        object.put("user", user);
    }

    public ParseGeoPoint getLocation() {
        return (ParseGeoPoint) object.get("car_location");
    }

    public void setLocation(ParseGeoPoint location) {
        object.put("car_location", location);
    }
}
