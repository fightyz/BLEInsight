package org.esec.mcg.bleinsight.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.esec.mcg.bleinsight.LogViewFragment;
import org.esec.mcg.bleinsight.PeripheralDetailFragment;
import org.esec.mcg.utils.logger.LogUtils;

/**
 * Created by yz on 2015/10/19.
 */
public class PeripheralPagerAdapter extends FragmentStatePagerAdapter {
    public PeripheralPagerAdapter(FragmentManager fm) { super(fm); }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return PeripheralDetailFragment.newInstance(position);
        } else {
            return LogViewFragment.newInstance();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return "Device Detail";
        } else {
            return "Log View";
        }
    }
}
