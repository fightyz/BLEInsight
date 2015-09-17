package org.esec.mcg.bleinsight.adapter;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.os.Handler;
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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by yz on 2015/9/9.
 */
public class DeviceListAdapter extends BaseAdapter {

    private ArrayList<BluetoothDevice> mDevices;
    private ArrayList<ScanRecord> mRecords;
    private ArrayList<Integer> mRssis;
    private ArrayList<Queue<Integer>> mRssiList; /* ArrayList对应每个设备，Queue对应设备的rssi值队列 */
    private LayoutInflater mInflater;
    private ScanDeviceActivity mParent;

    private String name;
    private String address;
    private String rssiString;
    private String bondStateString;
    public static boolean startUpdateRssiThread = true;
    private static boolean repeatEnable = false;

    private Handler rssiUpdateHandler = new Handler();

    public DeviceListAdapter(Activity parent) {
        super();
        mDevices    = new ArrayList<BluetoothDevice>();
        mRecords    = new ArrayList<ScanRecord>();
        mRssiList   = new ArrayList<Queue<Integer>>();
        mInflater   = parent.getLayoutInflater();
        mParent     = (ScanDeviceActivity)parent;
        mRssis      = new ArrayList<Integer>();
    }

    public void addDevice(ScanResult scanResult) {
        BluetoothDevice device = scanResult.getDevice();
        ScanRecord record = scanResult.getScanRecord();
        int rssi = scanResult.getRssi();
        if (mDevices.contains(device) == false) { /* 扫描到新的设备 */
            mDevices.add(device);
            mRecords.add(record);
            mRssis.add(rssi);

            Queue rssiQueue = new LinkedList<Integer>(); /* 新建rssi值队列 */
            rssiQueue.offer(rssi);
            mRssiList.add(rssiQueue); /* 将队列加入设备ArrayList */

        } else { /* 扫描到已有设备，更新设备对应rssi值Queue */
            int index = mDevices.indexOf(device);
//            mRssis.set(index, rssi);
            mRssiList.get(index).offer(rssi);
        }

        if (startUpdateRssiThread) {
            startUpdateRssiThread = false;
            updatePeriodicalyeRssi(true);
        }
    }

    public void updatePeriodicalyeRssi(final boolean repeat) {
        repeatEnable = repeat;
        if (repeatEnable == false) {
            startUpdateRssiThread = true;
            return;
        }
        Runnable updateRssiThread = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < mRssiList.size(); i++) { /* 遍历设备列表 */
                    Queue<Integer> rssiQueue = mRssiList.get(i); /* 取出一个设备的rssi值队列 */
                    while (rssiQueue.size() > 5) {
                        rssiQueue.remove();
                    }
                    if (rssiQueue.peek() != null) { /* 如果有rssi值 */
                        mRssis.set(i, rssiQueue.remove()); /* 更新adapter中rssi值列表 */
                    }
                } // 设备列表遍历完成
                notifyDataSetChanged(); /* rssi值列表有更新，通知界面刷新 */
                updatePeriodicalyeRssi(repeatEnable);
            }
        };
        rssiUpdateHandler.postDelayed(updateRssiThread, 1000);
    }

    public void clearList() {
        mDevices.clear();
        mRecords.clear();
        mRssis.clear();
        mRssiList.clear();
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

        int rssi;
        if (position >= mRssis.size()) rssi = 0;
        else rssi = mRssis.get(position);
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
                    updatePeriodicalyeRssi(false);
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
