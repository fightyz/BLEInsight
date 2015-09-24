package org.esec.mcg.bleinsight.adapter;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.views.CheckBox;

import org.esec.mcg.bleinsight.R;
import org.esec.mcg.bleinsight.wrapper.BLENameResolver;
import org.esec.mcg.utils.logger.LogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by yz on 2015/9/22.
 */
public class DeviceDetailAdapter extends BaseExpandableListAdapter {

    private ArrayList<BluetoothGattService> mBTService;
    private ArrayList<List<BluetoothGattCharacteristic>> mCharacteristics;
    private LayoutInflater mInflater;

    public DeviceDetailAdapter(Activity parent) {
        super();
        mBTService = new ArrayList<BluetoothGattService>();
        mCharacteristics = new ArrayList<List<BluetoothGattCharacteristic>>();
        mInflater = parent.getLayoutInflater();
    }

    public void addService(BluetoothGattService service) {
        if (mBTService.contains(service) == false) {
            mBTService.add(service);
            List<BluetoothGattCharacteristic> chars = service.getCharacteristics();
            if (chars != null) {
                mCharacteristics.add(chars);
            }

        }

    }

    public void addCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mCharacteristics.contains(characteristic) == false) {
//            mCharacteristics.add(characteristic);
        }
    }

    @Override
    public int getGroupCount() {
        return mBTService.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mCharacteristics.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
//        return "group-" + groupPosition;
        return mBTService.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mCharacteristics.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View v = convertView;
        ViewHolder holder;
        if (v == null) {
            v = mInflater.inflate(R.layout.service_list, null);
            holder = new ViewHolder(v);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }

        BluetoothGattService service = mBTService.get(groupPosition);
        String uuid = service.getUuid().toString().toLowerCase(Locale.getDefault());
        String name = BLENameResolver.resolveServiceName(uuid);
        String type = (service.getType() == BluetoothGattService.SERVICE_TYPE_PRIMARY) ? "Primary" : "Secondary";

        holder.serviceName.setText(name);
        holder.serviceUuid.setText(uuid);
        holder.serviceType.setText(type);

        return v;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        LogUtils.d("绘制childView");

        BluetoothGattCharacteristic characteristic = (BluetoothGattCharacteristic)getChild(groupPosition, childPosition);

        View v = convertView;
        v = mInflater.inflate(R.layout.characteristic_list, null);
        final ViewHolder holder = new ViewHolder(v);

        if (characteristic != null) {
            String uuid = characteristic.getUuid().toString().toLowerCase(Locale.getDefault());
            String name = BLENameResolver.resolveCharacteristicName(uuid);

            int properties = characteristic.getProperties();
            StringBuilder propertiesString = new StringBuilder();
            if ((properties & BluetoothGattCharacteristic.PROPERTY_BROADCAST) != 0)
                propertiesString.append("BROADCAST, ");
            if ((properties & BluetoothGattCharacteristic.PROPERTY_EXTENDED_PROPS) != 0)
                propertiesString.append("EXTENDED_PROPS, ");
            if ((properties & BluetoothGattCharacteristic.PROPERTY_INDICATE) != 0) {
                propertiesString.append("INDICATE, ");
                holder.characteristicCccd.setVisibility(View.VISIBLE);
                holder.characteristicCccdText.setText("INDICATE");
                holder.characteristicCccdText.setVisibility(View.VISIBLE);
            }

            if ((properties & BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0) {
                propertiesString.append("NOTIFY, ");
                holder.characteristicCccd.setVisibility(View.VISIBLE);
                holder.characteristicCccdText.setVisibility(View.VISIBLE);
            }

            if ((properties & BluetoothGattCharacteristic.PROPERTY_READ) != 0) {
                propertiesString.append("READ, ");
                holder.characteristicRead.setVisibility(View.VISIBLE);
            }

            if ((properties & BluetoothGattCharacteristic.PROPERTY_SIGNED_WRITE) != 0)
                propertiesString.append("SIGNED WRITE, ");
            if ((properties & BluetoothGattCharacteristic.PROPERTY_WRITE) != 0) {
                propertiesString.append("WRITE, ");
                holder.characteristicWrite.setVisibility(View.VISIBLE);
            }

            if ((properties & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) != 0)
                propertiesString.append("WRITE NO RESPONSE, ");

            holder.characteristicName.setText(name);
            holder.characteristicUuid.setText(uuid);
            holder.characteristicProperties.setText(propertiesString.substring(0, propertiesString.length() - 2));
            holder.characteristicRead.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LogUtils.d("点击了read按钮");
                }
            });
        }
        return v;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    private class ViewHolder {

        TextView serviceName;
        TextView serviceUuid;
        TextView serviceType;

        TextView characteristicName;
        TextView characteristicUuid;
        TextView characteristicProperties;

        ButtonFlat characteristicRead;
        ButtonFlat characteristicWrite;
        TextView characteristicCccdText;
        CheckBox characteristicCccd;


        public ViewHolder(View v) {
            serviceName = (TextView)v.findViewById(R.id.service_name);
            serviceUuid = (TextView)v.findViewById(R.id.service_uuid);
            serviceType = (TextView)v.findViewById(R.id.service_type);

            characteristicName = (TextView) v.findViewById(R.id.characteristic_name);
            characteristicUuid = (TextView) v.findViewById(R.id.characteristic_uuid_value);
            characteristicProperties = (TextView) v.findViewById(R.id.characteristic_properties_value);

            characteristicRead = (ButtonFlat) v.findViewById(R.id.read_button);
            characteristicWrite = (ButtonFlat) v.findViewById(R.id.write_button);
            characteristicCccdText = (TextView) v.findViewById(R.id.cccd_text);
            characteristicCccd = (CheckBox) v.findViewById(R.id.cccd_check);
        }
    }
}
