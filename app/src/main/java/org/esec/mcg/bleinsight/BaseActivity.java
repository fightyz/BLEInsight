package org.esec.mcg.bleinsight;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.VelocityTracker;

import org.esec.mcg.library.logger.LogUtils;

/**
 * Created by yz on 2016/6/13.
 */
public class BaseActivity extends AppCompatActivity {

    // 手指向右滑动时的最小速度
    private static final int XSPEED_MIN = 200;

    // 手指向右滑动时的最小距离
    private static final int XDISTANCE_MIN = 150;

    // 记录手指按下时的横坐标
    private float xDown;

    // 记录手指移动时的横坐标
    private float xMove;

    // 用于计算手指滑动的速度
    private VelocityTracker mVelocityTracker;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        LogUtils.d("onTouchEvent");
        createVelocityTracker(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xDown = event.getRawX();
                break;

            case MotionEvent.ACTION_MOVE:
                xMove = event.getRawX();
                // 活动的距离
                int distanceX = (int) (xMove - xDown);
                // 获取顺时速度
                int xSpeed = getScrollVelocity();
                // 当滑动的距离大于我们设定的最小距离且滑动的瞬间速度大于我们设定的速度时，返回到上一个activity
                if (distanceX > XDISTANCE_MIN && xSpeed > XSPEED_MIN) {
                    finish();
                    overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                }
                break;

            case MotionEvent.ACTION_UP:
                recycleVelocityTracker();
                break;

            default:
                break;

        }
        return super.dispatchTouchEvent(event);
    }

    private void createVelocityTracker(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    /**
     * 获取手指在content界面滑动的速度
     * @return 滑动速度，以每秒钟移动了多少像素值为单位
     */
    private int getScrollVelocity() {
        mVelocityTracker.computeCurrentVelocity(1000);
        int velocity = (int)mVelocityTracker.getXVelocity();
        return Math.abs(velocity);
    }

    /**
     * 回收VelocityTracker对象
     */
    private void recycleVelocityTracker() {
        mVelocityTracker.recycle();
        mVelocityTracker = null;
    }
}
