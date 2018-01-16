package com.example.instant_deliver.identifyView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;

import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.instant_deliver.R;

/**
 * Created by King on 2017/5/6.
 */

public class Topbar extends RelativeLayout {

    private ImageView leftImageview;
    private Drawable leftBackground;
    
    private TextView tvTitle;
    private float titleTextSize;
    private int titleColor;
    private String title;


    private TextView rightText;
    private float rightTextSize;
    private int rightColor;
    private String right;

    private LayoutParams leftParam,rightParam,titleParam;

    private topbarClickListener listener;
    private boolean flag;
    //接口
    public  interface topbarClickListener{
        public void leftclick();
        public void rightclick();
    }
    public void setOnTopbarClickListener(topbarClickListener listener){
        this.listener=listener;
    }

    public Topbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.flag=true;

        TypedArray ta=context.obtainStyledAttributes(attrs, R.styleable.Topbar);


        leftBackground=ta.getDrawable(R.styleable.Topbar_leftTextBackground);

        titleColor=ta.getColor(R.styleable.Topbar_titleColor,0);
        titleTextSize=ta.getDimension(R.styleable.Topbar_titleTextsize,10);
        title=ta.getString(R.styleable.Topbar_title);
        
        rightColor=ta.getColor(R.styleable.Topbar_rightColor,0);
        rightTextSize=ta.getDimension(R.styleable.Topbar_rightTextsize,10);
        right=ta.getString(R.styleable.Topbar_righttext);

        

        //回收
        ta.recycle();
        leftImageview=new ImageView(context);
        rightText=new TextView(context);
        tvTitle=new TextView(context);

       leftImageview.setImageDrawable(leftBackground);

        //文字居中
        tvTitle.setGravity(Gravity.CENTER);
        tvTitle.setText(title);
        tvTitle.setTextColor(titleColor);
        tvTitle.setTextSize(titleTextSize);

        //文字居中
        rightText.setGravity(Gravity.CENTER);
        rightText.setText(right);
        rightText.setTextColor(rightColor);
        rightText.setTextSize(rightTextSize);


        leftParam=new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        leftParam.addRule(RelativeLayout.ALIGN_PARENT_LEFT,TRUE);
        addView(leftImageview,leftParam);

        rightParam=new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        rightParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,TRUE);
        addView(rightText,rightParam);

        titleParam=new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        titleParam.addRule(RelativeLayout.CENTER_IN_PARENT,TRUE);
        addView(tvTitle,titleParam);


      leftImageview.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            listener.leftclick();
            }
        });

        rightText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            listener.rightclick();
            }
        });
    }

    //控件是否可见
    public void leftVisable(boolean flag){
        this.flag=flag;
        if(this.flag){
            leftImageview.setVisibility(View.VISIBLE);
        }else {
            leftImageview.setVisibility(View.GONE);
        }
    }

    public void rightVisable(boolean flag){
        this.flag=flag;
        if(this.flag){
            rightText.setVisibility(View.VISIBLE);
        }else {
            rightText.setVisibility(View.GONE);
        }
    }
    
}
