package in.mings.littledog;

import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import in.mings.littledog.db.Device;

public class DeviceListFragment extends ListFragment {

    public interface OnDeviceItemClickListener {
        public void onDeviceItemClick(Device device, int pos);
    }

    private OnDeviceItemClickListener onDeviceItemClickListener;

    public void setOnDeviceItemClickListener(OnDeviceItemClickListener listener) {
        onDeviceItemClickListener = listener;
    }

    private DeviceAdapter mAdapter;

    public void setItems(ArrayList<Device> items) {
        if (mAdapter != null) {
            mAdapter.setDevice(items);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new DeviceAdapter(getActivity());
        setListAdapter(mAdapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if (onDeviceItemClickListener != null) {
            onDeviceItemClickListener.onDeviceItemClick((Device) mAdapter.getItem(position), position);
        }
    }

    public class DeviceAdapter extends BaseAdapter {
        private Context mContext;
        private ArrayList<Device> mDevices = new ArrayList<Device>();

        DeviceAdapter(Context context) {
            mContext = context;
        }

        public void setDevice(ArrayList<Device> devices) {
            mDevices = devices;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mDevices.size();
        }

        @Override
        public Object getItem(int position) {
            return mDevices.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = LayoutInflater.from(mContext).inflate(android.R.layout.simple_list_item_1, parent, false);
            TextView tv = (TextView) v.findViewById(android.R.id.text1);
            Device device = mDevices.get(position);
            tv.setText(device.address + " : " + device.name);
            return v;
        }
    }
}
