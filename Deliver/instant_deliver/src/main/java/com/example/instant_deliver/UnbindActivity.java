package com.example.instant_deliver;

import android.app.Activity;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instant_deliver.beans.Users;
import com.example.instant_deliver.identifyView.Topbar;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

public class UnbindActivity extends Activity implements View.OnClickListener {
    //解绑的号码
    private TextView unbindphonenum;
    //解绑验证码
    private EditText uncode;
    //获取解绑验证码的按钮
    private Button getunbindcode;
    //确认解绑按钮
    private Button ensure;
    //导航栏
    private Topbar topbar;
    //倒计时类对象
    private CountDownTimer timer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unbind);
        //初始化控件
        init();
    }

    //解绑函数
    private void unbindphone() {
        String phonenum = unbindphonenum.getText().toString();
        if (uncode.getText().toString().trim().length() != 6) {
            Toast.makeText(UnbindActivity.this, "验证码不正确" , Toast.LENGTH_SHORT).show();
        } else {
            //检验验证码是否正确有效
            BmobSMS.verifySmsCode(phonenum, uncode.getText().toString().trim(), new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    //验证码有效
                    if (e == null) {
                        Users user = new Users();
                        user.setMobilePhoneNumber("");
                        user.setMobilePhoneNumberVerified(false);
                        Users cur = BmobUser.getCurrentUser(Users.class);
                        user.update(cur.getObjectId(), new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    Toast.makeText(UnbindActivity.this, "解绑成功", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent();
                                    //设置返回结果
                                    setResult(2, intent);
                                    finish();
                                } else {
                                    Toast.makeText(UnbindActivity.this, "解绑失败" + e.toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                    //验证码无效
                    else {
                        Toast.makeText(UnbindActivity.this, "验证码错误" + e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    //请求解绑验证码
    private void requestUnbindcode() {
        //手机框的内容
        String phonenum = unbindphonenum.getText().toString();
        BmobSMS.requestSMSCode(phonenum, "unbindp", new QueryListener<Integer>() {
            @Override
            public void done(Integer integer, BmobException e) {
                if (e == null) {
                    //开始倒计时
                    timer.start();
                    Toast.makeText(UnbindActivity.this, "验证码已经发出，请注意查收", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(UnbindActivity.this, "验证码强求失败" + e.toString(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    //初始化控件
    private void init() {
        unbindphonenum = (TextView) findViewById(R.id.unbindphone_phonenum);
        uncode = (EditText) findViewById(R.id.unbindphone_code);
        getunbindcode = (Button) findViewById(R.id.unbindphone_getcode);
        ensure = (Button) findViewById(R.id.unbindphone_ensure);
        topbar = (Topbar) findViewById(R.id.unbindphone_topbar);

        //设置监听
        getunbindcode.setOnClickListener(this);
        ensure.setOnClickListener(this);

        //倒计时设置
        timer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                getunbindcode.setEnabled(false);
                getunbindcode.setText(millisUntilFinished / 1000 + "秒后重发");
            }

            @Override
            public void onFinish() {
                getunbindcode.setEnabled(true);
                getunbindcode.setText("获取验证码");
            }
        };

        //获取当前用户的绑定的手机号
        Users user = BmobUser.getCurrentUser(Users.class);
        //设置textview 的值
        unbindphonenum.setText(user.getMobilePhoneNumber());

        topbar.setOnTopbarClickListener(new Topbar.topbarClickListener() {
            @Override
            public void leftclick() {
                //关闭当前页面
                finish();
            }

            @Override
            public void rightclick() {

            }
        });


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.unbindphone_getcode:
                requestUnbindcode();
                ;
                break;
            case R.id.unbindphone_ensure:
                unbindphone();
                ;
                break;
        }
    }
}
