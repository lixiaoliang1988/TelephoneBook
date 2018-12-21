package com.buyi.telephonebook.Activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.buyi.telephonebook.ContractBean;
import com.buyi.telephonebook.NumberAdapter;
import com.buyi.telephonebook.R;
import com.buyi.telephonebook.View.DialDialog;
import com.buyimingyue.framework.Base.MyBaseActivity;
import com.buyimingyue.framework.Utils.LogUtils;
import com.buyimingyue.framework.Utils.StringUtils;
import com.buyimingyue.mymodule.View.Titlebar;

import java.util.Arrays;
import java.util.Random;

public class DetialActivity extends AppCompatActivity implements View.OnClickListener {
    private ContractBean name;
    private String phones;
    private TextView name_tv;
    private ImageView imageView;
    private NumberAdapter adapter;
    private ListView lv;
    private DialDialog dialog;
    private Titlebar titlebar;
    private int [] bg_mipmaps = {R.mipmap.a,R.mipmap.b,R.mipmap.c,R.mipmap.d,R.mipmap.e,R.mipmap.f,
            R.mipmap.g,R.mipmap.h,R.mipmap.i,R.mipmap.j,R.mipmap.k,R.mipmap.l};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detial);
        initData(savedInstanceState);
        logicOperate();
    }

    protected void initData(Bundle bundle) {
        name = (ContractBean) getIntent().getSerializableExtra("name");
        imageView = (ImageView) findViewById(R.id.bg_image);
        name_tv = (TextView) findViewById(R.id.name_tv);
        lv = (ListView) findViewById(R.id.phone_number_lv);
        adapter =  new NumberAdapter(this);
        lv.setAdapter(adapter);
        if (null != name)
            adapter.setContents(name.phones);
        int a = (int) (Math.random()*bg_mipmaps.length);
        imageView.setImageResource(bg_mipmaps[a]);
        titlebar = (Titlebar) findViewById(R.id.title_bar);
        titlebar.setTitle("");
        titlebar.setOther("编辑",true);
    }


    protected void logicOperate() {
        name_tv.setText(name.name);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (dialog == null){
                    dialog = new DialDialog(DetialActivity.this);
                    dialog.setOnClickListener(DetialActivity.this);
                }
                try {
                    dialog.setMessage((String) adapter.getItem(position));
                }catch (Exception e){
                    dialog.setMessage("");
                }
                dialog.show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.dial_btn:
                dialTelephone(clickButton());
                break;
            case R.id.send_btn:
                sendSMS(clickButton());
                break;
            case R.id.dial_cancel_btn:
                dialog.dismiss();
                break;
        }
    }
    //点击对话框
    private String clickButton(){
        dialog.dismiss();
        String content = dialog.getMessage();
        if (StringUtils.isEmpty(content))
            return content;
        content = content.replaceAll("\\s*", "");
        if (StringUtils.isEmpty(content))
            return content;
        return content;
    }
    //打电话
    private void dialTelephone(String content){
        if (StringUtils.isEmpty(content))
            return;
        // 2、使用Uri.parse(String a)创建Uri。
         Uri uri = Uri.parse("tel:"+content);
        //3、创建打电话的意图。
        Intent intent = new Intent(Intent.ACTION_DIAL, uri);
        if (getPackageManager().checkPermission(Manifest.permission.CALL_PHONE,this.getPackageName())== PackageManager.PERMISSION_GRANTED)
            //4、启动系统打电话页面。
            startActivity(intent);
        else {
            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
            requestPermissions(new String [] {Manifest.permission.CALL_PHONE},0x123);
        }
    }
    //发短信
    private void sendSMS(String phoneNumber){
        if (StringUtils.isEmpty(phoneNumber))
            return;
        //1、创建Uri，设置行为和号码
        Uri uri2 = Uri.parse("smsto:"+phoneNumber);
        //2、创建意图.
        Intent intentMessage = new Intent(Intent.ACTION_VIEW,uri2);
        //3、打开系统短信界面，号码已经填写，只需填写要发送
        startActivity(intentMessage);
    }
}
