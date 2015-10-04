package org.esec.mcg.bleinsight.wrapper;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

/**
 * Created by yangzhou on 10/4/15.
 */
public interface CommandUiCallbacks {
    public void uiNewValueForCharacteristic(final BluetoothGatt gatt,
                                                        final BluetoothDevice device,
                                                        final BluetoothGattService service,
                                                        final BluetoothGattCharacteristic ch,
                                                        final String strValue);
}
