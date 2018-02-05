package com.example.instant_deliver.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.example.instant_deliver.R;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;

/**
 * A simple {@link Fragment} subclass.
 */
public class messageFragment extends Fragment {

    //下拉刷新控件
    private PullToRefreshScrollView scrollView;
    private View view;
    public messageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_message, container, false);
        }
        //初始化
        init();
        //监听
        listener();
        // Inflate the layout for this fragment
        return view;
    }
    private void init(){
        scrollView = (PullToRefreshScrollView) view.findViewById(R.id.message_scrollView);
        //手动把ScrollView滚动至最顶端
        scrollView.smoothScrollTo(0, 0);
    }

    private  void listener(){
        scrollView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        scrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ScrollView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {

                scrollView.getRefreshableView();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {

            }
        });
    }

}
