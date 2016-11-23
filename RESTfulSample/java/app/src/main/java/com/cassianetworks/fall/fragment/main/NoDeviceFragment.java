package com.cassianetworks.fall.fragment.main;

import android.view.View;


import com.cassianetworks.fall.BaseFragment;
import com.cassianetworks.fall.R;
import com.cassianetworks.fall.activities.SearchDeviceActivity;

import org.xutils.view.annotation.ContentView;

@ContentView(R.layout.fragment_no_device)
public class NoDeviceFragment extends BaseFragment {


    @Override
    protected void init() {
        findViewById(R.id.iv_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(SearchDeviceActivity.class);
            }
        });

    }
}
