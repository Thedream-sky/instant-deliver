package com.example.instant_deliver.services;
import android.util.Log;

import com.example.instant_deliver.beans._User;
import com.example.instant_deliver.tools.bmobinit;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.EaseUI;

import org.litepal.LitePal;
import org.litepal.LitePalApplication;

import cn.bmob.v3.BmobUser;


/**
 * Created by King on 2018/1/16.
 */

public class myApplication extends LitePalApplication{

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化BmobSDK
        new bmobinit(this);
        //初始化环信设置
        initHuanxin();
        //创建数据库
        createDatabase();
    }

    private void initHuanxin() {
        Log.i("nini","初始化");
        EMOptions options = new EMOptions();
        // 默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(true);
        //环信服务器，默认使用环信服务器上传下载，如果设为 false，需要开发者自己处理附件消息的上传和下载
        //初始化
        EMClient.getInstance().init(this,options);
        //初始化easeui
         EaseUI.getInstance().init(this,options);
        //在做打包混淆时，关闭debug模式，避免消耗不必要的资源
        EMClient.getInstance().setDebugMode(true);

    }
    //创建数据库
    private void createDatabase(){
        //LitePal.initialize(this);
        //创建数据库
        LitePal.getDatabase();
    }

    //获取当前对象
    public _User getCurrentUser(){
        _User user = BmobUser.getCurrentUser(_User.class);
        if(user!=null){
            return user;
        }
        return null;
    }

}
