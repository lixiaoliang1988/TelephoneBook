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

public class TelephoneAdapter extends MyBaseListAdapter<ContractBean,TelephoneAdapter.TelephoneView> {
    private int [] bg_mipmaps = {R.mipmap.a,R.mipmap.b,R.mipmap.c,R.mipmap.d,R.mipmap.e,R.mipmap.f,
            R.mipmap.g,R.mipmap.h,R.mipmap.i,R.mipmap.j,R.mipmap.k,R.mipmap.l};

    public TelephoneAdapter(Context context) {
        super(context);
    }

    @Override
    protected int getItemLayout() {
        return R.layout.telephone_item;
    }

    @Override
    protected void viewHolderOperate(TelephoneView telephoneView, int i, ContractBean s) {
        int a = (int) (Math.random()*bg_mipmaps.length);
        telephoneView.imageView.setImageResource(bg_mipmaps[a]);
        telephoneView.tv.setText(s.name+":"+(s.phones==null||s.phones.size()<1?"":s.phones.get(0)));
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
