package com.cassianetworks.fall.fragment.main;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cassianetworks.fall.BaseApplication;
import com.cassianetworks.fall.BaseFragment;
import com.cassianetworks.fall.R;
import com.cassianetworks.fall.activities.DeviceActivity;
import com.cassianetworks.fall.activities.SearchDeviceActivity;
import com.cassianetworks.fall.domain.Device;
import com.cassianetworks.fall.views.MyGridView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

@ContentView(R.layout.fragment_show_device)
public class ShowDeviceFragment extends BaseFragment {
    private List<Device> devicesList;
    private DeviceAdapter adapter;
    private Device addIcon;
    @ViewInject(R.id.gv_device)
    MyGridView gvDevice;
    @ViewInject(R.id.iv_add)
    ImageView ivAdd;


    @Override
    protected void init() {
        devicesList = BaseApplication.deviceManager.getDevList();
        adapter = new DeviceAdapter();
        gvDevice.setAdapter(adapter);
        gvDevice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Device device = adapter.getItem(position);
                Bundle bundle = new Bundle();
                bundle.putString("device_bdaddr", device.getBdaddr());
                startActivity(DeviceActivity.class, bundle);

            }
        });

        ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(SearchDeviceActivity.class);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    private class DeviceAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return devicesList == null ? 0 : devicesList.size();
        }

        @Override
        public Device getItem(int position) {
            return devicesList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            DeviceViewHolder holder;

            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_gv_device_list, parent, false);
                holder = new DeviceViewHolder();
                x.view().inject(holder, convertView);
                convertView.setTag(holder);
            } else {
                holder = (DeviceViewHolder) convertView.getTag();
            }
            Device device = getItem(position);
            holder.tvName.setText(device.getName());
            holder.ivIcon.setImageResource(R.mipmap.device_icon);
            return convertView;
        }
    }

    class DeviceViewHolder {
        @ViewInject(R.id.tv_name)
        private TextView tvName;
        @ViewInject(R.id.iv_icon)
        private ImageView ivIcon;
    }
}
