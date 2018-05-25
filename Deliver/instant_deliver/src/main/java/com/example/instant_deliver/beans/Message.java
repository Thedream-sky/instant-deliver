package com.example.instant_deliver.beans;

import org.litepal.crud.DataSupport;

import java.util.Date;

/**
 * Created by King on 2018/2/7.
 */

public class Message extends DataSupport {
    private String uuid;
    //好友friendId
    private String friendId;
    //当前用户id
    private String ownid;
    //好友名称
    private String friendName;
    //好友头像
    private String imageUri;
   //最后一条消息
    private String msg;
    //最后一次聊天的时间
    private Date date;
    //是否显示的状态
    private String state;
    //未读消息数
    private Integer count;

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getOwnid() {
        return ownid;
    }

    public void setOwnid(String ownid) {
        this.ownid = ownid;
    }


    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
