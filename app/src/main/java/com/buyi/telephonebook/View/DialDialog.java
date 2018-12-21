package com.buyi.telephonebook.View;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.view.View;
import android.widget.TextView;

import com.buyi.telephonebook.R;

/**
 * Created by dell on 2018/12/21.
 */

public class DialDialog extends Dialog {
    private Context context;
    private TextView tv;
    public DialDialog(@NonNull Context context) {
        this(context,android.R.style.Theme_Translucent_NoTitleBar);
    }

    public DialDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        this.context  =  context;
        initView(context);
    }
    private void initView(Context context){
        setContentView(R.layout.dialog_dial);
        setCanceledOnTouchOutside(false);
        setCancelable(true);
        tv = findViewById(R.id.dial_number_tv);
    }
    public void setOnClickListener(View.OnClickListener listener){
        findViewById(R.id.dial_btn).setOnClickListener(listener);
        findViewById(R.id.send_btn).setOnClickListener(listener);
        findViewById(R.id.dial_cancel_btn).setOnClickListener(listener);
    }
    public void setMessage(String content){
        if (null != tv)
            tv.setText(content);
    }
    public String getMessage(){
        return tv==null?null:tv.getText().toString();
    }
}
