package com.example.instant_deliver.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import com.example.instant_deliver.R;
import com.example.instant_deliver.tools.topStatusTool;
import com.hyphenate.easeui.ui.EaseChatFragment;

public class ChatActivity extends FragmentActivity {
    private String uuid;
    private int RESULT_CODE = 10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        //解决沉浸式状态栏问题
        topStatusTool.applyKitKatTranslucency(this,R.color.deepskyblue);

        //uuid = getIntent().getStringExtra("msgUuid");
        EaseChatFragment easeChatFragment = new EaseChatFragment();  //环信聊天界面
        easeChatFragment.setArguments(getIntent().getExtras()); //需要的参数
        getSupportFragmentManager().beginTransaction().add(R.id.layout_myChat,easeChatFragment).commit();  //Fragment切换
    }

    @Override
    public void finish() {
        super.finish();
        /*Intent intent = new Intent();
        intent.putExtra("uuid",uuid );
        setResult(RESULT_CODE,intent);*/
    }
}
