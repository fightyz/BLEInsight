package org.esec.mcg.bleinsight.wrapper;

import android.bluetooth.BluetoothDevice;

/**
 * Created by yz on 2015/9/9.
 */
public interface ScanDeviceUiCallbacks {

    public void uiDeviceFound(final BluetoothDevice device, int rssi, byte[] record);
    public void uiStopScanning();
}
