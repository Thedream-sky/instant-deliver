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
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instant_deliver.ChatActivity;
import com.example.instant_deliver.R;
import com.example.instant_deliver.beans.Message;
import com.example.instant_deliver.tools.messageAdapter;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class messageFragment extends Fragment implements AdapterView.OnItemClickListener {
    private EMMessageListener msgListener;
    //下拉刷新控件
    private PullToRefreshScrollView scrollView;
    private View view;
    private ListView listView;
    private List<Message> list =new ArrayList<>();
   /* private messageAdapter.CallBack callBack;*/
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            if(msg.what == 1){
                list =getMessageList();
                if(list!=null){
                    messageAdapter messageadapter =new messageAdapter(list,getActivity(),msgListener);
                    listView.setAdapter(messageadapter);
                }
            }
        }
    };
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
        listView = (ListView) view.findViewById(R.id.message_listView);
        //监听子项目的点击事件
        listView.setOnItemClickListener(this);
        //请求访问数据库
        requestMessages();

    }
    //请求查询数据库
    private void requestMessages(){
        android.os.Message msg = new android.os.Message();
        msg.what = 1;
        handler.sendMessage(msg);
    }

    //获取数据
    public List<Message> getMessageList(){
        List<Message>list = DataSupport.where("state = ?","1").find(Message.class);
        return list;
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

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        TextView friendIdView = (TextView) view.findViewById(R.id.friendid);
        TextView friendNameView = (TextView) view.findViewById(R.id.friendName);
        String friendId =  friendIdView.getText().toString().trim();
        String friendName = friendNameView.getText().toString().trim();
        Intent intent =new Intent(getActivity(),ChatActivity.class);
        intent.putExtra(EaseConstant.EXTRA_USER_ID,friendId);  //对方账号
        intent.putExtra(EaseConstant.EXTRA_USER_NAME,friendName);
        intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE, EMMessage.ChatType.Chat); //单聊模式
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        EMClient.getInstance().chatManager().removeMessageListener(msgListener);
        super.onDestroy();
    }


}
