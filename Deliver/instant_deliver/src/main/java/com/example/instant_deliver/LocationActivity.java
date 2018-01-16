package com.example.instant_deliver;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
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
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.example.instant_deliver.identifyView.Topbar;
import com.example.instant_deliver.tools.ActivityManagerTool;
import com.example.instant_deliver.tools.schoolAdapter;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class LocationActivity extends Activity implements PoiSearch.OnPoiSearchListener{

    //handler机制更新主UI,返回学校列表
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            List<String>schoolsList= (List<String>) msg.obj;
            schoolAdapter adapter=new schoolAdapter(schoolsList, getApplicationContext());
            listView.setAdapter(adapter);
        }
    };
    //地图对象
    private MapView mapView;
    private AMap aMap;
    //poi搜索
    private PoiSearch poiSearch;
    //query查询对象
    PoiSearch.Query query;
    // poi返回的结果
    private PoiResult Result;
    //定位maker
    private  MarkerOptions markerOptions;

    //
    private ProgressDialog progressDialog;

    //声明AMapLocationClient类对象  
    private AMapLocationClient mLocationClient = null;
    //声明AMapLocationClientOption对象
    private AMapLocationClientOption mLocationOption = null;

    private double lat;//纬度
    private double lon;//经度
    //学校列表
    private ListView listView;
    //搜索框
    private EditText search_editText;
    private Topbar topbar;
    //声明定位回调监听器
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation amapLocation) {
            if (amapLocation != null) {
                progressDialog.dismiss();
                if (amapLocation.getErrorCode() == 0) {
                    Toast.makeText(LocationActivity.this, "定位成功", Toast.LENGTH_SHORT).show();
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


                    /**
                     * poi查询
                     */
                    //显示定位图标
                    showmark();
                    //传递消息
                    Message message = new Message();
                    message.obj = "" + Province + city + district + street + streetNum + aoiName + BuildingId + floor;
                    //搜索周边
                    dosearchQueryAround("大学","141201",citycode,0);
                } else {
                    Toast.makeText(LocationActivity.this, "定位失败", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };


    //搜索poi,key为搜索关键字，type为搜索类型，code为城市编码，currentPage为当前页码
    private void dosearchQuery(String key, String type, String code, int currentPage) {
        query = new PoiSearch.Query(key, type, code);
        query.setPageSize(1000);// 设置每页最多返回多少条poiitem
        query.setPageNum(currentPage);//设置查询页码
        poiSearch = new PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(this);
        //异步搜索
        poiSearch.searchPOIAsyn();
    }

    //搜索poi,key为搜索关键字，type为搜索类型，code为城市编码，currentPage为当前页码
    private void dosearchQueryAround(String key, String type, String code, int currentPage) {
        query = new PoiSearch.Query(key, type, code);
        query.setPageSize(1000);// 设置每页最多返回多少条poiitem
        query.setPageNum(currentPage);//设置查询页码
        poiSearch = new PoiSearch(this, query);
        poiSearch.setBound(new PoiSearch.SearchBound(new LatLonPoint(markerOptions.getPosition().latitude,
                markerOptions.getPosition().longitude), 2*1000));//设置周边搜索的中心点以及半径
        poiSearch.setOnPoiSearchListener(this);
        //异步搜索
        poiSearch.searchPOIAsyn();
    }

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
        setContentView(R.layout.activity_location);

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
        mapView = (MapView) findViewById(R.id.mymap);
        //大学列表
        listView= (ListView) findViewById(R.id.universitys);
        //初始地图
        init();
        //搜索栏
        search_editText= (EditText) findViewById(R.id.search_et);
        progressDialog=ProgressDialog.show(this,"提示","定位中");
        topbar= (Topbar) findViewById(R.id.topbar_location);
    }

    private void initListener() {
        //list子项目监听
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView= (TextView) view.findViewById(R.id.school_text);
                String school=textView.getText().toString();
                Intent intent=new Intent(LocationActivity.this,RegistActivity.class);
                intent.putExtra("school",school);
                startActivity(intent);
            }
        });
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        topbar.setOnTopbarClickListener(new Topbar.topbarClickListener() {
            @Override
            public void leftclick() {
                finish();
            }

            @Override
            public void rightclick() {

            }
        });
        search_editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String key=String.valueOf(s);
                dosearchQuery(key+"区","141201","",0);
            }

            @Override
            public void afterTextChanged(Editable s) {

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

    /**
     * POI信息查询回调方法
     */
    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {
        if(i== AMapException.CODE_AMAP_SUCCESS){
            if(poiResult!=null&&poiResult.getQuery()!=null){//搜索poi结果
                if(poiResult.getQuery().equals(query)){//判断是否为同一条
                    Result=poiResult;
                    List<String>list=new ArrayList<>();
                    //返回当前页所有POI结果。
                    List<PoiItem> poiItems = poiResult.getPois();
                    List<String>unis=new ArrayList<>();
                    for(int k=0;k<poiItems.size();k++){
                       unis.add(poiItems.get(k).getTitle().toString());
                    }
                    unis.add("其他学校");
                    Message message=new Message();
                    message.obj=unis;
                    handler.sendMessage(message);
                }
            }

        }
}

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }


}
