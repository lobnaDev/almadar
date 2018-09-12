package com.android.il.almadar.core;

import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Date;


public class UserService {

    ParseObject object;

    public ParseObject getObject() {
        return object;
    }

    public void setObject(ParseObject object) {
        this.object = object;
    }

    public UserService(){
        object = new ParseObject("UserService");
    }


    /*
    public boolean getServiceStatus() {
        return object.getBoolean("status");
    }

    public void setServiceStatus(boolean tool) {
        object.put("status", tool);
    }
    */

    public boolean getServiceTowCar() {
        return object.getBoolean("tow");
    }

    public void setServiceSubTowCar(boolean tool) {
        object.put("tow", tool);
    }
    public String getServiceSubTool() {
        return object.getString("subtool");
    }

    public void setServiceSubTool(String tool) {
        object.put("subtool", tool);
    }
    public String getServiceTool() {
        return object.getString("tool");
    }

    public void setServiceTool(String tool) {
        object.put("tool", tool);
    }

    public String getServiceTechComment() {
        return object.getString("tech_comment");
    }

    public void setServiceTechComment(String tool) {
        object.put("tech_comment", tool);
    }

    public Date getServiceFinishDate() {
        return (Date) object.get("finish_date");
    }

    public void setServiceFinishDate(Date date) {
        object.put("finish_date", date);
    }
    public String getServiceIssue() {
        return object.getString("issue");
    }

    public void setServiceIssue(String tool) {
        object.put("issue", tool);
    }

    public RepairShop getRepairShop() {

        RepairShop shop = new RepairShop();
        shop.setParseObject((ParseObject)object.get("repair_shop"));
        return shop;
    }

    public void setRepairShop(ParseObject shop) {

        object.put("repair_shop", shop);
    }

    public Car getCar() {

        Car shop = new Car("Car");
        shop.setObject((ParseObject)object.get("Car"));
        return shop;
    }

    public void setCar(ParseObject shop) {

        object.put("Car", shop);
    }


    public ParseUser getServiceUser() {
        return (ParseUser) object.get("User");
    }

    public void setServiceUser(ParseUser tool) {
        object.put("User", tool);
    }


    public ParseUser getServiceTech() {
        return (ParseUser) object.get("Tech");
    }

    public void setServiceTech(ParseUser tool) {
        object.put("Tech", tool);
    }


    public Date getServiceDate() {
        return (Date)object.get("date");
    }


    public void setServiceDate(Date date) {
        object.put("date", date);
    }

    public String getServiceName() {
        return object.getString("Service");
    }

    public void setServiceName(String carType) {
        object.put("Service", carType);
    }

}
