package org.esec.mcg.bleinsight.animator.item;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

/**
 * Created by yz on 2016/6/29.
 * 帮助显示左滑菜单
 */
public class ItemSlideHelper implements RecyclerView.OnItemTouchListener {

    private final int DEFAULT_DURATION = 200;

    /**
     * 已经左滑展开了的 item
     */
    private View mTargetView;

    private int mTouchSlop;

    private int mMaxVelocity;

    private int mLastX;
    private int mLastY;

    private boolean mIsDragging;

    private Animator mExpandAndCollapseAnim;

    private Callback mCallback;

    public ItemSlideHelper(Context context, Callback callback) {
        this.mCallback = callback;

        ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledTouchSlop();
        mMaxVelocity = configuration.getScaledMaximumFlingVelocity();
    }

    //这里采用的是外部拦截法
    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        Log.e("onInterceptTouchEvent", "MotionEvent: " + e.getAction());

        int action = MotionEventCompat.getActionMasked(e);
        // 这里获得的是点击点相对于容器的坐标，这里的容器是recyclerView而不是其中每一个item
        int x = (int) e.getX();
        int y = (int) e.getY();

        // 如果 RecyclerView 处于滚动状态 targetView 不是空
        if (rv.getScrollState() != RecyclerView.SCROLL_STATE_IDLE) {
            if (mTargetView != null) {
                // 将已展开的 item 折叠起来
                smoothHorizontalExpandOrCollapse(DEFAULT_DURATION / 2);
                mTargetView = null;
            }
            // return false, 表示不拦截任何事件（其实主要是MOVE事件）
            return false;
        }

        // 如果正在运行动画，直接拦截什么都不做
        if (mExpandAndCollapseAnim != null && mExpandAndCollapseAnim.isRunning()) {
            return true;
        }

        boolean needIntercept = false;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                Log.e("onInterceptTouchEvent", "ACTION_DOWN");
                mLastX = (int) e.getX();
                mLastY = (int) e.getY();

                /**
                 * 如果之前有一个已经展开的 item，当此次点击事件没有发生在右侧的菜单中则返回 true，
                 * 对 ACTION_DOWN 进行拦截，
                 * 如果点击的是右侧菜单那么返回 false，这样做的原因是菜单需要响应 onClick
                 */
                if (mTargetView != null) {
                    boolean tmp = !inView(x, y);
                    Log.e("onInterceptTouchEvent", "!inView(x, y) = " + tmp);
                    return tmp;
                }

                // 查找需要显示菜单的 View；
                mTargetView = mCallback.findTargetView(x, y);
                break;

            case MotionEvent.ACTION_MOVE:
                Log.e("onInterceptTouchEvent", "ACTION_MOVE");
                int deltaX = (x - mLastX);
                int deltaY = (y - mLastY);

                // 不是左右滑动，不拦截事件
                if (Math.abs(deltaY) > Math.abs(deltaX)) {
                    Log.e("onInterceptTouchEvent", "????");
                    return false;
                }

                // 如果移动距离达到要求，则拦截
                needIntercept = mIsDragging = mTargetView != null && Math.abs(deltaX) >= mTouchSlop;
                break;

            case MotionEvent.ACTION_CANCEL:
                Log.e("onInterceptTouchEvent", "ACTION_CANCEL");
            case MotionEvent.ACTION_UP:
                Log.e("onInterceptTouchEvent", "ACTION_UP");
                /**
                 * 走这是因为没有发生过拦截事件
                 */
                if (isExpanded()) {
                    if (inView(x, y)) {
                        // 如果走这，那么这个 ACTION_UP 事件会发生在右侧的菜单中
                        Log.d("onInterceptTouchEvent", "click item");
                    } else {
                        // 拦截事件，防止 targetView 执行 onClick 事件
                        needIntercept = true;
                    }

                    // 折叠菜单
                    smoothHorizontalExpandOrCollapse(DEFAULT_DURATION / 2);
                }

                mTargetView = null;
                break;
        }
        return needIntercept;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        Log.e("onTouchEvent", "" + e.getAction());

        if (mExpandAndCollapseAnim != null && mExpandAndCollapseAnim.isRunning() || mTargetView == null) {
            return;
        }

        int x = (int) e.getX();
        int y = (int) e.getY();
        int action = MotionEventCompat.getActionMasked(e);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                break;

            case MotionEvent.ACTION_MOVE:
                int deltaX = (int) (mLastX - e.getX());
                if (mIsDragging) { // 跟手滑动
                    horizontalDrag(deltaX);
                }
                mLastX = x;
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                Log.e("onTouchEvent", "scrollX => " + mTargetView.getScrollX());
                if (mIsDragging) { // 如果在拖动过程中触发了该事件
                    // 在已经折叠的情况下，用户还是向右滑动
                    // 其中 smoothHorizontalExpandOrCollapse(0) 的作用有：
                    // 1. 在未完全展开或折叠的情况下，完成动画，并返回 true
                    // 2. 在已经展开或折叠完成的情况下，不做任何操作，返回 false
                    if (!smoothHorizontalExpandOrCollapse(0) && isCollapsed()) {
                        mTargetView = null;
                    }
                    mIsDragging = false;
                }
                break;
        }
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    /**
     * 根据 targetView 的 scrollX 计算出 targetView 的偏移，这样能够知道这个 point 是在右侧的菜单中
     * @param x
     * @param y
     * @return 是否在右侧的菜单中
     */
    private boolean inView(int x, int y) {
        if (mTargetView == null) {
            return false;
        }

        int scrollX = mTargetView.getScrollX();
        // getWidth()是获得 View 的 width，而不是 View 的内容的 width
        int left = mTargetView.getWidth() - scrollX;
        int top = mTargetView.getTop();
        int right = left + getHorizontalRange();
        int bottom = mTargetView.getBottom();
        Log.e("inView", "scrollX => " + scrollX);
        Log.e("inView", "left => " + left + "\ttop => " + top + "\tright => " + right + "\tbottom => " + bottom);
        Rect rect = new Rect(left, top, right, bottom);
        return rect.contains(x, y);
    }

    private int getHorizontalRange() {
        RecyclerView.ViewHolder viewHolder = mCallback.getChildViewHolder(mTargetView);
        return mCallback.getHorizontalRange(viewHolder);
    }

    private boolean isExpanded() {
        return mTargetView != null && mTargetView.getScrollX() == getHorizontalRange();
    }

    private boolean isCollapsed() {
        return mTargetView != null && mTargetView.getScrollX() == 0;
    }

    /**
     * 根据当前 scrollX 的位置判断是执行展开动画还是折叠动画
     * @param velocityX
     * @return
     */
    private boolean smoothHorizontalExpandOrCollapse(float velocityX) {
        if (mTargetView == null) {
            return false;
        }
        int scrollX = mTargetView.getScrollX();
        int scrollRange = getHorizontalRange();

        if (mExpandAndCollapseAnim != null) {
            return false;
        }

        int to = 0;
        int duration = DEFAULT_DURATION;

        if (velocityX == 0) {
            // 如果已经展一半，平滑展开
            if (scrollX > scrollRange / 2) {
                to = scrollRange;
            }
        } else {
            if (velocityX > 0) { // 右滑，折叠
                to = 0;
            } else { // 左滑，展开
                to = scrollRange;
            }

            duration = (int) ((1.f - Math.abs(velocityX) / mMaxVelocity) * DEFAULT_DURATION);
        }

        // 表明展开或折叠已经在跟手滑动中完成，不再需要下面的动画
        if (to == scrollX) {
            return false;
        }

        mExpandAndCollapseAnim = ObjectAnimator.ofInt(mTargetView, "scrollX", to);
        mExpandAndCollapseAnim.setDuration(duration);
        mExpandAndCollapseAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mExpandAndCollapseAnim = null;
                if (isCollapsed()) {
                    mTargetView = null;
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mExpandAndCollapseAnim = null;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mExpandAndCollapseAnim.start();

        return true;
    }

    /**
     * 根据 touch 事件来滚动 View 的 scrollX
     * @param delta
     */
    private void horizontalDrag(int delta) {
        int scrollX = mTargetView.getScrollX();
        int scrollY = mTargetView.getScrollY();
        Log.e("horizontalDrag", "delta => " + delta + "\tscrollX => " + scrollX + "\tscrollY => " + scrollY);
        if ((scrollX + delta) <= 0) { // 折叠
            mTargetView.scrollTo(0, scrollY);
            return;
        }

        int horRange = getHorizontalRange();
        scrollX += delta;
        if (Math.abs(scrollX) < horRange) {
            mTargetView.scrollTo(scrollX, scrollY);
        } else {
            mTargetView.scrollTo(horRange, scrollY);
        }
    }

    public void clearState() {
        // 折叠菜单
        smoothHorizontalExpandOrCollapse(DEFAULT_DURATION / 2);
    }

    public interface Callback {
        /**
         * 获得右侧菜单的宽度，这里是100dp
         */
        int getHorizontalRange(RecyclerView.ViewHolder holder);

        /**
         * 通过 View 获得它所对应的在 RecyclerView 中的 ViewHolder
         */
        RecyclerView.ViewHolder getChildViewHolder(View childView);

        /**
         * 通过点击的坐标获得所点击的 View
         */
        View findTargetView(float x, float y);
    }
}
