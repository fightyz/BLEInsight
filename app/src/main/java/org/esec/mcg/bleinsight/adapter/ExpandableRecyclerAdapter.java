package org.esec.mcg.bleinsight.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import org.esec.mcg.bleinsight.model.ParentListItem;
import org.esec.mcg.bleinsight.model.ParentWrapper;
import org.esec.mcg.bleinsight.viewholder.ChildViewHolder;
import org.esec.mcg.bleinsight.viewholder.ParentViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yz on 2015/9/28.
 */
public abstract class ExpandableRecyclerAdapter<PVH extends ParentViewHolder, CVH extends ChildViewHolder>
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ParentViewHolder.ParentListItemExpandCollapseListener {

    private static final String EXPANDED_STATE_MAP = "ExpandableRecyclerAdapter.ExpandedStateMap";
    private static final int TYPE_PARENT = 0;
    private static final int TYPE_CHILD = 1;

    /**
     * 所有初始的父节点及其展开的子节点
     */
    protected List<Object> mItemList;

    private List<? extends ParentListItem> mParentItemList;
    private ExpandCollapseListener mExpandCollapseListener;
    private List<RecyclerView> mAttachedRecyclerViewPool;

    public interface ExpandCollapseListener {
        void onListItemExpanded(int position);
        void onListItemCollapse(int position);
    }

    public ExpandableRecyclerAdapter() {
        super();
//        mParentItemList = parentItemList;
//        mItemList = ExpandableRecyclerAdapterHelper.generateParentChildItemList(parentItemList);
//        mAttachedRecyclerViewPool = new ArrayList<>();
        mItemList = new ArrayList<>();
    }

    public void setParentItemList(List<? extends ParentListItem> parentItemList) {
        this.mParentItemList = parentItemList;
        mItemList = ExpandableRecyclerAdapterHelper.generateParentChildItemList(parentItemList);
//        mItemList = new ArrayList<>();
//        mAttachedRecyclerViewPool = new ArrayList<>();

    }

    /**
     * 节点渲染时的回调函数
     * @param viewGroup
     * @param viewType
     * @return
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewType == TYPE_PARENT) {
            PVH pvh = onCreateParentViewHolder(viewGroup);
            pvh.setParentListItemExpandCollapseListener(this);
            return pvh;
        } else if (viewType == TYPE_CHILD) {
            return onCreateChildViewHolder(viewGroup);
        } else {
            throw new IllegalStateException("Incorrect ViewType found");
        }
    }

    /**
     * 渲染节点的函数
     * @param viewHolder
     * @param position
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        Object listItem = getListItem(position);

        if (listItem instanceof ParentWrapper) { //如果是父节点
            PVH parentViewHolder = (PVH) viewHolder;

            if (parentViewHolder.shouldItemViewClickToggleExpansion()) {
                parentViewHolder.setMainItemClickToExpand();
            }

            ParentWrapper parentWrapper = (ParentWrapper) listItem;
            parentViewHolder.setExpanded(parentWrapper.isExpanded());
            onBindParentViewHolder(parentViewHolder, position, parentWrapper.getParentListItem());
        } else if (listItem == null) {
            throw new IllegalStateException("Incorrect ViewHolder found");
        } else {
            onBindChildViewHolder((CVH) viewHolder, position, listItem);
        }
    }

    /**
     * 返回节点对象
     * @param position
     * @return
     */
    protected Object getListItem(int position) {
        return mItemList.get(position);
    }

    /**
     * 父节点和展开的子节点个数
     * @return
     */
    @Override
    public int getItemCount() { return mItemList.size(); }

    /**
     * 收起父节点
     * @param position
     */
    @Override
    public void onParentListItemCollapsed(int position) {
        Object listItem = getListItem(position);
        if (listItem instanceof ParentWrapper) {
            collapseParentListItem((ParentWrapper) listItem, position, true);
        }
    }

    @Override
    public void onParentListItemExpanded(int position) {
        Object listItem = getListItem(position);
        if (listItem instanceof ParentWrapper) {
            expandParentListItem((ParentWrapper) listItem, position, true);
        }
    }

    /**
     * 收起父节点
     * @param parentWrapper
     * @param parentIndex
     * @param collapseTriggeredByListItemClick
     */
    private void collapseParentListItem(ParentWrapper parentWrapper, int parentIndex, boolean collapseTriggeredByListItemClick) {
        if (parentWrapper.isExpanded()) {
            parentWrapper.setExpanded(false);

            if (collapseTriggeredByListItemClick && mExpandCollapseListener != null) {
                int expandedCountBeforePosition =getExpandedItemCount(parentIndex);
                // 这里传入的是父节点的index(不包含展开的子节点)
                mExpandCollapseListener.onListItemCollapse(parentIndex - expandedCountBeforePosition);
            }

            List<?> childItemList = parentWrapper.getChildItemList();
            if (childItemList != null) {
                for (int i = childItemList.size() - 1; i >= 0; i--) {
                    mItemList.remove(parentIndex + i + 1);
                    notifyItemRemoved(parentIndex + i + 1);
                }
            }
        }
    }

    /**
     * 展开父节点
     * @param parentWrapper
     * @param parentIndex
     * @param expansionTriggeredByListItemClick
     */
    private void expandParentListItem(ParentWrapper parentWrapper, int parentIndex, boolean expansionTriggeredByListItemClick) {
        if (!parentWrapper.isExpanded()) {
            parentWrapper.setExpanded(true);

            if (expansionTriggeredByListItemClick && mExpandCollapseListener != null) {
                int expandedCountBeforePosition = getExpandedItemCount(parentIndex);
                mExpandCollapseListener.onListItemExpanded(parentIndex - expandedCountBeforePosition);
            }

            List<?> childItemList = parentWrapper.getChildItemList();
            if (childItemList != null) {
                int childListItemCount = childItemList.size();
                for (int i = 0; i < childListItemCount; i++) {
                    mItemList.add(parentIndex + i + 1, childItemList.get(i));
                    notifyItemInserted(parentIndex + i + 1);
                }
            }
        }
    }

    /**
     * 返回父节点前被展开的子节点个数
     * @param position
     * @return
     */
    private int getExpandedItemCount(int position) {
        if (position == 0) {
            return 0;
        }

        int expandedCount = 0;
        for (int i = 0; i < position; i++) {
            Object listItem = getListItem(i);
            if (!(listItem instanceof ParentWrapper)) {
                expandedCount++;
            }
        }
        return expandedCount;
    }

    public void setExpandCollapseListener(ExpandCollapseListener expandCollapseListener) {
        mExpandCollapseListener = expandCollapseListener;
    }

    @Override
    public int getItemViewType(int position) {
        Object listItem = getListItem(position);
        if (listItem instanceof ParentWrapper) {
            return TYPE_PARENT;
        } else if (listItem == null) {
            throw new IllegalStateException("Null object added");
        } else {
            return TYPE_CHILD;
        }
    }

    /**
     * 父节点的渲染回调
     * @param parentViewGroup
     * @return
     */
    public abstract PVH onCreateParentViewHolder(ViewGroup parentViewGroup);

    /**
     * 子节点的渲染回调
     * @param childViewGroup
     * @return
     */
    public abstract CVH onCreateChildViewHolder(ViewGroup childViewGroup);

    /**
     * 渲染父节点的回调
     * @param parentViewHolder
     * @param position
     * @param parentListItem
     */
    public abstract void onBindParentViewHolder(PVH parentViewHolder, int position, ParentListItem parentListItem);

    /**
     * 渲染子节点的回调
     * @param childViewHolder
     * @param position
     * @param childListItem
     */
    public abstract void onBindChildViewHolder(CVH childViewHolder, int position, Object childListItem);
}