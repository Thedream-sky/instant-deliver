package com.example.instant_deliver.tools;

import android.content.Context;
import android.util.Log;

import com.example.instant_deliver.adapters.orderManageAdapter;
import com.example.instant_deliver.beans.Order;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by King on 2018/5/19.
 */

public class orderManager {
   public static void refreshData(final Context context, final orderManageAdapter.CallBack callBack, final String flag, String userid, final ListviewForScrollView listviewForScrollView, String state, final PullToRefreshScrollView pullToRefreshScrollView){

        BmobQuery<Order> query = new BmobQuery<>();
        if(flag.equals("1")){
            query.addWhereEqualTo("launcher",userid);
        }else {
            query.addWhereEqualTo("reciver",userid);
        }
        if(!state.equals("")){
            query.addWhereEqualTo("orderState",Integer.parseInt(state));
        }

        query.findObjects(new FindListener<Order>() {
            @Override
            public void done(List<Order> list, BmobException e) {
                if(list!=null&&list.size()>0){
                    orderManageAdapter ordermanageAdapter = new orderManageAdapter(context,list,callBack,flag);
                    listviewForScrollView.setAdapter(ordermanageAdapter);
                }
                pullToRefreshScrollView.onRefreshComplete();
            }
        });

    }
}
