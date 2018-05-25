package com.example.instant_deliver.beans;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;

/**
 * Created by King on 2017/5/13.
 */

public class Order extends BmobObject {
    //发起人名称
    private String launchername;
    //发起人名称
    private String receviername;
    //发起人
    private String launcher;
    //发起人头像
    private String launcherhead;
    //发起人学校
    private String school;
    //接单人
    private String reciver;
    //订单类型
    private String orderType;
    //订单状态：1：未接单、2：接单中、3：订单完成、4：取消订单、/*5：订单失效（该状态不需要，只需不显示即可）*/
    private Integer orderState;
    //订单详情描述
    private String info;
    //发起时间
    private BmobDate putDate;
    //结束时间
    private BmobDate endDate;
    //具体地址
    private String adress;
    //佣金
    private Integer award;
    //手机号
    private String phonenum;
    //地址id
    private String adsObject;


    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public int getOrderState() {
        return orderState;
    }

    public void setOrderState(int orderState) {
        this.orderState = orderState;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public Integer getAward() {
        return award;
    }

    public void setAward(Integer award) {
        this.award = award;
    }

    public String getPhonenum() {
        return phonenum;
    }

    public void setPhonenum(String phonenum) {
        this.phonenum = phonenum;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getLauncherhead() {
        return launcherhead;
    }

    public void setLauncherhead(String launcherhead) {
        this.launcherhead = launcherhead;
    }

    public BmobDate getPutDate() {
        return putDate;
    }

    public void setPutDate(BmobDate putDate) {
        this.putDate = putDate;
    }

    public BmobDate getEndDate() {
        return endDate;
    }

    public void setEndDate(BmobDate endDate) {
        this.endDate = endDate;
    }

    public String getLaunchername() {
        return launchername;
    }

    public void setLaunchername(String launchername) {
        this.launchername = launchername;
    }

    public String getReceviername() {
        return receviername;
    }

    public void setReceviername(String receviername) {
        this.receviername = receviername;
    }

    public String getLauncher() {
        return launcher;
    }

    public void setLauncher(String launcher) {
        this.launcher = launcher;
    }

    public String getReciver() {
        return reciver;
    }

    public void setReciver(String reciver) {
        this.reciver = reciver;
    }

    public String getAdsObject() {
        return adsObject;
    }

    public void setAdsObject(String adsObject) {
        this.adsObject = adsObject;
    }
}
