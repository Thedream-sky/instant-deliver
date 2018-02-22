package com.example.instant_deliver.tools;

import android.util.Log;

import com.example.instant_deliver.beans.Friends;
import com.example.instant_deliver.beans.Order;
import com.example.instant_deliver.beans._User;
import com.example.instant_deliver.beans.orderState;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;


/**
 * Created by King on 2018/1/31.
 */

public class usersManager {
    //获取好友列表
    public static List<String> getAllfriends() throws HyphenateException {
        return EMClient.getInstance().contactManager().getAllContactsFromServer();
    }

    //添加好友关系
    public static void addFriend(String idOne, String idTwo){
        final String toadd = idTwo;
        Log.i("hash",""+idOne.hashCode());
        Log.i("hash",""+idTwo.hashCode());
        //默认关系表里前者的哈希值小于或等于后者,必须遵守
        if(idOne.hashCode()>idTwo.hashCode()){
            String temp = idOne;
            idOne = idTwo;
            idTwo = temp;
        }
        BmobQuery<Friends> query =new BmobQuery<>();
        query.addWhereEqualTo("idOne",idOne);
        query.addWhereEqualTo("idTwo",idTwo);
        //只有变量结束交换完，值不再变化时才能使用final
        final String finalIdTwo = idTwo;
        final String finalIdOne = idOne;
        query.findObjects(new FindListener<Friends>() {
            @Override
            public void done(List<Friends> list, BmobException e) {
                //表为空时添加条件
                if(list == null){
                    Friends friends = new Friends();
                    friends.setIdOne(finalIdOne);
                    friends.setIdTwo(finalIdTwo);
                    friends.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            Log.i("add","添加成功");
                            //参数为要添加的好友的username和添加理由
                            try {
                                EMClient.getInstance().contactManager().addContact(toadd, "");
                            } catch (HyphenateException e1) {
                                e1.printStackTrace();
                            }
                        }
                    });
                }
                //表非空时添加条件
                if(list!=null&&list.size()==0){
                    Friends friends = new Friends();
                    friends.setIdOne(finalIdOne);
                    friends.setIdTwo(finalIdTwo);
                    friends.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            Log.i("add","添加成功");
                            //参数为要添加的好友的username和添加理由
                            try {
                                EMClient.getInstance().contactManager().addContact(toadd, "");
                            } catch (HyphenateException e1) {
                                e1.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
    }

    //查询订单是否没有订单纠纷，返回为true则可以解除好友关系，false则不能解除好友关系
   /* private static boolean isorderFinished(_User uOne, _User uTwo){

    }*/


}
