package com.example.instant_deliver.activities;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.example.instant_deliver.R;
import com.example.instant_deliver.beans._User;
import com.example.instant_deliver.identifyView.Topbar;
import com.example.instant_deliver.tools.StringRegexUtils;
import com.example.instant_deliver.tools.topStatusTool;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class FindBackActivity extends Activity {
    private Topbar topbar;
    private EditText findbackEmail;
    private TextView warn;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==1){
                final String email=findbackEmail.getText().toString().trim();
                _User.resetPasswordByEmail(email, new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if(e==null){
                            Toast.makeText(getApplication(),"重置密码请求成功，请到" + email + "邮箱进行密码重置操作",Toast.LENGTH_SHORT).show();
                            finish();
                        }else{
                          warn.setText("请求失败:" + e.getMessage());
                          warn.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_back);
        //解决沉浸式状态栏问题
        topStatusTool.applyKitKatTranslucency(this,R.color.deepskyblue);
        //初始化
        init();
        //监听
        listener();

    }

    //监听事件
    private void listener(){
        //topbar监听事件
        topbar.setOnTopbarClickListener(new Topbar.topbarClickListener() {
            @Override
            public void leftclick() {
                //关闭当前页
                finish();
            }

            @Override
            public void rightclick() {
                String value=findbackEmail.getText().toString().trim();
               if( StringRegexUtils.Validate(value, StringRegexUtils.email_regexp)){
                //发送空消息，调用handler机制
                handler.sendEmptyMessage(1);
               }
            }
        });

        //邮箱输入框监听事件
        findbackEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String value = String.valueOf(s).trim().replaceAll("\\s*", "");
                if (value.length() > 0) {
                    if (!StringRegexUtils.Validate(value, StringRegexUtils.email_regexp)) {
                        //可见
                        warn.setVisibility(View.VISIBLE);
                        warn.setText("邮箱格式不对");
                    } else {
                        warn.setVisibility(View.GONE);
                        warn.setText(null);
                    }

                } else {
                    //不可见
                    warn.setVisibility(View.GONE);
                    warn.setText(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    //初始化控件
    private void init() {
        topbar = (Topbar) findViewById(R.id.findback_topbar);
        findbackEmail = (EditText) findViewById(R.id.findback_email);
        warn = (TextView) findViewById(R.id.findback_warn);
    }


}
