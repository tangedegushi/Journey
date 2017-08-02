package com.rollup.journey.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.rollup.journey.R;
import com.rollup.journey.adapter.Blue_scan_listview;
import com.rollup.journey.bean.BlueGattAttributes;
import com.rollup.journey.utils.BlueProfile;
import com.rollup.journey.utils.BlueProfileBackData;
import com.rollup.journey.utils.ConstantValue;
import com.rollup.journey.utils.DbEngine;
import com.rollup.journey.utils.SpUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zq on 2016/12/19.
 */
public class BluetoothActivity extends BlueBaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private static final int REQUEST_ENABLE_BT = 100;
    private boolean isableBluetooth = false;
    private TextView blue_btn1;
    private TextView blue_btn2;
    private ListView blue_list;
    private BluetoothAdapter mBluetoothAdapter;
    private Blue_scan_listview mLeDeviceListAdapter;

    private boolean mScanning;
    private Handler mHandler;

    private static final long SCAN_PERIOD = 3000;
    private String teamName;
    private List<String> allDeviceNameAndIndex;
    private ProgressBar progress;
    private String deviceAddress;
    private Timer timer;
    boolean isOpen = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();
        initView();
        initEvent();
        initBluetooth();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_bluetooth;
    }

    private void initEvent() {
        mLeDeviceListAdapter = new Blue_scan_listview(this);
        blue_list.setAdapter(mLeDeviceListAdapter);
    }

    private void initView() {
        findViewById(R.id.blue_title).findViewById(R.id.imageView).setVisibility(View.INVISIBLE);
        blue_list = (ListView) findViewById(R.id.blue_list);
        progress = (ProgressBar) findViewById(R.id.blue_progress);
        blue_btn1 = (TextView) findViewById(R.id.blue_btn1);
        blue_btn2 = (TextView) findViewById(R.id.blue_btn2);
        blue_btn1.setOnClickListener(this);
        blue_list.setOnItemClickListener(this);
    }

    private void setDialog() {
        blue_btn1.setText(R.string.str_blue_link_s);
        blue_btn1.setBackgroundResource(R.mipmap.blue_btn_s);
        deviceAddress = SpUtils.getString(this, ConstantValue.DEVICEADDRESS, null);
        if (deviceAddress != null){
            blue_btn2.setText(R.string.str_blue_link_quick);
            blue_btn2.setBackgroundResource(R.mipmap.blue_btn_s);
            blue_btn2.setOnClickListener(this);
        }
    }

    private void initBluetooth() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            SpUtils.showToast(this, getResources().getString(R.string.str_blue_device_no_ble));
            finish();
        }
        BluetoothManager manager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = manager.getAdapter();
        if (mBluetoothAdapter == null) {
            SpUtils.showToast(getApplicationContext(), getResources().getString(R.string.str_blue_device_no_blue));
            finish();
        } else {
            if (mBluetoothAdapter.isEnabled()) {
                isableBluetooth = true;
                scanLeDevice(true);
                setDialog();
            }
        }
        if (!instance.initialize()) {
            SpUtils.showToast(this, getResources().getString(R.string.str_blue_device_no_ble));
            Logger.e("Unable to initialize Bluetooth");
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.blue_btn1:
                if (!isableBluetooth) {
                    openBluetooth();
                }else{
                    scanLeDevice(true);
                }
                break;
            case R.id.blue_btn2:
                if (mScanning) {
                    scanLeDevice(false);
                }
                DbEngine.getInstance(this).showDialog(BluetoothActivity.this, getResources().getString(R.string.str_data_loading));
                if (MyApplication.deviceAddress != null && MyApplication.isConnect && (MyApplication.deviceAddress == deviceAddress)){
                    listBytes = BlueProfile.getInstance().requestData(BlueProfile.getInstance().contentReal(ConstantValue.FUNC_BATTERY, ""));
                    sendMessage();
                }else{
                    MyApplication.deviceAddress = deviceAddress;
                    instance.connect(deviceAddress);//"06:02:01:33:22:22"
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!MyApplication.isConnect) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        DbEngine.getInstance(BluetoothActivity.this).dismissDialog();
                                        SpUtils.showToast(BluetoothActivity.this,getResources().getString(R.string.str_link_fail_relink));
                                    }
                                });
                            }
                        }
                    },15000);
                }
//                instance.readCharacteristic(instance.getReadCharacter(null));
                break;
            default:
                break;
        }
    }

    private void openBluetooth() {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        isableBluetooth = true;
        setDialog();
        scanLeDevice(true);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final BluetoothDevice device = mLeDeviceListAdapter.getDevice(position);
        if (device == null) return;
        if (MyApplication.deviceAddress != null && MyApplication.isConnect){
            if (MyApplication.deviceAddress.equals(device.getAddress())){
                listBytes = BlueProfile.getInstance().requestData(BlueProfile.getInstance().contentReal(ConstantValue.FUNC_BATTERY, ""));
                sendMessage();
            }
        }else{
            MyApplication.deviceAddress = device.getAddress();
            if (mScanning) {
                scanLeDevice(false);
            }
            DbEngine.getInstance(this).showDialog(this, getResources().getString(R.string.str_data_loading));
            instance.connect(device.getAddress());
            timer = new Timer();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            DbEngine.getInstance(BluetoothActivity.this).dismissDialog();
                            SpUtils.showToast(BluetoothActivity.this,getResources().getString(R.string.str_link_time_out));
                        }
                    });
                }
            };
            timer.schedule(task,15000);
        }
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    invalidateOptionsMenu();
                    progress.setVisibility(View.GONE);
                }
            }, SCAN_PERIOD);
            progress.setVisibility(View.VISIBLE);
            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            progress.setVisibility(View.GONE);
        }
        invalidateOptionsMenu();
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mLeDeviceListAdapter.addDevice(device);
                                mLeDeviceListAdapter.notifyDataSetChanged();
                            }
                        });
                }
            };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mBluetoothAdapter.isEnabled()) {
            SpUtils.showToast(this, getResources().getString(R.string.str_blue_open));
            return false;
        }
        getMenuInflater().inflate(R.menu.blue_scan_menu, menu);
        menu.findItem(R.id.menu_scan).setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_scan:
                scanLeDevice(true);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onServiceDiscover(BluetoothGatt gatt) {
        Logger.d("onServiceDiscover.........................");
        List<BluetoothGattService> gattServices = instance.getSupportedGattServices();
        for (BluetoothGattService service : gattServices) {
            String serviceUUID = service.getUuid().toString();
//            Logger.d("输出服务的UUID："+serviceUUID);
            String lookup = BlueGattAttributes.lookup(serviceUUID, null);
            if (!TextUtils.isEmpty(lookup)) {
                List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
                for (BluetoothGattCharacteristic character : characteristics) {
                    String characterUUID = character.getUuid().toString();
//                    Logger.d("输出character的UUID:"+characterUUID+"..."+character.getProperties());
                    String characterValue = BlueGattAttributes.lookup(characterUUID, null);
                    if (!TextUtils.isEmpty(characterValue)) {
                        if (characterValue.equals("read")) {
                            instance.setReadCharacter(character);
                            instance.setCharacteristicNotification(character, true);
                        } else if (characterValue.equals("write")) {
                            instance.setWriteCharacter(character);
                            isOpen = true;
                            MyApplication.isConnect = true;
                        }
                    }
                }
            }
        }
        if (isOpen){
            listBytes = BlueProfile.getInstance().requestData(BlueProfile.getInstance().contentReal(ConstantValue.FUNC_BATTERY, ""));
            sendMessage();
        }
    }

    @Override
    public void handlerBackMsg() {
        byte dataType = BlueProfileBackData.getInstance().getDataType();
        Logger.d(dataType);
        for (byte b : data) {
            Log.e("TAG",b+"");
        }
        if (ConstantValue.FUNC_BATTERY == dataType) {
            MyApplication.battery = data[0];
            Logger.d("这是电量："+MyApplication.battery);
            teamName = SpUtils.getString(this, ConstantValue.TEAMNAME, null);
            //处理团名是否一致
            if (teamName != null) {
                listBytes = BlueProfile.getInstance().requestData(BlueProfile.getInstance().contentReal(ConstantValue.FUNC_REDD_MD5, ""));
                sendMessage();
            } else {
                //清楚设备端数据
                clearRemoteDeviceData();
            }
        } else if (ConstantValue.FUNC_REDD_MD5 == dataType) {
            //判断手机端和设备端存储的团档是否一致
            byte[] md5Byte = null;
            if (teamName != null) {
                try {
                    MessageDigest md5 = MessageDigest.getInstance("MD5");
                    md5Byte = md5.digest(teamName.getBytes("UTF8"));
                } catch (NoSuchAlgorithmException e) {
                } catch (Exception e) {
                }
            }
            if (Arrays.equals(data, md5Byte)) {
                openActivity();
            } else {
                clearRemoteDeviceData();
            }
        } else if (ConstantValue.FUNC_ADD_DEV == dataType) {
            //同步数据返回后处理
            if (data[2] == ConstantValue.BACK_STATE_SUC){
                openActivity();
            }else{
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setMessage(R.string.str_dialog_message_update_device_fail)
                        .setNegativeButton(R.string.str_dialog_no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                                openActivity();
                            }
                        })
                        .setPositiveButton(R.string.str_dialog_yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                byte[] bytes = BlueProfile.getInstance().contentAddMulDevice(allDeviceNameAndIndex);
                                listBytes = BlueProfile.getInstance().requestData(bytes);
                                sendMessage();
                            }
                        }).create();
                dialog.show();
            }
        } else if (ConstantValue.FUNC_CLEAR_MSG == dataType) {
            //清空数据返回后处理,先添加团名
            if(data[0] == ConstantValue.BACK_STATE_SUC){
                if (teamName != null) {
                    //同步数据
                    byte[] bytes = BlueProfile.getInstance().contentMd5(teamName);
                    listBytes = BlueProfile.getInstance().requestData(bytes);
                    for (byte[] listByte : listBytes) {
                        for (byte b : listByte) {
                            System.out.println(b);
                        }
                    }
                    sendMessage();
                }else {
                    openActivity();
                }
            }else{
                SpUtils.showToast(this, getResources().getString(R.string.str_message_clear_fail));
            }
        } else if (ConstantValue.FUNC_WRITE_MD5 == dataType) {
            //再添加团成员
            if (data[0] == ConstantValue.BACK_STATE_SUC) {
                allDeviceNameAndIndex = DbEngine.getInstance(this).getAllDeviceNameAndIndex();
                Logger.d(allDeviceNameAndIndex);
                if (allDeviceNameAndIndex.isEmpty()) {
                    openActivity();
                }else{
                    byte[] bytes = BlueProfile.getInstance().contentAddMulDevice(allDeviceNameAndIndex);
                    listBytes = BlueProfile.getInstance().requestData(bytes);
                    for (byte[] listByte : listBytes) {
                        for (byte b : listByte) {
                            Log.d("同步设备",b+"..");
                        }
                    }
                    Logger.e("有几组数据："+listBytes.size());
                    sendMessage();
                }
            }else{
                SpUtils.showToast(this, getResources().getString(R.string.str_message_update_device_fail));
            }
        }
    }

    private void clearRemoteDeviceData() {
        listBytes = BlueProfile.getInstance().requestData(BlueProfile.getInstance().contentClearData());
        sendMessage();
    }

    private void openActivity() {
        if (timer != null) {
            timer.cancel();
        }
        SpUtils.putString(this,ConstantValue.DEVICEADDRESS,MyApplication.deviceAddress);
        DbEngine.getInstance(this).dismissDialog();
        Intent intent = new Intent(BluetoothActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
