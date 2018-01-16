package com.example.instant_deliver.identifyView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by King on 2017/5/4.
 */

public class LineView extends EditText {
    private Paint paint;

    public LineView(Context context,AttributeSet attrs) {
        super(context);
        //设置画笔的属性  
      paint=new Paint();
      paint.setStyle(Paint.Style.STROKE);
      //可以自定义画笔的颜色，我这里设置成黑色  
      paint.setColor(Color.BLACK);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLine(0,this.getHeight()-2,this.getWidth()-2,this.getHeight()-2,paint);
    }
}
