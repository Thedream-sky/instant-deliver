package com.example.instant_deliver.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instant_deliver.R;
import com.example.instant_deliver.activities.ChatActivity;
import com.example.instant_deliver.adapters.friendAdapter;
import com.example.instant_deliver.beans._User;
import com.example.instant_deliver.services.myApplication;
import com.example.instant_deliver.tools.ListviewForScrollView;
import com.example.instant_deliver.tools.usersManager;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.exceptions.HyphenateException;

import java.util.Arrays;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

import static com.example.instant_deliver.R.id.friendName;

public class contantFragment extends Fragment {

    //下拉刷新控件
    private PullToRefreshScrollView scrollView;
    private ListviewForScrollView listView;
    private View view;
    private myApplication myapplication;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(android.os.Message msg) {
            if(msg.what==1){
                try {
                    getFriend();
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }else if(msg.what==2){
                try {
                    getFriend();
                    scrollView.onRefreshComplete();
                    Toast.makeText(getActivity().getApplicationContext(),"刷新成功",Toast.LENGTH_SHORT).show();
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public contantFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_contant, container, false);
        }
        //初始化
        init();
        // Inflate the layout for this fragment
        return view;
    }

    private void init(){
        scrollView = (PullToRefreshScrollView) view.findViewById(R.id.contant_scrollView);
        listView = (ListviewForScrollView) view.findViewById(R.id.contant_listView);
        myapplication = (myApplication)getActivity().getApplicationContext();

        getData(1);
        //监听
        listener();

    }
    private void listener(){
        //手动把ScrollView滚动至最顶端
        scrollView.smoothScrollTo(0, 0);
        scrollView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        scrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ScrollView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                getData(2);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView friendId = (TextView) view.findViewById(R.id.contant_fid);
                TextView friendName = (TextView) view.findViewById(R.id.myFriendName);
                Intent intent =new Intent(getActivity(),ChatActivity.class);
                Log.i("hhhhhhhhhhh","&"+friendId.getText().toString());
                intent.putExtra(EaseConstant.EXTRA_USER_ID,friendId.getText().toString());  //对方账号
                intent.putExtra(EaseConstant.EXTRA_USER_NAME,friendName.getText().toString());
                intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE, EMMessage.ChatType.Chat); //单聊模式
                getActivity().startActivity(intent);
            }
        });
    }

    //调用异步任务
    private void getData (int what){
        android.os.Message msg = new android.os.Message();
        msg.what = what;
        handler.sendMessage(msg);
    }

    //查询数据
    private void getFriend() throws HyphenateException {
        new Thread(){
            @Override
            public void run() {
                super.run();
                List<String> array = usersManager.getAllfriends();
                if(array!=null&&array.size()>0){
                    String[] strings = new String[array.size()];
                    array.toArray(strings);
                    BmobQuery query = new BmobQuery<>();
                    query.addWhereContainedIn("objectId", Arrays.asList(strings));
                    query.findObjects(new FindListener<_User>() {
                        @Override
                        public void done(List<_User> list, BmobException e) {
                            if(list!=null){
                                //适配器
                                listView.setAdapter(new friendAdapter(list,getActivity()));
                            }
                        }
                    });
                }else {
                    Log.i("error","获取失败" );
                }
            }
        }.start();
    }

}
