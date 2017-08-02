package com.rollup.journey.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rollup.journey.R;
import com.rollup.journey.adapter.Test_Record_Listview;
import com.rollup.journey.customview.TitleView;
import com.rollup.journey.utils.DbEngine;

import java.util.List;

public class Main1TestRecordActivity extends AppCompatActivity implements View.OnClickListener{

    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1_test_record);
        name = getIntent().getExtras().getString("name");
        initView();
        initUI();
    }

    private void initUI() {
        TextView bottom_index = bottom_index = (TextView) findViewById(R.id.bottom_index);
        if (bottom_index != null) {
            TextView bottom_state = (TextView) findViewById(R.id.bottom_state);
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
    }

//    @Override
//    public int getLayoutId() {
//        return R.layout.activity_main1_test_record;
//    }

    private void initView() {
        TitleView titleVIew = (TitleView) findViewById(R.id.main1_test_record_title);
        TextView textName = (TextView) findViewById(R.id.main1_test_record_name);
        textName.setText(name);
        ListView main1_test_record_listView = (ListView) findViewById(R.id.main1_test_record_listView);
        List<String> linkRecord = DbEngine.getInstance(this).getLinkRecord(name);
        main1_test_record_listView.setAdapter(new Test_Record_Listview(this,linkRecord));
        titleVIew.setOnClickListenerLeft(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imageView:
                finish();
                break;
        }
    }
}
