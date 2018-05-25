package com.example.instant_deliver.activities;

import android.app.Activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.instant_deliver.R;
import com.example.instant_deliver.beans.feedback;
import com.example.instant_deliver.identifyView.Topbar;
import com.example.instant_deliver.services.myApplication;
import com.example.instant_deliver.tools.topStatusTool;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class feedbackActivity extends Activity {
    private EditText message;
    private Button button;
    private Topbar topbar;
    private myApplication myapplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        //解决沉浸式状态栏问题
        topStatusTool.applyKitKatTranslucency(this,R.color.deepskyblue);

        init();
        listener();
    }

    private void init(){
        topbar = (Topbar) findViewById(R.id.feedback_topbar);
        message = (EditText) findViewById(R.id.feedback_message);
        button = (Button) findViewById(R.id.feedback_submit);
        myapplication = new myApplication();
    }

    private void listener(){
        topbar.setOnTopbarClickListener(new Topbar.topbarClickListener() {
            @Override
            public void leftclick() {
                finish();
            }

            @Override
            public void rightclick() {

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = message.getText().toString();
                feedback feedback =new feedback();
                feedback.setUserId(myapplication.getCurrentUser().getObjectId());
                feedback.setMessage(msg);
                if(msg.length()>0){
                    feedback.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if(e!=null){
                                Toast.makeText(getApplicationContext(),"提交成功",Toast.LENGTH_LONG).show();
                                message.setText("");
                            }else {
                                Toast.makeText(getApplicationContext(),"提交失败，"+e.getMessage(),Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
