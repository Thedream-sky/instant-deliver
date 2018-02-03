package com.example.instant_deliver;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MarkerOptions;
import com.example.instant_deliver.beans.myAddress;
import com.example.instant_deliver.beans.Users;
import com.example.instant_deliver.identifyView.Topbar;
import com.example.instant_deliver.tools.ActivityManagerTool;
import com.example.instant_deliver.tools.topStatusTool;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class getLocationActivity extends Activity implements View.OnClickListener {

    //地图对象
    private MapView mapView;
    private AMap aMap;
    //地址
    private EditText address;
    //详细地址
    private EditText addressdetail;
    //临时用户姓名
    private EditText username;
    //提交按钮
    private Button submit;
    //定位maker
    private MarkerOptions markerOptions;

    //提示条
    private ProgressDialog progressDialog;
    //声明AMapLocationClient类对象  
    private AMapLocationClient mLocationClient = null;
    //声明AMapLocationClientOption对象
    private AMapLocationClientOption mLocationOption = null;

    private double lat;//纬度
    private double lon;//经度
    //导航栏
    private Topbar topbar;

    //声明定位回调监听器
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation amapLocation) {
            if (amapLocation != null) {
                progressDialog.dismiss();
                if (amapLocation.getErrorCode() == 0) {
                    Toast.makeText(getLocationActivity.this, "定位成功", Toast.LENGTH_SHORT).show();
                    amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                    lat = amapLocation.getLatitude();//获取纬度
                    lon = amapLocation.getLongitude();//获取经度
                    amapLocation.getAccuracy();//获取精度信息
                    amapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                    String country = amapLocation.getCountry();//国家信息
                    String Province = amapLocation.getProvince();//省信息
                    String city = amapLocation.getCity();//城市信息
                    String district = amapLocation.getDistrict();//城区信息
                    String street = amapLocation.getStreet();//街道信息
                    String streetNum = amapLocation.getStreetNum();//街道门牌号信息
                    String citycode = amapLocation.getCityCode();//城市编码
                    String adcode = amapLocation.getAdCode();//地区编码
                    String aoiName = amapLocation.getAoiName();//获取当前定位点的AOI信息
                    String BuildingId = amapLocation.getBuildingId();//获取当前室内定位的建筑物Id
                    String floor = amapLocation.getFloor();//获取当前室内定位的楼层
                    amapLocation.getGpsAccuracyStatus();//获取GPS的当前状态
                    //获取定位时间
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date(amapLocation.getTime());
                    df.format(date);

                    //显示当前位置
                    address.setText(city + district + street + aoiName);

                    //显示定位图标
                    showmark();
                } else {
                    Toast.makeText(getLocationActivity.this, "定位失败", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    //显示定位图标
    public void showmark() {
        //设置当前地图显示为当前位置
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 19));
        markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(lat, lon));
        markerOptions.title("当前位置");
        markerOptions.visible(true);
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.b_poi));
        markerOptions.icon(bitmapDescriptor);
        aMap.addMarker(markerOptions);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_location);
        //解决沉浸式状态栏问题
        topStatusTool.applyKitKatTranslucency(this,R.color.deepskyblue);
        //添加activity
        ActivityManagerTool.pushActivity(this);
        //初始化控件及设置
        initAll();
        //初始监听
        initListener();
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mapView.onCreate(savedInstanceState);

    }

    //初始所有控件
    private void initAll() {
        //获取地图控件引用
        mapView = (MapView) findViewById(R.id.getLocationMap);
        //地址显示栏
        address = (EditText) findViewById(R.id.getLocation_textView);
        //用户名
        username = (EditText) findViewById(R.id.getLocation_username);
        //详细地址
        addressdetail = (EditText) findViewById(R.id.getLocation_detail);
        //提交
        submit = (Button) findViewById(R.id.getLocation_ensure);
        //监听
        submit.setOnClickListener(this);

        //初始地图
        init();
        progressDialog = ProgressDialog.show(this, "提示", "定位中");
        topbar = (Topbar) findViewById(R.id.getLocation_topbar);
    }

    //初始监听
    private void initListener() {

        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        topbar.setOnTopbarClickListener(new Topbar.topbarClickListener() {
            @Override
            public void leftclick() {
                setResult(200);
                finish();
            }

            @Override
            public void rightclick() {

            }
        });

    }

    //初始化map对象
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        setupMap();
    }

    //参数设置
    private void setupMap() {
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //获取一次定位结果：
        //该方法默认为false。
        mLocationOption.setOnceLocation(false);
        //获取最近3s内精度最高的一次定位结果：
        //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
        mLocationOption.setOnceLocationLatest(true);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否强制刷新WIFI，默认为true，强制刷新。
        mLocationOption.setWifiActiveScan(false);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
        mLocationOption.setHttpTimeOut(20000);
        //关闭缓存机制
        mLocationOption.setLocationCacheEnable(false);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }


    @Override
    protected void onStop() {
        super.onStop();
        mLocationClient.stopLocation();//停止定位后，本地定位服务并不会被销毁
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        mLocationClient.onDestroy();//销毁定位客户端，同时销毁本地定位服务。
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.getLocation_ensure:
                saveAddress();
                ;break;
        }

    }


    //保存地址
    private void saveAddress() {
        String name = username.getText().toString().trim();
        String ads = address.getText().toString().trim();
        String adtails = addressdetail.getText().toString();

        if (name.equals("") || name == null || ads.equals("") || ads == null || adtails.equals("") || adtails == null) {
            Toast.makeText(getLocationActivity.this, "地址或者用户名不能有空", Toast.LENGTH_SHORT).show();
        } else {
            Users currency = BmobUser.getCurrentUser(Users.class);
            myAddress myaddress = new myAddress();
            myaddress.setName(name);
            myaddress.setAddress(ads + adtails);
            myaddress.setUsers(currency);
            //保存地址
            myaddress.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if (e == null) {
                        Toast.makeText(getLocationActivity.this, "地址保存成功", Toast.LENGTH_SHORT).show();
                        setResult(200);
                        finish();
                    } else {
                        Toast.makeText(getLocationActivity.this, "地址保存失败" + e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }
}
