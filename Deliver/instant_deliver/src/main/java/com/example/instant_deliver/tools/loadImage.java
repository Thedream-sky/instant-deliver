package com.example.instant_deliver.tools;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.example.instant_deliver.R;
import com.example.instant_deliver.identifyView.ImageViewPlus;

/**
 * Created by King on 2017/5/30.
 */

public class loadImage  {
    //异步加载图片
   public static void loadImageVolley(Context context, String imageurl, ImageViewPlus image){

        RequestQueue requestQueue= Volley.newRequestQueue(context);
        ImageLoader imageLoader=new ImageLoader(requestQueue,new BitmapCache());
        ImageLoader.ImageListener listener=imageLoader.getImageListener(image, R.drawable.more_personal,R.drawable.more_personal);
        //加载图片
        imageLoader.get(imageurl,listener);

    }


}
