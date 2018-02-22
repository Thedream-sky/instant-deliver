package com.example.instant_deliver.tools;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.instant_deliver.R;
import com.example.instant_deliver.beans.Message;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by King on 2018/2/7.
 */
/**
 * 接口回调实现列表消息监听
 */
public class messageAdapter extends BaseAdapter{
   /* private CallBack msgcallBack;*/
    private EMMessageListener msgListener;
   /* //定义回调接口
    public interface CallBack {
        //收到消息
        void onMessageReceived(List<EMMessage> messages) ;
        //收到透传消息
        void onCmdMessageReceived(List<EMMessage> messages);
        //收到已读回执
        void onMessageRead(List<EMMessage> messages) ;
        //收到已送达回执
        void onMessageDelivered(List<EMMessage> message);
        //消息被撤回
        void onMessageRecalled(List<EMMessage> messages);
        //消息状态变动
        void onMessageChanged(EMMessage message, Object change);
    }*/
    private List<Message> MessageList = new ArrayList<>();
    private Context context;
    public messageAdapter(List<Message> MessageList,Context context,EMMessageListener msgListener/*,CallBack msgcallBack*/){
        this.MessageList = MessageList;
        this.context = context;
        this.msgListener = msgListener;
        /*this.msgcallBack = msgcallBack;*/
    }

    @Override
    public int getCount() {
        return MessageList.size();
    }

    @Override
    public Object getItem(int i) {
        return MessageList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = new ViewHolder();
        final Message message = MessageList.get(i);
        if(view==null){
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            view = layoutInflater.inflate(R.layout.message_item, null);
            //用户头像
            viewHolder.headImg = (ImageView) view.findViewById(R.id.friendImguri);
            viewHolder.friendName = (TextView) view.findViewById(R.id.friendName);
            viewHolder.lastMsg = (TextView) view.findViewById(R.id.lastMessage);
            viewHolder.lastdate = (TextView) view.findViewById(R.id.lastDate);
            viewHolder.msgCount = (TextView) view.findViewById(R.id.msgCount);
            viewHolder.deleteMsg = (TextView) view.findViewById(R.id.msgDel);
            viewHolder.topMsg = (TextView) view.findViewById(R.id.msgTop);
            viewHolder.friendId = (TextView) view.findViewById(R.id.friendid);
            view.setTag(viewHolder);
        }else {
           viewHolder = (ViewHolder) view.getTag();
        }
        //加载圆角头像
        loadImage.loadImageGlideRound(context,message.getImageUri(),viewHolder.headImg);
        viewHolder.friendName.setText(message.getFriendName());
        viewHolder.lastdate.setText(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(message.getDate()));
        viewHolder.lastMsg.setText(message.getMsg());
        viewHolder.friendId.setText(message.getObjectid());
        final ViewHolder finalViewHolder = viewHolder;
        //消息监听
        msgListener = new EMMessageListener() {

            @Override
            public void onMessageReceived(List<EMMessage> messages) {
                finalViewHolder.lastMsg.setText(messages.get(messages.size()-1).getBody().toString());
                EMConversation conversation = EMClient.getInstance().chatManager().getConversation(message.getObjectid());
                int count = conversation.getAllMsgCount();
                Log.i("count",""+count);
               /* if(count == 0){
                    finalViewHolder.msgCount.setVisibility(View.INVISIBLE);
                }else {
                    finalViewHolder.msgCount.setVisibility(View.VISIBLE);
                }*/
                finalViewHolder.msgCount.setText(""+count);

            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> messages) {
                //收到透传消息
            }

            @Override
            public void onMessageRead(List<EMMessage> messages) {
                //收到已读回执
            }

            @Override
            public void onMessageDelivered(List<EMMessage> message) {
                //收到已送达回执
            }
            @Override
            public void onMessageRecalled(List<EMMessage> messages) {
                //消息被撤回
            }

            @Override
            public void onMessageChanged(EMMessage message, Object change) {
                //消息状态变动
            }
        };
        //监听
        EMClient.getInstance().chatManager().addMessageListener(msgListener);
        return view;
    }


    //内部类（内容提供者）
    class ViewHolder {
        private ImageView headImg;
        private TextView  friendName;
        private TextView  lastMsg;
        private TextView  lastdate;
        private TextView  msgCount;
        private TextView  friendId;
        private TextView  deleteMsg;
        private TextView  topMsg;
    }

}
