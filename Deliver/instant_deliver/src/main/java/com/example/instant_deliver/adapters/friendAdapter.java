package com.example.instant_deliver.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.instant_deliver.R;
import com.example.instant_deliver.beans._User;
import com.example.instant_deliver.tools.loadImage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by King on 2018/5/18.
 */

public class friendAdapter extends BaseAdapter {
    private List<_User> list=new ArrayList<>();
    private Context context;

    public friendAdapter(List<_User> list,Context context){
        this.list = list;
        this.context =context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Viewholder viewholder=new Viewholder();
        _User user=list.get(i);
        if(view == null){
            LayoutInflater layoutInflater=LayoutInflater.from(context);
            view=layoutInflater.inflate(R.layout.contant_item,null);
            viewholder.imageView= (ImageView) view.findViewById(R.id.friendImg);
            viewholder.userName = (TextView) view.findViewById(R.id.myFriendName);
            viewholder.signature = (TextView) view.findViewById(R.id.mySignature);
            viewholder.friendId = (TextView) view.findViewById(R.id.contant_fid);
            view.setTag(viewholder);
        }else {
            viewholder = (Viewholder) view.getTag();
        }
        //加载圆角头像
        loadImage.loadImageGlide(context,user.getHeadurl(),viewholder.imageView);
        viewholder.userName.setText(user.getUsername());
        if(user.getSignature() == null){
            viewholder.signature.setText("便利生活就来校园速递吧！");
        }else {
            viewholder.signature.setText(user.getSignature());
        }
        viewholder.friendId.setText(user.getObjectId());
        return view;
    }

    //内部类
    class  Viewholder{
        private ImageView imageView;
        private TextView userName;
        private TextView signature;
        private TextView friendId;
    }
}
