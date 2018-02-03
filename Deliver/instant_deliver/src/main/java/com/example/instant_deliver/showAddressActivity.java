package com.example.instant_deliver;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.instant_deliver.beans.myAddress;
import com.example.instant_deliver.identifyView.Topbar;
import com.example.instant_deliver.tools.topStatusTool;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class showAddressActivity extends Activity implements View.OnClickListener {
    private Topbar topbar;
    private ImageView del;
    private EditText name;
    private EditText address;
    private Button submit;
    private myAddress ads;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //关闭页面
            if(msg.what==1){
                setResult(200);
                finish();
            };
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_address);
        //解决沉浸式状态栏问题
        topStatusTool.applyKitKatTranslucency(this,R.color.deepskyblue);
        //初始化
        init();
    }

    //控件初始化
    private void init() {
        topbar = (Topbar) findViewById(R.id.alteraddress_topbar);
        del = (ImageView) findViewById(R.id.delAddress_img);
        name = (EditText) findViewById(R.id.alter_username);
        address = (EditText) findViewById(R.id.alter_address);
        submit = (Button) findViewById(R.id.aletrinfo_ensure_btn);

        //接受传过来的值
        Intent intent = getIntent();
        ads = (myAddress) intent.getSerializableExtra("addressinfo");

        //给文本框赋值
        name.setText(ads.getName());
        address.setText(ads.getAddress());
        //删除监听
        del.setOnClickListener(this);
        submit.setOnClickListener(this);

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
    //修改地址
    private void uptate(){
        String na=name.getText().toString().trim();
        String adds=address.getText().toString().trim();
        if(na.equals("")||na==null||adds.equals("")||adds==null){
            Toast.makeText(showAddressActivity.this,"姓名或者地址不能为空",Toast.LENGTH_SHORT).show();
        }else {
            myAddress address1=new myAddress();
            address1.setName(na);
            address1.setAddress(adds);
            address1.update(ads.getObjectId(), new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if(e==null) {
                        Toast.makeText(showAddressActivity.this,"修改成功",Toast.LENGTH_SHORT).show();
                        handler.sendEmptyMessageDelayed(1,200);
                    }else {
                        Toast.makeText(showAddressActivity.this,"修改失败",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    //删除地址
    private void deleteads() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("确定删除");
        builder.setTitle("提示");
        builder.setIcon(R.drawable.logo);

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //提示框消失
                dialog.dismiss();
            }
        });

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                myAddress mads = new myAddress();
                mads.setObjectId(ads.getObjectId());
                //删除监听事件
                mads.delete(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        //提示框消失
                        dialog.dismiss();
                        if (e == null) {
                            Toast.makeText(showAddressActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                           handler.sendEmptyMessageDelayed(1,200);
                        } else {
                            Toast.makeText(showAddressActivity.this, "删除失败" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        //显示dialog
        builder.create().show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.delAddress_img:
                //删除地址
                deleteads();
                break;
            case R.id.aletrinfo_ensure_btn:
                //修改地址
                uptate();
                ;break;

        }
    }
}
