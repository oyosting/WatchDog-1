package in.mings.littledog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

import in.mings.littledog.bt.IBluetoothLe;
import in.mings.littledog.db.Device;
import in.mings.mingle.utils.Logger;


public class MainActivity extends BleActivity implements DeviceListFragment.OnDeviceItemClickListener {
    public static final String TAG = MainActivity.class.getSimpleName();
    private DeviceListFragment deviceListFragment;

    private ArrayList<Device> mItems = new ArrayList<Device>();
    private BroadcastReceiver mBtDeviceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.equals(action, IBluetoothLe.ACTION_DEVICE_FOUND)) {
                Device device = intent.getParcelableExtra(IBluetoothLe.EXTRA_DEVICE);
                Logger.d(TAG, "Device found : %s", device);
                if (!mItems.contains(device) && deviceListFragment != null) {
                    mItems.add(device);
                    deviceListFragment.setItems(mItems);
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        deviceListFragment = (DeviceListFragment) getFragmentManager().findFragmentById(R.id.fragment_device);
        deviceListFragment.setOnDeviceItemClickListener(this);
    }


    protected void onStart() {
        super.onStart();
        registerReceiver();
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(IBluetoothLe.ACTION_DEVICE_FOUND);
        registerReceiver(mBtDeviceReceiver, filter);
    }

    private void unregisterReceiver() {
        unregisterReceiver(mBtDeviceReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDeviceItemClick(Device device, int pos) {
//        if (bluetoothLeService != null) {
//            bluetoothLeService.stopLeScan();
//            bluetoothLeService.connect(device.address);
//        }

        Intent intent = new Intent(this, DeviceDetailFragment.DummyActivity.class);
        intent.putExtra(IBluetoothLe.EXTRA_DEVICE, device);
        startActivity(intent);
        if(bluetoothLeService != null) {
            bluetoothLeService.stopLeScan();
        }
    }
}
