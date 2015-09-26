package org.esec.mcg.bleinsight;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.Toolbar;

import org.esec.mcg.bleinsight.adapter.DeviceDetailAdapter;
import org.esec.mcg.bleinsight.adapter.DeviceListAdapter;
import org.esec.mcg.bleinsight.adapter.ScanDeviceAdapter;
import org.esec.mcg.bleinsight.wrapper.BLEWrapper;
import org.esec.mcg.bleinsight.wrapper.ScanDeviceUiCallbacks;
import org.esec.mcg.utils.logger.LogUtils;

public class ScanDeviceActivity extends Activity implements ScanDeviceUiCallbacks {
    private static final long SCANNING_TIMEOUT = 20 * 1000;  /* 5 seconds */
    private static final int ENABLE_BT_REQUEST_ID = 1;

    public BLEWrapper mBLEWrapper;
    public static boolean mScanning = false; /* actionBar上start&stop的开关 */
    private ScanDeviceAdapter mScanDeviceAdapter;
    private RecyclerView mRecyclerView;
    private Toolbar toolbar;
    private Toolbar.OnMenuItemClickListener onMenuItemClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_scan);

        mScanDeviceAdapter = new ScanDeviceAdapter(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.device_list_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mScanDeviceAdapter);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        onMenuItemClickListener = new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();

                switch (id) {
                    case R.id.scanning_start:
                        startScanningInit();
                        break;
                    case R.id.scanning_stop:
                        stopScanningInit();
                        mBLEWrapper.stopScanning();
                        break;
                }

                invalidateOptionsMenu();

                return true;
            }
        };
        toolbar.inflateMenu(R.menu.menu_device_scan);
        setActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(onMenuItemClickListener);
//        toolbar.set
//        toolbar.

        mBLEWrapper = new BLEWrapper(this);
        mBLEWrapper.setScanDeviceUiCallbacks(this);

        if (mBLEWrapper.checkBleHardwareAvailable() == false) {
            BLEMissing();
        }
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


        startScanningInit();
        invalidateOptionsMenu();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopScanningInit();
        mBLEWrapper.stopScanning();
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_device_scan, menu);

        if (mScanning) {
            menu.findItem(R.id.scanning_start).setVisible(false);
            menu.findItem(R.id.scanning_stop).setVisible(true);
//            menu.findItem(R.id.scanning_indicator).setActionView(R.layout.progress_indicator);
        } else {
            menu.findItem(R.id.scanning_start).setVisible(true);
            menu.findItem(R.id.scanning_stop).setVisible(false);
//            menu.findItem(R.id.scanning_indicator).setActionView(null);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.scanning_start:
                startScanningInit();
                break;
            case R.id.scanning_stop:
                stopScanningInit();
                mBLEWrapper.stopScanning();
                break;
        }

        invalidateOptionsMenu();

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ENABLE_BT_REQUEST_ID) {
            if (resultCode == Activity.RESULT_CANCELED) {
                BLEDisabled();
                return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 开始扫描前的一些初始化工作
     */
    public void startScanningInit() {
        toolbar.findViewById(R.id.toolbar_progress_bar).setVisibility(View.VISIBLE);
        DeviceListAdapter.startUpdateRssiThread = true;
        mScanning = true;
        mScanDeviceAdapter.clearList();
        mBLEWrapper.startScanning(SCANNING_TIMEOUT);
    }

    /**
     * 结束扫描后的一些初始化工作
     */
    public void stopScanningInit() {
        toolbar.findViewById(R.id.toolbar_progress_bar).setVisibility(View.INVISIBLE);
        mScanning = false;
        mScanDeviceAdapter.updatePeriodicalyRssi(false);
    }

    private void BLEMissing() {
        Toast.makeText(this, "BLE Hardware is required but not available!", Toast.LENGTH_LONG).show();
        finish();
    }

    private void BLEDisabled() {
        Toast.makeText(this, "Sorry, BT has to be turned ON for us to work!", Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void uiDeviceFound(ScanResult scanResult) {
        mScanDeviceAdapter.addDevice(scanResult);
    }

    @Override
    public void uiStopScanning() {
        stopScanningInit();
        invalidateOptionsMenu();
    }
}
