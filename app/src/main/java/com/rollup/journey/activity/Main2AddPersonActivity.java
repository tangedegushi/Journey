package com.rollup.journey.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.rollup.journey.R;
import com.rollup.journey.adapter.Msg_Add_Listview;
import com.rollup.journey.customview.TitleView;
import com.rollup.journey.utils.DbEngine;

import java.util.List;

public class Main2AddPersonActivity extends AppCompatActivity implements View.OnClickListener {

    private TitleView main2_msg_add_title;
    private EditText main2_msg_add_search;
    private TextView main2_msg_add_all;
    private TextView main2_msg_add_confirm;
    private ListView main2_msg_add_listView;
    private Msg_Add_Listview adapter;
    private List<String> allPerson = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2_add_person);
        initView();
        initEvent();
    }

//    @Override
//    public int getLayoutId() {
//        return R.layout.activity_main2_add_person;
//    }

    private void initView() {
        main2_msg_add_title = (TitleView) findViewById(R.id.main2_msg_add_title);
        main2_msg_add_search = (EditText) findViewById(R.id.main2_msg_add_search);
        main2_msg_add_all = (TextView) findViewById(R.id.main2_msg_add_all);
        main2_msg_add_listView = (ListView) findViewById(R.id.main2_msg_add_listView);
        main2_msg_add_confirm = (TextView) findViewById(R.id.main2_msg_add_confirm);

        TextView bottom_index = (TextView) findViewById(R.id.bottom_index);
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
        if (MyApplication.isConnect) {
            bottom_state.setText(R.string.str_base_linked);
        }else{
            bottom_state.setText(R.string.str_base_link_n);
        }
    }

    private void initEvent() {
        main2_msg_add_title.setOnClickListenerLeft(this);
        main2_msg_add_all.setOnClickListener(this);
        main2_msg_add_confirm.setOnClickListener(this);
        allPerson = DbEngine.getInstance(this).getAllPerson(null);
        adapter = new Msg_Add_Listview(this,allPerson);
        adapter.addListIndex();
        main2_msg_add_listView.setAdapter(adapter);
        main2_msg_add_search.addTextChangedListener(watcher);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imageView:
                finish();
                break;
            case R.id.main2_msg_add_all:
                if (main2_msg_add_all.getText().toString().trim().equals(getResources().getString(R.string.str_main_message_select_all))){
                    adapter.setAllChecked(true);
                    adapter.addListIndex();
                    main2_msg_add_all.setText(R.string.str_main_message_select_all_no);
                }else{
                    adapter.setAllChecked(false);
                    adapter.removetListIndex();
                    main2_msg_add_all.setText(R.string.str_main_message_select_all);
                }
                adapter.notifyDataSetChanged();
                break;
            case R.id.main2_msg_add_confirm:
                MyApplication.getInstance().nameList = adapter.getListIndex();
                Intent back = new Intent();
                setResult(20,back);
                finish();
                break;
            default:
                break;
        }
    }

    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String s1 = s.toString();
            if (!TextUtils.isEmpty(s1)){
                allPerson = DbEngine.getInstance(Main2AddPersonActivity.this).getAllPerson(s1);
            }else{
                allPerson = DbEngine.getInstance(Main2AddPersonActivity.this).getAllPerson(null);
            }
            adapter.setData(allPerson);
        }
    };
}
