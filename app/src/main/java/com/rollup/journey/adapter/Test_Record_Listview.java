package com.rollup.journey.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rollup.journey.R;
import com.rollup.journey.utils.BlueProfile;
import com.rollup.journey.utils.DbEngine;

import java.util.List;

/**
 * Created by zq on 2016/12/23.
 */

public class Test_Record_Listview extends BaseAdapter {

    private Context mContext;
    private List<String> linkRecord;
    private final String success;
    private final String fail;

    public Test_Record_Listview(Context context,List<String> list){
        success = context.getResources().getString(R.string.str_main_success);
        fail = context.getResources().getString(R.string.str_main_fail);
        mContext = context;
        linkRecord = list;
    }
    @Override
    public int getCount() {
        return linkRecord.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView == null){
            viewHolder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.test_link_record_item,null);
            viewHolder.time = (TextView) convertView.findViewById(R.id.main1_test_record_item_time);
            viewHolder.type = (TextView) convertView.findViewById(R.id.main1_test_record_item_type);
            viewHolder.state = (TextView) convertView.findViewById(R.id.main1_test_record_item_state);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String s = linkRecord.get(linkRecord.size()-position-1);
        String[] split = s.split(BlueProfile.SPLIT);
        viewHolder.time.setText(split[0]);
        viewHolder.type.setText(split[1]);
        if (split[2].equals("0")){
            viewHolder.state.setText(fail);
        }else{
            viewHolder.state.setText(success);
        }
        return convertView;
    }

    public static class ViewHolder{
        TextView time;
        TextView type;
        TextView state;
    }
}
