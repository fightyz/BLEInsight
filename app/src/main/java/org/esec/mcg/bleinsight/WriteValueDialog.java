package org.esec.mcg.bleinsight;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.esec.mcg.bleinsight.wrapper.BLEWrapper;
import org.esec.mcg.utils.ByteUtil;
import org.esec.mcg.utils.StringUtil;
import org.esec.mcg.utils.logger.LogUtils;

/**
 * Created by yz on 2015/10/8.
 */
public class WriteValueDialog {

    private LayoutInflater layoutInflater;
    private RelativeLayout writeValueLayout;
    private Dialog mDialog;
    private Context mContext;
    private BLEWrapper mBLEWrapper;
    private CharacteristicItemBean mCharacteristicItemBean;

    public WriteValueDialog(Context context, BLEWrapper BleWrapper, CharacteristicItemBean characteristicItemBean) {
//        layoutInflater = LayoutInflater.from(context);
//        writeValueLayout = (RelativeLayout) layoutInflater.inflate(R.layout.write_value_dialog, null);
//        mDialog = new AlertDialog.Builder(context).create();
        mContext = context;
        this.mBLEWrapper = BleWrapper;
        this.mCharacteristicItemBean = characteristicItemBean;
//        mDialog = new AlertDialog.Builder(context).create();
    }

    public void show() {
        mDialog = new AlertDialog.Builder(mContext).setView(R.layout.write_value_dialog).setTitle("Write Value")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        byte[] writeValueBytes = null;
                        String writeValueString = ((TextView) mDialog.findViewById(R.id.write_value)).getText().toString();
                        if (((Spinner) mDialog.findViewById(R.id.value_type)).getSelectedItem().toString().equals("String")) {
                            writeValueBytes = writeValueString.getBytes();
                        } else {
                            writeValueBytes = StringUtil.HexStringToByteArray(writeValueString);
                        }

                        LogUtils.d(ByteUtil.ByteArrayToHexString(writeValueBytes));
                        mBLEWrapper.writeDataToCharacteristic(mCharacteristicItemBean.getCharacteristic(), writeValueBytes);
                    }
                }).setNegativeButton("取消", null).show();
    }
}
