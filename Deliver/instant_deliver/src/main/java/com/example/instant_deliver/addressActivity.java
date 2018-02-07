package com.example.instant_deliver;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instant_deliver.beans._User;
import com.example.instant_deliver.beans.myAddress;
import com.example.instant_deliver.identifyView.Topbar;
import com.example.instant_deliver.tools.addressAdapter;
import com.example.instant_deliver.tools.getConnState;
import com.example.instant_deliver.tools.topStatusTool;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class addressActivity extends Activity implements View.OnClickListener {
    private Topbar topbar;
    private TextView addAdresses;
    private ListView listView;
    private Intent intent;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                if(getConnState.isConn(addressActivity.this)){
                    //查询地址
                    queryAdress();
                }else {
                    Toast.makeText(addressActivity.this, "当前网络不可用", Toast.LENGTH_SHORT).show();

                }

            }
        }
    };

    //返回处理
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //添加地址返回刷新
        if (requestCode == 100 && resultCode == 200) {
            //获取地址
            handler.sendEmptyMessage(1);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        //解决沉浸式状态栏问题
        topStatusTool.applyKitKatTranslucency(this,R.color.deepskyblue);
        //初始化控件
        init();
        //获取地址
        handler.sendEmptyMessage(1);
    }

    private void init() {
        //获取intent意向
        intent = getIntent();

        //导航栏
        topbar = (Topbar) findViewById(R.id.updateSingnature_topbar);
        //添加栏
        addAdresses = (TextView) findViewById(R.id.addAdresses);
        //显示内容
        listView = (ListView) findViewById(R.id.addressList);

        topbar.setOnTopbarClickListener(new Topbar.topbarClickListener() {
            @Override
            public void leftclick() {
                finish();
            }

            @Override
            public void rightclick() {

            }
        });

        // 设置监听
        addAdresses.setOnClickListener(this);
    }


    //查询地址
    private void queryAdress() {
        //当前用户
        _User user = BmobUser.getCurrentUser(_User.class);
        BmobQuery<myAddress> query = new BmobQuery<>();
        query.addWhereEqualTo("users", user);
        //查询
        query.findObjects(new FindListener<myAddress>() {
            @Override
            public void done(final List<myAddress> list, final BmobException e) {
                //返回查询结果
                addressAdapter adapter = new addressAdapter(list, getApplicationContext());
                listView.setAdapter(adapter);
                //标记，表明来向
                final String tag = intent.getStringExtra("tag");
                //listview单击事件
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        myAddress myAddress = list.get(position);
                        if (tag.equals("fromownfragment")) {
                            Intent intent = new Intent(addressActivity.this, showAddressActivity.class);
                            intent.putExtra("addressinfo", myAddress);
                            startActivityForResult(intent, 100);
                        }

                        if (tag.equals("fromputActivity")) {
                            Intent intent2 = new Intent();
                            intent2.putExtra("address", myAddress);
                            setResult(30, intent2);
                            finish();
                        }
                    }
                });
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addAdresses:
                startActivityForResult(new Intent(this, getLocationActivity.class), 100);
                break;
        }
    }
}
