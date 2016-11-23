package com.cassianetworks.fall.activities;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.cassianetworks.fall.BaseActivity;
import com.cassianetworks.fall.BaseFragment;
import com.cassianetworks.fall.R;
import com.cassianetworks.fall.fragment.main.NoDeviceFragment;
import com.cassianetworks.fall.fragment.main.ShowDeviceFragment;

import org.xutils.view.annotation.ContentView;

import static com.cassianetworks.fall.BaseApplication.deviceManager;

@ContentView(R.layout.activity_main)
public class MainActivity extends BaseActivity {
    public static MainActivity instance;

    @Override
    protected void init() {
        instance = this;
        initFragment();
    }

    private void initFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        BaseFragment fragment;
        if (deviceManager.getDevList().size() == 0) {
            fragment = new NoDeviceFragment();
        } else {
            fragment = new ShowDeviceFragment();
        }

        transaction.add(R.id.container, fragment);
        transaction.commit();

    }
}
