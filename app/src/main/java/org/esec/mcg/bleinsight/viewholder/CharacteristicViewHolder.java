package org.esec.mcg.bleinsight.viewholder;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import org.esec.mcg.bleinsight.CharacteristicItemBean;
import org.esec.mcg.bleinsight.PeripheralDetailActivity;
import org.esec.mcg.bleinsight.R;
import org.esec.mcg.bleinsight.WriteValueDialog;
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
    public TextView characteristicWriteType;
    public TextView characteristicWriteTypeValue;
    public TextView characteristicValueText;
    public TextView characteristicValue;

    public TextView characteristicCccdSwitchText;
    public Switch characteristicCccdSwitch;
    public Button characteristicReadButton;
    public Button characteristicWriteButton;

    public CharacteristicItemBean characteristicItemBean;

    /**
     * 每一个ViewHolder都持有自己的BLEWrapper实例，克隆自PeripheralDetailActivity的BLEWrapper实例
     * 并在之后将context中的BLEWrapper实例设置为该viewHolder的BLEWrapper实例，这样能够保证回调时是回到
     * 该ViewHolder
     */
    private BLEWrapper mBLEWrapper;
    private PeripheralDetailActivity mContext;

    public CharacteristicViewHolder(View itemView) {
        super(itemView);

        /**
         * 每一个ViewHolder持有自己的BLEWrapper
         */
        mContext = (PeripheralDetailActivity)itemView.getContext();
        mBLEWrapper = (BLEWrapper)mContext.getBLEWrapper().clone();
        mBLEWrapper.setCommandUiCallbacks(this);

        characteristicName = (TextView) itemView.findViewById(R.id.characteristic_name);
        characteristicUuidValue = (TextView) itemView.findViewById(R.id.characteristic_uuid_value);
        characteristicPropertiesValue = (TextView) itemView.findViewById(R.id.characteristic_properties_value);
        characteristicWriteType = (TextView) itemView.findViewById(R.id.characteristic_write_type);
        characteristicWriteTypeValue = (TextView) itemView.findViewById(R.id.characteristic_write_type_value);
        characteristicValueText = (TextView) itemView.findViewById(R.id.characteristic_value_text);
        characteristicValue = (TextView) itemView.findViewById(R.id.characteristic_value);

        characteristicCccdSwitchText = (TextView) itemView.findViewById(R.id.cccd_switch_text);
        characteristicCccdSwitch = (Switch) itemView.findViewById(R.id.cccd_switch);
        characteristicCccdSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (characteristicItemBean != null) {
                    characteristicItemBean.setSwitchState(isChecked);
                }
                mContext.getBLEWrapper().self = mBLEWrapper;
                // 因为当页面中同时出现两个notify的时候，后出现的notify B也会调用上面的方法设置self，从而将notify A
                // 所持有的BLEWrapper给冲刷掉，因此需要维护一个map，使程序知道回调时是哪个characteristic的值
                mContext.getBLEWrapper().addCharWrapperElement(characteristicItemBean.getCharacteristic(), mBLEWrapper);
                // 根据是notify还是indicate向cccd写入不同的值
                // 开启cccd
                mBLEWrapper.setCccdForCharacteristic(characteristicItemBean.getCharacteristic(), isChecked);
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
                /**
                 * 让context中持有该ViewHolder的BLEWrapper实例
                 */
                mContext.getBLEWrapper().self = mBLEWrapper;
                mBLEWrapper.requestCharacteristicValue(characteristic);
            }
        });

        characteristicWriteButton = (Button) itemView.findViewById(R.id.write_button);
        characteristicWriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new AlertDialog.Builder(mContext).setView(new EditText(mContext)).setTitle("Write Value")
//                        .setPositiveButton("确定", null).setNegativeButton("取消", null).show();
                mContext.getBLEWrapper().self = mBLEWrapper;
                WriteValueDialog writeValueDialog = new WriteValueDialog(mContext, mBLEWrapper, characteristicItemBean);
                writeValueDialog.show();
//                new AlertDialog.Builder(mContext).setView(R.layout.write_value_dialog).setTitle("Write Value")
//                        .setPositiveButton("确定", null).setNegativeButton("取消", null).show();
            }
        });




        LogUtils.d("mBLEWrapper addr: " + mBLEWrapper);
        LogUtils.d("origin BLEWrapper: " + mContext.getBLEWrapper());
    }

    public void bind(CharacteristicItemBean characteristicItemBean) {
        characteristicName.setText(characteristicItemBean.getCharacteristicName());

        characteristicUuidValue.setText(characteristicItemBean.getCharacteristicUuid());
        characteristicPropertiesValue.setText(characteristicItemBean.getCharacteristicPropertires());
        this.characteristicItemBean = characteristicItemBean;

        characteristicCccdSwitch.setChecked(characteristicItemBean.getSwitchState());

        if ((characteristicItemBean.getCharacteristic().getProperties() & BluetoothGattCharacteristic.PROPERTY_WRITE) != 0) {
            characteristicWriteButton.setEnabled(true);
            characteristicWriteButton.setVisibility(View.VISIBLE);

            characteristicWriteType.setVisibility(View.VISIBLE);
            characteristicWriteTypeValue.setVisibility(View.VISIBLE);
            characteristicWriteTypeValue.setText("WRITE REQUEST");
        } else {
            characteristicWriteButton.setVisibility(View.GONE);
            characteristicWriteType.setVisibility(View.GONE);
            characteristicWriteTypeValue.setVisibility(View.GONE);
        }
        if ((characteristicItemBean.getCharacteristic().getProperties() & BluetoothGattCharacteristic.PROPERTY_READ) != 0) {
            characteristicReadButton.setEnabled(true);
            characteristicReadButton.setVisibility(View.VISIBLE);
        } else {
            characteristicReadButton.setVisibility(View.GONE);
        }
        if ((characteristicItemBean.getCharacteristic().getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0 ||
                (characteristicItemBean.getCharacteristic().getProperties() & BluetoothGattCharacteristic.PROPERTY_INDICATE) != 0) {
            characteristicCccdSwitchText.setVisibility(View.VISIBLE);
            characteristicCccdSwitch.setVisibility(View.VISIBLE);
            if ((characteristicItemBean.getCharacteristic().getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0) {
                characteristicCccdSwitchText.setText("NOTIFY");
            } else {
                characteristicCccdSwitchText.setText("INDICATE");
            }
        } else {
            characteristicCccdSwitchText.setVisibility(View.INVISIBLE);
            characteristicCccdSwitch.setVisibility(View.INVISIBLE);
        }

        if (characteristicItemBean.getCharacteristicValue() != null) {
            characteristicValueText.setVisibility(View.VISIBLE);
            characteristicValue.setVisibility(View.VISIBLE);
            characteristicValue.setText(characteristicItemBean.getCharacteristicValue());
        } else {
            characteristicValueText.setVisibility(View.GONE);
            characteristicValue.setVisibility(View.GONE);
        }

        if (!characteristicItemBean.getConnectState()) { // 已经断连
            characteristicName.setTextColor(mContext.getResources().getColor(R.color.grey));
            characteristicReadButton.setVisibility(View.GONE);
            characteristicWriteButton.setVisibility(View.GONE);
            characteristicCccdSwitchText.setVisibility(View.GONE);
            characteristicCccdSwitch.setVisibility(View.GONE);
        } else {
            characteristicName.setTextColor(mContext.getResources().getColor(R.color.black));
        }
    }

    @Override
    public void uiNewValueForCharacteristic(BluetoothGatt gatt, BluetoothDevice device, BluetoothGattService service, BluetoothGattCharacteristic ch, final String strValue) {
//        LogUtils.d("read characteristic callback");
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                characteristicItemBean.setCharacteristicValue(strValue);
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
