package com.example.instant_deliver.tools;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.exceptions.HyphenateException;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by King on 2018/5/7.
 */

public class MessageTool {

    //转换消息格式
    public static String msgTransform(EMMessage emsg) throws HyphenateException, JSONException {
        EMMessage.Type emsgType=emsg.getType();
        String msg = null;
       if(emsgType == EMMessage.Type.IMAGE){
            msg = "对方给你发来一张图片";
        }else if(emsgType == EMMessage.Type.VOICE){
            msg = "对方给你发来一条语音消息";
        }else if(emsgType == EMMessage.Type.VIDEO){
            msg = "对方给你发来一段视频";
        }else if(emsgType == EMMessage.Type.LOCATION){
            msg = "对方给你发来地图消息";
        }else if(emsgType == EMMessage.Type.FILE){
            msg = "对方给你发来文件";
        }else {
            //去掉两边的双引号
            String temp = emsg.getBody().toString() .replace("\"","").replace("\"","");
            msg = temp.substring(4);
        }
       return msg;
    }

}
