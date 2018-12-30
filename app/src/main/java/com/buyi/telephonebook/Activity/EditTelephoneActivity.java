package com.buyi.telephonebook.Activity;

import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.buyi.telephonebook.R;
import com.buyimingyue.framework.Utils.StringUtils;
import com.buyimingyue.framework.Utils.ToastUtil;

import java.util.List;

public class EditTelephoneActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_telephone);
    }
    //向通讯录里写入联系人
    private void writeToContactBook(String name ,List<String> phones){
        if (StringUtils.isEmpty(name)||phones ==null||phones.size()<1) {
            ToastUtil.showToast("Nothing to add !",this);
            return;
        }
        ContentValues values = new ContentValues();
        /*
         * 首先向RawContacts.CONTENT_URI执行一个空值插入，目的是获得系统返回的rawContactId
         */
        Uri rawContactUri = getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI,values);
        long rawContactId = ContentUris.parseId(rawContactUri);
        //添加姓名
        values.clear();
        values.put(ContactsContract.Data.CONTACT_ID,rawContactId);
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE); //内容类型
        values.put( ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, name);
        getContentResolver().insert(ContactsContract.Data.CONTENT_URI,values);
        //往data表里写入电话数据
        for (int i = 0; i < phones.size(); i++) {
            values.clear();
            values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
            values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
            values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, phones.get(i));
            values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
            getContentResolver().insert(android.provider.ContactsContract.Data.CONTENT_URI, values);
        }
    }
    //向xls文件写入数据


}
