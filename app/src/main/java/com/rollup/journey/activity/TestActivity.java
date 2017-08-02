package com.rollup.journey.activity;

import android.bluetooth.BluetoothGatt;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.rollup.journey.R;
import com.rollup.journey.utils.BlueProfile;
import com.rollup.journey.utils.BlueProfileBackData;

import java.util.ArrayList;
import java.util.List;

public class TestActivity extends BlueBaseActivity implements View.OnClickListener{

    private TextView textView;
    private TextView textView1;
    private EditText editText1;
    private EditText editText2;
    private List<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        list = new ArrayList<>();
        list.add("1");
        list.add("2");
        list.add("3");
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_test;
    }

    private void initView() {
        Button btn1 = (Button) findViewById(R.id.btn1);
        Button btn2 = (Button) findViewById(R.id.btn2);
        Button btn3 = (Button) findViewById(R.id.btn3);
        Button btn4 = (Button) findViewById(R.id.btn4);
        Button btn5 = (Button) findViewById(R.id.btn5);
        Button btn6 = (Button) findViewById(R.id.btn6);
        Button btn7 = (Button) findViewById(R.id.btn7);
        Button btn8 = (Button) findViewById(R.id.btn8);
        Button btn9 = (Button) findViewById(R.id.btn9);
        textView = (TextView) findViewById(R.id.test_text);
        textView1 = (TextView) findViewById(R.id.test_md5);
        editText1 = (EditText) findViewById(R.id.add1);
        editText2 = (EditText) findViewById(R.id.add2);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
        btn5.setOnClickListener(this);
        btn6.setOnClickListener(this);
        btn7.setOnClickListener(this);
        btn8.setOnClickListener(this);
        btn9.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        BlueProfile bp = BlueProfile.getInstance();
        byte[] bytes = null;
        switch (v.getId()) {
            case R.id.btn1:
                bytes = bp.contentReal(0x01, "");
                break;
            case R.id.btn2:
                bytes = bp.contentMd5("team");
                String str = "生成md5码：";
                for (byte aByte : bytes) {
                    str = str+Integer.toHexString(aByte&0xff)+",";
                }
                textView1.setText(str);
                break;
            case R.id.btn3:
                bytes = bp.contentReal(0x09,"");
                break;
            case R.id.btn4:
                List<String> mList = new ArrayList<>();
                String trim = editText1.getText().toString().trim();
                String trim1 = editText2.getText().toString().trim();
                if (TextUtils.isEmpty(trim) || TextUtils.isEmpty(trim1))
                    Toast.makeText(this, "请输入开始和结束设备号", Toast.LENGTH_LONG).show();
                int i = Integer.parseInt(trim);
                int i1 = Integer.parseInt(trim1);
                for (int j = i; j < i1+1; j++) {
                    mList.add(j+BlueProfile.SPLIT+"aaaaa"+(j%10));
                }
                bytes = bp.contentAddMulDevice(mList);
                break;
            case R.id.btn5:
                bytes = bp.contentTestMulDevice(list);
                break;
            case R.id.btn6:
                List<String> mList1 = new ArrayList<>();
                mList1.add("1");
                bytes = bp.contentSendMsg(mList1,"这是一个测试");
                break;
            case R.id.btn7:
                List<String> mList2 = new ArrayList<>();
                mList2.add("1");
                bytes = bp.contentAddPosition(mList2);
                break;
            case R.id.btn8:
                bytes = bp.contentSendTime(list);
                break;
            case R.id.btn9:
                bytes = bp.contentClearData();
                break;
            default:
                break;
        }
        listBytes = bp.requestData(bytes);
        sendMessage();
    }

    @Override
    public void handlerBackMsg() {
        String str = "返回类型：";
        byte dataType = BlueProfileBackData.getInstance().getDataType();
        str = str+dataType+"\n";
        str = str+"返回数据：";
        for (byte b : data) {
            String s = Integer.toHexString(b&0xff);
            str = str+s+",";
        }
        textView.setText(str);
    }

    @Override
    public void onServiceDiscover(BluetoothGatt gatt) {
        super.onServiceDiscover(gatt);
        Logger.d("testActivity.............");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        instance.disconnect();
        instance.close();
    }
}
