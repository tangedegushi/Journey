package com.rollup.journey.receiver;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.orhanobut.logger.Logger;

/**
 * Created by zq on 2016/12/19.
 */

public class BlueChangReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
//        监听手机本身蓝牙状态的广播 BluetoothAdapter.ACTION_STATE_CHANGED
        if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                    BluetoothAdapter.STATE_OFF);
            switch (state) {
                case BluetoothAdapter.STATE_OFF:
                    Logger.d("STATE_OFF 手机蓝牙关闭");
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                    Logger.d("STATE_TURNING_OFF 手机蓝牙正在关闭");
                    break;
                case BluetoothAdapter.STATE_ON:
                    Logger.d( "STATE_ON 手机蓝牙开启");
                    break;
                case BluetoothAdapter.STATE_TURNING_ON:
                    Logger.d("STATE_TURNING_ON 手机蓝牙正在开启");
                    break;
            }
        }
//        蓝牙设备配对和解除配对时发送
//        action: BluetoothDevice.ACTION_BOND_STATE_CHANGED
        if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
//            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//            String name = device.getName();
//            Log.d("aaa", "device name: " + name);
            int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1);
            switch (state) {
                case BluetoothDevice.BOND_NONE:
                    Logger.d("BOND_NONE 删除配对");
                    break;
                case BluetoothDevice.BOND_BONDING:
                    Logger.d( "BOND_BONDING 正在配对");
                    break;
                case BluetoothDevice.BOND_BONDED:
                    Logger.d( "BOND_BONDED 配对成功");
                    break;
            }
        }
//        蓝牙设备连接上和断开连接时发送, 这两个监听的是底层的连接状态
//        action: BluetoothDevice.ACTION_ACL_CONNECTED   BluetoothDevice.ACTION_ACL_DISCONNECTED
        if (action.equals(BluetoothDevice.ACTION_ACL_CONNECTED)) {
//            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            Logger.d(  " ACTION_ACL_CONNECTED");
        } else if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
//            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            Logger.d(" ACTION_ACL_DISCONNECTED");
        }

    }
}
