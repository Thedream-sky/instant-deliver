package com.example.instant_deliver.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instant_deliver.R;
import com.example.instant_deliver.beans._User;
import com.example.instant_deliver.beans.identify;
import com.example.instant_deliver.fragments.contantFragment;
import com.example.instant_deliver.fragments.homeFragment;
import com.example.instant_deliver.fragments.messageFragment;
import com.example.instant_deliver.fragments.ownFragment;
import com.example.instant_deliver.services.myApplication;
import com.example.instant_deliver.tools.ActivityManagerTool;
import com.example.instant_deliver.tools.topStatusTool;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;


public class MainActivity extends FragmentActivity implements View.OnClickListener {
    private _User user;
    private boolean identity;
    private ImageView homeImage, messageImage, contactImage, ownImage, toput;
    private TextView homeText, messageText, contactText, ownText;
    private LinearLayout homeLayout, toputLayout, contactLayout,ownLayout;
    private RelativeLayout msgLayout;
    //消息提醒
    private TextView message_clue;
    private homeFragment homefragment;
    private messageFragment messagefragment;
    private ownFragment ownfragment;
    private contantFragment contantfragment;
    private String HOME="HOME",MESSAGE="MESSAGE",CONTACT="CONTACT",OWN="OWN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //保存当前状态
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //解决沉浸式状态栏问题
        topStatusTool.applyKitKatTranslucency(this,R.color.deepskyblue);
        //添加activity
        ActivityManagerTool.pushActivity(this);
        //初始化
        init(savedInstanceState);
        //获取用户是否实名验证
        identifyisShow();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data!=null&&data.getStringExtra("identify").equals("identifySuccess")){
            identifyisShow();
        }
    }

    private void identifyisShow() {
        BmobQuery<identify> query =new BmobQuery<>();
        query.addWhereEqualTo("userid",user.getObjectId());
        query.findObjects(new FindListener<identify>() {
            @Override
            public void done(List<identify> list, BmobException e) {
                if(list!=null&&list.size()>0){
                    identity = true;
                }else {
                    identity = false;
                }
            }
        });
    }
    //控件初始化
    private void init(Bundle savedInstanceState) {
        user = new myApplication().getCurrentUser();
        //控件初始化
        homeImage = (ImageView) findViewById(R.id.homeImage);
        messageImage = (ImageView) findViewById(R.id.messageImage);
        contactImage = (ImageView) findViewById(R.id.contactImage);
        ownImage = (ImageView) findViewById(R.id.ownImage);
        toput = (ImageView) findViewById(R.id.toput);

        homeText = (TextView) findViewById(R.id.homeText);
        messageText = (TextView) findViewById(R.id.messageText);
        contactText = (TextView) findViewById(R.id.contactText);
        ownText = (TextView) findViewById(R.id.ownText);

        homeLayout = (LinearLayout) findViewById(R.id.homeLayout);
        contactLayout = (LinearLayout) findViewById(R.id.contactLayout);
        ownLayout = (LinearLayout) findViewById(R.id.ownLayout);
        msgLayout = (RelativeLayout) findViewById(R.id.msgLayout);
        toputLayout = (LinearLayout) findViewById(R.id.toputLayout);

        //监听事件
        homeLayout.setOnClickListener(this);
        msgLayout.setOnClickListener(this);
        contactLayout.setOnClickListener(this);
        ownLayout.setOnClickListener(this);
        toputLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(identity){
                   if(user.getMobilePhoneNumber()==null||!user.getMobilePhoneNumberVerified()){
                       Toast.makeText(getApplicationContext(),"请绑定手机号",Toast.LENGTH_SHORT).show();
                       Intent intent = new Intent(MainActivity.this, BindActivity.class);
                       startActivity(intent);
                   }else {
                       Intent intent = new Intent(MainActivity.this, putActivity.class);
                       startActivity(intent);
                   }
                }else {
                    Toast.makeText(getApplicationContext(),"请实名验证",Toast.LENGTH_SHORT).show();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    //隐藏所有fragment
                    if (homefragment != null) {
                        transaction.hide(homefragment);
                    }
                    if (messagefragment != null) {
                        transaction.hide(messagefragment);
                    }
                    if (ownfragment != null) {
                        transaction.hide(ownfragment);
                    }
                    if (contantfragment != null) {
                        transaction.hide(contantfragment);
                    }
                    initState();
                    ownImage.setImageResource(R.drawable.ic_person_press_36dp);
                    ownText.setTextColor(Color.parseColor("#000000"));
                    transaction.show(ownfragment);
                    transaction.commit();
                }
            }
        });

        //默认初始化状态
        setDefault(savedInstanceState);
    }

    //默认状态
    private void setDefault(Bundle savedInstanceState) {
        homeImage.setImageResource(R.drawable.ic_home_black_36dp);
        homeText.setTextColor(Color.parseColor("#000000"));
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if(savedInstanceState == null){
            homefragment=new homeFragment();
            messagefragment = new messageFragment();
            contantfragment = new contantFragment();
            ownfragment = new ownFragment();
        }else {
            homefragment= (homeFragment) fragmentManager.findFragmentByTag(HOME);
            messagefragment = (messageFragment) fragmentManager.findFragmentByTag(MESSAGE);
            contantfragment = (contantFragment) fragmentManager.findFragmentByTag(CONTACT);
            ownfragment = (ownFragment) fragmentManager.findFragmentByTag(OWN);
        }

        transaction.add(R.id.fragmentBody, homefragment,HOME );
        transaction.add(R.id.fragmentBody, messagefragment,MESSAGE);
        transaction.add(R.id.fragmentBody, contantfragment,CONTACT);
        transaction.add(R.id.fragmentBody, ownfragment,OWN);

        transaction.commit();

    }

    //初始化所有的图片显示状态
    private void initState() {
        homeImage.setImageResource(R.drawable.ic_home_brown_100_36dp);
        messageImage.setImageResource(R.drawable.ic_message_brown_100_36dp);
        contactImage.setImageResource(R.drawable.ic_group_brown_100_36dp);
        ownImage.setImageResource(R.drawable.ic_person_outline_brown_100_36dp);

        homeText.setTextColor(Color.parseColor("#FFFFFF"));
        messageText.setTextColor(Color.parseColor("#FFFFFF"));
        contactText.setTextColor(Color.parseColor("#FFFFFF"));
        ownText.setTextColor(Color.parseColor("#FFFFFF"));

    }

    @Override
    public void onClick(View v) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        //隐藏所有fragment
        if (homefragment != null) {
            transaction.hide(homefragment);
        }
        if (messagefragment != null) {
            transaction.hide(messagefragment);
        }
        if (ownfragment != null) {
            transaction.hide(ownfragment);
        }
        if (contantfragment != null) {
            transaction.hide(contantfragment);
        }

        //每次执行都初始化
        initState();
        switch (v.getId()) {
            case R.id.homeLayout:
                homeImage.setImageResource(R.drawable.ic_home_black_36dp);
                homeText.setTextColor(Color.parseColor("#000000"));
                transaction.show(homefragment);
                break;
            case R.id.msgLayout:
                messageImage.setImageResource(R.drawable.ic_message_press);
                messageText.setTextColor(Color.parseColor("#000000"));
                transaction.show(messagefragment);
                break;
            case R.id.contactLayout:
                contactImage.setImageResource(R.drawable.ic_group_press);
                contactText.setTextColor(Color.parseColor("#000000"));
                transaction.show(contantfragment);
                break;
            case R.id.ownLayout:
                ownImage.setImageResource(R.drawable.ic_person_press_36dp);
                ownText.setTextColor(Color.parseColor("#000000"));
                transaction.show(ownfragment);
                break;
        }
        //提交事件
        transaction.commit();
    }

}
