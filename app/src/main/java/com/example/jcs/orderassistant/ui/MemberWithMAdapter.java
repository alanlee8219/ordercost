package com.example.jcs.orderassistant.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.example.jcs.orderassistant.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by JCS on 2015/10/10.
 */

public class MemberWithMAdapter extends ArrayAdapter<MemberInfoWithId> {

    private int resourceId;

    private  class ViewHolder
    {
        private  CheckedTextView myCheckText;
        private  EditText mEditText;
    };


    private List<MemberInfoWithId> list;
    private LayoutInflater mInflater;

    public MemberWithMAdapter(Context context, int textViewResourceId,
                         List<MemberInfoWithId> objects) {
        super(context, textViewResourceId, objects);
        list = objects;
        resourceId = textViewResourceId;
        this.mInflater = LayoutInflater.from(context);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final MemberInfoWithId info = getItem(position); // 获取当前项的Member实例

        if (convertView == null){
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.select_member_withm_item, null);
            holder.myCheckText = (CheckedTextView)convertView.findViewById(R.id.select_member_withm);
            holder.mEditText = (EditText)convertView.findViewById(R.id.sep_money);

            final ViewHolder finalViewHolder = holder;
            holder.myCheckText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finalViewHolder.myCheckText.toggle();
                    if (info.getSelected() == true) {
                        info.setSelected(false);
                    } else {
                        info.setSelected(true);
                    }
                }
            });
            holder.mEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus == false) {
                        EditText edit = (EditText) v.findViewById(R.id.sep_money);
                        String each = edit.getText().toString();
                        if (!each.isEmpty()) {
                            info.setEach(Float.valueOf(each));
                            //info.setEach(Integer.valueOf(each));
                        }
                    }
                }
            });
            convertView.setTag(holder);

        }else{
            holder = (ViewHolder)convertView.getTag();
        }

        Float data = Float.valueOf(info.getEach());
        if (data.compareTo(0.0f) == 0) {
            holder.mEditText.setHint(Float.toString(info.getEach()));
        } else {
            holder.mEditText.setText(Float.toString(info.getEach()));
        }
       // holder.mEditText.setText(Integer.toString(info.getEach()));
        holder.myCheckText.setChecked(info.getSelected());
        holder.myCheckText.setText(info.getName());

        return convertView;
    }
}
