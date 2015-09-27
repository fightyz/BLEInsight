package org.esec.mcg.bleinsight;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import org.esec.mcg.bleinsight.adapter.DeviceDetailAdapter;
import org.esec.mcg.bleinsight.wrapper.BLEWrapper;
import org.esec.mcg.bleinsight.wrapper.InsightDeviceUiCallbacks;
import org.esec.mcg.utils.logger.LogUtils;

import java.util.List;

public class PeripheralActivity extends Activity implements InsightDeviceUiCallbacks{

    public static final String EXTRAS_DEVICE_NAME       = "BLE_DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS    = "BLE_DEVICE_ADDRESS";
    public static final String EXTRAS_DEVICE_RSSI       = "BLE_DEVICE_RSSI";
    public static final String EXTRAS_DEVICE_BOND       = "BLE_DEVICE_BOND";

    private static final int ENABLE_BT_REQUEST_ID = 1;

    private String mDeviceName;
    private String mDeviceAddress;
    private String mDeviceRSSI;

    private BLEWrapper mBLEWrapper;

    private TextView mDeviceNameView;
    private TextView mDeviceAddressView;
    private TextView mDeviceRssiView;
    private TextView mDeviceStatusView;

    private ExpandableListView deviceDetailElv;

    private DeviceDetailAdapter mDeviceDetailAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peripheral);

        connectViewsVariables();

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        LogUtils.d(mDeviceName);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
        mDeviceRSSI = intent.getStringExtra(EXTRAS_DEVICE_RSSI);
        mDeviceNameView.setText(mDeviceName);
        mDeviceAddressView.setText(mDeviceAddress);
        mDeviceRssiView.setText(mDeviceRSSI);

        Toolbar toolbar = (Toolbar) findViewById(R.id.peripheral_toolbar);
        toolbar.setTitle(mDeviceName);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final TextView connectToggle = (TextView) findViewById(R.id.connect_toggle);
        connectToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tv = (TextView)v;
                if (tv.getText().equals("DISCONNECT")) {
                    // TODO 将界面中所有字体设置为灰色
                } else if (tv.getText().equals("CONNECT")) {
                    // TODO: 9/27/15 显示菊花进度条并连接
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mBLEWrapper == null) mBLEWrapper = new BLEWrapper(this);
        mBLEWrapper.setInsightDeviceUiCallbacks(this);

        if (mBLEWrapper.isBtEnabled() == false) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, ENABLE_BT_REQUEST_ID);
        }

        if (mBLEWrapper.initialize() == false) {
            finish();
        }

        if (mDeviceDetailAdapter == null) mDeviceDetailAdapter = new DeviceDetailAdapter(this);

        deviceDetailElv.setFocusable(false);
        deviceDetailElv.setAdapter(mDeviceDetailAdapter);

        mDeviceStatusView.setText("connecting...");
        mBLEWrapper.connect(mDeviceAddress);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mBLEWrapper.stopMonitoringRssiValue();
        mBLEWrapper.disconnect();
        mBLEWrapper.close();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ENABLE_BT_REQUEST_ID) {
            if (requestCode == Activity.RESULT_CANCELED) {
                BLEDisabled();
                return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void connectViewsVariables() {
        mDeviceNameView = (TextView) findViewById(R.id.peripheral_name);
        mDeviceAddressView = (TextView) findViewById(R.id.peripheral_address);
        mDeviceRssiView = (TextView) findViewById(R.id.peripheral_rssi);
        mDeviceStatusView = (TextView) findViewById(R.id.peripheral_status);
        deviceDetailElv = (ExpandableListView) findViewById(R.id.device_detail_list_view);
    }

    @Override
    public void uiDeviceConnected(BluetoothGatt gatt, BluetoothDevice device) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mDeviceStatusView.setText("connected");
            }
        });
    }

    @Override
    public void uiDeviceDisconnected(BluetoothGatt gatt, BluetoothDevice device) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mDeviceStatusView.setText("disconnected");
            }
        });
    }

    @Override
    public void uiNewRssiAvailable(final BluetoothGatt gatt, final BluetoothDevice device, final int rssi) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mDeviceRSSI = rssi + "db";
                mDeviceRssiView.setText(mDeviceRSSI);
            }
        });
    }

    @Override
    public void uiAvailableServices(BluetoothGatt gatt, BluetoothDevice device, final List<BluetoothGattService> services) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                deviceDetailElv.setAdapter(mDeviceDetailAdapter);

                for (BluetoothGattService service : services) {
                    mDeviceDetailAdapter.addService(service);
                }
                mDeviceDetailAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void uiCharacteristicsForService(BluetoothGatt gatt, BluetoothDevice device, BluetoothGattService service, final List<BluetoothGattCharacteristic> characteristics) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (BluetoothGattCharacteristic ch : characteristics) {
                    mDeviceDetailAdapter.addCharacteristic(ch);
                }
                mDeviceDetailAdapter.notifyDataSetChanged();
            }
        });
    }

    private void BLEDisabled() {
        Toast.makeText(this, "Sorry, BT has to be turned ON for us to work!", Toast.LENGTH_LONG).show();
        finish();
    }
}
