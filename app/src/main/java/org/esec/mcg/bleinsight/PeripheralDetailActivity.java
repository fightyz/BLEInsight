package org.esec.mcg.bleinsight;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.esec.mcg.bleinsight.adapter.ExpandableRecyclerAdapter;
import org.esec.mcg.bleinsight.adapter.PeripheralDetailAdapter;

import java.util.ArrayList;
import java.util.List;

public class PeripheralDetailActivity extends Activity implements ExpandableRecyclerAdapter.ExpandCollapseListener {

    private static final int NUM_TEST_DATA_ITEMS = 20;

    private PeripheralDetailAdapter mPeripheralDetailAdapter;

    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peripheral_detail);

        mRecyclerView = (RecyclerView) findViewById(R.id.peripheral_detail_recycler_view);

        mPeripheralDetailAdapter = new PeripheralDetailAdapter(this, setUpTestData(NUM_TEST_DATA_ITEMS));

        mPeripheralDetailAdapter.setExpandCollapseListener(this);

        mRecyclerView.setAdapter(mPeripheralDetailAdapter);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_peripheral_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private List<ServiceItem> setUpTestData(int numItems) {
        List<ServiceItem> serviceParentList = new ArrayList<>();

        for (int i = 0; i < numItems; i++) {
            List<CharacteristicItem> childItemList = new ArrayList<>();

            CharacteristicItem characteristicItem = new CharacteristicItem();
            characteristicItem.setChildText("Child {i}");
            childItemList.add(characteristicItem);

            if (i % 2 == 0) {
                CharacteristicItem characteristicItem1 = new CharacteristicItem();
                characteristicItem1.setChildText("Child {i}_2");
                childItemList.add(characteristicItem);
            }

            ServiceItem serviceItem = new ServiceItem();
            serviceItem.setChildItemList(childItemList);
            serviceItem.setParentNumber(i);
            serviceItem.setParentText("Parent {i}");
            if (i == 0) {
                serviceItem.setInitiallyExpanded(true);
            }
            serviceParentList.add(serviceItem);
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
}
