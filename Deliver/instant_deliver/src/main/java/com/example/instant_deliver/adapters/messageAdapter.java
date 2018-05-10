package com.example.instant_deliver.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.example.instant_deliver.activities.ChatActivity;
import com.example.instant_deliver.R;
import com.example.instant_deliver.beans.Message;
import com.example.instant_deliver.tools.loadImage;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by King on 2018/2/7.
 */
/**
 * 接口回调实现列表消息监听
 */
public class messageAdapter extends BaseSwipeAdapter /*implements EMMessageListener*/{
  /*  private messageAdapter.CallBack msgcallBack;
    private EMMessageListener msgListener;

    public interface CallBack {
        //收到消息
        void onMessageReceived();
    }*/

  /*  @Override
    public void onMessageReceived(List<EMMessage> list) {
        msgcallBack.onMessageReceived(list);
    }

    @Override
    public void onCmdMessageReceived(List<EMMessage> list) {
        msgcallBack.onCmdMessageReceived(list);
    }

    @Override
    public void onMessageRead(List<EMMessage> list) {
        msgcallBack.onMessageRead(list);
    }

    @Override
    public void onMessageDelivered(List<EMMessage> list) {
        msgcallBack.onMessageDelivered(list);
    }

    @Override
    public void onMessageRecalled(List<EMMessage> list) {
        msgcallBack.onMessageRecalled(list);
    }

    @Override
    public void onMessageChanged(EMMessage emMessage, Object o) {

    }*/

  /*  //定义回调接口
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
    public messageAdapter(List<Message> MessageList,Context context/*, CallBack msgcallBack*//*,EMMessageListener msgListener,CallBack msgcallBack*/){
        this.MessageList = MessageList;
        this.context = context;
        //this.msgListener = msgListener;
        //this.msgcallBack = msgcallBack;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.messageItem;
    }

    @Override
    public View generateView(final int position, ViewGroup parent) {
        View v = LayoutInflater.from(context).inflate(R.layout.message_item, null);
        SwipeLayout swipeLayout = (SwipeLayout)v.findViewById(getSwipeLayoutResourceId(position));

        //点击事件
        swipeLayout.getSurfaceView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView friendIdView = (TextView) view.findViewById(R.id.friendid);
                TextView friendNameView = (TextView) view.findViewById(R.id.friendName);
                String friendId =  friendIdView.getText().toString().trim();
                String friendName = friendNameView.getText().toString().trim();
                Intent intent =new Intent(context,ChatActivity.class);
                intent.putExtra(EaseConstant.EXTRA_USER_ID,friendId);  //对方账号
                intent.putExtra(EaseConstant.EXTRA_USER_NAME,friendName);
                intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE, EMMessage.ChatType.Chat); //单聊模式
                context.startActivity(intent);
            }
        });

        swipeLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onOpen(SwipeLayout layout) {
                // SwipeLayout划出时调用
            }

            @Override
            public void onClose(SwipeLayout layout) {
                // SwipeLayout关闭时调用
                super.onClose(layout);

            }
        });

        swipeLayout.setOnDoubleClickListener(new SwipeLayout.DoubleClickListener() {
            @Override
            public void onDoubleClick(SwipeLayout layout, boolean surface) {
                // SwipeLayout双击时调用
            }
        });
        v.findViewById(R.id.msgDel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 监听SwipeLayout中的组件删除的点击事件
            }
        });
        v.findViewById(R.id.msgTop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 监听SwipeLayout中的组件置顶的点击事件
            }
        });

        return v;
    }

    @Override
    public void fillValues(int position, View view) {
        Message message = MessageList.get(position);

        ViewHolder viewHolder = new ViewHolder();
        viewHolder.headImg = (ImageView) view.findViewById(R.id.friendImguri);
        viewHolder.friendName = (TextView) view.findViewById(R.id.friendName);
        viewHolder.lastMsg = (TextView) view.findViewById(R.id.lastMessage);
        viewHolder.lastdate = (TextView) view.findViewById(R.id.lastDate);
        viewHolder.msgCount = (TextView) view.findViewById(R.id.msgCount);
        viewHolder.deleteMsg = (TextView) view.findViewById(R.id.msgDel);
        viewHolder.topMsg = (TextView) view.findViewById(R.id.msgTop);
        viewHolder.friendId = (TextView) view.findViewById(R.id.friendid);

        //加载圆角头像
        loadImage.loadImageGlideRound(context,message.getImageUri(),viewHolder.headImg);
        viewHolder.friendName.setText(message.getFriendName());
        viewHolder.lastdate.setText(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(message.getDate()));
        viewHolder.lastMsg.setText(message.getMsg());
        viewHolder.friendId.setText(message.getObjectid());
        viewHolder.msgCount.setText(""+message.getCount());
        if(message.getCount() == 0){
            viewHolder.msgCount.setVisibility(View.INVISIBLE);
        }else{
            viewHolder.msgCount.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getCount() {
        return MessageList.size();
    }

    @Override
    public Object getItem(int i) {
        return  MessageList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

   /* @Override
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
        viewHolder.msgCount.setText(""+message.getCount());
        if(message.getCount() == 0){
            viewHolder.msgCount.setVisibility(View.INVISIBLE);
        }else{
            viewHolder.msgCount.setVisibility(View.VISIBLE);
        }*//*
       // final ViewHolder finalViewHolder = viewHolder;
       *//* //消息监听
        msgListener = new EMMessageListener() {

            @Override
            public void onMessageReceived(List<EMMessage> messages) {
                messages.get(messages.size()-1).getBody().toString();
               *//**//* Log.i("dasd","接到了");
                finalViewHolder.lastMsg.setText(messages.get(messages.size()-1).getBody().toString());*//**//*
                EMConversation conversation = EMClient.getInstance().chatManager().getConversation(message.getObjectid());
                int count = conversation.getAllMsgCount();
                message.setCount(count);
                message.setMsg(conversation.getLastMessage().toString());
                message.saveOrUpdate();
                //回调更新
                msgcallBack.onMessageReceived();
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
        EMClient.getInstance().chatManager().addMessageListener(msgListener);*//*
        return view;
    }
*/

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
