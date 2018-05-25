package com.example.instant_deliver.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.example.instant_deliver.R;
import com.example.instant_deliver.beans.Order;
import com.example.instant_deliver.beans._User;
import com.example.instant_deliver.beans.myAddress;
import com.example.instant_deliver.identifyView.Topbar;
import com.example.instant_deliver.tools.StringRegexUtils;
import com.example.instant_deliver.tools.topStatusTool;

import java.util.Date;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class putActivity extends Activity implements View.OnClickListener {

    //导航栏
    private Topbar topbar;
    //下拉框：需求类型，结束时间，小费
    private Spinner spinner, enddateSpinner, awardSpinner;

    private EditText phone;
    private EditText info;
    private TextView address;
    private Button submit;
    private _User users;
    private myAddress ads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_put);
        //解决沉浸式状态栏问题
        topStatusTool.applyKitKatTranslucency(this,R.color.deepskyblue);
        init();
        initSpinner();

        topbar.setOnTopbarClickListener(new Topbar.topbarClickListener() {
            @Override
            public void leftclick() {
                finish();
            }

            @Override
            public void rightclick() {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 20 && resultCode == 30) {
            //返回地址
            ads = (myAddress) data.getSerializableExtra("address");
            address.setText(ads.getAddress());
        }
    }

    //初始控件
    private void init() {
        //当前用户
        users = BmobUser.getCurrentUser(_User.class);
        //提交按钮
        submit = (Button) findViewById(R.id.putbtu);
        //导航栏
        topbar = (Topbar) findViewById(R.id.putTopbar);
        //服务类型
        spinner = (Spinner) findViewById(R.id.typeSpinner);
        //截止时间
        enddateSpinner = (Spinner) findViewById(R.id.enddateSpinner);
        //跑腿报酬
        awardSpinner = (Spinner) findViewById(R.id.awardSpinner);
        //地址
        address = (TextView) findViewById(R.id.putAddress);
        //手机号
        phone = (EditText) findViewById(R.id.put_phone);
        //订单需求内容
        info = (EditText) findViewById(R.id.put_orderinfo);

        address.setOnClickListener(this);
        if (users.getMobilePhoneNumber() != null) {
            phone.setText(users.getMobilePhoneNumber());
        }
        //设置监听
        submit.setOnClickListener(this);
    }

    //初始下拉框
    private void initSpinner() {
        //适配需求类型
        String[] items = {"饮食", "快递", "其他"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, items);
        spinner.setAdapter(arrayAdapter);
        //适配时间
        String[] date = {"一小时以内", "两小时以内", "三小时以内", "半天以内", "一天以内"};
        ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, date);
        enddateSpinner.setAdapter(arrayAdapter2);
        //适配金额
        String[] award = {"1元", "2元", "3元", "4元", "5元"};
        ArrayAdapter<String> arrayAdapter3 = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, award);
        awardSpinner.setAdapter(arrayAdapter3);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.putAddress:
                Intent intent=new Intent(putActivity.this, addressActivity.class);
                intent.putExtra("tag","fromputActivity");
                startActivityForResult(intent, 20);
                break;
            case R.id.putbtu:
                //提交订单
                put();
                break;
        }
    }


    //提交订单
    private void put() {
        String infor = info.getText().toString().trim();
        String adss = address.getText().toString().trim();
        String pnum = phone.getText().toString().trim();
        if (infor.equals("") || infor == null || adss.equals("") || adss == null || pnum.equals("") || pnum == null) {
            Toast.makeText(getApplication(), "不能有空", Toast.LENGTH_SHORT).show();
        } else if (!StringRegexUtils.Validate(pnum, StringRegexUtils.phone_regexp)) {
            Toast.makeText(getApplication(), "手机号格式不正确", Toast.LENGTH_SHORT).show();
        } else {
            initorder();
        }
    }

    //提交订单
    private void initorder() {
        Order order = new Order();
        //当前系统时间
        Date date = new Date();
        //当前用户为发起者
        order.setLauncher(users.getObjectId());
        order.setLaunchername(users.getUsername());
        order.setReciver(null);
        order.setOrderType((String) spinner.getSelectedItem());
        order.setOrderState(1);
        order.setLauncherhead(users.getHeadurl());
        order.setInfo(info.getText().toString());
        order.setAdress(address.getText().toString());
        order.setAward(changeaward((String) awardSpinner.getSelectedItem()));
        order.setPhonenum(phone.getText().toString());
        order.setSchool(users.getUniversity());
        order.setAdsObject(ads.getObjectId());
        //设置发布时间
        order.setPutDate(new BmobDate(date));
        //设置截止时间
        order.setEndDate(new BmobDate(changeDate(date.getTime()+StringTodate((String) enddateSpinner.getSelectedItem()))));
        order.save(new SaveListener<String>() {
            ProgressDialog dialog=new ProgressDialog(putActivity.this);
            @Override
            public void onStart() {
               dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
               dialog.setMessage("上传中");
               dialog.show();
            }

            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    Toast.makeText(getApplication(), "发布成功", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(getApplication(), "发布失败"+e.toString(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFinish() {
               //dialog消失
               dialog.dismiss();
            }
        });
    }

    //毫秒转换为date
    private Date changeDate(long t){
        Date date=new Date(t);
        return  date;
    };

    //将字符转换为毫秒
    private long StringTodate(String a){
       long time=3600000;
        if(a.equals("一小时以内")){
            time*=1 ;
        }else if(a.equals("两小时以内")){
            time*=2;
        }else if(a.equals("三小时以内")){
            time*=3;
        }else if(a.equals("半天以内")){
            time*=12;
        }else if(a.equals("一天以内")){
            time*=24;
        }
       return time;
    };
    //改变奖励
    private Integer changeaward(String a) {
        int result = 0;
        if (a.equals("1元")) {
            result = 1;
        } else if (a.equals("2元")) {
            result = 2;
        } else if (a.equals("3元")) {
            result = 3;
        } else if (a.equals("4元")) {
            result = 4;
        } else if (a.equals("5元")) {
            result = 5;
        }
        return result;
    }
}
