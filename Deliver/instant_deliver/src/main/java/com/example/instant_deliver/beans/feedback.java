package com.example.instant_deliver.beans;

import cn.bmob.v3.BmobObject;

/**
 * Created by King on 2018/5/21.
 */

public class feedback extends BmobObject {
    private String userId;
    private String message;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
