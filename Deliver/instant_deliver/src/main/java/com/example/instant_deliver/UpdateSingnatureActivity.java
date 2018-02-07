package com.example.instant_deliver;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.instant_deliver.beans._User;
import com.example.instant_deliver.identifyView.Topbar;
import com.example.instant_deliver.tools.topStatusTool;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class UpdateSingnatureActivity extends Activity {
    private Topbar topbar;
    private EditText singnature;
    private Button updatebtu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_singnature);
        //解决沉浸式状态栏问题
        topStatusTool.applyKitKatTranslucency(this,R.color.deepskyblue);
        //更新
        updateSingnature();
    }
    private void updateSingnature(){
        String sing=getIntent().getStringExtra("singnature");
        singnature= (EditText) findViewById(R.id.update_singnature);
        topbar= (Topbar) findViewById(R.id.updateSingnature_topbar);
        updatebtu= (Button) findViewById(R.id.updateSingnature_btu);
        //设置签名初始值
        singnature.setText(sing);
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
                final String name= singnature.getText().toString().trim();
                if(name.length()<5||name.length()>20){
                    Toast.makeText(UpdateSingnatureActivity.this,"长度不得小于5，大于20",Toast.LENGTH_SHORT).show();
                }else {
                    _User myuser=new _User();
                    //设置新的签名
                    myuser.setSignature(name);
                    myuser.setSessionToken(token);
                    _User user= BmobUser.getCurrentUser(_User.class);
                    myuser.update(user.getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if(e==null){
                                Toast.makeText(UpdateSingnatureActivity.this,"更新成功",Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent();
                                //设置返回结果
                                setResult(2,intent);
                                finish();
                            }else {
                                Toast.makeText(UpdateSingnatureActivity.this,"更新失败"+e.toString(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
