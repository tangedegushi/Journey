package com.rollup.journey.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rollup.journey.R;
import com.rollup.journey.bean.MessageInfo;
import com.rollup.journey.utils.DbEngine;

import java.util.List;

/**
 * Created by zq on 2016/12/23.
 */

public class Msg_had_send extends BaseAdapter {

    private Context mContext;
    private final List<MessageInfo> allSendMessage;
    private final String allSuccess;
    private final String send;
    private final String people;

    public Msg_had_send(Context context){
        allSuccess = context.getResources().getString(R.string.str_send_message_state_success);
        send = context.getResources().getString(R.string.str_send);
        people = context.getResources().getString(R.string.str_people);
        mContext = context;
        allSendMessage = DbEngine.getInstance(context).getAllSendMessage();
    }
    @Override
    public int getCount() {
        return allSendMessage.size();
    }

    @Override
    public Object getItem(int position) {
        return allSendMessage.get(allSendMessage.size()-position-1);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.msg_send_item,null);
            viewHolder.content = (TextView) convertView.findViewById(R.id.main2_msg_send_item_content);
            viewHolder.time = (TextView) convertView.findViewById(R.id.main2_msg_send_item_time);
            viewHolder.num = (TextView) convertView.findViewById(R.id.main2_msg_send_item_num);
            viewHolder.state = (TextView) convertView.findViewById(R.id.main2_msg_send_item_state);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        MessageInfo messageInfo = allSendMessage.get(allSendMessage.size()-position-1);
        viewHolder.content.setText(messageInfo.getMessage());
        String sub = messageInfo.getTime().substring(5, 16);
        sub = sub.replace("-","月");
        sub = sub.replace(" ","日 ");
        if (sub.substring(0,1).equals("0")){
            sub = sub.substring(1);
        }
        viewHolder.time.setText(sub);
        viewHolder.num.setText(send+messageInfo.getCount()+people);
        String state = messageInfo.getState();
        viewHolder.state.setText(state);
        if (!state.equals(allSuccess)) {
            viewHolder.state.setTextColor(Color.RED);
        }
        return convertView;
    }

    public static class ViewHolder{
        TextView content;
        TextView time;
        TextView num;
        TextView state;
    }
}
