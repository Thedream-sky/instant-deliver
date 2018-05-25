package com.example.instant_deliver.tools;

import android.content.Context;
import android.util.Log;

import com.example.instant_deliver.beans.Friends;
import com.example.instant_deliver.beans.Message;
import com.example.instant_deliver.beans.Messageforbmob;
import com.example.instant_deliver.beans._User;
import com.example.instant_deliver.beans.orderState;
import com.example.instant_deliver.services.myApplication;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;

import org.litepal.crud.DataSupport;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SQLQueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import static com.example.instant_deliver.R.id.list;
import static com.hyphenate.easeui.EaseConstant.CHATTYPE_GROUP;

/**
 * Created by King on 2018/2/8.
 */

public class orderListTool {
    //保存订单好友关系表,idOne为接单的，idTwo是发单的
    public static void saveState(final Context context, String idOne, String idTwo, final String orderId) {
        //发送消息
        sendMessage("我抢了你的订单，有事请吩咐！",idTwo);
        //默认关系表里前者的哈希值小于或等于后者,必须遵守
        if (idOne.hashCode() > idTwo.hashCode()) {
            String temp = idOne;
            idOne = idTwo;
            idTwo = temp;
        }
        BmobQuery<Friends> query = new BmobQuery<>();
        query.addWhereEqualTo("idOne", idOne);
        query.addWhereEqualTo("idTwo", idTwo);
        final String finalIdOne = idOne;
        final String finalIdTwo = idTwo;
        query.findObjects(new FindListener<Friends>() {
            @Override
            public void done(List<Friends> list, BmobException e) {
                //保存订单状态表
                if (list != null && list.size() == 1) {
                    orderState orderstate = new orderState();
                    orderstate.setFriendsId(list.get(0).getObjectId());
                    orderstate.setOrderId(orderId);
                    orderstate.setState(2);
                    orderstate.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            Log.i("save", "保存成功");
                            //保存数据库
                            saveMsgtoDB(context, finalIdOne, finalIdTwo, "我抢了你的订单，有事请吩咐！",1);
                        }
                    });

                }

            }
        });
    }

    private static void sendMessage(String  msg,String toChatUsername) {
        //创建一条文本消息，content为消息文字内容，toChatUsername为对方用户或者群聊的id，后文皆是如此
        EMMessage message = EMMessage.createTxtSendMessage(msg, toChatUsername);
        //发送消息
        EMClient.getInstance().chatManager().sendMessage(message);


    }

    //保存数据
    public static void saveMsgtoDB(Context context, String idOne, String idTwo, final String msg, final int count) {

        myApplication myapplication = (myApplication) context.getApplicationContext();
        final _User myuser = myapplication.getCurrentUser();
        String friendid = null;
        if (myuser.getObjectId() != null && myuser.getObjectId().equals(idOne)) {
            friendid = idTwo;
        } else {
            friendid = idOne;
        }

        BmobQuery<_User> bmobQuery = new BmobQuery<>();
        bmobQuery.getObject(friendid, new QueryListener<_User>() {
            @Override
            public void done(_User user, BmobException e) {
                if (user != null) {
                  /*  //保存到本地
                    Message message = new Message();
                    message.setFriendId(user.getObjectId());
                    message.setImageUri(user.getHeadurl());
                    message.setOwnid(myuser.getObjectId());
                    message.setMsg(msg);
                    message.setDate(new Date());
                    message.setState("1");
                    message.setCount(count);
                    message.setFriendName(user.getUsername());
                    //查询数据库
                    List<Message> list = DataSupport.where("friendId = ? and ownid = ?", user.getObjectId(),myuser.getObjectId()).find(Message.class);
                    if(list == null|| list.size() == 0){
                        Log.i("kk","添加");
                        message.setUuid(UUID.randomUUID().toString());
                        message.save();
                    }else {
                        Log.i("hh","修改");
                        Log.i("uuid",list.get(0).getUuid());
                        message.setUuid(list.get(0).getUuid());
                        //更新数据
                        message.updateAll("uuid = ?",list.get(0).getUuid());
                    }*/

                    //保存到云数据库
                    final Messageforbmob msgbmob = new Messageforbmob();
                    msgbmob.setFriendId(user.getObjectId());
                    msgbmob.setImageUri(user.getHeadurl());
                    msgbmob.setOwnid(myuser.getObjectId());
                    msgbmob.setMsg(msg);
                    msgbmob.setDate(new Date());
                    msgbmob.setState("1");
                    msgbmob.setCount(count);
                    msgbmob.setFriendName(user.getUsername());

                    BmobQuery<Messageforbmob> query = new BmobQuery<>();
                    query.addWhereEqualTo("friendId",user.getObjectId());
                    query.addWhereEqualTo("ownid",myuser.getObjectId());

                    query.findObjects(new FindListener<Messageforbmob>() {
                        @Override
                        public void done(List<Messageforbmob> list, BmobException e) {
                            Log.i("kk","***"+list.size());
                            if(list == null|| list.size() == 0){
                                //保存数据库
                                msgbmob.save(new SaveListener<String>() {
                                    @Override
                                    public void done(String s, BmobException e) {
                                        if( e==null ){
                                            Log.i("save","成功");
                                        }else {
                                            Log.i("save",e.getMessage());
                                        }
                                    }
                                });
                            }else if(list.size()==1){
                                //更新数据库
                                msgbmob.update(list.get(0).getObjectId(), new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        Log.i("update","成功");
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });

    }


}
