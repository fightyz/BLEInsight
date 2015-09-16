package org.esec.mcg.bleinsight.adapter;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import org.esec.mcg.bleinsight.PeripheralActivity;
import org.esec.mcg.bleinsight.R;
import org.esec.mcg.bleinsight.ScanDeviceActivity;
import org.esec.mcg.bleinsight.wrapper.ScanDeviceUiCallbacks;
import org.esec.mcg.utils.logger.LogUtils;

import java.util.ArrayList;

/**
 * Created by yz on 2015/9/9.
 */
public class DeviceListAdapter extends BaseAdapter {

    private ArrayList<BluetoothDevice> mDevices;
    private ArrayList<ScanRecord> mRecords;
    private ArrayList<Integer> mRssis;
    private LayoutInflater mInflater;
    private ScanDeviceActivity mParent;

    private String name;
    private String address;
    private String rssiString;
    private String bondStateString;

    public DeviceListAdapter(Activity parent) {
        super();
        mDevices    = new ArrayList<BluetoothDevice>();
        mRecords    = new ArrayList<ScanRecord>();
        mRssis      = new ArrayList<Integer>();
        mInflater   = parent.getLayoutInflater();
        mParent     = (ScanDeviceActivity)parent;
    }

    public void addDevice(ScanResult scanResult) {
        BluetoothDevice device = scanResult.getDevice();
        ScanRecord record = scanResult.getScanRecord();
        int rssi = scanResult.getRssi();
        if (mDevices.contains(device) == false) {
            mDevices.add(device);
            mRecords.add(record);
            mRssis.add(rssi);
        } else {
            int index = mDevices.indexOf(device);
            mRssis.set(index, rssi);
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
    public View getView(int position, View convertView, final ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.activity_device_scan_item, null);
            viewHolder = new ViewHolder();
            viewHolder.deviceName       = (TextView) convertView.findViewById(R.id.tv_device_name);
            viewHolder.deviceAddress    = (TextView) convertView.findViewById(R.id.tv_device_address);
            viewHolder.deviceBondState  = (TextView) convertView.findViewById(R.id.tv_bond_state);
            viewHolder.deviceRssi       = (TextView) convertView.findViewById(R.id.tv_rssi_value);
            viewHolder.connect          = (Button) convertView.findViewById(R.id.btn_connect);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        BluetoothDevice device = mDevices.get(position);

        name = device.getName();
        if (name == null || name.length() <= 0) name = "Unknown Device";

        address = device.getAddress();

        int bondState = device.getBondState();
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
        rssiString = (rssi == 0) ? "N/A" : rssi + " db";

        viewHolder.deviceName.setText(name);
        viewHolder.deviceAddress.setText(address);
        viewHolder.deviceBondState.setText(bondStateString);
        viewHolder.deviceRssi.setText(rssiString);
        viewHolder.connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(mParent, PeripheralActivity.class);
                intent.putExtra(PeripheralActivity.EXTRAS_DEVICE_NAME, name);
                intent.putExtra(PeripheralActivity.EXTRAS_DEVICE_ADDRESS, address);
                intent.putExtra(PeripheralActivity.EXTRAS_DEVICE_RSSI, rssiString);
                intent.putExtra(PeripheralActivity.EXTRAS_DEVICE_BOND, bondStateString);

                if (ScanDeviceActivity.mScanning) {
                    ScanDeviceActivity.mScanning = false;
                    mParent.invalidateOptionsMenu();
                    mParent.mBLEWrapper.stopScanning();
                }

                mParent.startActivity(intent);
            }
        });

        return convertView;
    }

    private class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
        TextView deviceBondState;
        TextView deviceRssi;
        Button connect;
    }
}
