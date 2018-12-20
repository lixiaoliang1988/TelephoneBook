package com.buyi.telephonebook;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.buyimingyue.framework.Base.MyBaseListAdapter;

/**
 * Created by dell on 2018/12/19.
 */

public class NumberAdapter extends MyBaseListAdapter<String,NumberAdapter.NumberViewHolder> {

    public NumberAdapter(Context context) {
        super(context);
    }

    @Override
    protected int getItemLayout() {
        return R.layout.number_item;
    }

    @Override
    protected void viewHolderOperate(NumberViewHolder numberViewHolder, int i, String s) {
        numberViewHolder.number_tv.setText(s);
    }

    @Override
    protected NumberViewHolder getViewHolder(View view) {
        NumberViewHolder viewHolder = new NumberViewHolder();
        viewHolder.number_tv = findViewNoCast(R.id.number_item_tv);
        return viewHolder;
    }

    public class NumberViewHolder extends MyBaseListAdapter.ViewHolder{
        TextView number_tv;
    }
}
