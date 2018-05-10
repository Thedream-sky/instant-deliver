package com.example.instant_deliver.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.instant_deliver.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by King on 2017/5/3.
 */

public class schoolAdapter extends BaseAdapter{
    private List<String>list=new ArrayList<>();
    private Context context;
    public schoolAdapter(List<String>list, Context context){
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
        Viewholder viewholder=new Viewholder();
        String school=list.get(position);
        if(convertView==null){
            LayoutInflater layoutInflater=LayoutInflater.from(context);
            convertView=layoutInflater.inflate(R.layout.school_item,null);
            viewholder.textView= (TextView) convertView.findViewById(R.id.school_text);
            convertView.setTag(viewholder);
        }else {
            viewholder= (Viewholder) convertView.getTag();
        }
        viewholder.textView.setText(school);
        return convertView;
    }

    //内部类
    class Viewholder{
        private TextView textView;

    }
}
