package in.mings.littledog;

import android.app.ListFragment;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import in.mings.littledog.bt.Device;
import in.mings.littledog.db.DeviceStore;

public class DeviceListFragment extends ListFragment {

    public interface OnDeviceItemClickListener {
        public void onDeviceItemClick(Device device, int pos);
    }

    private OnDeviceItemClickListener onDeviceItemClickListener;

    public void setOnDeviceItemClickListener(OnDeviceItemClickListener listener) {
        onDeviceItemClickListener = listener;
    }

    private DeviceAdapter mAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Cursor c = DeviceStore.newInstance(getActivity()).queryAll();
        mAdapter = new DeviceAdapter(getActivity(), c);
        setListAdapter(mAdapter);

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if (onDeviceItemClickListener != null) {
            onDeviceItemClickListener.onDeviceItemClick((Device) mAdapter.getItem(position), position);
        }
    }


    public class DeviceAdapter extends CursorAdapter {

        public DeviceAdapter(Context context, Cursor c) {
            super(context, c, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_device, parent, false);
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ViewHolder holder = (ViewHolder) view.getTag();
            if (holder == null) {
                holder = new ViewHolder(view);
            }
            holder.alias.setText(cursor.getString(1));
            holder.state.setText(cursor.getString(2));
        }

        class ViewHolder {
            @InjectView(R.id.tv_alias)
            TextView alias;
            @InjectView(R.id.tv_state)
            TextView state;

            public ViewHolder(View view) {
                ButterKnife.inject(this, view);
            }
        }
    }
}
