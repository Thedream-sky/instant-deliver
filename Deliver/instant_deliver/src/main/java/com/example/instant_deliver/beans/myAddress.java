package com.example.instant_deliver.beans;

import cn.bmob.v3.BmobObject;

/**
 * Created by King on 2017/5/20.
 */

public class myAddress extends BmobObject{
    //用户
    private _User users;
    //地址
    private String address;
    //地址用户名
    private String name;
    
    private double lat;//纬度
    private double lon;//经度

    public _User getUsers() {
        return users;
    }

    public void setUsers(_User users) {
        this.users = users;
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}
