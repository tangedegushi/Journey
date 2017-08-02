package com.rollup.journey.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.rollup.journey.R;
import com.rollup.journey.customview.TitleView;

public class Main2Activity extends BlueBaseActivity implements View.OnClickListener {

    private TitleView titleView;
    private RelativeLayout main2_msg_layout1;
    private RelativeLayout main2_msg_layout2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initEvent();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main2;
    }

    private void initView() {
        titleView = (TitleView) findViewById(R.id.main2_msg_title);
        main2_msg_layout1 = (RelativeLayout) findViewById(R.id.main2_msg_layout1);
        main2_msg_layout2 = (RelativeLayout) findViewById(R.id.main2_msg_layout2);
    }

    private void initEvent() {
        titleView.setOnClickListenerLeft(this);
        main2_msg_layout1.setOnClickListener(this);
        main2_msg_layout2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imageView:
                finish();
                break;
            case R.id.main2_msg_layout1:
                Intent intent1 = new Intent(Main2Activity.this,Main2NewmsgActivity.class);
                startActivity(intent1);
                break;
            case R.id.main2_msg_layout2:
                Intent intent2 = new Intent(Main2Activity.this,Main2SendmsgActivity.class);
                startActivity(intent2);
                break;
        }
    }
}
