package org.esec.mcg.bleinsight.wrapper;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
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
    private static final int RSSI_UPDATE_TIME_INTERVAL = 1500; // 1.5 seconds

    private Context mContext;
    private ScanDeviceUiCallbacks mScanDeviceUiCallbacks;
    private InsightDeviceUiCallbacks mInsightDevcieUiCallbacks;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner;
    private BluetoothGatt mBluetoothGatt = null;
    private BluetoothDevice mBluetoothDevice = null;
    private Handler mHandler = new Handler();
    private Queue<Integer> rssiQueue;
    private List<BluetoothGattService> mBluetoothGattServices = null;

    private String mDeviceAddress = "";
    private boolean mConnected = false;

    private Handler mTimerHandler = new Handler();
    private boolean mTimerEnabled = false;

    public List<BluetoothGattService> getCachedServices() {return mBluetoothGattServices;}

    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            mScanDeviceUiCallbacks.uiDeviceFound(result);
        }
    };

    private final BluetoothGattCallback mBleCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                mConnected = true;
                mInsightDevcieUiCallbacks.uiDeviceConnected(mBluetoothGatt, mBluetoothDevice);

                startServicesDiscovery();
                startMonitoringRssiValue();

            }
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                mInsightDevcieUiCallbacks.uiNewRssiAvailable(mBluetoothGatt, mBluetoothDevice, rssi);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                getSupportedServices();
            }
        }
    };

    public BLEWrapper(Context context) {
        this.mContext = context;
    }

    /**
     * 设置扫描设备时ui的callback
     * @param scanDeviceUiCallbacks ui的callback，调用者须实现
     */
    public void setScanDeviceUiCallbacks(ScanDeviceUiCallbacks scanDeviceUiCallbacks) {
        this.mScanDeviceUiCallbacks = scanDeviceUiCallbacks;
    }

    /**
     * 设置发现设备service和characteristic的ui的callback
     * @param insightDeviceUiCallbacks ui的callback，调用者须实现
     */
    public void setInsightDeviceUiCallbacks(InsightDeviceUiCallbacks insightDeviceUiCallbacks) {
        this.mInsightDevcieUiCallbacks = insightDeviceUiCallbacks;
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
     * 获得BluetoothManager & BluetoothAdapter & BluetoothLeScanner的实例
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
                stopScanning();
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

    /**
     * 连接指定地址的设备
     * @param deviceAddress 需要连接的设备地址
     * @return
     */
    public boolean connect(final String deviceAddress) {
        if (mBluetoothAdapter == null || deviceAddress == null) return false;
        mDeviceAddress = deviceAddress;

        // 判断是重新连接还是新建连接
        if (mBluetoothGatt != null && mBluetoothGatt.getDevice().getAddress().equals(deviceAddress)) {
            return mBluetoothGatt.connect();
        } else {
            mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(mDeviceAddress);
            if (mBluetoothDevice == null) {
                return false;
            }
            mBluetoothGatt = mBluetoothDevice.connectGatt(mContext, false, mBleCallback);
        }
        return true;
    }

    /**
     * 断开连接，但是之后可以重连
     */
    public void disconnect() {
        if (mBluetoothGatt != null) mBluetoothGatt.disconnect();
        mInsightDevcieUiCallbacks.uiDeviceConnected(mBluetoothGatt, mBluetoothDevice);
    }

    /**
     * 完全关掉gatt客户端
     */
    public void close() {
        if (mBluetoothGatt != null) mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    /**
     * 开始监听rssi值
     */
    public void startMonitoringRssiValue() {
        readPeriodicalyRssiValue(true);
    }

    public void stopMonitoringRssiValue() {
        readPeriodicalyRssiValue(false);
    }

    /**
     * 周期性地读取更新rssi值
     * @param repeat
     */
    public void readPeriodicalyRssiValue(final boolean repeat) {
        mTimerEnabled = repeat;

        if (mConnected == false || mBluetoothGatt == null || mTimerEnabled == false) {
            mTimerEnabled = false;
            return;
        }

        mTimerHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mBluetoothGatt == null || mBluetoothAdapter == null || mConnected == false) {
                    mTimerEnabled = false;
                    return;
                }

                mBluetoothGatt.readRemoteRssi();
                readPeriodicalyRssiValue(mTimerEnabled);
            }
        }, RSSI_UPDATE_TIME_INTERVAL);
    }

    /**
     * 请求发现设备上提供的所有services
     */
    public void startServicesDiscovery() {
        if (mBluetoothGatt != null) mBluetoothGatt.discoverServices();
    }

    public void getSupportedServices() {
        if (mBluetoothGattServices != null && mBluetoothGattServices.size() > 0) mBluetoothGattServices.clear();

        if (mBluetoothGatt != null) mBluetoothGattServices = mBluetoothGatt.getServices();

        mInsightDevcieUiCallbacks.uiAvailableServices(mBluetoothGatt, mBluetoothDevice, mBluetoothGattServices);
    }
}
