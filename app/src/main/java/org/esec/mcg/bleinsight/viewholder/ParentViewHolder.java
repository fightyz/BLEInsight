package org.esec.mcg.bleinsight.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by yz on 2015/9/28.
 */
public class ParentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private ParentListItemExpandCollapseListener mParentListItemExpandCollapseListener;
    private boolean mExpanded;

    @Override
    public void onClick(View v) {
        if (mExpanded) {
            collapseView();
        } else {
            expandView();
        }
    }

    /**
     * 点击父节点，展开收起的回调
     */
    public interface ParentListItemExpandCollapseListener {

        void onParentListItemExpanded(int position);

        void onParentListItemCollapsed(int position);
    }

    public ParentViewHolder(View itemView) {
        super(itemView);
        mExpanded = false;
    }

    public void setMainItemClickToExpand() { itemView.setOnClickListener(this); }

    /**
     * 是否点击整个item也会展开？？
     * @return
     */
    public boolean shouldItemViewClickToggleExpansion() {
        return true;
    }

    /**
     * 设置当前列表的展开收起状态
     * @param expanded
     */
    public void setExpanded(boolean expanded) {
        mExpanded = expanded;
    }

    /**
     * 没搞懂这个的作用，说是用于实现展开收起动画效果？
     * @param expanded
     */
    public void onExpansionToggled(boolean expanded) {

    }

    public ParentListItemExpandCollapseListener getParentListItemExpandCollapseListener() {
        return mParentListItemExpandCollapseListener;
    }

    public void setParentListItemExpandCollapseListener(ParentListItemExpandCollapseListener parentListItemExpandCollapseListener) {
        mParentListItemExpandCollapseListener = parentListItemExpandCollapseListener;
    }

    protected void expandView() {
        setExpanded(true);
        onExpansionToggled(false);

        if (mParentListItemExpandCollapseListener != null) {
            mParentListItemExpandCollapseListener.onParentListItemExpanded(getAdapterPosition());
        }
    }

    protected void collapseView() {
        setExpanded(false);
        onExpansionToggled(true);

        if (mParentListItemExpandCollapseListener != null) {
            mParentListItemExpandCollapseListener.onParentListItemCollapsed(getAdapterPosition());
        }
    }
}
