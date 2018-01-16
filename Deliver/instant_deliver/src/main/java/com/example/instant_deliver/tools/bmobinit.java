package com.example.instant_deliver.tools;

import android.content.Context;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobConfig;

/**
 * Created by King on 2017/5/4.
 */

public class bmobinit {
    //上下文对象
    private Context context;
    public bmobinit(Context context){
        this.context=context;
        BmobConfig config =new BmobConfig.Builder(context)
        //设置appkey
        .setApplicationId("f3c01ff770ab0f21e5cdf0fa93f0e386")
        //请求超时时间（单位为秒）：默认15s
        .setConnectTimeout(30)
        //文件分片上传时每片的大小（单位字节），默认512*1024
        .setUploadBlockSize(1024*1024)
        //文件的过期时间(单位为秒)：默认1800s
        .setFileExpiration(2500)
        .build();
        Bmob.initialize(config);
    }
}
