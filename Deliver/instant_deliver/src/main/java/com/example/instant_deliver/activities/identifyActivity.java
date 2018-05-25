package com.example.instant_deliver.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instant_deliver.R;
import com.example.instant_deliver.beans._User;
import com.example.instant_deliver.beans.identify;
import com.example.instant_deliver.identifyView.Topbar;
import com.example.instant_deliver.services.myApplication;
import com.example.instant_deliver.tools.StringRegexUtils;
import com.example.instant_deliver.tools.topStatusTool;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

public class identifyActivity extends Activity implements View.OnClickListener{

    private EditText identifyName;
    private EditText identifyID;
    private Button   submit;
    private Topbar  identifyTopbar;
    private _User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identify);
        //解决沉浸式状态栏问题
        topStatusTool.applyKitKatTranslucency(this,R.color.deepskyblue);
        init();
    }

    private void init(){
        identifyID = (EditText) findViewById(R.id.identifyID);
        identifyName = (EditText) findViewById(R.id.identifyName);
        submit = (Button) findViewById(R.id.identify_submit);
        submit.setOnClickListener(this);
        identifyTopbar = (Topbar) findViewById(R.id.identify_Topbar);
        user = new  myApplication().getCurrentUser();

        identifyTopbar.setOnTopbarClickListener(new Topbar.topbarClickListener() {
            @Override
            public void leftclick() {
                finish();
            }

            @Override
            public void rightclick() {

            }
        });

    }

    @Override
    public void onClick(View view) {
       if(identifyID.getText()!=null&&identifyName.getText()!=null){
           final String name = identifyName.getText().toString();
           final String ID = identifyID.getText().toString();
           Log.i("ss",""+StringRegexUtils.Validate(ID,StringRegexUtils.ID_card_regexp));
           if(name.length()>0&&ID.length()>0&& StringRegexUtils.Validate(ID,StringRegexUtils.ID_card_regexp)&&StringRegexUtils.Validate(name,StringRegexUtils.china_regexp)){
              final identify identify = new identify();
               BmobQuery<identify> query =new BmobQuery<>();
               query.addWhereEqualTo("userid",user.getObjectId());
               query.findObjects(new FindListener<com.example.instant_deliver.beans.identify>() {
                   @Override
                   public void done(List<identify> list, BmobException e) {
                       if(list==null || list.size()==0){
                           identify.setUserid(user.getObjectId());
                           identify.setUserName(name);
                           identify.setIdentifyID(ID);
                           identify.save(new SaveListener<String>() {
                               @Override
                               public void done(String s, BmobException e) {
                                   if(e==null){
                                       Toast.makeText(getApplicationContext(),"保存成功",Toast.LENGTH_SHORT).show();
                                       Intent intent =new Intent();
                                       intent.putExtra("identify","identifySuccess");
                                       setResult(5,intent);
                                       finish();
                                   }else {
                                       Log.i("sd","保存失败");
                                   }
                               }
                           });
                       }
                   }
               });
           }else {
               Toast.makeText(getApplicationContext(),"姓名或者身份证不正确",Toast.LENGTH_SHORT).show();
           }
       }
    }

}
