package org.esec.mcg.bleinsight.adapter;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.esec.mcg.bleinsight.PeripheralActivity;
import org.esec.mcg.bleinsight.PeripheralDetailActivity;
import org.esec.mcg.bleinsight.R;
import org.esec.mcg.bleinsight.ScanDeviceActivity;
import org.esec.mcg.utils.logger.LogUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by yangzhou on 9/26/15.
 */
public class ScanDeviceAdapter extends RecyclerView.Adapter<ScanDeviceAdapter.ListItemViewHolder> {

    private ArrayList<BluetoothDevice> mDevices;
    private ArrayList<ScanRecord> mRecords;
    private ArrayList<Integer> mRssis;
    private ArrayList<Queue<Integer>> mRssiList; /* ArrayList对应每个设备，Queue对应设备的rssi值队列 */
    private ScanDeviceActivity mParent;

    public static boolean startUpdateRssiThread = true;
    private static boolean repeatEnable = false;

    private Handler rssiUpdateHandler = new Handler();

    public ScanDeviceAdapter(Activity parent) {
        super();
        mDevices    = new ArrayList<BluetoothDevice>();
        mRecords    = new ArrayList<ScanRecord>();
        mRssiList   = new ArrayList<Queue<Integer>>();
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
            updatePeriodicalyRssi(true);
        }
    }

    public void updatePeriodicalyRssi(final boolean repeat) {
        LogUtils.d("repeat = " + repeat);
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
                LogUtils.d("notifyDataSetChanged???");
                notifyDataSetChanged(); /* rssi值列表有更新，通知界面刷新 */
                updatePeriodicalyRssi(repeatEnable);
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
    public ListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.activity_device_scan_item, parent, false);
        return new ListItemViewHolder(itemView);

    }

    @Override
    public long getItemId(int position) {
        LogUtils.d("getItemId: " + position);
        return position;
    }

    @Override
    public void onBindViewHolder(ListItemViewHolder holder, final int position) {
        final String name;
        final String address;
        final String rssiString;
        final String bondStateString;

        LogUtils.d("onBindViewHolder: " + position);
        BluetoothDevice device = mDevices.get(position);
        LogUtils.d(mDevices);

        String tmpName = device.getName();
        if (tmpName == null || tmpName.length() <= 0) name = "Unknown Device";
        else name = tmpName;

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

        holder.deviceName.setText(name);
        holder.deviceAddress.setText(address);
        holder.deviceBondState.setText(bondStateString);
        holder.deviceRssi.setText(rssiString);
        holder.connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                final Intent intent = new Intent(mParent, PeripheralActivity.class);
//                intent.putExtra(PeripheralActivity.EXTRAS_DEVICE_NAME, name);
//                intent.putExtra(PeripheralActivity.EXTRAS_DEVICE_ADDRESS, address);
//                intent.putExtra(PeripheralActivity.EXTRAS_DEVICE_RSSI, rssiString);
//                intent.putExtra(PeripheralActivity.EXTRAS_DEVICE_BOND, bondStateString);
//
//                if (ScanDeviceActivity.mScanning) {
//                    mParent.mBLEWrapper.stopScanning();
//                }
//
//                mParent.startActivity(intent);
                LogUtils.d(mDevices);
                LogUtils.d("On Click Connect Button: " + position);
                LogUtils.d("device name: " + name);
                LogUtils.d("device address: " + address);
                final Intent intent = new Intent(mParent, PeripheralDetailActivity.class);
                intent.putExtra(PeripheralDetailActivity.EXTRAS_DEVICE_NAME, name);
                intent.putExtra(PeripheralDetailActivity.EXTRAS_DEVICE_ADDRESS, address);
                intent.putExtra(PeripheralDetailActivity.EXTRAS_DEVICE_RSSI, rssiString);
                intent.putExtra(PeripheralDetailActivity.EXTRAS_DEVICE_BOND, bondStateString);

                if (ScanDeviceActivity.mScanning) {
                    mParent.mBLEWrapper.stopScanning();
                }

                mParent.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDevices.size();
    }

    public final static class ListItemViewHolder extends RecyclerView.ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
        TextView deviceBondState;
        TextView deviceRssi;
        Button connect;

        public ListItemViewHolder(View itemView) {
            super(itemView);
            deviceName = (TextView) itemView.findViewById(R.id.tv_device_name);
            deviceAddress = (TextView) itemView.findViewById(R.id.tv_device_address);
            deviceBondState = (TextView) itemView.findViewById(R.id.tv_bond_state);
            deviceRssi = (TextView) itemView.findViewById(R.id.tv_rssi_value);
            connect = (Button) itemView.findViewById(R.id.btn_connect);
        }
    }
}
