package org.esec.mcg.bleinsight.adapter;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattService;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import org.esec.mcg.bleinsight.R;
import org.esec.mcg.bleinsight.wrapper.BLENameResolver;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by yz on 2015/9/22.
 */
public class DeviceDetailAdapter extends BaseExpandableListAdapter {

    private ArrayList<BluetoothGattService> mBTService;
    private LayoutInflater mInflater;

    public DeviceDetailAdapter(Activity parent) {
        super();
        mBTService = new ArrayList<BluetoothGattService>();
        mInflater = parent.getLayoutInflater();
    }

    public void addService(BluetoothGattService service) {
        if (mBTService.contains(service) == false) {
            mBTService.add(service);
        }
    }

    @Override
    public int getGroupCount() {
        return mBTService.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return "group-" + groupPosition;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
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
        return null;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    private class ViewHolder {

        TextView serviceName;
        TextView serviceUuid;
        TextView serviceType;

        public ViewHolder(View v) {
            serviceName = (TextView)v.findViewById(R.id.service_name);
            serviceUuid = (TextView)v.findViewById(R.id.service_uuid);
            serviceType = (TextView)v.findViewById(R.id.service_type);
        }
    }
}
