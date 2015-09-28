package org.esec.mcg.bleinsight.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.esec.mcg.bleinsight.CharacteristicItem;
import org.esec.mcg.bleinsight.R;
import org.esec.mcg.bleinsight.ServiceItem;
import org.esec.mcg.bleinsight.model.ParentListItem;
import org.esec.mcg.bleinsight.viewholder.CharacteristicViewHolder;
import org.esec.mcg.bleinsight.viewholder.ServiceViewHolder;

import java.util.List;

/**
 * Created by yz on 2015/9/28.
 */
public class PeripheralDetailAdapter extends ExpandableRecyclerAdapter<ServiceViewHolder, CharacteristicViewHolder> {

    private LayoutInflater mInflater;

    public PeripheralDetailAdapter(Context context, List<? extends ParentListItem> parentItemList) {
        super(parentItemList);
        mInflater = LayoutInflater.from(context);
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
        ServiceItem serviceItem = (ServiceItem) parentListItem;
        parentViewHolder.bind(serviceItem.getParentNumber(), serviceItem.getParentText());
    }

    @Override
    public void onBindChildViewHolder(CharacteristicViewHolder childViewHolder, int position, Object childListItem) {
        CharacteristicItem childItem = (CharacteristicItem) childListItem;
        childViewHolder.bind(childItem.getChildText());
    }
}
