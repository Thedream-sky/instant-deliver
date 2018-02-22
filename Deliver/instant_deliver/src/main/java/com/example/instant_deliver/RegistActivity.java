package com.example.instant_deliver;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.example.instant_deliver.beans._User;
import com.example.instant_deliver.identifyView.Topbar;
import com.example.instant_deliver.tools.ActivityManagerTool;
import com.example.instant_deliver.tools.StringRegexUtils;
import com.example.instant_deliver.tools.getConnState;
import com.example.instant_deliver.tools.topStatusTool;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;


public class RegistActivity extends Activity {
    private Topbar topbar;
    private TextView school,warn;
    private EditText email,password;
    //handler操作ui线程
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==1){
                //回到登陆页面
                ActivityManagerTool.finishActivity(RegistActivity.this);
                ActivityManagerTool.finishActivity(LocationActivity.class);
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        //解决沉浸式状态栏问题
        topStatusTool.applyKitKatTranslucency(this,R.color.deepskyblue);
        //添加activity
        ActivityManagerTool.pushActivity(this);
        //初始化
        init();
        //监听事件
        Listener();

    }

    private void init(){
        topbar= (Topbar) findViewById(R.id.topbar);
        school= (TextView) findViewById(R.id.regist_school);
        email= (EditText) findViewById(R.id.regist_email);
        password= (EditText) findViewById(R.id.regist_password);
        warn= (TextView) findViewById(R.id.regist_warn);
        school.setText(getIntent().getStringExtra("school").toString());
    }

    private void Listener(){
        //topbar监听
        topbar.setOnTopbarClickListener(new Topbar.topbarClickListener() {
            @Override
            public void leftclick() {

            }

            @Override
            public void rightclick() {
                //上传信息
                upload();
            }
        });

        //email监听
        email.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String value=String.valueOf(s).trim().replaceAll("\\s*","");
                if(value.length()>0){
                    if(!StringRegexUtils.Validate(value,StringRegexUtils.email_regexp)){
                        //可见
                        warn.setVisibility(View.VISIBLE);
                        warn.setText("邮箱格式不对");
                    }else{
                        warn.setVisibility(View.GONE);
                        warn.setText(null);
                    }

                }else {
                    //不可见
                    warn.setVisibility(View.GONE);
                    warn.setText(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String value=String.valueOf(s).trim().replaceAll("\\s*","");
                if(value.length()>0){
                    if(value.length()<6){
                        //可见
                        warn.setVisibility(View.VISIBLE);
                        warn.setText("密码长度不得小于6");
                    }else if(value.length()>24){
                        //可见
                        warn.setVisibility(View.VISIBLE);
                        warn.setText("密码长度不得大于24");
                    }else {
                        warn.setVisibility(View.GONE);
                        warn.setText(null);
                    }
                }else{
                    warn.setVisibility(View.GONE);
                    warn.setText(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    //提交数据
    private void upload(){
        final String em=String.valueOf(email.getText()).trim().replaceAll("\\s*","");
        final String pass=String.valueOf(password.getText()).trim().replaceAll("\\s*","");
        String sc=school.getText().toString().trim();

        //用户名与密码是必须的
        final _User user=new _User();
        user.setUsername(em);
        user.setEmail(em);
        user.setPassword(pass);
        user.setUniversity(sc);

        //文本格式验证
        if(em.length()>0&&StringRegexUtils.Validate(em,StringRegexUtils.email_regexp)&&pass.length()>5&&pass.length()<=24){
            //进行网络状态的验证
            if(!getConnState.isConn(this)){
                Toast.makeText(getApplication(),"当前网络不可用",Toast.LENGTH_LONG).show();
            }else{
                user.signUp(new SaveListener<_User>() {
                    @Override
                    public void done(final _User users, BmobException e) {
                        if(e==null){
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        EMClient.getInstance().createAccount(users.getObjectId(),pass);
                                    } catch (HyphenateException e1) {
                                        Log.i("fsa","注册失败");
                                        e1.printStackTrace();
                                    }
                                }
                            }).start();
                            //创建构建器
                            AlertDialog.Builder builder=new AlertDialog.Builder(RegistActivity.this);
                            AlertDialog alertDialog=builder.setTitle("提示").setMessage("注册成功，稍后会有邮箱验证短信，请注意查收！")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    handler.sendEmptyMessageDelayed(1,1000);
                                }
                            }).show();
                            alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                        }else {
                                warn.setText("邮箱已经被注册！");
                                warn.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }

        }
    }

}
