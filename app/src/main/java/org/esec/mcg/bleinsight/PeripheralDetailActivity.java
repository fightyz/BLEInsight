package org.esec.mcg.bleinsight;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.esec.mcg.bleinsight.adapter.ExpandableRecyclerAdapter;
import org.esec.mcg.bleinsight.adapter.PeripheralDetailAdapter;
import org.esec.mcg.bleinsight.wrapper.BLEWrapper;
import org.esec.mcg.bleinsight.wrapper.InsightDeviceUiCallbacks;
import org.esec.mcg.utils.logger.LogUtils;

import java.util.ArrayList;
import java.util.List;

public class PeripheralDetailActivity extends AppCompatActivity
        implements ExpandableRecyclerAdapter.ExpandCollapseListener, InsightDeviceUiCallbacks {

    public static final String EXTRAS_DEVICE_NAME = "BLE_DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "BLE_DEVICE_ADDRESS";
    public static final String EXTRAS_DEVICE_RSSI = "BLE_DEVICE_RSSI";
    public static final String EXTRAS_DEVICE_BOND = "BLE_DEVICE_BOND";

    private static final int ENABLE_BT_REQUEST_ID = 1;

    private String mDeviceName;
    private String mDeviceAddress;
    private String mDeviceRSSI;

    private BLEWrapper mBLEWrapper;

    private TextView mDeviceNameView;
    private TextView mDeviceAddressView;
    private TextView mDeviceStatusView;
    private TextView connectToggle;
    private Toolbar toolbar;

    private PeripheralDetailAdapter mPeripheralDetailAdapter;

    private RecyclerView mRecyclerView;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peripheral_detail);

        connectViewsVariables();
        setSupportActionBar(toolbar);

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
        mDeviceRSSI = intent.getStringExtra(EXTRAS_DEVICE_RSSI);
        mDeviceAddressView.setText(mDeviceAddress);

        mCollapsingToolbarLayout.setTitle(mDeviceName);

        connectToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tv = (TextView) v;
                if (tv.getText().equals("DISCONNECT")) {
                    mProgressDialog = ProgressDialog.show(PeripheralDetailActivity.this, mDeviceName, "断连中...");
                    mBLEWrapper.disconnect();
                } else if (tv.getText().equals("CONNECT")) {
                    mProgressDialog = ProgressDialog.show(PeripheralDetailActivity.this, mDeviceName, "连接中...");
                    mPeripheralDetailAdapter.clearList();
                    mBLEWrapper.connect(mDeviceAddress);
                }
            }
        });

        if (mBLEWrapper == null) mBLEWrapper = new BLEWrapper(this);
        mBLEWrapper.setInsightDeviceUiCallbacks(this);

        mPeripheralDetailAdapter = new PeripheralDetailAdapter(this);

        mPeripheralDetailAdapter.setExpandCollapseListener(this);

        mRecyclerView.setAdapter(mPeripheralDetailAdapter);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mBLEWrapper.isBtEnabled() == false) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, ENABLE_BT_REQUEST_ID);
        }

        if (mBLEWrapper.initialize() == false) {
            finish();
        }

        if (mPeripheralDetailAdapter == null)
            mPeripheralDetailAdapter = new PeripheralDetailAdapter(this);

        mDeviceStatusView.setText("connecting...");
        mProgressDialog = ProgressDialog.show(PeripheralDetailActivity.this, mDeviceName, "连接中...");
        mBLEWrapper.connect(mDeviceAddress);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBLEWrapper.disconnect();
        mBLEWrapper.close();
    }

    @Override
    public void onListItemExpanded(int position) {
        String toastMessage = getString(R.string.item_expanded, position);
        Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onListItemCollapse(int position) {
        String toastMessage = getString(R.string.item_collapsed, position);
        Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show();
    }

    private void connectViewsVariables() {
        toolbar = (Toolbar) findViewById(R.id.peripheral_toolbar);
        mDeviceAddressView = (TextView) findViewById(R.id.peripheral_address);
        mDeviceStatusView = (TextView) findViewById(R.id.peripheral_status);
        mRecyclerView = (RecyclerView) findViewById(R.id.peripheral_detail_recycler_view);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbarLayout);
        connectToggle = (TextView) findViewById(R.id.connect_toggle);
    }

    public BLEWrapper getBLEWrapper() { return mBLEWrapper; }

    @Override
    public void uiDeviceConnected(BluetoothGatt gatt, BluetoothDevice device) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                mDeviceStatusView.setText("connected");
                connectToggle.setText("DISCONNECT");
            }
        });
    }

    @Override
    public void uiNewRssiAvailable(BluetoothGatt gatt, BluetoothDevice device, final int rssi) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mDeviceRSSI = rssi + "db";
                mCollapsingToolbarLayout.setTitle(mDeviceName + mDeviceRSSI);
            }
        });
    }

    @Override
    public void uiDeviceDisconnected(BluetoothGatt gatt, BluetoothDevice device) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mDeviceStatusView.setText("disconnected");
                connectToggle.setText("CONNECT");
                mPeripheralDetailAdapter.setServiceCharacteristicItemGrey();
                mPeripheralDetailAdapter.notifyDataSetChanged();
                mProgressDialog.dismiss();
            }
        });
    }

    @Override
    public void uiAvailableServices(BluetoothGatt gatt, BluetoothDevice device, final List<BluetoothGattService> services) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (BluetoothGattService service : services) {
                    mPeripheralDetailAdapter.addService(service);
                }

                mPeripheralDetailAdapter.setParentItemList();
                mPeripheralDetailAdapter.notifyDataSetChanged();
                mProgressDialog.dismiss();
            }
        });
    }

    @Override
    public void uiCharacteristicsForService(BluetoothGatt gatt, BluetoothDevice device, BluetoothGattService service, List<BluetoothGattCharacteristic> characteristic) {

    }
}
