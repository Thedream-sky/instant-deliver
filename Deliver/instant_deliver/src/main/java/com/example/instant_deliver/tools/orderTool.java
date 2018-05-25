package com.example.instant_deliver.tools;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.example.instant_deliver.beans.myAddress;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;

/**
 * Created by King on 2018/5/25.
 */

public class orderTool {
    public static void opengaode(String id, final Context context){
        BmobQuery<myAddress> query = new BmobQuery<>();
        query.getObject(id, new QueryListener<myAddress>() {
            @Override
            public void done(myAddress myAddress, BmobException e) {
                if(myAddress!=null){
                    boolean flag=GaodeMaptools.isInstallByread("com.autonavi.minimap",context);
                    Log.i("oo",""+flag);
                    if(flag){
                        GaodeMaptools.openGaoDeMap(context,myAddress.getLon(),myAddress.getLat(),myAddress.getAddress());
                    }else {
                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.VIEW");
                        intent.setData(Uri.parse("http://www.autonavi.com/"));
                        context.startActivity(intent);
                    }
                }
            }
        });
    }
}
