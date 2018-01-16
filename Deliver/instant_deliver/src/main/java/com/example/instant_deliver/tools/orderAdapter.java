package com.example.instant_deliver.tools;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.example.instant_deliver.R;
import com.example.instant_deliver.beans.Order;
import com.example.instant_deliver.beans.Users;
import com.example.instant_deliver.identifyView.ImageViewPlus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by King on 2017/5/24.
 */

public class orderAdapter extends BaseAdapter  implements View.OnClickListener {
    private List<Order> orderList = new ArrayList<>();
    private Context context;
    private CallBack mCallBack;
    //构造方法传参
    public orderAdapter(List<Order> orderList, Context context,CallBack callback) {
        this.orderList = orderList;
        this.context = context;
        mCallBack=callback;
    }

    //定义回调接口
    public interface CallBack{
        public void Buttonclick(View view);
    }

    @Override
    public int getCount() {
        return orderList.size();
    }

    @Override
    public Object getItem(int position) {
        return orderList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
       ViewHolder viewHolder = new ViewHolder();
        //获取当前数据
        final Order order = orderList.get(position);
        final String[] username = {};
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.ordersitem, null);
           // viewHolder.imageViewPlus = (ImageViewPlus) convertView.findViewById(R.id.order_head);
            viewHolder.address = (TextView) convertView.findViewById(R.id.order_address);
            viewHolder.endtime = (TextView) convertView.findViewById(R.id.order_endtime);
            viewHolder.award = (TextView) convertView.findViewById(R.id.order_award);
            viewHolder.phone = (TextView) convertView.findViewById(R.id.order_conphonenum);
            viewHolder.orderinfo = (TextView) convertView.findViewById(R.id.order_info);
            viewHolder.getorder = (Button) convertView.findViewById(R.id.order_getorder);
            viewHolder.orderuser= (TextView) convertView.findViewById(R.id.order_user);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.getorder.setOnClickListener(this);
        viewHolder.getorder.setTag(position);

        //送达地址
        viewHolder.address.setText("送达地址："+order.getAdress());

        //异步加载图片
        //loadImage.loadImageVolley(context,order.getLauncherhead(),viewHolder.imageViewPlus);

        BmobDate bmobDate=order.getEndDate();

        //截止时间
        viewHolder.endtime.setText("截止时间: "+bmobDate.getDate());
        //报酬
        viewHolder.award.setText("  报酬："+order.getAward().toString()+"元");
        //电话号
        viewHolder.phone.setText( "联系电话："+order.getPhonenum().toString());
        //订单详情
        viewHolder.orderinfo.setText("  "+order.getInfo().toString());
        viewHolder.orderuser.setText(order.getLaunchername());

        return convertView;
    }

    //点击事件
    @Override
    public void onClick(View v) {
        //实现接口里的方法
        mCallBack.Buttonclick(v);
    }

    //内部类（内容提供者）
    class ViewHolder {
        //头像
       // private ImageViewPlus imageViewPlus;
        //结束时间
        private TextView endtime;
        //送达地址
        private TextView address;
        //手机号
        private TextView phone;
        //订单详情
        private TextView orderinfo;
        //报酬
        private TextView award;
        //抢单按钮
        private Button getorder;
        //发单用户
        private TextView orderuser;
    }


}
