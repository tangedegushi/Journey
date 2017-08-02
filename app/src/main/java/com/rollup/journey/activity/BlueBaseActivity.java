package com.rollup.journey.activity;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.rollup.journey.R;
import com.rollup.journey.bean.BlueGattAttributes;
import com.rollup.journey.utils.BlueProfile;
import com.rollup.journey.utils.BlueProfileBackData;
import com.rollup.journey.utils.BluetoothLeClass;
import com.rollup.journey.utils.ConstantValue;
import com.rollup.journey.utils.DbEngine;
import com.rollup.journey.utils.SpUtils;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ThreadFactory;

public abstract class BlueBaseActivity extends AppCompatActivity implements BluetoothLeClass.OnConnectListener,BluetoothLeClass.OnDisconnectListener,
        BluetoothLeClass.OnServiceDiscoverListener,BluetoothLeClass.OnDataAvailableListener,BluetoothLeClass.OnBagSendSuccess{

    public BluetoothLeClass instance;
    public List<byte[]> listBytes;
    public byte[] data;
    public int bottomState = R.string.str_base_linked;
    public Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == ConstantValue.HANDLER_SEND){
                handlerSendMsg();
            }else if(msg.what == ConstantValue.HANDLER_BACK){
                handlerBackMsg();
            }else if(msg.what == ConstantValue.HANDLER_DISCONNECT){
                Intent intent = new Intent(getApplicationContext(),BluetoothActivity.class);
                startActivity(intent);
            }else if (msg.what == ConstantValue.HANDLER_THREAD){
                //子线程操作完数据库后的处理
                handlerThreadBack();
            }
        }
    };
    private Timer timer;
    private TimerTask task;
    private TextView bottom_state;
    private TextView bottom_index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        instance = BluetoothLeClass.getInstance(this);
        instance.setOnConnectListener(this);
        instance.setOnDisconnectListener(this);
        instance.setOnServiceDiscoverListener(this);
        instance.setOnDataAvailableListener(this);
        instance.setOnBagSendSuccess(this);
        timer = new Timer();
        initUI();
    }

    private void initUI() {
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
            RelativeLayout layout = (RelativeLayout) findViewById(R.id.bottom_item);
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (bottomState == R.string.str_base_link_n) {
                        if (!MyApplication.isConnect){
                            bottom_state.setText(R.string.str_base_linking);
                            instance.connect(MyApplication.deviceAddress);
                        }
                    }
                }
            });
        }
    }

    public abstract int getLayoutId();
    @Override
    public void onConnect(BluetoothGatt gatt) {
        bottomState = R.string.str_base_linked;
        setBottomState();
        MyApplication.isConnect = true;
        Logger.d("onConnect");
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        byte[] value = characteristic.getValue();
        for (byte b : value) {
            Log.e("TAG","onCharacteristicRead:"+b);
        }
        Logger.d("BlueBaseActivity onCharacteristicRead"+value.length+"...."+characteristic.getValue()[0]);
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        data = BlueProfileBackData.getInstance().getData();
        mHandler.sendEmptyMessage(ConstantValue.HANDLER_BACK);
        Logger.d("BlueBaseActivity onCharacteristicWrite");
    }

    @Override
    public void onDisconnect(BluetoothGatt gatt) {
        bottomState = R.string.str_base_link_n;
        setBottomState();
        MyApplication.isConnect = false;
        instance.close();
//        boolean connect = instance.connect(MyApplication.deviceAddress);
//        mHandler.sendEmptyMessage(ConstantValue.HANDLER_DISCONNECT);
//        Logger.d("BlueBaseActivity onDisconnect:"+MyApplication.isConnect+"..."+connect);
    }

    private void setBottomState() {
        if (bottom_state!=null){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    bottom_state.setText(bottomState);
                }
            });
        }
    }

    @Override
    public void onServiceDiscover(BluetoothGatt gatt) {
        Logger.d("BlueBaseActivity onServiceDiscover");
        if (bottom_state!=null){
            setBottomState();
        }
        if (instance.getReadCharacter(null) == null){
            BluetoothGattService service = instance.mBluetoothGatt.getService(UUID.fromString(BlueGattAttributes.JOURNEY_SERVICE));
            List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
            for (BluetoothGattCharacteristic characteristic : characteristics) {
                String uuid = characteristic.getUuid().toString();
                String lookup = BlueGattAttributes.lookup(uuid, null);
                if (!TextUtils.isEmpty(lookup)){
                    if (lookup.equals("read")) {
                        instance.setReadCharacter(characteristic);
                        instance.setCharacteristicNotification(characteristic, true);
                    } else if (lookup.equals("write")) {
                        instance.setWriteCharacter(characteristic);
                        MyApplication.isConnect = true;
                    }
                }
            }
            listBytes = BlueProfile.getInstance().requestData(BlueProfile.getInstance().contentReal(ConstantValue.FUNC_BATTERY, ""));
            sendMessage();
        }else{
            MyApplication.isConnect = true;
            instance.setCharacteristicNotification(instance.getReadCharacter(null), true);
            listBytes = BlueProfile.getInstance().requestData(BlueProfile.getInstance().contentReal(ConstantValue.FUNC_BATTERY, ""));
            sendMessage();
        }
    }

    int num = 0;
    public void sendMessage(){
        if (MyApplication.isConnect){
            Logger.e(this.getClass()+"...发");
            num = 0;
            index = 0;
            instance.setTimeOut(false);
            if (listBytes == null){
                Logger.d("发送数据内容为空");
                return;
            }
            mHandler.postDelayed(delay,50);
        }else{
            SpUtils.showToast(this,getResources().getString(R.string.str_base_linking));
        }

    }

    Runnable delay = new Runnable() {
        @Override
        public void run() {
            if (num < listBytes.size()){
                num++;
//                mHandler.sendEmptyMessageDelayed(ConstantValue.HANDLER_SEND,50);
                mHandler.sendEmptyMessage(ConstantValue.HANDLER_SEND);
            }
        }
    };

    int index = 0;
    public void handlerSendMsg() {
        instance.writeCharacteristic(instance.getWriteCharacter(listBytes.get(index)));
        index++;
        if (index != listBytes.size()) {
            if (listBytes.size() != 1) {
                task = new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                DbEngine.getInstance(BlueBaseActivity.this).dismissDialog();
                                SpUtils.showToast(getApplicationContext(),"手表无响应");
                            }
                        });
                        num =0;
                        index = 0;
                        instance.setTimeOut(true);
                    }
                };
                timer.schedule(task,10000);
            }
        }

    }

    public void handlerBackMsg() {}
    public void handlerThreadBack(){}

    //发送一个包后确认返回回调
    @Override
    public void onBagSendSuccessMsg() {
        Logger.e(this.getClass()+".....收");
        task.cancel();
        mHandler.postDelayed(delay,50);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!MyApplication.isConnect && MyApplication.deviceAddress != null) {
            instance.connect(MyApplication.deviceAddress);
        }
        if (bottom_index != null){
            if (MyApplication.isConnect) {
                bottom_state.setText("已连接");
            }else{
                bottom_state.setText("未连接");
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (!pm.isScreenOn()) {
            instance.disconnect();
            instance.close();
        }
    }

    @Override
    protected void onDestroy() {
        setContentView(R.layout.no_content);
        super.onDestroy();
        mHandler.removeCallbacks(task);
    }
}
