package org.esec.mcg.bleinsight.adapter;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.esec.mcg.bleinsight.CharacteristicItemBean;
import org.esec.mcg.bleinsight.R;
import org.esec.mcg.bleinsight.ServiceItemBean;
import org.esec.mcg.bleinsight.model.ParentListItem;
import org.esec.mcg.bleinsight.viewholder.CharacteristicViewHolder;
import org.esec.mcg.bleinsight.viewholder.ServiceViewHolder;
import org.esec.mcg.bleinsight.wrapper.BLENameResolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by yz on 2015/9/28.
 */
public class PeripheralDetailAdapter extends ExpandableRecyclerAdapter<ServiceViewHolder, CharacteristicViewHolder> {

    private ArrayList<BluetoothGattService> mBTService;
    private ArrayList<List<BluetoothGattCharacteristic>> mCharacteristics;
    private List<ServiceItemBean> serviceParentList;
    private List<CharacteristicItemBean> characteristicChildList;
    private LayoutInflater mInflater;

    public PeripheralDetailAdapter(Context context) {
        super();
        mBTService = new ArrayList<>();
        mCharacteristics = new ArrayList<>();
        serviceParentList = new ArrayList<>();
        characteristicChildList = new ArrayList<>();
        mInflater = LayoutInflater.from(context);
    }

    public void addService(BluetoothGattService service) {
        if (mBTService.contains(service) == false) {
            mBTService.add(service);
            List<BluetoothGattCharacteristic> chars = service.getCharacteristics();
            if (chars != null) {
                mCharacteristics.add(chars);
            }

            String uuid = service.getUuid().toString().toLowerCase(Locale.getDefault());
            String name = BLENameResolver.resolveServiceName(uuid);
            String type = (service.getType() == BluetoothGattService.SERVICE_TYPE_PRIMARY) ? "Primary" : "Secondary";

            ServiceItemBean serviceItemBean = new ServiceItemBean();
            serviceItemBean.setServiceName(name);
            serviceItemBean.setServiceUuid(uuid);
            serviceItemBean.setServiceType(type);
            serviceItemBean.setChildItemList(characteristicChildList);

            serviceParentList.add(serviceItemBean);
        }
    }

    public void setParentItemList() {
        super.setParentItemList(serviceParentList);
    }

    @Override
    public ServiceViewHolder onCreateParentViewHolder(ViewGroup parentViewGroup) {
        View view = mInflater.inflate(R.layout.service_item, parentViewGroup, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public CharacteristicViewHolder onCreateChildViewHolder(ViewGroup childViewGroup) {
        View view = mInflater.inflate(R.layout.characteristic_item, childViewGroup, false);
        return new CharacteristicViewHolder(view);
    }

    @Override
    public void onBindParentViewHolder(ServiceViewHolder parentViewHolder, int position, ParentListItem parentListItem) {
        ServiceItemBean serviceItemBean = (ServiceItemBean) parentListItem;
        parentViewHolder.bind(serviceItemBean);
    }

    @Override
    public void onBindChildViewHolder(CharacteristicViewHolder childViewHolder, int position, Object childListItem) {
        CharacteristicItemBean childItem = (CharacteristicItemBean) childListItem;
        childViewHolder.bind(childItem.getChildText());
    }
}
