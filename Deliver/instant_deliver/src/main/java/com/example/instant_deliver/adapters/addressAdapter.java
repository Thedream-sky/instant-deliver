package com.example.instant_deliver.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.instant_deliver.R;
import com.example.instant_deliver.beans.myAddress;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by King on 2017/5/22.
 */

public class addressAdapter extends BaseAdapter {
    List<myAddress> list=new ArrayList<>();
    Context context;
    //构造函数传参
    public addressAdapter(List<myAddress> list, Context context){
        this.list=list;
        this.context=context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //获取信息
        myAddress myaddress=list.get(position);
        //内容提供者
        ViewHolder viewHolder=new ViewHolder();

        if(convertView==null){
            LayoutInflater layoutInflater=LayoutInflater.from(context);
            convertView=layoutInflater.inflate(R.layout.addressitem,null);
            viewHolder.address= (TextView) convertView.findViewById(R.id.ads);
            viewHolder.name= (TextView) convertView.findViewById(R.id.usn);
            viewHolder.alter= (ImageView) convertView.findViewById(R.id.alteradsinfo);
            convertView.setTag(viewHolder);
        }else {
            viewHolder= (ViewHolder) convertView.getTag();
        }
        //赋值
        viewHolder.name.setText(myaddress.getName());
        viewHolder.address.setText(myaddress.getAddress());

        return convertView;
    }

    class ViewHolder{
        private TextView address;
        private TextView name;
        private ImageView alter;
    }
}
