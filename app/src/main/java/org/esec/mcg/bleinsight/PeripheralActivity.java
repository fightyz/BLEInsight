package org.esec.mcg.bleinsight;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.esec.mcg.bleinsight.wrapper.BLEWrapper;
import org.esec.mcg.utils.logger.LogUtils;
import org.w3c.dom.Text;

public class PeripheralActivity extends AppCompatActivity {

    public static final String EXTRAS_DEVICE_NAME       = "BLE_DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS    = "BLE_DEVICE_ADDRESS";
    public static final String EXTRAS_DEVICE_RSSI       = "BLE_DEVICE_RSSI";
    public static final String EXTRAS_DEVICE_BOND       = "BLE_DEVICE_BOND";

    private String mDeviceName;
    private String mDeviceAddress;
    private String mDeviceRSSI;

    private BLEWrapper mBleWrapper;

    private TextView mDeviceNameView;
    private TextView mDeviceAddressView;
    private TextView mDeviceRssiView;
    private TextView mDeviceStatusView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peripheral);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        connectViewsVariables();

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        LogUtils.d(mDeviceName);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
        mDeviceRSSI = intent.getStringExtra(EXTRAS_DEVICE_RSSI);
        mDeviceNameView.setText(mDeviceName);
        mDeviceAddressView.setText(mDeviceAddress);
        mDeviceRssiView.setText(mDeviceRSSI);
        getSupportActionBar().setTitle(mDeviceName);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_peripheral, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void connectViewsVariables() {
        mDeviceNameView = (TextView) findViewById(R.id.peripheral_name);
        mDeviceAddressView = (TextView) findViewById(R.id.peripheral_address);
        mDeviceRssiView = (TextView) findViewById(R.id.peripheral_rssi);
        mDeviceStatusView = (TextView) findViewById(R.id.peripheral_status);

    }
}
