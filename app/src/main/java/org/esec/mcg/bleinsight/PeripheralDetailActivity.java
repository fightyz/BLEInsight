package org.esec.mcg.bleinsight;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import org.esec.mcg.bleinsight.adapter.ExpandableRecyclerAdapter;
import org.esec.mcg.bleinsight.adapter.PeripheralDetailAdapter;
import org.esec.mcg.bleinsight.wrapper.BLEWrapper;
import org.esec.mcg.bleinsight.wrapper.InsightDeviceUiCallbacks;
import org.esec.mcg.utils.logger.LogUtils;

import java.util.ArrayList;
import java.util.List;

public class PeripheralDetailActivity extends Activity
        implements ExpandableRecyclerAdapter.ExpandCollapseListener, InsightDeviceUiCallbacks
{

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

    private PeripheralDetailAdapter mPeripheralDetailAdapter;

    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peripheral_detail);

        connectViewsVariables();

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
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

        mPeripheralDetailAdapter = new PeripheralDetailAdapter(this);

        mPeripheralDetailAdapter.setExpandCollapseListener(this);

        mRecyclerView.setAdapter(mPeripheralDetailAdapter);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
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

        if (mPeripheralDetailAdapter == null) mPeripheralDetailAdapter = new PeripheralDetailAdapter(this);

        mDeviceStatusView.setText("connecting...");
        mBLEWrapper.connect(mDeviceAddress);
    }

    private List<ServiceItemBean> setUpTestData(int numItems) {
        List<ServiceItemBean> serviceParentList = new ArrayList<>();

        for (int i = 0; i < numItems; i++) {
            List<CharacteristicItemBean> childItemList = new ArrayList<>();

            CharacteristicItemBean characteristicItemBean = new CharacteristicItemBean();
            characteristicItemBean.setChildText(getString(R.string.child_text, i));
            childItemList.add(characteristicItemBean);

            if (i % 2 == 0) {
                CharacteristicItemBean characteristicItemBean1 = new CharacteristicItemBean();
                characteristicItemBean1.setChildText(getString(R.string.second_child_text, i));
                childItemList.add(characteristicItemBean);
            }

            ServiceItemBean serviceItemBean = new ServiceItemBean();
            serviceItemBean.setChildItemList(childItemList);
//            serviceItemBean.setParentNumber(i);
//            serviceItemBean.setParentText(getString(R.string.parent_text, i));
            if (i == 0) {
                serviceItemBean.setInitiallyExpanded(true);
            }
            serviceParentList.add(serviceItemBean);
        }

        return serviceParentList;
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
        mDeviceNameView = (TextView) findViewById(R.id.peripheral_name);
        mDeviceAddressView = (TextView) findViewById(R.id.peripheral_address);
        mDeviceRssiView = (TextView) findViewById(R.id.peripheral_rssi);
        mDeviceStatusView = (TextView) findViewById(R.id.peripheral_status);
        mRecyclerView = (RecyclerView) findViewById(R.id.peripheral_detail_recycler_view);
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
    public void uiNewRssiAvailable(BluetoothGatt gatt, BluetoothDevice device, final int rssi) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mDeviceRSSI = rssi + "db";
                mDeviceRssiView.setText(mDeviceRSSI);
            }
        });
    }

    @Override
    public void uiDeviceDisconnected(BluetoothGatt gatt, BluetoothDevice device) {

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
            }
        });
    }

    @Override
    public void uiCharacteristicsForService(BluetoothGatt gatt, BluetoothDevice device, BluetoothGattService service, List<BluetoothGattCharacteristic> characteristic) {

    }
}
