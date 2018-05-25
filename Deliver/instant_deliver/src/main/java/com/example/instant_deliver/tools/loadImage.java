package com.example.instant_deliver.tools;

import android.content.Context;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.example.instant_deliver.R;

/**
 * Created by King on 2017/5/30.
 */

public class loadImage  {
    //异步加载图片
   public static void loadImageGlide(Context context, String imageurl, ImageView image){

      /*  RequestQueue requestQueue= Volley.newRequestQueue(context);
        ImageLoader imageLoader=new ImageLoader(requestQueue,new BitmapCache());
        ImageLoader.ImageListener listener=imageLoader.getImageListener(image, R.drawable.more_personal,R.drawable.more_personal);
        //加载图片
        imageLoader.get(imageurl,listener);
      */
       image.setScaleType(ImageView.ScaleType.FIT_CENTER);

       Glide.with(context).load(imageurl)
               .placeholder(R.drawable.more_personal)
               .error(R.drawable.more_personal)
               .crossFade(1000)//淡入淡出,注意:如果设置了这个,则必须要去掉asBitmap
               .override(80,80)//设置最终显示的图片像素为80*80,注意:这个是像素,而不是控件的宽高
               .centerCrop()//中心裁剪,缩放填充至整个ImageView
               .transform(new GlideCircleTransform(context))
               .into(image);
    }
    //异步加载图片
    public static void loadImageGlideRound(Context context, String imageurl, ImageView image){
        Glide.with(context).load(imageurl)
                .placeholder(R.drawable.more_personal)
                .error(R.drawable.more_personal)
                .crossFade(1000)//淡入淡出,注意:如果设置了这个,则必须要去掉asBitmap
                .override(80,80)//设置最终显示的图片像素为80*80,注意:这个是像素,而不是控件的宽高
                .centerCrop()//中心裁剪,缩放填充至整个ImageView
                .transform(new GlideRoundTransform(context,3))
                .into(image);
    }

}
