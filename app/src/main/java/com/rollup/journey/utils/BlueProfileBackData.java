package com.rollup.journey.utils;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * adb
 * Created by zq on 2017/1/6.
 */

public class BlueProfileBackData {

    public List<Byte> dataList = new ArrayList<>();
    private byte dataType;

    private BlueProfileBackData() {
    }

    private static class newInstance {
        private static BlueProfileBackData instance = new BlueProfileBackData();
    }

    public static BlueProfileBackData getInstance() {
        return newInstance.instance;
    }

    //处理返回来的数据
    public int dealBackData(byte[] data) {
        byte bagCurrent = data[2];
        if (bagCurrent == 0) {
            dataList.clear();
            dataType = data[4];
            Logger.d(dataType);
            for (int i = 6; i < data.length; i++) {
                dataList.add(data[i]);
            }
        }else{
            for (int i = 4; i < data.length; i++) {
                dataList.add(data[i]);
            }
        }
        Logger.d(dataList);
        return bagCurrent;
    }

    //返回数据集合 数据层所有数据
    public byte[] getData() {
        byte[] bt = new byte[dataList.size()];
        Byte[] bytes = dataList.toArray(new Byte[0]);
        for (int i = 0; i < bytes.length; i++) {
            bt[i] = bytes[i];
        }
        return bt;
    }

    //返回数据类型
    public byte getDataType() {
        return dataType;
    }
}
