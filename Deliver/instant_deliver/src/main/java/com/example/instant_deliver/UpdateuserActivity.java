package com.example.instant_deliver;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.instant_deliver.beans.Users;
import com.example.instant_deliver.identifyView.Topbar;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class UpdateuserActivity extends Activity {
    private Topbar topbar;
    private EditText username;
    private Button updatebtu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updateuser);
        //更新
        updateUsername();

    }

    private void updateUsername() {
        String name = getIntent().getStringExtra("name");
        username = (EditText) findViewById(R.id.updateuser_name);
        topbar = (Topbar) findViewById(R.id.updateUser_topbar);
        updatebtu = (Button) findViewById(R.id.updateUser_btu);
        //设置初始值
        username.setText(name);
        topbar.setOnTopbarClickListener(new Topbar.topbarClickListener() {
            @Override
            public void leftclick() {
                finish();
            }

            @Override
            public void rightclick() {

            }
        });

        //按钮点击事件
        updatebtu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
                String token = sharedPreferences.getString("token", "");

                final String name = username.getText().toString().trim();
                if (name.length() < 2 || name.length() > 5) {
                    Toast.makeText(UpdateuserActivity.this, "用户名长度不得小于2或大于5", Toast.LENGTH_SHORT).show();
                } else {
                    Users myuser = new Users();
                    myuser.setUsername(name);
                    myuser.setSessionToken(token);

                    Users user = BmobUser.getCurrentUser(Users.class);
                    myuser.update(user.getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                Toast.makeText(UpdateuserActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent();
                                //设置返回结果
                                setResult(2, intent);
                                finish();
                            } else {
                                Toast.makeText(UpdateuserActivity.this, "更新失败" + e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
