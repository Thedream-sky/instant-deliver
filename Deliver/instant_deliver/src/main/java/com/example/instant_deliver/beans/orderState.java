package com.example.instant_deliver.beans;

import cn.bmob.v3.BmobObject;

/**
 * Created by King on 2018/2/7.
 */

public class orderState extends BmobObject {
    //用户关系id(唯一)
    private String friendsId;
    //订单id
    private String orderId;
    //订单状态：1：未接单、2：接单中、3：订单完成、4：取消订单、/*5：订单失效（该状态不需要，只需不显示即可）
    private Integer state;

    public String getFriendsId() {
        return friendsId;
    }

    public void setFriendsId(String friendsId) {
        this.friendsId = friendsId;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
