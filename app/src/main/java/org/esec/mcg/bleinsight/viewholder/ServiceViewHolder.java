package org.esec.mcg.bleinsight.viewholder;

import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import org.esec.mcg.bleinsight.PeripheralDetailActivity;
import org.esec.mcg.bleinsight.R;
import org.esec.mcg.bleinsight.ServiceItemBean;
import org.esec.mcg.utils.logger.LogUtils;

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
    public PeripheralDetailActivity mContext;

    public ServiceViewHolder(View itemView) {
        super(itemView);

        mServiceNameTextView = (TextView) itemView.findViewById(R.id.service_name);
        mServiceUuidTextView = (TextView) itemView.findViewById(R.id.service_uuid);
        mServiceTypeTextView = (TextView) itemView.findViewById(R.id.service_type);
        mContext = (PeripheralDetailActivity)itemView.getContext();
    }

    public void bind(ServiceItemBean serviceItemBean) {
        LogUtils.d("ServiceItemHolder.bind");
        mServiceNameTextView.setText(serviceItemBean.getServiceName());
        if (!serviceItemBean.getConnectState()) {
            //TODO 这里方法过时，要换成colorStateList作参数
            mServiceNameTextView.setTextColor(mContext.getResources().getColor(R.color.grey));
        } else {
            mServiceNameTextView.setTextColor(mContext.getResources().getColor(R.color.black));
        }
        mServiceUuidTextView.setText(serviceItemBean.getServiceUuid());
        mServiceTypeTextView.setText(serviceItemBean.getServiceType());
    }
}
