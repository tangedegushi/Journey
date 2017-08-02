package com.rollup.journey.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.rollup.journey.R;
import com.rollup.journey.adapter.Msg_Add_Listview;
import com.rollup.journey.customview.TitleView;
import com.rollup.journey.utils.BlueProfile;
import com.rollup.journey.utils.BlueProfileBackData;
import com.rollup.journey.utils.ConstantValue;
import com.rollup.journey.utils.DbEngine;
import com.rollup.journey.utils.SpUtils;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Main2NewmsgActivity extends BlueBaseActivity implements View.OnClickListener{

    private TitleView main2_msg_new_title;
    private RelativeLayout main2_msg_add_receiver;
    private EditText main2_msg_edit;
    private TextView main2_msg_new_send;
    private ListView main2_msg_add_listview;
    private List<String> nameAndIndex;
    private ArrayList<String> listIndex = new ArrayList<>();
    private Msg_Add_Listview adapter;
    private String sendMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initEvent();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main2_newmsg;
    }

    private void initView() {
        main2_msg_new_title = (TitleView) findViewById(R.id.main2_msg_new_title);
        main2_msg_add_receiver = (RelativeLayout) findViewById(R.id.main2_msg_new_receiver);
        main2_msg_add_listview = (ListView) findViewById(R.id.main2_msg_new_listView);
        main2_msg_edit = (EditText) findViewById(R.id.main2_msg_new_edit);
        main2_msg_new_send = (TextView) findViewById(R.id.main2_msg_new_send);
    }

    private void initEvent() {
        main2_msg_new_title.setOnClickListenerLeft(this);
        main2_msg_add_receiver.setOnClickListener(this);
        main2_msg_new_send.setOnClickListener(this);
        adapter = new Msg_Add_Listview(this,nameAndIndex,true);
        main2_msg_add_listview.setAdapter(adapter);
        main2_msg_edit.addTextChangedListener(watcher);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imageView:
                finish();
                break;
            case R.id.main2_msg_new_receiver:
                Intent intent = new Intent(Main2NewmsgActivity.this,Main2AddPersonActivity.class);
                startActivityForResult(intent,10);
                break;
            case R.id.main2_msg_new_send:
                if (nameAndIndex == null){
                    SpUtils.showToast(this,getResources().getString(R.string.str_toast_choose_contact_person));
                    return;
                }
                listIndex.clear();
                for (String s : nameAndIndex) {
                    String[] split = s.split(BlueProfile.SPLIT);
                    listIndex.add(split[1]);
                }
                DbEngine.getInstance(this).showDialog(this,getResources().getString(R.string.str_toast_message_sending));
                sendMsg = main2_msg_edit.getText().toString().trim();

                listBytes = BlueProfile.getInstance().requestData(BlueProfile.getInstance().contentSendMsg(listIndex, sendMsg));
                for (byte[] listByte : listBytes) {
                    for (byte b : listByte) {
                        Log.d("消息",b+"..sendMsg.."+sendMsg);
                    }
                    System.out.println();
                }
                try {
                    byte[] bytes = sendMsg.getBytes("UTF-8");
                    Log.d("length",bytes.length+"");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                sendMessage();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 10 && resultCode == 20){
            nameAndIndex = MyApplication.getInstance().nameList;
            adapter.setData(nameAndIndex);
        }
    }

    @Override
    public void handlerBackMsg() {
        DbEngine.getInstance(this).dismissDialog();
        byte dataType = BlueProfileBackData.getInstance().getDataType();
        if (dataType == ConstantValue.FUNC_SEND_MSG){
            String state = getResources().getString(R.string.str_send_message_state_success);
            boolean hasFail = false;
            boolean hasSuccess = false;
            Cursor query = DbEngine.db.query(ConstantValue.MESSAGEINFO, null, null, null, null, null, null);
            int peopleIndex = query.getCount()+1;
            query.close();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = format.format(new Date());
            ContentValues value = new ContentValues();
            value.put("peopleIndex",peopleIndex);
            for (int i = 0; i < data.length/2; i++) {
                int mIndex = data[2*i];
                String personName = DbEngine.getInstance(Main2NewmsgActivity.this).getPersonName(mIndex + "");
                value.put("peopleName",personName);
                String st = "0";
                if (data[2*i+1] == ConstantValue.BACK_STATE_SUC){
                    st = "1";
                    value.put("state",st);
                    hasSuccess = true;
                }else{
                    st = "0";
                    value.put("state",st);
                    hasFail = true;
                }
                DbEngine.db.insert(ConstantValue.PERSONINDEX,null,value);
                ContentValues values1 = new ContentValues();
                values1.put("peopleName",personName);
                values1.put("type",getResources().getString(R.string.str_message_receiver));
                values1.put("time",time);
                values1.put("state",st);
                DbEngine.db.insert(ConstantValue.LINKRECORD,null,values1);
            }
            if (hasFail & hasSuccess){
                state = getResources().getString(R.string.str_send_message_state_part_fail);
            } else if (hasFail & !hasSuccess) {
                state = getResources().getString(R.string.str_send_message_state_fail);
            } else if (!hasFail & hasSuccess) {
                state = getResources().getString(R.string.str_send_message_state_success);
            }
            ContentValues values = new ContentValues();
            values.put("message",sendMsg);
            values.put("peopleIndex",peopleIndex);
            values.put("count",listIndex.size());
            values.put("time",time);
            values.put("state",state);
            DbEngine.db.insert(ConstantValue.MESSAGEINFO,null,values);
            SpUtils.showToast(this,getResources().getString(R.string.str_message_receiver)+state);
        }
    }

    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        private boolean isMax = false;
        @Override
        public void afterTextChanged(Editable s) {
            String content = s.toString();
            byte[] bytes = null;
            try {
                bytes = content.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (bytes.length > 114){
                isMax = true;
                main2_msg_edit.setError("发送内容已达上限");
            }else{
                isMax = false;
                main2_msg_edit.setError(null);
            }
        }
    };
}
