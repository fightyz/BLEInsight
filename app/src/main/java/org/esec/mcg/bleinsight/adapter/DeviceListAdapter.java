package org.esec.mcg.bleinsight.adapter;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.esec.mcg.bleinsight.R;

import java.util.ArrayList;

/**
 * Created by yz on 2015/9/9.
 */
public class DeviceListAdapter extends BaseAdapter {

    private ArrayList<BluetoothDevice> mDevices;
    private ArrayList<byte[]> mRecords;
    private ArrayList<Integer> mRssis;
    private LayoutInflater mInflater;

    public DeviceListAdapter(Activity parent) {
        super();
        mDevices    = new ArrayList<BluetoothDevice>();
        mRecords    = new ArrayList<byte[]>();
        mRssis      = new ArrayList<Integer>();
        mInflater   = parent.getLayoutInflater();
    }

    public void addDevice(BluetoothDevice device, int rssi, byte[] record) {
        if (mDevices.contains(device) == false) {
            mDevices.add(device);
            mRecords.add(record);
            mRssis.add(rssi);
        }
    }

    public void clearList() {
        mDevices.clear();
        mRecords.clear();
        mRssis.clear();
    }

    @Override
    public int getCount() {
        return mDevices.size();
    }

    @Override
    public Object getItem(int position) {
        return mDevices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.activity_device_scan_item, null);
            viewHolder = new ViewHolder();
            viewHolder.deviceName       = (TextView) convertView.findViewById(R.id.tv_device_name);
            viewHolder.deviceAddress    = (TextView) convertView.findViewById(R.id.tv_device_address);
            viewHolder.deviceBondState  = (TextView) convertView.findViewById(R.id.tv_bond_state);
            viewHolder.deviceRssi       = (TextView) convertView.findViewById(R.id.tv_rssi_value);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        BluetoothDevice device = mDevices.get(position);

        String name = device.getName();
        if (name == null || name.length() <= 0) name = "Unknown Device";

        String address = device.getAddress();

        int bondState = device.getBondState();
        String bondStateString;
        switch (bondState) {
            case BluetoothDevice.BOND_BONDED:
                bondStateString = "BONDED";
                break;
            case BluetoothDevice.BOND_BONDING:
                bondStateString = "BONDING";
                break;
            case BluetoothDevice.BOND_NONE:
                bondStateString = "NOT BONDED";
                break;
            default:
                bondStateString = null;
        }

        int rssi = mRssis.get(position);
        String rssiString = (rssi == 0) ? "N/A" : rssi + " db";

        viewHolder.deviceName.setText(name);
        viewHolder.deviceAddress.setText(address);
        viewHolder.deviceBondState.setText(bondStateString);
        viewHolder.deviceRssi.setText(rssiString);

        return convertView;
    }

    private class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
        TextView deviceBondState;
        TextView deviceRssi;
    }
}
