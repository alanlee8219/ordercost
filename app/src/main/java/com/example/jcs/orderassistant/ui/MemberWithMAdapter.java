package com.example.jcs.orderassistant.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
    private int index;

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
        index = -1;
        this.mInflater = LayoutInflater.from(context);
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        MemberInfoWithId info = getItem(position); // 获取当前项的Member实例

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
                    MemberInfoWithId info = (MemberInfoWithId) finalViewHolder.myCheckText.getTag();
                    if (info.getSelected() == true) {
                        info.setSelected(false);
                    } else {
                        info.setSelected(true);
                    }
                }
            });

            convertView.setTag(holder);
            holder.myCheckText.setTag(info);
            holder.mEditText.setTag(info);

        }else{
            holder = (ViewHolder)convertView.getTag();
            holder.myCheckText.setTag(info);
            holder.mEditText.setTag(info);
        }

        if(Float.compare(list.get(position).getEach(),0.0f) != 0 ) {
            holder.mEditText.setText(Float.toString(list.get(position).getEach()));
        }

        holder.mEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus == false) {
                    //final int p1 = v.getId();
                    //MemberInfoWithId info = (MemberInfoWithId) finalViewHolder.mEditText.getTag();
                    MemberInfoWithId info = list.get(position);
                    //EditText edit = (EditText) v.findViewById(R.id.sep_money);
                    EditText edit = (EditText) v;
                    String each = edit.getText().toString();
                    if (!each.isEmpty()) {
                        info.setEach(Float.valueOf(each));
                        //info.setEach(Integer.valueOf(each));
                    }
                    //index = -1;
                }else{
                    //index = position;
                    //index = -1;
                }
            }
        });

        holder.mEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    index = position;
                }
                return false;
            }
        });

        holder.mEditText.clearFocus();
       if (index != -1 && index == position) {
            // 如果当前的行下标和点击事件中保存的index一致，手动为EditText设置焦点。
            holder.mEditText.requestFocus();
           holder.mEditText.setSelection( holder.mEditText.getText().length());
        }

       // holder.mEditText.setText(Integer.toString(info.getEach()));
        holder.myCheckText.setChecked(info.getSelected());
        holder.myCheckText.setText(info.getName());

        return convertView;
    }
}
