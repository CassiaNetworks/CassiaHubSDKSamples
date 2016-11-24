package com.cassianetworks.fall;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cassianetworks.fall.domain.Record;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZhangMin on 2016/8/17.
 */
public class RecordAdapter extends BaseAdapter {
    private LayoutInflater mInflater;

    private List<Record> recordList;
    private Context context;

    public RecordAdapter(Context context) {
        this.context = context;
        recordList = new ArrayList<>();
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void refreshData(List<Record> recordList) {
        if (recordList == null) recordList = new ArrayList<>();
        this.recordList = recordList;
    }

    @Override
    public int getCount() {
        return recordList == null ? 0 : recordList.size();
    }

    @Override
    public Record getItem(int position) {
        return recordList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addRecordItem(Record record) {
        if (record != null)
            recordList.add(record);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Record record = getItem(position);
        int type = getItemViewType(position);
        ViewHolderHead holderHead;
        ViewHolderBody holderBody;

        if (convertView == null) {
            switch (type) {
                case 0:
                    convertView = mInflater.inflate(R.layout.item_lv_record_head, parent, false);
                    holderHead = new ViewHolderHead();
                    x.view().inject(holderHead, convertView);
                    holderHead.tvData.setText(record.getTime().split(" ")[0]);
                    convertView.setTag(holderHead);
                    break;

                case 1:
                    convertView = mInflater.inflate(R.layout.item_lv_record_body, parent, false);
                    holderBody = new ViewHolderBody();
                    x.view().inject(holderBody, convertView);
                    setBodyViewValue(record, holderBody);
                    convertView.setTag(holderBody);
                    break;
                default:
                    break;
            }

        } else {
            switch (type) {
                case 0:
                    holderHead = (ViewHolderHead) convertView.getTag();
                    holderHead.tvData.setText(record.getTime().split(" ")[0]);
                    break;
                case 1:
                    holderBody = (ViewHolderBody) convertView.getTag();
                    setBodyViewValue(record, holderBody);
                    break;

                default:
                    break;
            }
        }
        return convertView;
    }

    private void setBodyViewValue(Record record, ViewHolderBody holderBody) {
        holderBody.tvTime.setText(String.format(context.getString(R.string.record_time), record.getTime().split(" ")[1]));
        holderBody.tvDataType.setText(String.format(context.getString(R.string.record_dataType), record.getDataType()));
        holderBody.tvHandle.setText(String.format(context.getString(R.string.record_handle), record.getHandle()));
        holderBody.tvName.setText(String.format(context.getString(R.string.record_name), record.getName()));
        holderBody.tvValue.setText(String.format(context.getString(R.string.record_value), record.getValue()));
    }

    /**
     * 根据数据源的position返回需要显示的的layout的type
     * <p/>
     * type的值必须从0开始
     */
    @Override
    public int getItemViewType(int position) {
        return getItem(position).getType();
    }

    /**
     * 返回所有的layout的数量
     */
    @Override
    public int getViewTypeCount() {
        return 2;
    }

    class ViewHolderHead {
        @ViewInject(R.id.tv_data)
        private TextView tvData;
    }

    class ViewHolderBody {
        @ViewInject(R.id.tv_time)
        private TextView tvTime;
        @ViewInject(R.id.tv_value)
        private TextView tvValue;
        @ViewInject(R.id.tv_name)
        private TextView tvName;
        @ViewInject(R.id.tv_dataType)
        private TextView tvDataType;
        @ViewInject(R.id.tv_handle)
        private TextView tvHandle;
    }

}