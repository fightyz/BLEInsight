package org.esec.mcg.bleinsight;

import org.esec.mcg.bleinsight.model.ParentListItem;

import java.util.List;

/**
 * Created by yz on 2015/9/28.
 */
public class ServiceItem implements ParentListItem {

    private List<CharacteristicItem> mChildItemList;
    private String mParentText;
    private int mParentNumber;
    private boolean mInitiallyExpanded;

    public String getParentText() {
        return mParentText;
    }

    public void setParentText(String parentText) {
        mParentText = parentText;
    }

    public int getParentNumber() {
        return mParentNumber;
    }

    public void setParentNumber(int parentNumber) {
        mParentNumber = parentNumber;
    }

    @Override
    public List<CharacteristicItem> getChildItemList() {
        return mChildItemList;
    }

    public void setChildItemList(List<CharacteristicItem> childItemList) {
        mChildItemList = childItemList;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return mInitiallyExpanded;
    }

    public void setInitiallyExpanded(boolean initiallyExpanded) {
        mInitiallyExpanded = initiallyExpanded;
    }
}
