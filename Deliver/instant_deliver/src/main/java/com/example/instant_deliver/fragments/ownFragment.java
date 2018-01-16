package com.example.instant_deliver.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.instant_deliver.BindActivity;
import com.example.instant_deliver.R;
import com.example.instant_deliver.addressActivity;
import com.example.instant_deliver.beans.Users;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import cn.bmob.v3.BmobUser;

public class ownFragment extends Fragment implements View.OnClickListener {
    private LinearLayout toparea;
    private TextView username;
    private TextView phonenum;
    private TextView singnature;
    private TextView own_adress;
    private View view;
    private ImageView tobind;
    private ImageView head;
    private Users user;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                user = BmobUser.getCurrentUser(Users.class);
                initinfo();
            }
        }
    };
    private String  PHOTO_FILE_NAME=null;


    //读取本地头像图片
    public  void getImage(){
        //读取用户头像
        File appDir = new File(Environment.getExternalStorageDirectory(), "instant_deliver");
        //当文件夹存在
        if(appDir.exists()){
            String fileName = PHOTO_FILE_NAME;
            File file = new File(appDir, fileName);
            if(file.exists()){
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(file);
                    Bitmap bitmap= BitmapFactory.decodeStream(fis);
                    //读取本地图片并显示
                    head.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }
        }
    }


    //初始化数据
    private void initinfo() {
        PHOTO_FILE_NAME = user.getObjectId() + ".jpg";
        //设置头像
        getImage();
        //设置用户名
        username.setText(user.getUsername());
        if (user.getMobilePhoneNumber()==null||user.getMobilePhoneNumber().equals("")) {
            phonenum.setText("手机号未绑定");
        } else {
            String phone = user.getMobilePhoneNumber();
            //拼接为保密号码
            phone = phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4, phone.length());
            phonenum.setText(phone);
        }

        if (user.getSignature() != null) {
            singnature.setText("个性签名："+user.getSignature());
        }
    }

    public ownFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //这个在fragment的值回调中必不可少
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==3&&resultCode==4){
            //回调返回user对象，重新刷新
            user= (Users) data.getSerializableExtra("user");
            //刷新数据
            initinfo();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_own, container, false);
        }
        //初始化控件
        init();
        //获取用户信息
        getinfo();
        return view;
    }

    //初始化控件
    private void init() {
        //用户名
        username = (TextView) view.findViewById(R.id.own_username);
        //手机号
        phonenum = (TextView) view.findViewById(R.id.own_phone);
        //签名
        singnature = (TextView) view.findViewById(R.id.own_singnature);
        //向右图标
        tobind = (ImageView) view.findViewById(R.id.own_rightarrow_toBind);
        //头像
        head = (ImageView) view.findViewById(R.id.own_head);
        //地址
        own_adress= (TextView) view.findViewById(R.id.own_address);

        //设置点击监听
        username.setOnClickListener(this);
        phonenum.setOnClickListener(this);
        singnature.setOnClickListener(this);
        tobind.setOnClickListener(this);
        head.setOnClickListener(this);
        own_adress.setOnClickListener(this);
    }

    //获取用户信息
    private void getinfo() {
        handler.sendEmptyMessage(1);
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getActivity(), BindActivity.class);
        switch (v.getId()) {
            case R.id.own_username:
               startActivityForResult(intent,3);
                break;
            case R.id.own_phone:
                startActivityForResult(intent,3);
                break;
            case R.id.own_rightarrow_toBind:
                startActivityForResult(intent,3);
                break;
            case R.id.own_singnature:
                startActivityForResult(intent,3);
                break;
            case R.id.own_head:
                startActivityForResult(intent,3);
                break;
            case R.id.own_address:
                Intent intent1=new Intent(getActivity(),addressActivity.class);
                intent1.putExtra("tag","fromownfragment");
                startActivity(intent1);
                ;break;
        }
    }
}
