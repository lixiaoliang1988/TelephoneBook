package com.buyi.telephonebook;

import android.content.Context;
import android.media.Image;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.buyimingyue.framework.Base.MyBaseListAdapter;

/**
 * Created by dell on 2018/12/18.
 */

public class TelephoneAdapter extends MyBaseListAdapter<String,TelephoneAdapter.TelephoneView> {


    public TelephoneAdapter(Context context) {
        super(context);
    }

    @Override
    protected int getItemLayout() {
        return R.layout.telephone_item;
    }

    @Override
    protected void viewHolderOperate(TelephoneView telephoneView, int i, String s) {
        telephoneView.tv.setText(s);
    }

    @Override
    protected TelephoneView getViewHolder(View view) {
        TelephoneView telephoneView = new TelephoneView();
        telephoneView.tv = view.findViewById(R.id.item_tv);
        telephoneView.imageView = view.findViewById(R.id.item_image);
        return telephoneView;
    }

    public class TelephoneView extends MyBaseListAdapter.ViewHolder{
        TextView tv;
        ImageView imageView;
    }
}
