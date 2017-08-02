package com.rollup.journey.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;
import com.rollup.journey.R;
import com.rollup.journey.customview.TitleView;
import com.rollup.journey.utils.BlueProfile;
import com.rollup.journey.utils.BlueProfileBackData;
import com.rollup.journey.utils.ConstantValue;
import com.rollup.journey.utils.DbEngine;
import com.rollup.journey.utils.SpUtils;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import karics.library.zxing.android.CaptureActivity;

public class Main1AddActivity extends BlueBaseActivity implements View.OnClickListener {

    private TitleView main1_add_title;
    private TextView main1_add_device_name;
    private TextView main1_add_person_info;
    private TextView main1_add_phone;
    private TextView main1_add_remark;
    private TextView main1_add_confirm;
    private static final int RESULTSCAN = 100;
    private String deviceName;
    private String info;
    private String phone;
    private String remark;
    private int deviceIndex;
    private boolean isAdding = false;
    private List<String> list = new ArrayList<>();
//    private String regEx = "[\u4e00-\u9fa5]";
    private String regEx = "^[0-9]{1}\\d{5}+$";
    private Pattern pat = Pattern.compile(regEx);

    List<String> nameList = new ArrayList<>();
    List<String> dNameList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initEvent();
        Cursor peopleName = DbEngine.db.query(ConstantValue.TEAMINFO, new String[]{"peopleName","deviceName"}, null, null, null, null, null);
        if (peopleName.moveToFirst()){
            do {
                String pName = peopleName.getString(0);
                String dName = peopleName.getString(1);
                nameList.add(pName);
                dNameList.add(dName);
            } while (peopleName.moveToNext());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        nameList.clear();
        dNameList.clear();
        nameList = null;
        dNameList = null;
    }

//
//    @Override
//    public void onBackPressed() {
//        Intent intent = new Intent(Intent.ACTION_MAIN);
//        intent.addCategory(Intent.CATEGORY_LAUNCHER);
//        startActivity(intent);
//    }

    //    @Override
//    public void onAttachedToWindow() {
//        super.onAttachedToWindow();
//        DisplayMetrics dm = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(dm);
//        float density = dm.density;
//        View view = getWindow().getDecorView();
//        WindowManager.LayoutParams lp = (WindowManager.LayoutParams)view.getLayoutParams();
//        Logger.d("onResume"+dm.widthPixels+">>>"+dm.heightPixels+">>>"+density+">>>");
//        lp.gravity = Gravity.CENTER;
//        lp.width = (dm.widthPixels * 4) / 5;
//        lp.height = (dm.heightPixels * 4) / 5;
//        getWindowManager().updateViewLayout(view,lp);
//        dispatchTouchEvent()
//    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main1_add;
    }

    private void initView() {
        main1_add_title = (TitleView) findViewById(R.id.main1_add_title);
        main1_add_device_name = (TextView) findViewById(R.id.main1_add_device_name);
        main1_add_device_name.addTextChangedListener(watcher1);
        main1_add_person_info = (TextView) findViewById(R.id.main1_add_person_info);
        main1_add_person_info.addTextChangedListener(watcher);
        main1_add_phone = (TextView) findViewById(R.id.main1_add_phone);
        main1_add_remark = (TextView) findViewById(R.id.main1_add_remark);
        main1_add_confirm = (TextView) findViewById(R.id.main1_add_confirm);
    }

    private void initEvent() {
        main1_add_title.setOnClickListenerLeft(this);
        main1_add_title.setOnClickListenerRight(this);
        main1_add_confirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageView:
                finish();
                break;
            case R.id.main1_add_confirm:
                //连接设备并保存数据到数据库
//                DbEngine.getInstance(this).showDialog(this,"加载中...");
//                add50Device();
                deviceName = main1_add_device_name.getText().toString().trim();
                info = main1_add_person_info.getText().toString().trim();
                phone = main1_add_phone.getText().toString().trim();
                remark = main1_add_remark.getText().toString().trim();
                if (dNameList.contains(deviceName)){
                    SpUtils.showToast(getApplicationContext(),getResources().getString(R.string.str_toast_device_had_add));
                } else if (nameList.contains(info)) {
                    SpUtils.showToast(getApplicationContext(),getResources().getString(R.string.str_toast_user_had_add));
                } else if (TextUtils.isEmpty(deviceName)) {
                    SpUtils.showToast(getApplicationContext(), getResources().getString(R.string.str_main_pl_add_device_name));
                } else if (TextUtils.isEmpty(info)) {
                    SpUtils.showToast(getApplicationContext(), getResources().getString(R.string.str_main_pl_add_person_info));
                } else if (TextUtils.isEmpty(phone)) {
                    SpUtils.showToast(getApplicationContext(), getResources().getString(R.string.str_main_pl_add_phone));
                } /*else if (TextUtils.isEmpty(remark)) {
                    SpUtils.showToast(getApplicationContext(), getResources().getString(R.string.str_main_pl_add_remark));
                }*/else {
                    //连接设备
                    if (!isAdding){
                        if (isOnlyNumber(deviceName)) {
                            Cursor query = DbEngine.db.query(ConstantValue.TEAMINFO, new String[]{ConstantValue.TEAMNAME}, null, null, null, null, null);
                            deviceIndex = query.getCount() + 1;
                            query.close();
                            list.clear();
                            list.add(deviceIndex + BlueProfile.SPLIT + deviceName);
                            listBytes = BlueProfile.getInstance().requestData(BlueProfile.getInstance().contentAddMulDevice(list));
                            sendMessage();
                        }else{
                            SpUtils.showToast(this,getResources().getString(R.string.str_toast_six_device_num));
                        }
                    }
                }
                break;
            case R.id.imageView2:
                //二维码扫描
                Intent intent = new Intent(this, CaptureActivity.class);
                startActivityForResult(intent, RESULTSCAN);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULTSCAN & resultCode == RESULT_OK) {
            String result = data.getExtras().getString("codedContent");
            main1_add_device_name.setText(result);
        }
    }

    @Override
    public void handlerBackMsg() {
        if (BlueProfileBackData.getInstance().getDataType() == 0x03){
            if (data[2] == ConstantValue.BACK_STATE_SUC){
                SpUtils.showToast(this,getResources().getString(R.string.str_toast_add_device_success));
                saveDataToDb();
                main1_add_device_name.setText("");
                main1_add_person_info.setText("");
                main1_add_phone.setText("");
                main1_add_remark.setText("");
                nameList.add(info);
                dNameList.add(deviceName);
//                add50Device();
            }else{
                SpUtils.showToast(this,getResources().getString(R.string.str_toast_add_device_fail));
            }
        }
        isAdding = false;
    }

    private void saveDataToDb() {
        String teamName = SpUtils.getString(this, ConstantValue.TEAMNAME, null);
        ContentValues values = new ContentValues();
        values.put(ConstantValue.TEAMNAME, teamName);
        values.put("deviceName", deviceName);
        values.put("deviceIndex", deviceIndex);
        values.put("peopleName", info);
        values.put("phoneNumber", phone);
        values.put("remark", remark);
        values.put("state", "0");
        values.put("timeState", "0");
        DbEngine.getInstance(this).insertTeamInfo(values);
//        ContentValues values1 = new ContentValues();
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String date = format.format(new Date());
//        values1.put("peopleName",info);
//        values1.put("type",getResources().getString(R.string.str_link));
//        values1.put("time",date);
//        values1.put("state","1");
//        DbEngine.getInstance(this).insertLinkRecord(values1);
    }

    public boolean isOnlyNumber(String str)
    {
        Matcher matcher = pat.matcher(str);
        return matcher.find();
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
            String user = s.toString();
            if (nameList.contains(user)){
                main1_add_person_info.setError(getResources().getString(R.string.str_toast_user_had_add));
            }else{
                main1_add_person_info.setError(null);
            }
        }
    };

    private TextWatcher watcher1 = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String device = s.toString();
            if (dNameList.contains(device)){
                main1_add_device_name.setError(getResources().getString(R.string.str_toast_device_had_add));
            }else{
                main1_add_device_name.setError(null);
            }
        }
    };

        int in = 123400;
    public void add50Device(){
        if (in < 123451){
            in = in + 1;
            deviceName = in+"";
            info = "姓名"+in;
            phone = "手机号码"+in;
            remark = "备注信息"+in;

            Cursor query = DbEngine.db.query(ConstantValue.TEAMINFO, new String[]{ConstantValue.TEAMNAME}, null, null, null, null, null);
            deviceIndex = query.getCount() + 1;
            query.close();
            list.clear();
            list.add(deviceIndex + BlueProfile.SPLIT + deviceName);
            listBytes = BlueProfile.getInstance().requestData(BlueProfile.getInstance().contentAddMulDevice(list));
            sendMessage();
        }else{
            DbEngine.getInstance(this).dismissDialog();
        }
    }
}
