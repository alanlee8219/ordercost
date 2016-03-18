package com.example.jcs.orderassistant.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.example.jcs.orderassistant.R;

import java.util.List;

/**
 * Created by JCS on 2016/2/24.
 */
public class DealInfoAdapter extends ArrayAdapter<DealInfo> {

    private int resourceId;
    private  List<DealInfo> list;

    public DealInfoAdapter(Context context, int textViewResourceId,
                         List<DealInfo> objects) {
        super(context, textViewResourceId, objects);
        list = objects;
        resourceId = textViewResourceId;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public DealInfo getItem(int position) {
        return list.get(position);
    }

    public void remove(int position) {
        list.remove(position);
        notifyDataSetChanged();
    }

    public void insert(int position, DealInfo item) {
        list.add(position, item);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DealInfo info = getItem(position); // 获取当前项的Member实例
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
        final TextView dining = (TextView) view.findViewById(R.id.diningText);
        dining.setText(info.getDining());
        final TextView date = (TextView) view.findViewById(R.id.dateText);
        date.setText(info.getDate());
        final TextView money = (TextView) view.findViewById(R.id.moneyText);
        String mm = Integer.toString(info.getMoney());
        mm+=".00";
        money.setText(mm);
        final TextView detail = (TextView) view.findViewById(R.id.detailText);
        detail.setText(info.getDetail());
        return view;
    }
}