package com.android.il.almadar.core;

import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import java.util.List;


public class RepairShop {

    private ParseObject parseObject;

    public RepairShop(){
        parseObject = new ParseObject("RepairShop");
    }

    public ParseGeoPoint getRepairShopLocation(){
        return parseObject.getParseGeoPoint("location");
    }

    public void setRepairShopLocation(ParseGeoPoint location){
        parseObject.put("location", location);
    }

    public String getRepairShopName(){
        return parseObject.getString("shop_name");
    }

    public void setRepairShop(String name){
        parseObject.put("shop_name", name);
    }

    public List<String> getRepairShopServices(){
        return parseObject.getList("services");
    }

    public void setRepairShopServices(List<String> name){
        parseObject.put("services", name);
    }


    public String getRepairShopMobile(){
        return parseObject.getString("shop_mobile");
    }

    public void setRepairShopMobile(String name){
        parseObject.put("shop_mobile", name);
    }


    public ParseObject getParseObject() {
        return parseObject;
    }


    public void setParseObject(ParseObject parseObject) {
        this.parseObject = parseObject;
    }
}
