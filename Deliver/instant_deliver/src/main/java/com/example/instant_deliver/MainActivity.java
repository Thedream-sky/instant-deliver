package com.example.instant_deliver;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.instant_deliver.fragments.contantFragment;
import com.example.instant_deliver.fragments.homeFragment;
import com.example.instant_deliver.fragments.messageFragment;
import com.example.instant_deliver.fragments.ownFragment;
import com.example.instant_deliver.tools.ActivityManagerTool;

public class MainActivity extends FragmentActivity implements View.OnClickListener {
    private ImageView homeImage, messageImage, contactImage, ownImage, toput;
    private TextView homeText, messageText, contactText, ownText;
    private homeFragment homefragment;
    private messageFragment messagefragment;
    private ownFragment ownfragment;
    private contantFragment contantfragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //添加activity
        ActivityManagerTool.pushActivity(this);
        //初始化
        init();

    }

    //控件初始化
    private void init() {
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


        //监听事件
        homeImage.setOnClickListener(this);
        messageImage.setOnClickListener(this);
        contactImage.setOnClickListener(this);
        ownImage.setOnClickListener(this);
        toput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, putActivity.class);
                startActivity(intent);
            }
        });

        //默认初始化状态
        setDefault();
    }

    //默认状态
    private void setDefault() {
        homeImage.setImageResource(R.drawable.ic_home_black_36dp);
        homeText.setTextColor(Color.parseColor("#2196F3"));
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        homefragment=new homeFragment();
        transaction.add(R.id.fragmentBody, homefragment);
        transaction.commit();

    }

    //初始化所有的图片显示状态
    private void initState() {
        homeImage.setImageResource(R.drawable.ic_home_brown_100_36dp);
        messageImage.setImageResource(R.drawable.ic_message_brown_100_36dp);
        contactImage.setImageResource(R.drawable.ic_group_brown_100_36dp);
        ownImage.setImageResource(R.drawable.ic_person_outline_brown_100_36dp);

        homeText.setTextColor(Color.parseColor("#a47c7979"));
        messageText.setTextColor(Color.parseColor("#a47c7979"));
        contactText.setTextColor(Color.parseColor("#a47c7979"));
        ownText.setTextColor(Color.parseColor("#a47c7979"));

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
            case R.id.homeImage:
                homeImage.setImageResource(R.drawable.ic_home_black_36dp);
                homeText.setTextColor(Color.parseColor("#2196F3"));
                if (homefragment == null) {
                    homefragment = new homeFragment();
                    transaction.add(R.id.fragmentBody, homefragment);
                }
                transaction.show(homefragment);
                break;
            case R.id.messageImage:
                messageImage.setImageResource(R.drawable.ic_message_press);
                messageText.setTextColor(Color.parseColor("#2196F3"));
                if (messagefragment == null) {
                    messagefragment = new messageFragment();
                    transaction.add(R.id.fragmentBody, messagefragment);
                }
                transaction.show(messagefragment);
                break;
            case R.id.contactImage:
                contactImage.setImageResource(R.drawable.ic_group_press);
                contactText.setTextColor(Color.parseColor("#2196F3"));
                if (contantfragment == null) {
                    contantfragment = new contantFragment();
                    transaction.add(R.id.fragmentBody, contantfragment);
                }
                transaction.show(contantfragment);
                break;
            case R.id.ownImage:
                ownImage.setImageResource(R.drawable.ic_person_press_36dp);
                ownText.setTextColor(Color.parseColor("#2196F3"));
                if (ownfragment == null) {
                    ownfragment = new ownFragment();
                    transaction.add(R.id.fragmentBody, ownfragment);
                }
                transaction.show(ownfragment);
                break;
        }
        //提交事件
        transaction.commit();
    }


}
