package org.esec.mcg.bleinsight.adapter;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.esec.mcg.bleinsight.CharacteristicItemBean;
import org.esec.mcg.bleinsight.PeripheralDetailActivity;
import org.esec.mcg.bleinsight.R;
import org.esec.mcg.bleinsight.ServiceItemBean;
import org.esec.mcg.bleinsight.model.ParentListItem;
import org.esec.mcg.bleinsight.viewholder.CharacteristicViewHolder;
import org.esec.mcg.bleinsight.viewholder.ServiceViewHolder;
import org.esec.mcg.bleinsight.wrapper.BLENameResolver;
import org.esec.mcg.bleinsight.wrapper.BLEWrapper;
import org.esec.mcg.utils.ByteUtil;
import org.esec.mcg.utils.logger.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by yz on 2015/9/28.
 */
public class PeripheralDetailAdapter extends ExpandableRecyclerAdapter<ServiceViewHolder, CharacteristicViewHolder> {

    private ArrayList<BluetoothGattService> mBTService;
    private ArrayList<List<BluetoothGattCharacteristic>> mCharacteristics;
    private List<ServiceItemBean> serviceParentList; // 用于recyclerView的显示
    private LayoutInflater mInflater;
    private PeripheralDetailActivity mContext;

//    private BLEWrapper mBLEWrapper;

    public PeripheralDetailAdapter(Context context) {
        super();
        mBTService = new ArrayList<>();
        mCharacteristics = new ArrayList<>();
        serviceParentList = new ArrayList<>();
        mInflater = LayoutInflater.from(context);
        mContext = (PeripheralDetailActivity)context;
    }

    public void clearList() {
        mBTService.clear();
        mCharacteristics.clear();
        serviceParentList.clear();
    }

    /**
     * 添加service和characteristic列表
     * @param service
     */
    public void addService(BluetoothGattService service) {
        int characteristicId = 0;
        List<CharacteristicItemBean> characteristicChildList = new ArrayList<>();
        if (mBTService.contains(service) == false) {
            mBTService.add(service);
            List<BluetoothGattCharacteristic> chars = service.getCharacteristics();
            if (chars != null) {
                mCharacteristics.add(chars);
                for (BluetoothGattCharacteristic characteristic : chars) {
                    String charUuid = characteristic.getUuid().toString().toLowerCase(Locale.getDefault());
                    String charName = BLENameResolver.resolveCharacteristicName(charUuid);
                    int properties = characteristic.getProperties();
                    StringBuilder propertiesString = new StringBuilder();
                    if ((properties & BluetoothGattCharacteristic.PROPERTY_BROADCAST) != 0)
                        propertiesString.append("BROADCAST, ");
                    if ((properties & BluetoothGattCharacteristic.PROPERTY_EXTENDED_PROPS) != 0)
                        propertiesString.append("EXTENDED_PROPS, ");
                    if ((properties & BluetoothGattCharacteristic.PROPERTY_INDICATE) != 0) {
                        propertiesString.append("INDICATE, ");
                    }

                    if ((properties & BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0) {
                        propertiesString.append("NOTIFY, ");
                    }

                    if ((properties & BluetoothGattCharacteristic.PROPERTY_READ) != 0) {
                        propertiesString.append("READ, ");
                    }
                    if ((properties & BluetoothGattCharacteristic.PROPERTY_SIGNED_WRITE) != 0)
                        propertiesString.append("SIGNED WRITE, ");
                    if ((properties & BluetoothGattCharacteristic.PROPERTY_WRITE) != 0) {
                        propertiesString.append("WRITE, ");
                    }
                    if ((properties & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) != 0)
                        propertiesString.append("WRITE NO RESPONSE, ");

                    CharacteristicItemBean characteristicItemBean = new CharacteristicItemBean();
                    characteristicItemBean.setCharacteristicName(charName);
                    characteristicItemBean.setCharacteristicUuid(charUuid);
                    characteristicItemBean.setCharacteristicPropertires(propertiesString.substring(0, propertiesString.length() - 2));
                    characteristicItemBean.setSwitchState(false);
                    characteristicItemBean.setCharacteristic(characteristic);
                    characteristicItemBean.setConnectState(true);
                    characteristicChildList.add(characteristicItemBean);
                }
            }

            String uuid = service.getUuid().toString().toLowerCase(Locale.getDefault());
            String name = BLENameResolver.resolveServiceName(uuid);
            String type = (service.getType() == BluetoothGattService.SERVICE_TYPE_PRIMARY) ? "PRIMARY SERVICE" : "SECONDARY SERVICE";

            ServiceItemBean serviceItemBean = new ServiceItemBean();
            serviceItemBean.setServiceName(name);
            serviceItemBean.setServiceUuid(uuid);
            serviceItemBean.setServiceType(type);
            serviceItemBean.setConnectState(true);
            serviceItemBean.setChildItemList(characteristicChildList);

            serviceParentList.add(serviceItemBean);
        }
    }

    public int updateCharacteristicForUuid(BluetoothGattCharacteristic characteristic) {
        int position = -1;
        for (Object item :
                mItemList) {
            position++;
            if (item instanceof CharacteristicItemBean) {
                CharacteristicItemBean characteristicItemBean = (CharacteristicItemBean)item;
                if (characteristic.getUuid().toString().equals(characteristicItemBean.getCharacteristicUuid())) {
                    characteristicItemBean.setCharacteristicValue(ByteUtil.ByteArrayToHexString(characteristic.getValue()));
                    return position;
                }
            }
        }
        return -1;
    }

    public void setParentItemList() {
        super.setParentItemList(serviceParentList);
    }

    public void setServiceCharacteristicItemGrey(boolean isGrey) {
        for (ServiceItemBean serviceItemBean : serviceParentList) {
            serviceItemBean.setConnectState(!isGrey);
            for (CharacteristicItemBean charactersiticItemBean : serviceItemBean.getChildItemList()) {
                charactersiticItemBean.setConnectState(!isGrey);
            }
        }
    }

    @Override
    public ServiceViewHolder onCreateParentViewHolder(ViewGroup parentViewGroup) {
        LogUtils.d("onCreateParentViewHolder");
        View view = mInflater.inflate(R.layout.service_item, parentViewGroup, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public CharacteristicViewHolder onCreateChildViewHolder(ViewGroup childViewGroup) {
        LogUtils.d("onCreateChildViewHolder");
        View view = mInflater.inflate(R.layout.characteristic_item, childViewGroup, false);
        return new CharacteristicViewHolder(view, mContext);
    }


    @Override
    public void onBindParentViewHolder(ServiceViewHolder parentViewHolder, int position, ParentListItem parentListItem) {
        LogUtils.d("onBindParentViewHolder");
        ServiceItemBean serviceItemBean = (ServiceItemBean) parentListItem;
        parentViewHolder.bind(serviceItemBean, mContext);
    }

    @Override
    public void onBindChildViewHolder(CharacteristicViewHolder childViewHolder, int position, Object childListItem) {
        LogUtils.d("onBindChildViewHolder");
        CharacteristicItemBean childItem = (CharacteristicItemBean) childListItem;
        childViewHolder.bind(childItem, mContext);
    }
}
