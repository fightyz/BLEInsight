package org.esec.mcg.bleinsight.wrapper;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanResult;

/**
 * Created by yz on 2015/9/9.
 */
public interface ScanDeviceUiCallbacks {

    public void uiDeviceFound(ScanResult scanResult);
    public void uiStopScanning();
}
