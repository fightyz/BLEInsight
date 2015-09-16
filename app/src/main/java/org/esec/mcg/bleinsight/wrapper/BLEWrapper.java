package org.esec.mcg.bleinsight.wrapper;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.util.Log;

import org.esec.mcg.utils.logger.LogUtils;

import java.util.List;
import java.util.Queue;

/**
 * Created by yz on 2015/9/9.
 */
public class BLEWrapper {
    private static final long SCANNING_TIMEOUT = 5 * 1000;

    private Context mContext;
    private ScanDeviceUiCallbacks mScanDeviceUiCallbacks;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner;
    private Handler mHandler = new Handler();
    private Queue<Integer> rssiQueue;

    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            LogUtils.d(result);
            mScanDeviceUiCallbacks.uiDeviceFound(result);
        }

//        @Override
//        public void onBatchScanResults(List<ScanResult> results) {
//            super.onBatchScanResults(results);
//            LogUtils.d(results);
//        }


    };

    public BLEWrapper(Context context) {
        this.mContext = context;
    }

    /**
     * 设置ui的callback
     * @param scanDeviceUiCallbacks ui的callback，调用者须实现
     */
    public void setScanDeviceUiCallbacks(ScanDeviceUiCallbacks scanDeviceUiCallbacks) {
        this.mScanDeviceUiCallbacks = scanDeviceUiCallbacks;
    }

    /**
     * 检查BLE是否可用
     * @return
     */
    public boolean checkBleHardwareAvailable() {
        final BluetoothManager manager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        if (manager == null) return false;

        final BluetoothAdapter adapter = manager.getAdapter();
        if (adapter == null) return false;

        boolean hasBLE = mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
        return hasBLE;
    }

    /**
     * 检查蓝牙是否开启
     * @return
     */
    public boolean isBtEnabled() {
        final BluetoothManager manager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        if(manager == null) return false;

        final BluetoothAdapter adapter = manager.getAdapter();
        if(adapter == null) return false;

        return adapter.isEnabled();
    }

    /**
     * 获得BluetoothManager & BluetoothAdapter的实例
     * @return
     */
    public boolean initialize() {
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) return false;
        }

        if (mBluetoothAdapter == null) {
            mBluetoothAdapter = mBluetoothManager.getAdapter();
            if (mBluetoothAdapter == null) return false;
        }

        if (mBluetoothLeScanner == null) {
            mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
            if (mBluetoothLeScanner == null) return false;
        }

        return true;
    }

    /**
     * 开始扫描BLE设备
     * @param scanningTimeout 扫描的timeout时间，如果是0则默认5秒
     */
    public void startScanning(long scanningTimeout) {
        if (scanningTimeout == 0) {
            scanningTimeout = SCANNING_TIMEOUT;
        }
        Runnable timeout = new Runnable() {
            @Override
            public void run() {
                mBluetoothLeScanner.stopScan(mScanCallback);
                mScanDeviceUiCallbacks.uiStopScanning();
            }
        };
        mHandler.postDelayed(timeout, scanningTimeout);
//        mBluetoothLeScanner.startScan(null,
//                new ScanSettings.Builder().setReportDelay(100).build(),
//                mScanCallback);
        mBluetoothLeScanner.startScan(mScanCallback);

    }

    /**
     * 停止扫描BLE设备
     */
    public void stopScanning() {
        mBluetoothLeScanner.stopScan(mScanCallback);
    }
}
