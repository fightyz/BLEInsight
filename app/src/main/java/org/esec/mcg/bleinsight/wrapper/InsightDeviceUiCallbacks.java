package org.esec.mcg.bleinsight.wrapper;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;

/**
 * Created by yz on 2015/9/22.
 */
public interface InsightDeviceUiCallbacks {
    public void uiDeviceConnected(final BluetoothGatt gatt, final BluetoothDevice device);
}
