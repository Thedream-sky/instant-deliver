package com.example.instant_deliver.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import com.astuetz.PagerSlidingTabStrip;
import com.example.instant_deliver.R;
import com.example.instant_deliver.adapters.MyPagerAdapter;
import com.example.instant_deliver.fragments.allOrderFragment;
import com.example.instant_deliver.fragments.cancelOrderFragment;
import com.example.instant_deliver.fragments.finishedFragment;
import com.example.instant_deliver.fragments.receiveFragment;
import com.example.instant_deliver.fragments.unReceiveFragment;
import com.example.instant_deliver.identifyView.Topbar;
import com.example.instant_deliver.tools.topStatusTool;

import java.util.ArrayList;
import java.util.List;

public class myOwnOrderActivity extends AppCompatActivity {
    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    private List<Fragment> fragments;
    private List<String> titles;
    private MyPagerAdapter adapter;
    private Topbar topbar;
    private Topbar topbar2;
    private String flag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_own_order);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        init();
        initViewPager();

    }
    //初始化
    private void init(){
        flag =getIntent().getStringExtra("flag");
        topbar = (Topbar)findViewById(R.id.own_order_topbar);
        topbar2 = (Topbar)findViewById(R.id.receive_order_topbar);
        tabs= (PagerSlidingTabStrip)findViewById(R.id.own_tabs);
        pager= (ViewPager)findViewById(R.id.ownPager);

        topbar.setOnTopbarClickListener(new Topbar.topbarClickListener() {
            @Override
            public void leftclick() {
                finish();
            }

            @Override
            public void rightclick() {
            }
        });

        topbar2.setOnTopbarClickListener(new Topbar.topbarClickListener() {
            @Override
            public void leftclick() {
                finish();
            }

            @Override
            public void rightclick() {

            }
        });

    }

    //初始化数据
    private void initViewPager(){
        fragments=new ArrayList<>();
        if(flag.equals("1")){
            topbar.setVisibility(View.VISIBLE);
            topbar2.setVisibility(View.GONE);
            //注意顺序
            fragments.add(new allOrderFragment(flag));
            fragments.add(new unReceiveFragment(flag));
            fragments.add(new receiveFragment(flag));
            fragments.add(new finishedFragment(flag));
            fragments.add(new cancelOrderFragment(flag));
            titles=new ArrayList<>();
            titles.add("全部");
            titles.add("未接");
            titles.add("已接");
            titles.add("完成");
            titles.add("取消");
        }else {
            topbar.setVisibility(View.GONE);
            topbar2.setVisibility(View.VISIBLE);
            //注意顺序
            fragments.add(new allOrderFragment(flag));
            fragments.add(new receiveFragment(flag));
            fragments.add(new finishedFragment(flag));
            fragments.add(new cancelOrderFragment(flag));
            titles=new ArrayList<>();
            titles.add("全部");
            titles.add("未完成");
            titles.add("已完成");
            titles.add("取消");
        }
        //适配
        adapter=new MyPagerAdapter(getSupportFragmentManager(),fragments,titles);
        pager.setAdapter(adapter);
        tabs.setViewPager(pager);
        tabs.setTextColor(R.color.textcolorHintb);
    }

}
