package com.rollup.journey.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.rollup.journey.R;

import java.util.ArrayList;

/**
 * Created by zq on 2016/12/27.
 */

public class Blue_scan_listview extends BaseAdapter {

        private ArrayList<BluetoothDevice> mLeDevices;
        private Context mContext;

        public Blue_scan_listview(Context context) {
            mLeDevices = new ArrayList<BluetoothDevice>();
            mContext = context;
        }

        public void addDevice(BluetoothDevice device) {
            if(!mLeDevices.contains(device)) {
                mLeDevices.add(device);
            }
        }

        public BluetoothDevice getDevice(int position) {
            return mLeDevices.get(position);
        }

        public void clear() {
            mLeDevices.clear();
        }

        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int i) {
            return mLeDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.blue_scan_item, null);
                viewHolder = new ViewHolder();
                viewHolder.deviceAddress = (TextView) view.findViewById(R.id.blue_scan_address);
                viewHolder.deviceName = (TextView) view.findViewById(R.id.blue_scan_name);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            BluetoothDevice device = mLeDevices.get(i);
            final String deviceName = device.getName();
            if (deviceName != null && deviceName.length() > 0)
                viewHolder.deviceName.setText(deviceName);
            else
                viewHolder.deviceName.setText(R.string.str_blue_device_no_know);
            viewHolder.deviceAddress.setText(device.getAddress());

            return view;
        }

    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
    }


}
