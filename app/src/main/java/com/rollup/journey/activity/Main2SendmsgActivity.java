package com.rollup.journey.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.orhanobut.logger.Logger;
import com.rollup.journey.R;
import com.rollup.journey.adapter.Msg_had_send;
import com.rollup.journey.bean.MessageInfo;
import com.rollup.journey.customview.TitleView;

public class Main2SendmsgActivity extends BlueBaseActivity implements View.OnClickListener,AdapterView.OnItemClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main2_sendmsg;
    }

    private void initView() {
        TitleView main2_msg_send_title = (TitleView) findViewById(R.id.main2_msg_send_title);
        main2_msg_send_title.setOnClickListenerLeft(this);
        ListView main2_msg_send_listView = (ListView) findViewById(R.id.main2_msg_send_listView);
        main2_msg_send_listView.setAdapter(new Msg_had_send(this));
        main2_msg_send_listView.setOnItemClickListener(this);
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MessageInfo info = (MessageInfo) parent.getItemAtPosition(position);
        Intent intent = new Intent(Main2SendmsgActivity.this,Main1TestActivity.class);
        intent.putExtra("tag","message");
        intent.putExtra("index",info.getIndex()+"");
        intent.putExtra("msg",info.getMessage());
        startActivity(intent);
    }
}
