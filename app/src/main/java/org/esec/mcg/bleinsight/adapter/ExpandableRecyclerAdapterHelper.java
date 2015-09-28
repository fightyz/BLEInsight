package org.esec.mcg.bleinsight.adapter;

import org.esec.mcg.bleinsight.model.ParentListItem;
import org.esec.mcg.bleinsight.model.ParentWrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * 生成一个List，包含所有父节点和已展开的子节点，按顺序来的
 * Created by yz on 2015/9/28.
 */
public class ExpandableRecyclerAdapterHelper {

    public static List<Object> generateParentChildItemList(List<? extends ParentListItem> parentItemList) {
        List<Object> parentWrapperList = new ArrayList<>();
        ParentListItem parentListItem;
        ParentWrapper parentWrapper;

        int parentListItemCount = parentItemList.size();
        for (int i = 0; i < parentListItemCount; i++) {
            parentListItem = parentItemList.get(i);
            parentWrapper = new ParentWrapper(parentListItem);
            parentWrapperList.add(parentWrapper);

            if (parentWrapper.isInitiallyExpanded()) {
                parentWrapper.setExpanded(true);

                int childListItemCount = parentWrapper.getChildItemList().size();
                for (int j = 0; j < childListItemCount; j++) {
                    parentWrapperList.add(parentWrapper.getChildItemList().get(j));
                }
            }
        }
        return parentWrapperList;
    }
}
