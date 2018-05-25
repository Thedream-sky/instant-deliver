package com.example.instant_deliver.beans;

import cn.bmob.v3.BmobObject;

/**
 * Created by King on 2018/5/22.
 */

public class identify extends BmobObject {
    private String userid;
    private String userName;
    private String identifyID;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getIdentifyID() {
        return identifyID;
    }

    public void setIdentifyID(String identifyID) {
        this.identifyID = identifyID;
    }
}
