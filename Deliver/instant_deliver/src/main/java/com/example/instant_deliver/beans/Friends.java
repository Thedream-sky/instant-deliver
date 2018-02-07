package com.example.instant_deliver.beans;

import cn.bmob.v3.BmobObject;

/**
 * Created by King on 2018/1/31.
 */

public class Friends extends BmobObject {
    private String idOne;
    private String idTwo;

    public String getIdOne() {
        return idOne;
    }

    public void setIdOne(String idOne) {
        this.idOne = idOne;
    }

    public String getIdTwo() {
        return idTwo;
    }

    public void setIdTwo(String idTwo) {
        this.idTwo = idTwo;
    }
}
