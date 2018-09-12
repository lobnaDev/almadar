package com.android.il.almadar.core;

import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Date;


public class CarWash {

    private ParseObject object;

    public  CarWash(){
        object = new ParseObject("Wash");
    }
    public ParseObject getObject() {
        return object;
    }

    public void setObject(ParseObject object) {
        this.object = object;
    }


    public boolean getApproval() {
        return object.getBoolean("approve");
    }

    public void setApproval(boolean tool) {
        object.put("approve", tool);
    }


    public Date getAppointmentDate() {
        return (Date) object.get("reservation_date");
    }

    public void setAppointmentDate(Date date) {
        object.put("reservation_date", date);
    }
    public String getInfo() {
        return object.getString("info");
    }

    public void setInfo(String tool) {
        object.put("info", tool);
    }

    public RepairShop getRepairShop() {

        RepairShop shop = new RepairShop();
        shop.setParseObject((ParseObject)object.get("shop"));
        return shop;
    }

    public void setRepairShop(ParseObject shop) {

        object.put("shop", shop);
    }


    public Car getCar() {

        Car car = new Car("Car");
        car.setObject((ParseObject) object.get("car"));
        return car;
    }

    public void setCar(ParseObject shop) {

        object.put("car", shop);
    }

    public ParseUser getUser() {
        return (ParseUser) object.get("user");
    }

    public void setUser(ParseUser shop) {

        object.put("user", shop);
    }
}
