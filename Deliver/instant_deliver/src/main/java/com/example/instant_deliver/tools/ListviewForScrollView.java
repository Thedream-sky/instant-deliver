package com.example.instant_deliver.tools;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by King on 2017/5/24.
 * 自定义listview适用于scrollowView控件
 * 此方法的缺点是默认将listview作为首项，需要手动把ScrollView滚动至最顶端：scrollowView.smoothScrollTo(0, 0);
 */

public class ListviewForScrollView extends ListView {
    public ListviewForScrollView(Context context) {
        super(context);
    }

    public ListviewForScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ListviewForScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    //重写该方法
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
         MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
