package com.rollup.journey.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.rollup.journey.R;
import com.rollup.journey.bean.MessageRecord;
import com.rollup.journey.customview.TitleView;
import com.rollup.journey.utils.DbEngine;

import java.util.List;

public class MapRecordActivity extends AppCompatActivity implements View.OnClickListener{

    private String name;
    private TextView map_msg_record;
    private ListView map_msg_listView;
    private String index;
    private List<MessageRecord> messageRecord;
    private String peopleName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_record);
        name = getIntent().getExtras().getString("name");
        index = getIntent().getExtras().getString("index");
        peopleName = getIntent().getExtras().getString("peopleName");
        initData();
        initView();
        initBottom();
    }

//    @Override
//    public int getLayoutId() {
//        return R.layout.activity_map_record;
//    }
    public int bottomState = R.string.str_base_linked;
    private TextView bottom_state;
    private TextView bottom_index;
    private void initBottom() {
        bottom_index = (TextView) findViewById(R.id.bottom_index);
        if (bottom_index != null) {
            bottom_state = (TextView) findViewById(R.id.bottom_state);
            ImageView bottom_battery = (ImageView) findViewById(R.id.bottom_battery);
            bottom_index.setText(MyApplication.deviceAddress);
            if (MyApplication.battery == 2) {
                bottom_battery.setImageResource(R.mipmap.bottom_battery1);
            } else if (MyApplication.battery == 4) {
                bottom_battery.setImageResource(R.mipmap.bottom_battery2);
            } else if (MyApplication.battery == 5) {
                bottom_battery.setImageResource(R.mipmap.bottom_battery3);
            } else if (MyApplication.battery == 6) {
                bottom_battery.setImageResource(R.mipmap.bottom_battery4);
            } else if (MyApplication.battery == 7) {
                bottom_battery.setImageResource(R.mipmap.bottom_battery4);
            }
        }
        bottom_state.setText(bottomState);
    }

    private void initView() {
        TitleView map_record_title = (TitleView) findViewById(R.id.map_record_title);
        map_record_title.setOnClickListenerLeft(this);
        map_msg_record = (TextView) findViewById(R.id.map_msg_record);
        map_msg_record.setText(peopleName);
        map_msg_listView = (ListView) findViewById(R.id.map_msg_listView);
        map_msg_listView.setAdapter(adapter);
    }

    private void initData() {
        messageRecord = DbEngine.getInstance(this).getMessageRecord(peopleName);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imageView:
                finish();
                break;
            default:
                break;
        }

    }

    private BaseAdapter adapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return messageRecord.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = View.inflate(MapRecordActivity.this, R.layout.message_record_item, null);
                holder.contentText = (TextView) convertView.findViewById(R.id.message_record_content);
                holder.iconState = (ImageView) convertView.findViewById(R.id.message_record_del);
                holder.time = (TextView) convertView.findViewById(R.id.message_record_time);
                convertView.setTag(holder);
            } else{
                holder = (ViewHolder) convertView.getTag();
            }
            MessageRecord record = messageRecord.get(messageRecord.size()-position-1);
            String state = record.getState();
            if (state.equals("0")) {
                holder.iconState.setImageResource(R.mipmap.main_map_record_del);
            }
            holder.contentText.setText(record.getContent());
            String sub = record.getTime().substring(5, 16);
            sub = sub.replace("-","月");
            sub = sub.replace(" ","日 ");
            if (sub.substring(0,1).equals("0")){
                sub = sub.substring(1);
            }
            holder.time.setText(sub);
            return convertView;
        }
    };

    public class ViewHolder{
        TextView contentText;
        ImageView iconState;
        TextView time;
    }
}
