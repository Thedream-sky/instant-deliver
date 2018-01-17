package com.example.instant_deliver;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instant_deliver.beans.Users;
import com.example.instant_deliver.tools.ActivityManagerTool;
import com.example.instant_deliver.tools.StringRegexUtils;
import com.example.instant_deliver.tools.bmobinit;
import com.example.instant_deliver.tools.getConnState;

import java.io.File;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.UpdateListener;

public class LoginActivity extends CheckPermissionsActivity implements View.OnClickListener {

    //账号
    private EditText counter;
    //密码
    private EditText password;
    //清除图片
    private ImageView clear1, clear2;
    //提示栏
    private TextView warn;
    //密码是否可见图片控件
    private ImageView eye;
    //设置密码是否可见
    private boolean flag;
    //注册与忘记密码
    private TextView regist, psforget;
    private Button login;
    private ProgressDialog progressDialog;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                //登陆方法
                if (counter.getText().toString().trim().length() > 0 && password.getText().toString().trim().length() > 0) {
                    login();
                }
            }
            if (msg.what == 2) {
                //检验用户是否登陆过
                Users users = BmobUser.getCurrentUser(Users.class);
                if (users != null) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            if (msg.what == 3) {
                //获取用户头像
                getHead();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //添加activity
        ActivityManagerTool.pushActivity(this);
        //初始化
        init();
        //登陆检查
        checkUserLogined();
    }

    //检查用户是否登陆过
    private void checkUserLogined() {
        handler.sendEmptyMessage(2);
    }

    //改变eye图标并且改变密码是否可见
    private void change(boolean a) {
        if (!a) {
            eye.setImageResource(R.drawable.input_password_invisible);
            //密码可见
            password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
        } else {
            //密码不可见
            eye.setImageResource(R.drawable.input_password_visible);
            password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        }
    }

    //初始化函数
    private void init() {
        SharedPreferences sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
        String coun = sharedPreferences.getString("coun", "");

        //初始设置不可见
        flag = false;
        counter = (EditText) findViewById(R.id.counter);
        password = (EditText) findViewById(R.id.password);
        clear1 = (ImageView) findViewById(R.id.clearCounter);
        clear2 = (ImageView) findViewById(R.id.clearPassword);
        eye = (ImageView) findViewById(R.id.passwordIfvisable);
        warn = (TextView) findViewById(R.id.showerro_tx);
        regist = (TextView) findViewById(R.id.regist);
        psforget = (TextView) findViewById(R.id.psforget);
        login = (Button) findViewById(R.id.but_login);

        //如果已经登陆过就无需再输入账号了
        counter.setText(coun);
        login.setOnClickListener(this);

        //输入框输入时监听
        counter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //去除所有的空格
                String value = String.valueOf(s).trim().replaceAll("\\s*", "");
                if (value.length() > 0) {
                    //当输入长度大于0，显示图标
                    clear1.setVisibility(View.VISIBLE);
                    if (!(StringRegexUtils.Validate(value, StringRegexUtils.phone_regexp) || StringRegexUtils.Validate(value, StringRegexUtils.email_regexp))) {
                        warn.setText("你输入的账号格式不对");
                        warn.setVisibility(View.VISIBLE);
                    } else {
                        warn.setText(null);
                        warn.setVisibility(View.GONE);
                    }
                } else {
                    //当输入为空，图标不可见
                    clear1.setVisibility(View.GONE);
                    warn.setText(null);
                    warn.setVisibility(View.GONE);
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
                warn.setText(null);
                warn.setVisibility(View.GONE);
                if (String.valueOf(s).length() > 0) {
                    //当输入长度大于0，显示图标
                    clear2.setVisibility(View.VISIBLE);

                } else {
                    //当输入为空，图标不可见
                    clear2.setVisibility(View.GONE);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //设置监听事件
        clear1.setOnClickListener(this);
        clear2.setOnClickListener(this);
        eye.setOnClickListener(this);
        regist.setOnClickListener(this);
        psforget.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //账号清屏
            case R.id.clearCounter:
                counter.setText(null);
                break;
            //密码清屏
            case R.id.clearPassword:
                password.setText(null);
                break;
            //密码是否可见
            case R.id.passwordIfvisable:
                if (!flag) {
                    flag = true;
                } else {
                    flag = false;
                }
                change(flag);
                ;
                break;
            //注册
            case R.id.regist:
                Intent intent = new Intent(this, LocationActivity.class);
                startActivity(intent);
                ;
                break;
            //登陆
            case R.id.but_login:
                handler.sendEmptyMessageDelayed(1, 1000);
                ;
                break;
            //找回密码
            case R.id.psforget:
                Intent intent1 = new Intent(this, FindBackActivity.class);
                startActivity(intent1);
                ;
                break;

        }
    }

    //登陆方法
    private void login() {
        progressDialog = ProgressDialog.show(this, "提示", "登陆中。。。");

        final Users user = new Users();
        final String coun = counter.getText().toString().trim();
        String pass = password.getText().toString().trim();

        user.setPassword(pass);
        if (!getConnState.isConn(this)) {
            warn.setText("当前网络不可用，登陆失败");
            warn.setVisibility(View.VISIBLE);
            progressDialog.dismiss();
        } else {
            if (StringRegexUtils.Validate(coun, StringRegexUtils.phone_regexp) || StringRegexUtils.Validate(coun, StringRegexUtils.email_regexp)) {
                //邮箱登陆方式
                if (StringRegexUtils.Validate(coun, StringRegexUtils.email_regexp)) {
                    user.setEmail(coun);
                    user.loginByAccount(coun, pass, new LogInListener<Users>() {
                        @Override
                        public void done(Users users, BmobException e) {
                            if (users != null) {
                                warn.setVisibility(View.GONE);
                                progressDialog.dismiss();
                                boolean flag = users.getEmailVerified();
                                if (flag) {
                                    //保存数据
                                    saveLoginCurrentCount(coun);
                                    //下载头像
                                    handler.sendEmptyMessage(3);

                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    //关闭当前页
                                    finish();
                                } else {
                                    //创建构建器
                                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                    AlertDialog alertDialog = builder.setTitle("提示").setIcon(R.drawable.logo)
                                            .setMessage("此邮箱未通过验证，是否重新进行验证")
                                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    user.requestEmailVerify(user.getEmail(), new UpdateListener() {
                                                        @Override
                                                        public void done(BmobException e) {
                                                            if (e == null) {
                                                                Toast.makeText(LoginActivity.this, "验证邮件已经重新发送，请注意查收", Toast.LENGTH_LONG).show();
                                                            } else {
                                                                Toast.makeText(LoginActivity.this, "验证邮件请求重新发送失败", Toast.LENGTH_LONG).show();

                                                            }
                                                        }
                                                    });
                                                }
                                            })
                                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            })
                                            .show();
                                    alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                                    alertDialog.getButton(alertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLUE);
                                }
                            } else {
                                warn.setText("密码或者邮箱不正确，登陆失败");
                                warn.setVisibility(View.VISIBLE);
                                progressDialog.dismiss();
                            }
                        }
                    });
                }


                //手机登陆方式
                if (StringRegexUtils.Validate(coun, StringRegexUtils.phone_regexp)) {
                    user.setMobilePhoneNumber(coun);
                    user.loginByAccount(coun, pass, new LogInListener<Users>() {
                        @Override
                        public void done(Users users, BmobException e) {
                            if (users != null) {
                                warn.setVisibility(View.GONE);
                                progressDialog.dismiss();
                                boolean flag = users.getMobilePhoneNumberVerified();
                                if (flag) {
                                    //保存数据
                                    saveLoginCurrentCount(coun);
                                    //下载头像
                                    handler.sendEmptyMessage(3);

                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);

                                    finish();
                                } else {
                                    warn.setText("此手机号还没通过验证，登陆失败");
                                    warn.setVisibility(View.VISIBLE);
                                }
                            } else {
                                warn.setText("密码或者手机号不正确，登陆失败");
                                warn.setVisibility(View.VISIBLE);
                                progressDialog.dismiss();
                            }
                        }
                    });


                }
            }
        }
    }

    //保存当前的登陆成功的账号
    public void saveLoginCurrentCount(String counter) {
        Users currentUser = BmobUser.getCurrentUser(Users.class);
        //保存当前登陆账号
        SharedPreferences sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("coun", counter);
        editor.putString("token",currentUser.getSessionToken());
        editor.commit();
    }

    //获取头像，每次登陆都会重新刷新下头像信息
    private void getHead() {
        Users users = BmobUser.getCurrentUser(Users.class);
        if (users.getHeadurl()!=null) {
            BmobFile bmobFile = new BmobFile(users.getObjectId() + ".jpg", "", users.getHeadurl());
            File appDir = new File(Environment.getExternalStorageDirectory(), "instant_deliver");
            if (!appDir.exists()) {
                appDir.mkdir();
            }
            File file = new File(appDir, users.getObjectId() + ".jpg");

            bmobFile.download(file, new DownloadFileListener() {
                @Override
                public void done(String s, BmobException e) {
                    if (e == null) {
                        Toast.makeText(LoginActivity.this, "头像获取成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "头像获取失败", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onProgress(Integer integer, long l) {

                }
            });
        }
    }
}
