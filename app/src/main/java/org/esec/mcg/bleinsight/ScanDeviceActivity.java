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
import android.widget.TextView;
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
    private TextView scanToggle;

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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("BLEInsight");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        scanToggle = (TextView) findViewById(R.id.scan_toggle);
        scanToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (scanToggle.getText().equals("SCAN")) {
                    startScanningInit();
                } else if (scanToggle.getText().equals("STOP SCANNING")) {
                    stopScanningInit();
                }
            }
        });

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
        scanToggle.setText("STOP SCANNING");
        findViewById(R.id.toolbar_progress_bar).setVisibility(View.VISIBLE);
        DeviceListAdapter.startUpdateRssiThread = true;
        mScanning = true;
        mScanDeviceAdapter.clearList();
        mBLEWrapper.startScanning(SCANNING_TIMEOUT);
    }

    /**
     * 结束扫描后的一些初始化工作
     */
    public void stopScanningInit() {
        scanToggle.setText("SCAN");
        findViewById(R.id.toolbar_progress_bar).setVisibility(View.INVISIBLE);
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
