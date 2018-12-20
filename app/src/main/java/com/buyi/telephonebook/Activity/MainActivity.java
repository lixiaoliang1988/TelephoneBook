package com.buyi.telephonebook.Activity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;


import com.buyi.telephonebook.R;
import com.buyi.telephonebook.TelephoneAdapter;
import com.buyimingyue.framework.Utils.LogUtils;
import com.buyimingyue.framework.Utils.StringUtils;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private String[] permissions = {Manifest.permission.CALL_PHONE,Manifest.permission.READ_CONTACTS};
    private List<String> phones = new ArrayList<>();
    private List<String> names = new ArrayList<>();
    private TelephoneAdapter adapter;
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData(savedInstanceState);
        logicOperate();
    }

    protected void initData(Bundle bundle) {
        findViewById(R.id.tv).setOnClickListener(this);
        lv = (ListView) findViewById(R.id.phone_lv);
        adapter = new TelephoneAdapter(this);
        lv.setAdapter(adapter);
        adapter.setContents(names);

    }

    protected void logicOperate() {
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString("name",names.get(position));
                bundle.putString("phone",phones.get(position));
                Intent intent = new Intent(MainActivity.this,DetialActivity.class);
                intent.putExtras(bundle);
                MainActivity.this.startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv:
                PackageManager pm = getPackageManager();
                if (pm.checkPermission(Manifest.permission.READ_CONTACTS,this.getPackageName())==PackageManager.PERMISSION_GRANTED)
                    readContacts();
                else {
                    if ( Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
                        this.requestPermissions(permissions,10);
                }
                break;
        }
    }
    private void readContacts(){
        ContentResolver contentResolver = getContentResolver();
        //获得所有的联系人
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null,null, null);
        //遍历
        if (cursor.moveToFirst()){
            int idColumn = cursor.getColumnIndex(ContactsContract.Contacts._ID);
            int displayNameColumn = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            do{
                // 获得联系人的ID号
                String contactId = cursor.getString(idColumn);
                // 获得联系人姓名
                String disPlayName = cursor.getString(displayNameColumn);
                // 查看该联系人有多少个电话号码。如果没有这返回值为0
                int phoneCount = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                LogUtils.i("msg","phoneCount = "+phoneCount);
                if (phoneCount>0){
                    StringBuffer sb = new StringBuffer();
                    // 获得联系人的电话号码列表
                    Cursor phonesCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID+ " = " + contactId, null,null);
                    if (phonesCursor.moveToFirst()) {
                        do {
                            // 遍历所有的电话号码
                            String phoneNumber = phonesCursor.getString(phonesCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            sb.append(phoneNumber+",");
                        } while (phonesCursor.moveToNext());
                    }
                    String result = sb.toString();
                    if (StringUtils.isEmpty(result)) {
                        phones.add("");
                        continue;
                    }
                    if (result.contains(","))
                       result =  result.substring(0,result.length()-1);
                    phones.add(result);
                    LogUtils.i("msg","联系人姓名：" + disPlayName+"\n联系人电话：" + result);;
                }
                 names.add(disPlayName);
            }while (cursor.moveToNext());
            adapter.notifyDataSetChanged();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i = 0; i <permissions.length ; i++) {
            if (permissions[i].equals(Manifest.permission.READ_CONTACTS))if (grantResults[i] == PackageManager.PERMISSION_GRANTED)readContacts();
        }
    }
}
