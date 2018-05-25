package com.example.instant_deliver.fragments;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.example.instant_deliver.R;
import com.example.instant_deliver.adapters.orderManageAdapter;
import com.example.instant_deliver.services.myApplication;
import com.example.instant_deliver.tools.ListviewForScrollView;
import com.example.instant_deliver.tools.orderManager;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;

/**
 * A simple {@link Fragment} subclass.
 */
public class unReceiveFragment extends Fragment {
    private String flag;
    private PullToRefreshScrollView pullToRefreshScrollView;
    private ListviewForScrollView listviewForScrollView;
    private View view;
    private String userid;
    private orderManageAdapter.CallBack callBack;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==1){
                //获取数据
                orderManager.refreshData(getActivity().getApplicationContext(),callBack,flag,userid,listviewForScrollView,"1",pullToRefreshScrollView);
            }
        }
    };
    public unReceiveFragment() {
        // Required empty public constructor
    }

    public unReceiveFragment(String flag){
        this.flag =flag;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(view==null){
            view = inflater.inflate(R.layout.fragment_un_receive, container, false);
        }

        init();
        listener();


        // Inflate the layout for this fragment
        return view;
    }

    private void init(){
        pullToRefreshScrollView = (PullToRefreshScrollView) view.findViewById(R.id.unReceive_order_scrollView);
        listviewForScrollView = (ListviewForScrollView) view.findViewById(R.id.unReceive_order_listView);
        userid = new myApplication().getCurrentUser().getObjectId();

        callBack = new orderManageAdapter.CallBack(){
            @Override
            public void deleteOrder(View view) {

            }

            @Override
            public void cancelOrder(View view) {

            }

        };
        //获取数据
        getdata();
    }

    private void listener(){
        //手动把ScrollView滚动至最顶端
        pullToRefreshScrollView.smoothScrollTo(0, 0);
        pullToRefreshScrollView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        pullToRefreshScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ScrollView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                getdata();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {

            }
        });

    }
    //获取数据
    private void getdata(){
        handler.sendEmptyMessageDelayed(1, 200);
    }



}
