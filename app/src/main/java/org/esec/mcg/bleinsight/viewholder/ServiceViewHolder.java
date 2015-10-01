package org.esec.mcg.bleinsight.viewholder;

import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import org.esec.mcg.bleinsight.R;
import org.esec.mcg.bleinsight.ServiceItemBean;

/**
 * Created by yz on 2015/9/28.
 */
public class ServiceViewHolder extends ParentViewHolder {

    private static final float INITIAL_POSITION = 0.0f;
    private static final float ROTATED_POSITION = 180f;
    private static final float PIVOT_VALUE = 0.5f;
    private static final long DEFAULT_ROTATE_DURATION_MS = 200;

    public TextView mServiceNameTextView;
    public TextView mServiceUuidTextView;
    public TextView mServiceTypeTextView;
//    public ImageView mArrowExpandImageView;

    public ServiceViewHolder(View itemView) {
        super(itemView);

        mServiceNameTextView = (TextView) itemView.findViewById(R.id.service_name);
        mServiceUuidTextView = (TextView) itemView.findViewById(R.id.service_uuid);
        mServiceTypeTextView = (TextView) itemView.findViewById(R.id.service_type);
//        mArrowExpandImageView = (ImageView) itemView.findViewById(R.id.list_item_parent_horizontal_arrow_imageView);
    }

    public void bind(ServiceItemBean serviceItemBean) {
        mServiceNameTextView.setText(serviceItemBean.getServiceName());
        mServiceUuidTextView.setText(serviceItemBean.getServiceUuid());
        mServiceTypeTextView.setText(serviceItemBean.getServiceType());
    }

//    @Override
//    public void setExpanded(boolean expanded) {
//        super.setExpanded(expanded);
//
//        if (expanded) {
//            mArrowExpandImageView.setRotation(ROTATED_POSITION);
//        } else {
//            mArrowExpandImageView.setRotation(INITIAL_POSITION);
//        }
//    }
//
//    @Override
//    public void onExpansionToggled(boolean expanded) {
//        super.onExpansionToggled(expanded);
//
//        RotateAnimation rotateAnimation = new RotateAnimation(ROTATED_POSITION,
//                INITIAL_POSITION,
//                RotateAnimation.RELATIVE_TO_SELF, PIVOT_VALUE,
//                RotateAnimation.RELATIVE_TO_SELF, PIVOT_VALUE);
//        rotateAnimation.setDuration(DEFAULT_ROTATE_DURATION_MS);
//        rotateAnimation.setFillAfter(true);
//        mArrowExpandImageView.startAnimation(rotateAnimation);
//    }
}
