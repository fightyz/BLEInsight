package org.esec.mcg.bleinsight.animator.loading;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;

import org.esec.mcg.bleinsight.PeripheralDetailActivity;
import org.esec.mcg.bleinsight.R;

/**
 * Created by yz on 2016/6/8.
 */
public class CustomProgressDialog extends Dialog {

    private Context context = null;
    private static CustomProgressDialog customProgressDialog = null;

    public CustomProgressDialog(Context context) {
        super(context);
        this.context = context;
    }

    public CustomProgressDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
    }

    public static CustomProgressDialog createDialog(Context context) {
        customProgressDialog = new CustomProgressDialog(context, R.style.CustomProgressDialog);
        customProgressDialog.setContentView(R.layout.custom_progress_dialog);
        customProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;

        return customProgressDialog;
    }

    public CustomProgressDialog setDeviceName(String deviceName) {
        TextView tvDeviceName = (TextView) findViewById(R.id.device_name);

        if (tvDeviceName != null) {
            tvDeviceName.setText(deviceName);
        }

        return customProgressDialog;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.e("Dialog", "onBackPressed");
        PeripheralDetailActivity activity = (PeripheralDetailActivity) context;
        activity.finish();
    }
}
