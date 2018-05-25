package com.example.instant_deliver.fragments;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.example.instant_deliver.R;
import com.example.instant_deliver.beans._User;
import com.example.instant_deliver.adapters.MyPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;

public class homeFragment extends Fragment {
    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    private List<Fragment> fragments;
    private List<String> titles;
    private View view ;
    private MyPagerAdapter adapter;
    private TextView titletext;

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //此处要获取网络数据，需要较多的时间，为了避免阻塞主UI，使用handler事件异步获取
            if(msg.what==1){
                initTitle();
            }

        }
    };

    public homeFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(view==null){
            view=inflater.inflate(R.layout.fragment_home,null);
        }

        init();
        initViewPager();

        return view;
    }



    //初始化控件
    private void init(){
        tabs= (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
        pager= (ViewPager) view.findViewById(R.id.pager);
        titletext= (TextView) view.findViewById(R.id.title);

    }
    //初始化数据
    private void initViewPager(){
        fragments=new ArrayList<>();
        //注意顺序
        fragments.add(new eatFragment());
        fragments.add(new deliverFragment());
        fragments.add(new otherFragment());
        titles=new ArrayList<>();
        titles.add("饮食专区");
        titles.add("快递专区");
        titles.add("日常其他");
        //适配
        adapter=new MyPagerAdapter(getChildFragmentManager(),fragments,titles);
        pager.setAdapter(adapter);
        tabs.setViewPager(pager);
        tabs.setTextColor(R.color.textcolorHintb);
        //获取title
        handler.sendEmptyMessage(1);
        //获取用户头像
        handler.sendEmptyMessage(2);

    }

    //获取当前校区
    private void initTitle(){
        //默认为“首页”
        String title="首页";
        _User user=BmobUser.getCurrentUser(_User.class);
        title=user.getUniversity().toString();
        titletext.setText(title);
    }



}
