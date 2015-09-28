package org.esec.mcg.bleinsight.model;

import java.util.List;

/**
 * Created by yz on 2015/9/28.
 */
public interface ParentListItem {
    /**
     * 获取该父节点下的所有子节点列表
     * @return 子节点列表
     */
    List<?> getChildItemList();

    /**
     * 该父节点是否初始化时就展开
     * @return
     */
    boolean isInitiallyExpanded();
}
