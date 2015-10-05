package org.esec.mcg.bleinsight.viewholder;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import org.esec.mcg.bleinsight.CharacteristicItemBean;
import org.esec.mcg.bleinsight.PeripheralDetailActivity;
import org.esec.mcg.bleinsight.R;
import org.esec.mcg.bleinsight.wrapper.BLEWrapper;
import org.esec.mcg.bleinsight.wrapper.CommandUiCallbacks;
import org.esec.mcg.utils.logger.LogUtils;
import org.w3c.dom.Text;

import java.util.HashMap;

/**
 * Created by yz on 2015/9/28.
 */
public class CharacteristicViewHolder extends ChildViewHolder implements CommandUiCallbacks {

    public TextView characteristicName;
    public TextView characteristicUuidValue;
    public TextView characteristicPropertiesValue;
    public TextView characteristicValueText;
    public TextView characteristicValue;

    public Switch characteristicCccdSwitch;
    public Button characteristicReadButton;
    public Button characteristicWriteButton;

    public CharacteristicItemBean characteristicItemBean;

    private BLEWrapper mBLEWrapper;
    private PeripheralDetailActivity mContext;

    public CharacteristicViewHolder(View itemView) {
        super(itemView);

        characteristicName = (TextView) itemView.findViewById(R.id.characteristic_name);
        characteristicUuidValue = (TextView) itemView.findViewById(R.id.characteristic_uuid_value);
        characteristicPropertiesValue = (TextView) itemView.findViewById(R.id.characteristic_properties_value);
        characteristicValueText = (TextView) itemView.findViewById(R.id.characteristic_value_text);
        characteristicValue = (TextView) itemView.findViewById(R.id.characteristic_value);

        characteristicCccdSwitch = (Switch) itemView.findViewById(R.id.cccd_switch);
        characteristicCccdSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (characteristicItemBean != null) {
                    characteristicItemBean.setSwitchState(isChecked);
                }
            }
        });

        characteristicReadButton = (Button) itemView.findViewById(R.id.read_button);
        characteristicReadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.d("read button click");
                LogUtils.d(mBLEWrapper.toString());
                BluetoothGattCharacteristic characteristic = characteristicItemBean.getCharacteristic();
                LogUtils.d(characteristicItemBean.getCharacteristicName());
                mContext.getBLEWrapper().self = mBLEWrapper;
                mBLEWrapper.requestCharacteristicValue(characteristic);
            }
        });

        characteristicWriteButton = (Button) itemView.findViewById(R.id.write_button);
        characteristicWriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mContext = (PeripheralDetailActivity)itemView.getContext();
        mBLEWrapper = (BLEWrapper)mContext.getBLEWrapper().clone();

        mBLEWrapper.setCommandUiCallbacks(this);
        LogUtils.d("mBLEWrapper addr: " + mBLEWrapper);
        LogUtils.d("origin BLEWrapper: " + mContext.getBLEWrapper());
    }

    public void bind(CharacteristicItemBean characteristicItemBean) {
        characteristicName.setText(characteristicItemBean.getCharacteristicName());
        characteristicUuidValue.setText(characteristicItemBean.getCharacteristicUuid());
        characteristicPropertiesValue.setText(characteristicItemBean.getCharacteristicPropertires());
        this.characteristicItemBean = characteristicItemBean;
        characteristicCccdSwitch.setChecked(characteristicItemBean.getSwitchState());
    }

    @Override
    public void uiNewValueForCharacteristic(BluetoothGatt gatt, BluetoothDevice device, BluetoothGattService service, BluetoothGattCharacteristic ch, final String strValue) {
        LogUtils.d("read characteristic callback");
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LogUtils.d(characteristicItemBean.getCharacteristicName());
                characteristicItemBean.setCharacteristicPropertires(strValue);
//                characteristicPropertiesValue.setText(strValue);
                characteristicValueText.setVisibility(View.VISIBLE);
                characteristicValue.setVisibility(View.VISIBLE);
                characteristicValue.setText(strValue);
            }
        });
        /**
         * TODO 其实这里应该用notifyDataChanged之类的？这样能够避免read时间过长没有返回，用户已经移动了列表
         * 导致characteristicPropertiesValue已经对应到另一个位置了？
         */

    }
}
