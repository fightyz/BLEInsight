package org.esec.mcg.bleinsight;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.esec.mcg.bleinsight.adapter.ExpandableRecyclerAdapter;
import org.esec.mcg.bleinsight.adapter.LogViewRecyclerAdapter;
import org.esec.mcg.bleinsight.adapter.PeripheralDetailAdapter;
import org.esec.mcg.bleinsight.adapter.PeripheralPagerAdapter;
import org.esec.mcg.bleinsight.animator.loading.CustomProgressDialog;
import org.esec.mcg.bleinsight.wrapper.BLEWrapper;
import org.esec.mcg.bleinsight.wrapper.InsightDeviceUiCallbacks;

import java.util.List;

public class PeripheralDetailActivity extends SwipeActivity
        implements ExpandableRecyclerAdapter.ExpandCollapseListener, InsightDeviceUiCallbacks {

    public static final String EXTRAS_DEVICE_NAME = "BLE_DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "BLE_DEVICE_ADDRESS";
    public static final String EXTRAS_DEVICE_RSSI = "BLE_DEVICE_RSSI";
    public static final String EXTRAS_DEVICE_BOND = "BLE_DEVICE_BOND";

    private static final int ENABLE_BT_REQUEST_ID = 1;

    private boolean PAUSE_FLAG = false;

    private String mDeviceName;
    private String mDeviceAddress;
    private String mDeviceRSSI;

    private BLEWrapper mBLEWrapper;

    private TextView mDeviceAddressView;
    private TextView mDeviceStatusView;
    private TextView connectToggle;
    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private PeripheralDetailAdapter mPeripheralDetailAdapter;
    private PeripheralPagerAdapter mPeripheralPagerAdapter;

    private RecyclerView mRecyclerView;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;

    private CustomProgressDialog mCustomProgressDialog = null;

    private LogViewRecyclerAdapter mLogViewRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peripheral_detail);
        setSwipeAnyWhere(false);

        connectViewsVariables();
        setSupportActionBar(mToolbar);

        mCustomProgressDialog = CustomProgressDialog.createDialog(this);

        mPeripheralPagerAdapter = new PeripheralPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPeripheralPagerAdapter);
        mTabLayout.setTabsFromPagerAdapter(mPeripheralPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
        mDeviceRSSI = intent.getStringExtra(EXTRAS_DEVICE_RSSI);
        mDeviceAddressView.setText(mDeviceAddress);


        connectToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tv = (TextView) v;
                if (tv.getText().equals("DISCONNECT")) {
                    mCustomProgressDialog.setDeviceName(mDeviceName);
                    mCustomProgressDialog.show();
                    mBLEWrapper.disconnect();
                } else if (tv.getText().equals("CONNECT")) {
                    mCustomProgressDialog.setDeviceName(mDeviceName);
                    mCustomProgressDialog.show();
                    mPeripheralDetailAdapter.clearList();
                    mBLEWrapper.connect(mDeviceAddress);
                }
            }
        });

        if (mBLEWrapper == null) mBLEWrapper = new BLEWrapper(this);
        mBLEWrapper.setInsightDeviceUiCallbacks(this);

        mPeripheralDetailAdapter = new PeripheralDetailAdapter(this);

        mPeripheralDetailAdapter.setExpandCollapseListener(this);

        mRecyclerView = new RecyclerView(this);

        mRecyclerView.setAdapter(mPeripheralDetailAdapter);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

//        mRecyclerView.setItemAnimator(new SlideInLeftItemAnimator(mRecyclerView));

        mLogViewRecyclerAdapter = LogViewRecyclerAdapter.getInstance(this);
    }

    @Override
    protected void onResume() {
//        LogUtils.d("PAUSE_FLAG = " + PAUSE_FLAG);
        super.onResume();

        mCollapsingToolbarLayout.setTitle(mDeviceName);

        if (PAUSE_FLAG) {
            PAUSE_FLAG = false;
            return;
        }

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
        mCustomProgressDialog.setDeviceName(mDeviceName);
        mCustomProgressDialog.show();
        mLogViewRecyclerAdapter.insertLogItem("Connecting to " + mDeviceAddress);
        mBLEWrapper.connect(mDeviceAddress);
    }

    @Override
    protected void onPause() {
        super.onPause();
        PAUSE_FLAG = true;
//        mPeripheralDetailAdapter.clearList();
//        mBLEWrapper.disconnectWithoutCallback();
//        mBLEWrapper.close();
    }

    @Override
    protected void onDestroy() {
        Log.e("PeripheralDeta", "onDestroy()");
        super.onDestroy();
        mPeripheralDetailAdapter.clearList();
        mBLEWrapper.disconnectWithoutCallback();
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
        mToolbar = (Toolbar) findViewById(R.id.peripheral_toolbar);
        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mDeviceAddressView = (TextView) findViewById(R.id.peripheral_address);
        mDeviceStatusView = (TextView) findViewById(R.id.peripheral_status);
//        mRecyclerView = (RecyclerView) findViewById(R.id.peripheral_detail_recycler_view);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbarLayout);
        connectToggle = (TextView) findViewById(R.id.connect_toggle);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
    }

    public BLEWrapper getBLEWrapper() { return mBLEWrapper; }

    @Override
    public void uiDeviceConnected(BluetoothGatt gatt, BluetoothDevice device) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                mDeviceStatusView.setText("connected");
                connectToggle.setText("DISCONNECT");
                mPeripheralDetailAdapter.clearList();
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
                mPeripheralDetailAdapter.setServiceCharacteristicItemGrey(true);
                mPeripheralDetailAdapter.notifyDataSetChanged();
                mCustomProgressDialog.dismiss();
            }
        });
    }

    @Override
    public void uiAvailableServices(BluetoothGatt gatt, BluetoothDevice device, final List<BluetoothGattService> services) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < services.size(); i++) {
                    BluetoothGattService service = services.get(i);
                    mPeripheralDetailAdapter.addService(service);
//                    mPeripheralDetailAdapter.notifyItemInserted(i);
                }

                mPeripheralDetailAdapter.setParentItemList();
                mPeripheralDetailAdapter.notifyDataSetChanged();
                mCustomProgressDialog.dismiss();
            }
        });
    }

    @Override
    public void uiCharacteristicsForService(BluetoothGatt gatt, BluetoothDevice device, BluetoothGattService service, List<BluetoothGattCharacteristic> characteristic) {

    }

    public void uiCharacteristicChanged(final BluetoothGattCharacteristic characteristic) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int position = mPeripheralDetailAdapter.updateCharacteristicForUuid(characteristic);
//                mPeripheralDetailAdapter.notifyDataSetChanged();
                mPeripheralDetailAdapter.notifyItemChanged(position);
            }
        });
    }

    @Override
    public void uiCharacteristicReaded(BluetoothGattCharacteristic characteristic) {

    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        mCustomProgressDialog.dismiss();
//        finish();
//    }

    /**
     * 打印log到LogTab
     * @param log
     */
    @Override
    public void uiLogConnectState(String log) {
        mLogViewRecyclerAdapter.insertLogItem(log);
    }

    public PeripheralDetailAdapter getPeripheralDetailAdapter() {
        return mPeripheralDetailAdapter;
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }
}
