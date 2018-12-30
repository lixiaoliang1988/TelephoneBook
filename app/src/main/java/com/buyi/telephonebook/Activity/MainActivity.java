package com.buyi.telephonebook.Activity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;


import com.buyi.telephonebook.ContractBean;
import com.buyi.telephonebook.R;
import com.buyi.telephonebook.TelephoneAdapter;
import com.buyi.telephonebook.Utils.ExcelUtils;
import com.buyi.telephonebook.View.ImportAndExportDialog;
import com.buyimingyue.framework.Utils.IntentUtils;
import com.buyimingyue.framework.Utils.LogUtils;
import com.buyimingyue.framework.Utils.StringUtils;
import com.buyimingyue.framework.Utils.ToastUtil;
import com.buyimingyue.mymodule.View.Titlebar;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener ,Handler.Callback{
    private String[] permissions = {Manifest.permission.CALL_PHONE,Manifest.permission.READ_CONTACTS,Manifest.permission.WRITE_CONTACTS,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE};
    private TelephoneAdapter adapter;
    private ListView lv;
    private List<ContractBean> contactList;
    private List<ContractBean> excelContactList;
    private Handler mHandler;
    private Titlebar titlebar;
    private ImportAndExportDialog dialog;
    private String importPath = "/sdcard/temp/telephone.xls";
    private String exportPath = "/sdcard/temp/telephone.xls";
    public final int FILE_SELECT_CODE = 0x031;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData(savedInstanceState);
        logicOperate();
    }

protected void initData(Bundle bundle) {
        findViewById(R.id.tv).setOnClickListener(this);
        findViewById(R.id.excel_tv).setOnClickListener(this);
        contactList = new ArrayList<>();
        excelContactList = new ArrayList<>();
        lv = (ListView) findViewById(R.id.phone_lv);
        titlebar = (Titlebar) findViewById(R.id.title_bar);
        adapter = new TelephoneAdapter(this);
        lv.setAdapter(adapter);
        mHandler = new Handler(this);
        titlebar.setBtn_backVisibility(View.INVISIBLE);
        titlebar.setTitle("电话簿");
        titlebar.setOther("导入",true);
        titlebar.setTextColor(R.color.white);
        titlebar.setOtherOnclick(this);
}

    protected void logicOperate() {
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("name",(ContractBean)adapter.getItem(position));
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
                if (contactList==null||contactList.size()==0) {
                    PackageManager pm = getPackageManager();
                    if (pm.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, this.getPackageName()) == PackageManager.PERMISSION_GRANTED) {
                        readContacts();
                        mHandler.sendEmptyMessage(5);
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                            this.requestPermissions(permissions, 10);
                    }
                }else
                    adapter.setContents(contactList);
                break;
            case R.id.excel_tv:
                if (excelContactList==null||excelContactList.size()==0)
                new Thread(()-> {
                        ExcelUtils.readExcelFile(new File(importPath),excelContactList);
                        mHandler.sendEmptyMessage(1);
                    }).start();
                else
                    mHandler.sendEmptyMessage(1);
                break;
            case R.id.title_tv_other:
//                new Thread(()-> {
//                    String path = "/sdcard/temp/telephone.xls";
//                    ExcelUtils.write2ExcelFile(path,contactList);
//                    mHandler.sendEmptyMessage(2);
//                }).start();
                PackageManager pm = getPackageManager();
                if (pm.checkPermission(Manifest.permission.WRITE_CONTACTS, this.getPackageName()) == PackageManager.PERMISSION_GRANTED) {
                    if (dialog == null){
                        dialog = new ImportAndExportDialog(this);
                        dialog.importOnClickListener(importListener);
                        dialog.exportOnClickListener(exportListener);
                    }
                    dialog.show();
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                        this.requestPermissions(permissions, 10);
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
                ContractBean contractBean = new ContractBean();
                if (phoneCount>0){
                    // 获得联系人的电话号码列表
                    Cursor phonesCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID+ " = " + contactId, null,null);
                    if (phonesCursor.moveToFirst()) {
                        do {
                            // 遍历所有的电话号码
                            String phoneNumber = phonesCursor.getString(phonesCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            contractBean.phones.add(phoneNumber);
                        } while (phonesCursor.moveToNext());
                    };
                }
                contractBean.name=disPlayName;
                contactList.add(contractBean);
            }while (cursor.moveToNext());

        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i = 0; i <permissions.length ; i++) {
            if (permissions[i].equals(Manifest.permission.READ_CONTACTS))if (grantResults[i] == PackageManager.PERMISSION_GRANTED){
                readContacts();
                mHandler.sendEmptyMessage(5);
            }
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what){
            case 1:
                adapter.setContents(excelContactList);
                break;
            case 2:
                ToastUtil.showToast("finish writing in!",this);
                break;
            case 3:
                break;
            case 4:
                dialog.dismiss();
                ToastUtil.showToast("Import finished !",this);
                contactList.clear();
                readContacts();
                adapter.setContents(contactList);
                break;
            case 5:
                adapter.setContents(contactList);
                break;
            case 6:
                dialog.dismiss();
                ToastUtil.showToast("Export finished !",this);
                break;
        }

        return false;
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
        LogUtils.i("msg","rawContactId = "+rawContactId);
        //添加姓名
        values.clear();
        values.put(ContactsContract.Data.RAW_CONTACT_ID,rawContactId);
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
            getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
        }
    }
    //向xls文件写入数据

    private View.OnClickListener importListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new Thread(()->{
                excelContactList.clear();
                if (excelContactList.size()<=0)
                    ExcelUtils.readExcelFile(new File(importPath),excelContactList);
                if (excelContactList.size()<=0){
                    mHandler.sendEmptyMessage(3);
                    return;
                }
                if (contactList.size()<=0)
                    readContacts();
                Iterator iterator = excelContactList.iterator();
                while (iterator.hasNext()){
                    ContractBean bean = (ContractBean) iterator.next();
                    boolean isRepetition = false;
                    for (int i = 0; i <contactList.size(); i++) {
                        if (bean.name.equals(contactList.get(i).name)) {
                            iterator.remove();
                            isRepetition = true;
                            break;
                        }
                    }
                    if (isRepetition)
                        continue;
                    writeToContactBook(bean.name,bean.phones);
                }
                mHandler.sendEmptyMessage(4);
            }).start();
        }
    };
    private View.OnClickListener exportListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new Thread(()->{
                contactList.clear();
                if (contactList.size()<=0)
                    readContacts();
                if (contactList.size()<=0){
                    mHandler.sendEmptyMessage(3);
                    return;
                }
                excelContactList.clear();
                if (excelContactList.size()<=0)
                    ExcelUtils.readExcelFile(new File(exportPath),excelContactList);
                Iterator iterator = contactList.iterator();
                while (iterator.hasNext()){
                    ContractBean bean = (ContractBean) iterator.next();
                    boolean isRepetition = false;
                    for (int i = 0; i <excelContactList.size(); i++) {
                        if (bean.name.trim().equals(excelContactList.get(i).name.trim())) {
                            iterator.remove();
                            isRepetition = true;
                            break;
                        }
                    }
                    if (isRepetition)
                        continue;
                    LogUtils.i("msg","name:"+bean.name);
                }

                LogUtils.i("msg","size :"+contactList.size());
                ExcelUtils.write2ExcelFile(exportPath,contactList);
                mHandler.sendEmptyMessage(6);
            }).start();
        }
    };
    private void chooseFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        //intent.setType(“image/*”);//选择图片
        //intent.setType(“audio/*”); //选择音频
        //intent.setType(“video/*”); //选择视频 （mp4 3gp 是android支持的视频格式）
        //intent.setType(“video/*;image/*”);//同时选择视频和图片
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        //
        try {
            startActivityForResult(Intent.createChooser(intent, "选择文件"), FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "亲，木有文件管理器啊-_-!!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_SELECT_CODE) {
            Uri uri = data.getData();
            LogUtils.i("msg", "------->" + getRealFilePath(this,uri));
            importPath = getRealFilePath(this,uri);
        }
    }
    public  String getRealFilePath(final Context context, final Uri uri ) {
        if ( null == uri ) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if ( scheme == null )
            data = uri.getPath();
        else if ( ContentResolver.SCHEME_FILE.equals( scheme ) ) {
            data = uri.getPath();
        } else if ( ContentResolver.SCHEME_CONTENT.equals( scheme ) ) {
            Cursor cursor = context.getContentResolver().query( uri, new String[] { MediaStore.Images.ImageColumns.DATA }, null, null, null );
            if ( null != cursor ) {
                if ( cursor.moveToFirst() ) {
                    int index = cursor.getColumnIndex( MediaStore.Images.ImageColumns.DATA );
                    if ( index > -1 ) {
                        data = cursor.getString( index );
                    }
                }
                cursor.close();
            }
        }
        return data;
    }
}
