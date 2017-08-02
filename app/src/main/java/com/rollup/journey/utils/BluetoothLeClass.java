/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.rollup.journey.utils;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.util.Log;

import com.orhanobut.logger.Logger;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

/**
 * Service for managing connection and data communication with a GATT server hosted on a
 * given Bluetooth LE device.
 */
public class BluetoothLeClass{
    private final static String TAG = BluetoothLeClass.class.getSimpleName();

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    public BluetoothGatt mBluetoothGatt;
    private BluetoothGattCharacteristic readCharacter;
    private BluetoothGattCharacteristic writeCharacter;
    private BluetoothGattCharacteristic readWriteNameCharacter;
    private BluetoothGattCharacteristic readBatteryCharacter;
    private BluetoothGattCharacteristic wreadEm1506Character;
    private BluetoothGattCharacteristic readVersionCharacter;

    public BluetoothGattCharacteristic getReadCharacter(byte[] request) {
        readCharacter.setValue(request);
        return readCharacter;
    }

    public void setReadCharacter(BluetoothGattCharacteristic readCharacter) {
        this.readCharacter = readCharacter;
    }

    public BluetoothGattCharacteristic getWriteCharacter(byte[] request) {
        writeCharacter.setValue(request);
        return writeCharacter;
    }

    public void setWriteCharacter(BluetoothGattCharacteristic writeCharacter) {
        this.writeCharacter = writeCharacter;
    }

	public interface OnBagSendSuccess {
		void onBagSendSuccessMsg();
	}
    public interface OnConnectListener {
		void onConnect(BluetoothGatt gatt);
	}
	public interface OnDisconnectListener {
		void onDisconnect(BluetoothGatt gatt);
	}
	public interface OnServiceDiscoverListener {
		void onServiceDiscover(BluetoothGatt gatt);
	}
	public interface OnDataAvailableListener {
		 void onCharacteristicRead(BluetoothGatt gatt,
                                          BluetoothGattCharacteristic characteristic,
                                          int status);
		 void onCharacteristicWrite(BluetoothGatt gatt,
                                           BluetoothGattCharacteristic characteristic);
	}
    
	private OnBagSendSuccess mOnBagSendSuccess;
	private OnConnectListener mOnConnectListener;
	private OnDisconnectListener mOnDisconnectListener;
	private OnServiceDiscoverListener mOnServiceDiscoverListener;
	private OnDataAvailableListener mOnDataAvailableListener;
	private static Context mContext;
	public void setOnBagSendSuccess(OnBagSendSuccess l){
        mOnBagSendSuccess = l;
	}public void setOnConnectListener(OnConnectListener l){
		mOnConnectListener = l;
	}
	public void setOnDisconnectListener(OnDisconnectListener l){
		mOnDisconnectListener = l;
	}
	public void setOnServiceDiscoverListener(OnServiceDiscoverListener l){
		mOnServiceDiscoverListener = l;
	}
	public void setOnDataAvailableListener(OnDataAvailableListener l){
		mOnDataAvailableListener = l;
	}
	
	private BluetoothLeClass(){}

    public static class newInstance{
        public static BluetoothLeClass blurtoothLe = new BluetoothLeClass();
    }

    public static BluetoothLeClass getInstance(Context context){
        mContext = context;
        return newInstance.blurtoothLe;
    }
	
    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered.
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
            	if(mOnConnectListener!=null)
            		mOnConnectListener.onConnect(gatt);
                Log.i(TAG, "Connected to GATT server.");
                // Attempts to discover services after successful connection.
                Log.i(TAG, "Attempting to start service discovery:" +
                        mBluetoothGatt.discoverServices());

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                if(mOnDisconnectListener!=null)
                	mOnDisconnectListener.onDisconnect(gatt);
                Log.i(TAG, "Disconnected from GATT server.");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS && mOnServiceDiscoverListener!=null) {
                	mOnServiceDiscoverListener.onServiceDiscover(gatt);
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            Logger.d("onCharacteristicRead：");
        	if (mOnDataAvailableListener!=null)
        		mOnDataAvailableListener.onCharacteristicRead(gatt, characteristic, status);
        }

//        @Override
//        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
//            super.onCharacteristicWrite(gatt, characteristic, status);
//            byte[] value = characteristic.getValue();
//            Logger.d("这是onCharacteristicWrite：");
//            if ((value[0]&0xff) == 0 || (value[0]&0xff) == 254 ) {
//                for (byte b : value) {
//                    Log.d("data","这数据是从onCharacteristicWrite:"+b);
//                }
//            }
//        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            byte[] value = characteristic.getValue();
            Logger.d("这是Bluetoothleclass："+(value[0]&0xff)+".."+(value[1]&0xff)+".."+(value[2]&0xff));
            if ((value[0]&0xff) == 0 || (value[0]&0xff) == 254 ) {
                for (byte b : value) {
                    Log.d("data","这数据是从哪来的:"+b);
                }
            }

            if ((value[0]&0xFF) == 0xF8){
                BlueProfileBackData instance = BlueProfileBackData.getInstance();
                instance.dealBackData(value);
                //如果只有一个包就不返回确认包
                if (value[1] != 0x01){
                    bagBack[0] = (byte) 0xF7;
                    bagBack[1] = value[1];
                    bagBack[2] = value[2];
                    bagBack[3] = 0;
                    writeCharacter.setValue(bagBack);
                    if (!(mOnDataAvailableListener!=null && (value[1]-1) == value[2])) {
                        writeCharacteristic(writeCharacter);
                    }
                }
                if (mOnDataAvailableListener!=null && (value[1]-1) == value[2]) {
                    mOnDataAvailableListener.onCharacteristicWrite(gatt, characteristic);
                }
            }else if((value[0]&0xFF) == 0xF7 && !timeOut){
                mOnBagSendSuccess.onBagSendSuccessMsg();
            }
        }
    };
    byte[] bagBack = new byte[4];

    public boolean initialize() {
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }
        return true;
    }

    public boolean connect(final String address) {
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                return true;
            } else {
                return false;
            }
        }
        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        mBluetoothGatt = device.connectGatt(mContext, false, mGattCallback);
        Log.d(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        return true;
    }

    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
    }

    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    public synchronized void readCharacteristic(final BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
//        Timer timer = new Timer();
//        TimerTask task = new TimerTask() {
//            @Override
//            public void run() {
                mBluetoothGatt.readCharacteristic(characteristic);
//            }
//        };
//        timer.schedule(task,500);
    }

    public synchronized void writeCharacteristic(final BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            initialize();
            return;
        }
//        for (int i = 0; i < 100; i++) {
//            Logger.d("消息怎么没有发送。。。");
//        }
//        Timer timer = new Timer();
//        TimerTask task = new TimerTask() {
//            @Override
//            public void run() {
                boolean b = mBluetoothGatt.writeCharacteristic(characteristic);
//            }
//        };
//        timer.schedule(task,50);
    }

    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        boolean b = mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
    }

    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) return null;
        return mBluetoothGatt.getServices();
    }

    private boolean timeOut = false;
    public void setTimeOut(boolean timeOut){
        this.timeOut = timeOut;
    }
}
