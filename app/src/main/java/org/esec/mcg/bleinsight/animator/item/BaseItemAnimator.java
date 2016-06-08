package org.esec.mcg.bleinsight.animator.item;

import android.support.v4.animation.AnimatorCompatHelper;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.Log;
import android.view.View;

import org.esec.mcg.utils.logger.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yz on 2016/5/30.
 */
public abstract class BaseItemAnimator extends SimpleItemAnimator {

    /**
     * RecyclerView
     */
    protected RecyclerView mRecyclerView;

    public BaseItemAnimator(RecyclerView recyclerView) { mRecyclerView = recyclerView; }

    private static final boolean DEBUG = false;

//    private ArrayList<RecyclerView.ViewHolder> mPendingRemovals = new ArrayList<>();
    private ArrayList<RecyclerView.ViewHolder> mPendingAdditions = new ArrayList<>();
//    private ArrayList<MoveInfo> mPendingMoves = new ArrayList<>();
//    private ArrayList<ChangeInfo> mPendingChanges = new ArrayList<>();

    private ArrayList<ArrayList<RecyclerView.ViewHolder>> mAdditionsList = new ArrayList<>();
//    private ArrayList<ArrayList<MoveInfo>> mMovesList = new ArrayList<>();
//    private ArrayList<ArrayList<ChangeInfo>> mChangesList = new ArrayList<>();

    protected ArrayList<RecyclerView.ViewHolder> mAddAnimations = new ArrayList<>();
//    protected ArrayList<RecyclerView.ViewHolder> mMoveAnimations = new ArrayList<>();
//    protected ArrayList<RecyclerView.ViewHolder> mRemoveAnimations = new ArrayList<>();
//    protected ArrayList<RecyclerView.ViewHolder> mChangeAnimations = new ArrayList<>();

    private static class MoveInfo {
        public RecyclerView.ViewHolder holder;
        public int fromX, fromY, toX, toY;

        private MoveInfo(RecyclerView.ViewHolder holder, int fromX, int fromY, int toX, int toY) {
            this.holder = holder;
            this.fromX = fromX;
            this.fromY = fromY;
            this.toX = toX;
            this.toY = toY;
        }
    }

    private static class ChangeInfo {
        public RecyclerView.ViewHolder oldHolder, newHolder;
        public int fromX, fromY, toX, toY;
        private ChangeInfo(RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder) {
            this.oldHolder = oldHolder;
            this.newHolder = newHolder;
        }

        private ChangeInfo(RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder,
                           int fromX, int fromY, int toX, int toY) {
            this(oldHolder, newHolder);
            this.fromX = fromX;
            this.fromY = fromY;
            this.toX = toX;
            this.toY = toY;
        }

        @Override
        public String toString() {
            return "ChangeInfo{" +
                    "oldHolder=" + oldHolder +
                    ", newHolder=" + newHolder +
                    ", fromX=" + fromX +
                    ", fromY=" + fromY +
                    ", toX=" + toX +
                    ", toY=" + toY +
                    '}';
        }
    }

    @Override
    public void runPendingAnimations() {
        LogUtils.d("runPendingAnimations");
        boolean additionsPending = !mPendingAdditions.isEmpty();
        if (!additionsPending) {
            // nothing to animate
            return;
        }

        // Next, add stuff
        if (additionsPending) {
            final ArrayList<RecyclerView.ViewHolder> additions = new ArrayList<>();
            additions.addAll(mPendingAdditions);
            mAdditionsList.add(additions);
            mPendingAdditions.clear();
            Runnable adder = new Runnable() {
                @Override
                public void run() {
                    for (RecyclerView.ViewHolder holder : additions) {
                        animateAddImpl(holder);
                    }
                    additions.clear();
                    mAdditionsList.remove(additions);
                }
            };
            adder.run();
        }
    }

    @Override
    public boolean animateRemove(RecyclerView.ViewHolder holder) {
        return false;
    }

    @Override
    public boolean animateAdd(final RecyclerView.ViewHolder holder) {
        LogUtils.d("animateAdd");
        resetAnimation(holder);
        prepareAnimateAdd(holder);
        ViewCompat.setAlpha(holder.itemView, 0);
        mPendingAdditions.add(holder);
        return true;
    }

    @Override
    public boolean animateMove(RecyclerView.ViewHolder holder, int fromX, int fromY, int toX, int toY) {
        return false;
    }

    @Override
    public boolean animateChange(RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder, int fromLeft, int fromTop, int toLeft, int toTop) {
        return false;
    }

    private void resetAnimation(RecyclerView.ViewHolder holder) {
        AnimatorCompatHelper.clearInterpolator(holder.itemView);
        endAnimation(holder);
    }

    @Override
    public void endAnimation(RecyclerView.ViewHolder item) {
        final View view = item.itemView;
        // this will trigger end callback which should set properties to their target values.
        ViewCompat.animate(view).cancel();

        if (mPendingAdditions.remove(item)) {
            ViewCompat.setAlpha(view, 1);
            dispatchAddFinished(item);
        }

        for (int i = mAdditionsList.size() - 1; i >= 0; i--) {
            ArrayList<RecyclerView.ViewHolder> additions = mAdditionsList.get(i);
            if (additions.remove(item)) {
                ViewCompat.setAlpha(view, 1);
                dispatchAddFinished(item);
                if (additions.isEmpty()) {
                    mAdditionsList.remove(i);
                }
            }
        }

        if (mAddAnimations.remove(item) && DEBUG) {
            throw new IllegalStateException("after animation is canceled, item should not be in " +
                    "mAddAnimations list");
        }

        dispatchFinishedWhenDone();
    }

    @Override
    public void endAnimations() {
        int count = mPendingAdditions.size();
        for (int i = count - 1; i >= 0; i--) {
            RecyclerView.ViewHolder item = mPendingAdditions.get(i);
            View view = item.itemView;
            ViewCompat.setAlpha(view, 1);
            dispatchAddFinished(item);
            mPendingAdditions.remove(i);
        }

        int listCount = mAdditionsList.size();
        for (int i = listCount - 1; i >= 0; i--) {
            ArrayList<RecyclerView.ViewHolder> additions = mAdditionsList.get(i);
            count = additions.size();
            for (int j = count - 1; j >= 0; j--) {
                RecyclerView.ViewHolder item = additions.get(j);
                View view = item.itemView;
                ViewCompat.setAlpha(view, 1);
                dispatchAddFinished(item);
                additions.remove(j);
                if (additions.isEmpty()) {
                    mAdditionsList.remove(additions);
                }
            }
        }

        cancelAll(mAddAnimations);

        dispatchAnimationsFinished();
    }

    void cancelAll(List<RecyclerView.ViewHolder> viewHolders) {
        for (int i = viewHolders.size() - 1; i >= 0; i--) {
            ViewCompat.animate(viewHolders.get(i).itemView).cancel();
        }
    }

    /**
     * Check the state of currently pending and running animations. If there are none
     * pending/running, call {@link #dispatchAnimationsFinished()} to notify any
     * listeners.
     */
    protected void dispatchFinishedWhenDone() {
        if (!isRunning()) {
            dispatchAnimationsFinished();
        }
    }

    protected abstract void animateAddImpl(final RecyclerView.ViewHolder holder);

    protected abstract void prepareAnimateAdd(final RecyclerView.ViewHolder holder);

    @Override
    public boolean isRunning() {
        return (!mPendingAdditions.isEmpty() ||
                !mAddAnimations.isEmpty() ||
                !mAdditionsList.isEmpty());
    }

    protected static class VpaListenerAdapter implements ViewPropertyAnimatorListener {
        @Override
        public void onAnimationStart(View view) {}

        @Override
        public void onAnimationEnd(View view) {}

        @Override
        public void onAnimationCancel(View view) {}
    }
}
