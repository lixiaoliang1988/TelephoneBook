package com.buyi.telephonebook.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.buyi.telephonebook.NumberAdapter;
import com.buyi.telephonebook.R;
import com.buyimingyue.framework.Base.MyBaseActivity;
import com.buyimingyue.framework.Utils.StringUtils;

import java.util.Arrays;

public class DetialActivity extends AppCompatActivity implements View.OnClickListener {
    private String name;
    private String phones;
    private TextView name_tv;
    private NumberAdapter adapter;
    private ListView lv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detial);
        initData(savedInstanceState);
        logicOperate();
    }

    protected void initData(Bundle bundle) {
        name = getIntent().getStringExtra("name");
        phones = getIntent().getStringExtra("phone");
        name_tv = (TextView) findViewById(R.id.name_tv);
        lv = (ListView) findViewById(R.id.phone_number_lv);
        adapter =  new NumberAdapter(this);
        lv.setAdapter(adapter);
        if (!StringUtils.isEmpty(phones))
            adapter.setContents(Arrays.asList(phones.split(",")));
    }


    protected void logicOperate() {
        name_tv.setText(name);
    }

    @Override
    public void onClick(View v) {

    }
}
