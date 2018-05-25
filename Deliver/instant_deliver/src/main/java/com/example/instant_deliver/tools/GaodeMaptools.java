package com.example.instant_deliver.tools;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

/**
 * Created by King on 2018/5/25.
 */

public class GaodeMaptools {

 public static void openGaoDeMap(Context context,double lon, double lat,String key) {

        try {
           /* StringBuilder loc = new StringBuilder();
            loc.append("cat=android.intent.category.DEFAULT\n");
            loc.append("dat=androidamap://navi?sourceApplication=instant_deliver");
            loc.append("&lat=");
            loc.append(lat);
            loc.append("&lon=");
            loc.append(lon);
            loc.append("&dev=0");
            loc.append("&style=2\n");
            loc.append("pkg=com.autonavi.minimap");
            Intent intent = new Intent(loc.toString());
            Log.i("hhk",loc.toString());*/
            Intent intent = new Intent("android.intent.action.VIEW",
                    android.net.Uri.parse("androidamap://navi?sourceApplication=instant_deliver&lat="+ lat+"&lng="+lon+ "&dev=0"));
            intent.setData(Uri.parse("androidamap://poi?sourceApplication=instant_deliver&keywords="+key));
            intent.setPackage("com.autonavi.minimap");
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //判断是否安装了高德
    public static boolean isInstallByread(String packageName,Context context) {
        PackageInfo paginfo;
        try {
            paginfo = context.getPackageManager().getPackageInfo("com.autonavi.minimap",0);
        } catch (PackageManager.NameNotFoundException e) {
            paginfo = null;
            e.printStackTrace();
        }
        if(paginfo == null)
        {
            return  false;
        }
        else
        {
            return  true;
        }
    }
}
