package com.rollup.journey.activity;

import android.app.Activity;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.ContentValues;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.rollup.journey.R;
import com.rollup.journey.adapter.Test_Listview;
import com.rollup.journey.bean.LinkState;
import com.rollup.journey.customview.TitleView;
import com.rollup.journey.database.MyOpenHelper;
import com.rollup.journey.utils.BlueProfile;
import com.rollup.journey.utils.BlueProfileBackData;
import com.rollup.journey.utils.ConstantValue;
import com.rollup.journey.utils.DbEngine;
import com.rollup.journey.utils.SpUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Main1TestActivity extends BlueBaseActivity implements View.OnClickListener{

    private TitleView main1_test_title;
    private EditText main1_test_search;
    private ListView main1_test_listView;
    private TextView main1_test_refresh;
    private String tag;
    private List<LinkState> linkTest;
    private List<LinkState> linkTestMsg;
    private Test_Listview adapter;
    private String index;
    private String msg;
    private String name;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tag = getIntent().getExtras().getString("tag");
        initView();
        initEvent();
        if(tag.equals("message")){
            main1_test_title.setTitleText(getResources().getString(R.string.str_main_state));
            index = getIntent().getExtras().getString("index");
            msg = getIntent().getExtras().getString("msg");
        }else if (tag.equals("time")){
            main1_test_title.setTitleText(getResources().getString(R.string.str_main_other3));
//            testAllDevice();
        }else if(tag.equals("test")){
//            testAllDevice();
        }
        initData();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main1_test;
    }

    //请求所有设备连接测试
    private void testAllDevice() {
        DbEngine.getInstance(this).showDialog(this,getResources().getString(R.string.str_data_loading));
        if (tag.equals("test")){
            listBytes = BlueProfile.getInstance().requestData(BlueProfile.getInstance().contentTestMulDevice(DbEngine.getInstance(this).allPeopleList));
        }else if(tag.equals("time")){
            List indexList = DbEngine.getInstance(this).getAllDeviceIndex(null);
            if (indexList.isEmpty()){
                SpUtils.showToast(this,getResources().getString(R.string.str_main_electric_add_first));
            }else{
                listBytes = BlueProfile.getInstance().requestData(BlueProfile.getInstance().contentSendTime(indexList));
            }
        }
        for (byte[] listByte : listBytes) {
            for (byte b : listByte) {
                Log.e("devicetest",b+"");
            }
        }
        sendMessage();
    }

    private void initView() {
        main1_test_title = (TitleView) findViewById(R.id.main1_test_title);
        main1_test_search = (EditText) findViewById(R.id.main1_test_search);
        main1_test_refresh = (TextView) findViewById(R.id.main1_test_refresh);
        if(tag.equals("message")){
            main1_test_refresh.setVisibility(View.GONE);
        }
        main1_test_listView = (ListView) findViewById(R.id.main1_test_listView);
    }

    private void initData() {
        if (tag.equals("test")){
            linkTest = new ArrayList();
            linkTest = DbEngine.getInstance(this).getLinkTest(null);
        }else if(tag.equals("message")){
            linkTest = DbEngine.getInstance(this).getSendMessageState(index);
        }else if(tag.equals("time")){
            linkTest = new ArrayList();
            linkTest = DbEngine.getInstance(this).getTimeTest(null);
        }
        adapter = new Test_Listview(this,linkTest,tag);
        if (tag.equals("message")) {
            adapter.setMsg(msg);
        }
        main1_test_listView.setAdapter(adapter);
    }

    private void initEvent() {
        main1_test_title.setOnClickListenerLeft(this);
        main1_test_refresh.setOnClickListener(this);
        main1_test_search.addTextChangedListener(watcher);
//        main1_test_listView.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imageView:
                finish();
                break;
            case R.id.main1_test_refresh:
                if(!tag.equals("message")){
                    testAllDevice();
                }
                initData();
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
            if (!TextUtils.isEmpty(s1)) {
                if (tag.equals("test")) {
                    linkTest = DbEngine.getInstance(Main1TestActivity.this).getLinkTest(s1);
                }else if (tag.equals("message")){
                    if (linkTestMsg == null){
                        linkTestMsg = new ArrayList<>();
                    } else {linkTestMsg.clear();}
                    for (LinkState linkState : linkTest) {
                        if (linkState.getName().contains(s1)){
                            linkTestMsg.add(linkState);
                        }
                    }
                    adapter.setData(linkTestMsg);
                    adapter.notifyDataSetChanged();
                    return;
                } else if (tag.equals("time")) {
                    linkTest = DbEngine.getInstance(Main1TestActivity.this).getTimeTest(s1);
                }
            }else{
                if (tag.equals("test")) {
                    linkTest = DbEngine.getInstance(Main1TestActivity.this).getLinkTest(null);
                } else if (tag.equals("message")){
                    linkTest = DbEngine.getInstance(Main1TestActivity.this).getSendMessageState(index);
                } else if (tag.equals("time")) {
                    linkTest = DbEngine.getInstance(Main1TestActivity.this).getTimeTest(null);
                }
            }
            adapter.setData(linkTest);
            adapter.notifyDataSetChanged();
        }
    };

//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        this.position = position;
//        //点击事件处理
//        LinkState linkState = (LinkState) parent.getItemAtPosition(position);
//        String state = linkState.getState();
//        name = linkState.getName();
//        ImageView refresh = (ImageView) view.findViewById(R.id.main1_test_item_refresh);
//        TextView stateView = (TextView) view.findViewById(R.id.main1_test_item_state);
//        if (state.equals("1")) {
//            Intent intent = new Intent(Main1TestActivity.this, Main1TestRecordActivity.class);
//            intent.putExtra("name", name);
//            startActivity(intent);
//        }else{
//            stateView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(Main1TestActivity.this, Main1TestRecordActivity.class);
//                    intent.putExtra("name", name);
//                    startActivity(intent);
//                }
//            });
//            refresh.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (tag.equals("test")){
//                        //重新去连接测试设备
//                        DbEngine.getInstance(Main1TestActivity.this).showDialog(Main1TestActivity.this,"设备测试中...");
//                        List allDeviceIndex = DbEngine.getInstance(Main1TestActivity.this).getAllDeviceIndex(name);
//                        listBytes = BlueProfile.getInstance().requestData(BlueProfile.getInstance().contentTestMulDevice(allDeviceIndex));
//                        sendMessage();
//                    }else if(tag.equals("message")){
//                        //重新发送消息
//                        DbEngine.getInstance(Main1TestActivity.this).showDialog(Main1TestActivity.this,"正在重新发送消息...");
//                        List allDeviceIndex = DbEngine.getInstance(Main1TestActivity.this).getAllDeviceIndex(name);
//                        listBytes = BlueProfile.getInstance().requestData(BlueProfile.getInstance().contentSendMsg(allDeviceIndex,msg));
//                        sendMessage();
//                    }else if(tag.equals("time")){
//                        //重新校准时间
//                        DbEngine.getInstance(Main1TestActivity.this).showDialog(Main1TestActivity.this,"重新校准时间...");
//                        List allDeviceIndex = DbEngine.getInstance(Main1TestActivity.this).getAllDeviceIndex(name);
//                        listBytes = BlueProfile.getInstance().requestData(BlueProfile.getInstance().contentTestMulDevice(allDeviceIndex));
//                        sendMessage();
//                    }
//                }
//            });
//        }
//    }

    @Override
    public void handlerBackMsg() {
        byte dataType = BlueProfileBackData.getInstance().getDataType();
        if (dataType == ConstantValue.FUNC_LINK_TEST || dataType == ConstantValue.FUNC_MODY_TIME){
            //0x04连接测试返回的数据,更新数据库  0x07校准时间返回数据，更新数据库
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = format.format(new Date());
            ContentValues values = new ContentValues();
            String tag = "state";
            String type = null;
            if (dataType == 0x04){
                type = getResources().getString(R.string.str_link);
                tag = "state";
            }else if(dataType == 0x07){
                type = getResources().getString(R.string.str_adjust_time);
                tag = "timeState";
            }
            for (int i = 0; i < data.length/2; i++) {
                int mIndex = data[2*i];
                String personName = DbEngine.getInstance(Main1TestActivity.this).getPersonName(mIndex + "");
                String typeState;
                if (data[2*i+1] == ConstantValue.BACK_STATE_SUC){
                    typeState = "1";
                    values.put(tag,"1");
                    DbEngine.getInstance(Main1TestActivity.this).updataDeviceStatus(values,mIndex+"");
                }else{
                    typeState = "0";
                    values.put(tag,"0");
                    DbEngine.getInstance(Main1TestActivity.this).updataDeviceStatus(values,mIndex+"");
                }
                ContentValues values1 = new ContentValues();
                values1.put("peopleName",personName);
                values1.put("type",type);
                values1.put("time",time);
                values1.put("state",typeState);
                DbEngine.db.insert(ConstantValue.LINKRECORD,null,values1);
            }

            if (dataType == ConstantValue.FUNC_LINK_TEST){
                linkTest = DbEngine.getInstance(this).getLinkTest(null);
            } else {
                linkTest = DbEngine.getInstance(this).getTimeTest(null);
            }
            adapter.setData(linkTest);
            adapter.notifyDataSetChanged();
        }else if(dataType == ConstantValue.FUNC_SEND_MSG){
            //发送消息返回的数据
            if (data[1] == ConstantValue.BACK_STATE_SUC){
                ContentValues values = new ContentValues();
                values.put("state","1");
                DbEngine.db.update(ConstantValue.PERSONINDEX,values,"peopleIndex=? and peopleName=?",new String[]{index,name});
                adapter.setState(position);
            }
        }
        DbEngine.getInstance(this).dismissDialog();
    }
}
