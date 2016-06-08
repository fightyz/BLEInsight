package org.esec.mcg.bleinsight.animator.item;

import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by yz on 2016/5/31.
 */
public class SlideInLeftItemAnimator extends BaseItemAnimator {

    public SlideInLeftItemAnimator(RecyclerView recyclerView) {
        super(recyclerView);
    }

    @Override
    protected void animateAddImpl(final RecyclerView.ViewHolder holder) {
        final View view = holder.itemView;
        final ViewPropertyAnimatorCompat animation = ViewCompat.animate(view);
        mAddAnimations.add(holder);
        animation.translationX(0)
                .alpha(1)
                .setDuration(getAddDuration())
                .setListener(new VpaListenerAdapter() {

                    @Override
                    public void onAnimationStart(View view) {
                        dispatchAddStarting(holder);
                    }

                    @Override
                    public void onAnimationEnd(View view) {
                        ViewCompat.setTranslationX(view, 0);
                        ViewCompat.setAlpha(view, 1);
                    }

                    @Override
                    public void onAnimationCancel(View view) {
                        animation.setListener(null);
                        ViewCompat.setTranslationX(view, 0);
                        ViewCompat.setAlpha(view, 1);
                        dispatchAddFinished(holder);
                        mAddAnimations.remove(holder);
                        dispatchFinishedWhenDone();
                    }
                }).start();
    }

    @Override
    protected void prepareAnimateAdd(RecyclerView.ViewHolder holder) {
        ViewCompat.setTranslationX(holder.itemView, -mRecyclerView.getLayoutManager().getWidth());
    }
}
