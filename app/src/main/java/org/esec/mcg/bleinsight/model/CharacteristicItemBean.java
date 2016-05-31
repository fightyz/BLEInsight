package org.esec.mcg.bleinsight.model;

import android.bluetooth.BluetoothGattCharacteristic;

import org.esec.mcg.bleinsight.viewholder.CharacteristicViewHolder;

/**
 * Created by yz on 2015/9/28.
 */
public class CharacteristicItemBean {

    /**
     * cccd button的状态
     */
    private boolean switchState;
    private String characteristicName;
    private String characteristicUuid;
    private String characteristicPropertires;
    private String characteristicValue;
    private BluetoothGattCharacteristic characteristic;

    private boolean connectState;

    public boolean getSwitchState() { return switchState; }
    public void setSwitchState(boolean switchState) {
        this.switchState = switchState;
    }

    public String getCharacteristicName() { return characteristicName; }
    public void setCharacteristicName(String characteristicName) {
        this.characteristicName = characteristicName;
    }

    public String getCharacteristicUuid() { return characteristicUuid; }
    public void setCharacteristicUuid(String characteristicUuid) {
        this.characteristicUuid = characteristicUuid;
    }

    public String getCharacteristicPropertires() { return characteristicPropertires; }
    public void setCharacteristicPropertires(String characteristicPropertires) {
        this.characteristicPropertires = characteristicPropertires;
    }

    public String getCharacteristicValue() { return characteristicValue; }
    public void setCharacteristicValue(String characteristicValue) {
        this.characteristicValue = characteristicValue;
    }

    public BluetoothGattCharacteristic getCharacteristic() { return characteristic; }
    public void setCharacteristic(BluetoothGattCharacteristic characteristic) {
        this.characteristic = characteristic;
    }

    public boolean getConnectState() { return connectState; }
    public void setConnectState(boolean connectState) {
        this.connectState = connectState;
    }
}
