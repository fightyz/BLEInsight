package org.esec.mcg.bleinsight.wrapper;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import java.util.List;

/**
 * Created by yz on 2015/9/22.
 */
public interface InsightDeviceUiCallbacks {
    void uiDeviceConnected(final BluetoothGatt gatt, final BluetoothDevice device);
    void uiNewRssiAvailable(final BluetoothGatt gatt, final BluetoothDevice device, final int rssi);
    void uiDeviceDisconnected(final BluetoothGatt gatt, final BluetoothDevice device);
    void uiAvailableServices(final BluetoothGatt gatt, final BluetoothDevice device, final List<BluetoothGattService> services);
    void uiCharacteristicsForService(final BluetoothGatt gatt,
                                            final BluetoothDevice device,
                                            final BluetoothGattService service,
                                            final List<BluetoothGattCharacteristic> characteristic);
    void uiLogConnectState(final String log);

    void uiCharacteristicChanged(BluetoothGattCharacteristic characteristic);

    void uiCharacteristicReaded(BluetoothGattCharacteristic characteristic);
}
