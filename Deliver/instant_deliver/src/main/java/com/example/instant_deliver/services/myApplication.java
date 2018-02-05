package com.example.instant_deliver.services;
import android.app.Application;
import android.util.Log;
import com.example.instant_deliver.tools.bmobinit;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.EaseUI;


/**
 * Created by King on 2018/1/16.
 */

public class myApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化BmobSDK
        new bmobinit(this);
        //初始化环信设置
        initHuanxin();
    }

    private void initHuanxin() {
        Log.i("nini","初始化");
        EMOptions options = new EMOptions();
        // 默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(false);
        //环信服务器，默认使用环信服务器上传下载，如果设为 false，需要开发者自己处理附件消息的上传和下载
        //初始化
        EMClient.getInstance().init(this,options);
        //初始化easeui
         EaseUI.getInstance().init(this,options);
        //在做打包混淆时，关闭debug模式，避免消耗不必要的资源
        EMClient.getInstance().setDebugMode(true);
    }

}
