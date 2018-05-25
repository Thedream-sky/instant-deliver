package com.example.instant_deliver.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.example.instant_deliver.R;
import com.example.instant_deliver.beans.Message;
import com.example.instant_deliver.beans.Messageforbmob;
import com.example.instant_deliver.tools.loadImage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by King on 2018/2/7.
 */
/**
 * 接口回调实现列表消息监听
 */
public class messageAdapter extends BaseSwipeAdapter {

    private List<Messageforbmob> MessageList = new ArrayList<>();
    private Context context;
    private CallBack callBack;
    public messageAdapter(List<Messageforbmob> MessageList, Context context, CallBack callBack){
        this.MessageList = MessageList;
        this.context = context;
        this.callBack = callBack;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.messageItem;
    }

    @Override
    public View generateView(final int position, ViewGroup parent) {
        final View v = LayoutInflater.from(context).inflate(R.layout.message_item, null);
        SwipeLayout swipeLayout = (SwipeLayout)v.findViewById(getSwipeLayoutResourceId(position));

        //点击事件
        swipeLayout.getSurfaceView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callBack.onClick(view);
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
                callBack.deleteItem(view);
            }
        });
      /*  v.findViewById(R.id.msgTop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 监听SwipeLayout中的组件置顶的点击事件
            }
        });
*/
        return v;
    }

    @Override
    public void fillValues(int position, View view) {
        Messageforbmob message = MessageList.get(position);

        ViewHolder viewHolder = new ViewHolder();
        viewHolder.headImg = (ImageView) view.findViewById(R.id.friendImguri);
        viewHolder.friendName = (TextView) view.findViewById(R.id.friendName);
        viewHolder.lastMsg = (TextView) view.findViewById(R.id.lastMessage);
        viewHolder.lastdate = (TextView) view.findViewById(R.id.lastDate);
        viewHolder.msgCount = (TextView) view.findViewById(R.id.msgCount);
        viewHolder.deleteMsg = (TextView) view.findViewById(R.id.msgDel);
        //viewHolder.topMsg = (TextView) view.findViewById(R.id.msgTop);
        viewHolder.friendId = (TextView) view.findViewById(R.id.friendid);
        viewHolder.msgUuid = (TextView) view.findViewById(R.id.msg_uuid);

        //加载圆角头像
        loadImage.loadImageGlideRound(context,message.getImageUri(),viewHolder.headImg);
        viewHolder.friendName.setText(message.getFriendName());
        viewHolder.lastdate.setText(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(message.getDate()));
        viewHolder.lastMsg.setText(message.getMsg());
        viewHolder.friendId.setText(message.getFriendId());
        viewHolder.msgCount.setText(""+message.getCount());

        viewHolder.msgUuid.setText(message.getObjectId());
        if(message.getCount() == 0){
            viewHolder.msgCount.setVisibility(View.INVISIBLE);
        }else{
            viewHolder.msgCount.setVisibility(View.VISIBLE);
        }
        view.setTag(position);
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

    public interface CallBack {
        //点击
        void onClick (View view);
        //删除
        void deleteItem(View view);
    }

    //内部类（内容提供者）
    class ViewHolder {
        private ImageView headImg;
        private TextView  friendName;
        private TextView  lastMsg;
        private TextView  lastdate;
        private TextView  msgCount;
        private TextView  friendId;
        private TextView  msgUuid;
        private TextView  deleteMsg;
        //private TextView  topMsg;
    }

}
