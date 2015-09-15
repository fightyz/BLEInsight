package org.esec.mcg.bleinsight.wrapper;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;

/**
 * Created by yz on 2015/9/9.
 */
public class BLEWrapper {
    private static final long SCANNING_TIMEOUT = 5 * 1000;

    private Context mContext;
    private ScanDeviceUiCallbacks mScanDeviceUiCallbacks;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler = new Handler();

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            mScanDeviceUiCallbacks.uiDeviceFound(device, rssi, scanRecord);
        }
    };

    public BLEWrapper(Context context) {
        this.mContext = context;
    }

    public void setScanDeviceUiCallbacks(ScanDeviceUiCallbacks scanDeviceUiCallbacks) {
        this.mScanDeviceUiCallbacks = scanDeviceUiCallbacks;
    }

    public boolean checkBleHardwareAvailable() {
        final BluetoothManager manager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        if (manager == null) return false;

        final BluetoothAdapter adapter = manager.getAdapter();
        if (adapter == null) return false;

        boolean hasBLE = mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
        return hasBLE;
    }

    public boolean isBtEnabled() {
        final BluetoothManager manager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        if(manager == null) return false;

        final BluetoothAdapter adapter = manager.getAdapter();
        if(adapter == null) return false;

        return adapter.isEnabled();
    }

    public boolean initialize() {
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) return false;
        }

        if (mBluetoothAdapter == null) {
            mBluetoothAdapter = mBluetoothManager.getAdapter();
            if (mBluetoothAdapter == null) return false;
        }

        return true;
    }

    public void startScanning(long scanningTimeout) {
        if (scanningTimeout == 0) {
            scanningTimeout = SCANNING_TIMEOUT;
        }
        Runnable timeout = new Runnable() {
            @Override
            public void run() {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                mScanDeviceUiCallbacks.uiStopScanning();
            }
        };
        mHandler.postDelayed(timeout, scanningTimeout);
        mBluetoothAdapter.startLeScan(mLeScanCallback);
    }

    public void stopScanning() {
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
    }
}
