package com.buyi.telephonebook.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.print.PageRange;
import android.util.AttributeSet;
import android.view.View;

import com.buyi.telephonebook.R;
import com.buyimingyue.framework.Utils.LogUtils;

public class MyProgressView extends View {
    private int parts = 5;
    private int begin = 10;
    private int width = 0;
    private int height =0;
    private Paint mPaint;
    private int index = 0;
    private boolean ischange = false,isMove = false,isLeft = false;
    private int [] colors= {Color.RED,Color.WHITE};
    public MyProgressView(Context context) {
        this(context,null);
    }

    public MyProgressView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MyProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
    }



    @Override
    protected void onDraw(Canvas canvas) {
        int hole = 0;
        int i = 0;
        while (hole < width) {
            Rect rect = new Rect();
            rect.left = hole;
            rect.top = 0;
            rect.bottom = height;
            if (i==0) {
                rect.right = rect.left + begin;
            }else
                rect.right = rect.left+width/parts;
            mPaint.setColor(colors[index]);
            if (index==0)
                index = 1;
            else
                index = 0;
            canvas.drawRect(rect,mPaint);
            i++;
            hole = rect.right;
            if (hole+width/parts>=width){
                mPaint.setColor(colors[index]);
                canvas.drawRect(hole,0,width,height,mPaint);
                hole = width;

            }
        }
        if (isMove)
            goMove();
    }
    private void goMove(){
        if (isLeft) {
            begin -= 5;
            if (begin < 0) {
                begin = width / parts;
                ischange = !ischange;
            }
        } else {
            begin+=5;
            if (begin>=width/parts){
                begin%=width/parts;
                ischange = !ischange;
            }
        }
        if (ischange)
            index = 1;
        else
            index = 0;
        postInvalidateDelayed(50);
    }
    public void startMoveToLeft(){
        isMove = true;
        isLeft = true;
        postInvalidate();
    }
    public void startMoveToRight(){
        isMove = true;
        isLeft = false;
        postInvalidate();
    }
    public void stop(){
        isMove = false;
        isLeft = false;
    }
}
