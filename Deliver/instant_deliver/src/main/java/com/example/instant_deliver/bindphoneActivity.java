package com.example.instant_deliver;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.instant_deliver.beans.Users;
import com.example.instant_deliver.identifyView.Topbar;
import com.example.instant_deliver.tools.StringRegexUtils;
import com.example.instant_deliver.tools.topStatusTool;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

public class bindphoneActivity extends Activity implements View.OnClickListener {
    //导航栏
    private Topbar topbar;
    //手机号
    private EditText phonenum;
    //验证码
    private EditText code;
    //获取验证码、确定
    private Button getcode, ensure;
    //倒计时类对象
    private CountDownTimer timer;

    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bindphone);
        //解决沉浸式状态栏问题
        topStatusTool.applyKitKatTranslucency(this,R.color.deepskyblue);
        //初始化控件
        init();
    }

    //初始化
    private void init() {
        //已登陆用户的token
        SharedPreferences sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
        token = sharedPreferences.getString("token", "");

        topbar = (Topbar) findViewById(R.id.bindphone_topbar);
        phonenum = (EditText) findViewById(R.id.bindphone_phonenum);
        code = (EditText) findViewById(R.id.bindphone_code);
        getcode = (Button) findViewById(R.id.bindphone_getcode);
        ensure = (Button) findViewById(R.id.bindphone_ensure);


        getcode.setOnClickListener(this);
        ensure.setOnClickListener(this);


        //导航栏监听事件
        topbar.setOnTopbarClickListener(new Topbar.topbarClickListener() {
            @Override
            public void leftclick() {
                finish();
            }

            @Override
            public void rightclick() {

            }
        });

        //重写倒计时类里的方法
        timer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                getcode.setEnabled(false);
                getcode.setText(millisUntilFinished / 1000 + "秒后重发");
            }

            @Override
            public void onFinish() {
                getcode.setEnabled(true);
                getcode.setText("获取验证码");
            }
        };
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bindphone_getcode:
                requestCode();
                break;
            case R.id.bindphone_ensure:
                bindphone();
                break;
        }
    }


    //确认验证码，绑定手机号
    private void bindphone() {
        if (code.getText().toString().trim().length() != 6) {
            Toast.makeText(bindphoneActivity.this, "验证码不正确" , Toast.LENGTH_SHORT).show();
        } else {
            BmobSMS.verifySmsCode(phonenum.getText().toString().trim(), code.getText().toString().trim(), new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    //验证码正确
                    if (e == null) {
                        Users user = new Users();
                        user.setMobilePhoneNumber(phonenum.getText().toString().trim());
                        user.setSessionToken(token);
                        user.setMobilePhoneNumberVerified(true);
                        Users cur = BmobUser.getCurrentUser(Users.class);
                        user.update(cur.getObjectId(), new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    Toast.makeText(bindphoneActivity.this, "绑定成功", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent();
                                    //设置返回结果
                                    setResult(2, intent);
                                    finish();
                                } else {
                                    Toast.makeText(bindphoneActivity.this, "绑定失败" + e.toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        //验证码错误
                    } else {
                        Toast.makeText(bindphoneActivity.this, "验证码错误" + e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    //请求验证码
    private void requestCode() {
        //手机框的内容
        String phone = phonenum.getText().toString().trim();
        if (phone != null && StringRegexUtils.Validate(phone, StringRegexUtils.phone_regexp)) {
            timer.start();
            //短信验证码请求
            BmobSMS.requestSMSCode(phone, "phonebind", new QueryListener<Integer>() {
                @Override
                public void done(Integer integer, BmobException e) {
                    if (e == null) {
                        Toast.makeText(bindphoneActivity.this, "验证码已经发出，请注意查收", Toast.LENGTH_LONG).show();
                        //开始倒计时
                        timer.start();
                    } else {

                    }
                }
            });
        } else {
            if (phone == null || phone.equals("")) {
                Toast.makeText(bindphoneActivity.this, "手机号能为空", Toast.LENGTH_LONG).show();
            } else if (!StringRegexUtils.Validate(phone, StringRegexUtils.phone_regexp)) {
                Toast.makeText(bindphoneActivity.this, "手机号格式不对", Toast.LENGTH_LONG).show();
            }
        }
    }
}
