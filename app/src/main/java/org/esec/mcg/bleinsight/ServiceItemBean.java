package org.esec.mcg.bleinsight;

import org.esec.mcg.bleinsight.model.ParentListItem;

import java.util.List;

/**
 * Created by yz on 2015/9/28.
 */
public class ServiceItemBean implements ParentListItem {

    private List<CharacteristicItemBean> mCharacteristicItemList;
    private String serviceName;
    private String serviceUuid;
    private String serviceType;
    private boolean mInitiallyExpanded;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceUuid() {
        return serviceUuid;
    }

    public void setServiceUuid(String serviceUuid) {
        this.serviceUuid = serviceUuid;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    @Override
    public List<CharacteristicItemBean> getChildItemList() {
        return mCharacteristicItemList;
    }

    public void setChildItemList(List<CharacteristicItemBean> childItemList) {
        mCharacteristicItemList = childItemList;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return mInitiallyExpanded;
    }

    public void setInitiallyExpanded(boolean initiallyExpanded) {
        mInitiallyExpanded = initiallyExpanded;
    }
}
