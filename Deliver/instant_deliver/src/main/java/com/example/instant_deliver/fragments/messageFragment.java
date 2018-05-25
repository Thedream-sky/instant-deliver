package com.example.instant_deliver.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instant_deliver.R;
import com.example.instant_deliver.activities.ChatActivity;
import com.example.instant_deliver.beans.Message;
import com.example.instant_deliver.adapters.messageAdapter;
import com.example.instant_deliver.beans.Messageforbmob;
import com.example.instant_deliver.services.myApplication;
import com.example.instant_deliver.tools.ListviewForScrollView;
import com.example.instant_deliver.tools.MessageTool;
import com.example.instant_deliver.tools.orderListTool;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.exceptions.HyphenateException;

import org.json.JSONException;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class messageFragment extends Fragment {
    private myApplication myapplication;
    private EMMessageListener msgListener;
    //下拉刷新控件
    private PullToRefreshScrollView scrollView;
    private View view;
    private ListviewForScrollView listView;
    private List<Message> list =new ArrayList<>();
    private messageAdapter.CallBack callBack;
    private messageAdapter messageadapter;
    private int RESULT_REQUEST = 11;
    private int RESULT_CODE = 10;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {

            if(msg.what == 1){
               getMessageList();
            }else if(msg.what ==2){
                getMessageList();
                scrollView.onRefreshComplete();
                Toast.makeText(getActivity().getApplicationContext(),"刷新成功",Toast.LENGTH_SHORT).show();
            }
        }
    };
    public messageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_REQUEST && resultCode == RESULT_CODE) {
          /*  //Log.i("myuuid",data.getStringExtra("uuid"));
            //查询数据库
            List<Message> list = DataSupport.where("uuid = ?", data.getStringExtra("uuid")).find(Message.class);
            Message message = list.get(0);
            message.setCount(0);
            message.saveOrUpdate();
            //更新列表
            requestMessages(1);*/
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_message, container, false);
        }
        getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
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
        listView = (ListviewForScrollView) view.findViewById(R.id.message_listView);
        myapplication = (myApplication)getActivity().getApplicationContext();
        callBack = new messageAdapter.CallBack(){
            @Override
            public void onClick(View view) {
                TextView friendIdView = (TextView) view.findViewById(R.id.friendid);
                TextView friendNameView = (TextView) view.findViewById(R.id.friendName);
                TextView msgUuidView = (TextView) view.findViewById(R.id.msg_uuid);
                String friendId =  friendIdView.getText().toString().trim();
                String friendName = friendNameView.getText().toString().trim();
                //String msgUuid = msgUuidView.getText().toString().trim();
                Intent intent =new Intent(getActivity(),ChatActivity.class);
                //intent.putExtra("msgUuid",msgUuid);
                intent.putExtra(EaseConstant.EXTRA_USER_ID,friendId);  //对方账号
                intent.putExtra(EaseConstant.EXTRA_USER_NAME,friendName);
                intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE, EMMessage.ChatType.Chat); //单聊模式
                getActivity().startActivityForResult(intent, RESULT_REQUEST);
            }

            @Override
            public void deleteItem(View view) {
                myApplication myapplication = (myApplication) getActivity().getApplicationContext();
                TextView friendIdView = (TextView) view.findViewById(R.id.friendid);

                String friendId =  friendIdView.getText().toString().trim();
                String myid = myapplication.getCurrentUser().getObjectId();
                android.os.Message message =new android.os.Message();

            }
        };

        //请求访问数据库
        requestMessages(1);
    }

    //请求查询数据库
    private void requestMessages(int what){
        android.os.Message msg = new android.os.Message();
        msg.what = what;
        handler.sendMessage(msg);
    }

    //delete
    public void deleteItem(String myid,String friendId){
        BmobQuery<Messageforbmob> query = new BmobQuery<>();
        query.addWhereEqualTo("friendId",friendId);
        query.addWhereEqualTo("ownid",myid);
        query.findObjects(new FindListener<Messageforbmob>() {
            @Override
            public void done(List<Messageforbmob> list, BmobException e) {
                if (list!=null&&list.size()==1){
                    Messageforbmob messageforbmob = list.get(0);
                    messageforbmob.setState("0");
                    messageforbmob.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            Log.i("delete","删除成功");
                        }
                    });
                }
            }
        });

       /* //查询数据库
        List<Message> list = DataSupport.where("friendId = ? and ownid = ?", friendId,myid).find(Message.class);
        if(list!=null&&list.size()==1){
            Message message =list.get(0);
            message.setState("0");
            message.saveOrUpdate();
        }*/
        getMessageList();
    }


    //获取数据
    public void getMessageList(){
        BmobQuery<Messageforbmob> query =new BmobQuery<>();
        query.addWhereEqualTo("state","1");
        query.addWhereEqualTo("ownid",myapplication.getCurrentUser().getObjectId());
        query.findObjects(new FindListener<Messageforbmob>() {
            @Override
            public void done(List<Messageforbmob> list, BmobException e) {
                if(list!=null&&list.size()>0){
                messageadapter =new messageAdapter(list,getActivity(),callBack);
                listView.setAdapter(messageadapter);
                }
            }
        });
       /* List<Message> list = DataSupport.where("state = ? and  ownid = ?","1",myapplication.getCurrentUser().getObjectId()).find(Message.class);
        return list;*/
    }

    //提示音
    private void startAlarm(Context context) {
        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
       /* mediaPlayer .setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.seekTo(0);
            }
        });*/
        AssetFileDescriptor file =context.getResources().openRawResourceFd(
                R.raw.warnring);
        try{
            mediaPlayer.setDataSource(file.getFileDescriptor(),

                    file.getStartOffset(), file.getLength());

            file.close();
            mediaPlayer.prepare();

        }catch (IOException ioe) {
            mediaPlayer = null;

        }
        mediaPlayer.start();
    }

    //保存聊天信息
    private void saveMessages(List<EMMessage> list) throws HyphenateException, JSONException {
        //Log.i("hh","保存聊天记录");
        for(EMMessage eMsg: list){
            //获取回话
            EMConversation conversation = EMClient.getInstance().chatManager().getConversation(eMsg.getUserName());
            orderListTool.saveMsgtoDB(getActivity(),eMsg.getFrom(),eMsg.getTo(), MessageTool.msgTransform(eMsg),conversation.getUnreadMsgCount());
        }
    }

    private void listener(){
        scrollView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        scrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ScrollView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                scrollView.getRefreshableView();
                requestMessages(2);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {

            }
        });

        //即时消息监听
        msgListener = new  EMMessageListener(){

            @Override
            public void onMessageReceived(List<EMMessage> list) {
                try {
                    saveMessages(list);
                    //刷新聊天页面
                    requestMessages(1);
                } catch (HyphenateException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startAlarm(getActivity());
            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> list) {

            }

            @Override
            public void onMessageRead(List<EMMessage> list) {

            }

            @Override
            public void onMessageDelivered(List<EMMessage> list) {

            }

            @Override
            public void onMessageRecalled(List<EMMessage> list) {

            }

            @Override
            public void onMessageChanged(EMMessage emMessage, Object o) {

            }
        };
        EMClient.getInstance().chatManager().addMessageListener(msgListener);
    }


    @Override
    public void onDestroy() {
        EMClient.getInstance().chatManager().removeMessageListener(msgListener);
        super.onDestroy();
    }


}
