package in.mings.littledog;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;

import java.util.ArrayList;

import in.mings.littledog.bt.BluetoothLeService;
import in.mings.littledog.bt.IBluetoothLe;
import in.mings.littledog.db.Device;
import in.mings.mingle.utils.Logger;

/**
 * Created by wangming on 10/17/14.
 */
public class BleActivity extends ActionBarActivity {
    private static final String TAG = BleActivity.class.getSimpleName();
    private boolean mBound;
    protected BluetoothLeService bluetoothLeService;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Logger.d(TAG, "Service connected");
            mBound = true;
            BluetoothLeService.BluetoothLeBinder binder = (BluetoothLeService.BluetoothLeBinder) service;
            bluetoothLeService = binder.getService();
            bluetoothLeService.startLeScan();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Logger.d(TAG, "Service onServiceDisconnected");
            bluetoothLeService = null;
            mBound = false;
        }
    };

    protected ArrayList<Device> mItems = new ArrayList<Device>();
    private BroadcastReceiver mBtDeviceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.equals(action, IBluetoothLe.ACTION_DEVICE_FOUND)) {
                Device device = intent.getParcelableExtra(IBluetoothLe.EXTRA_DEVICE);
                Logger.d(TAG, "Device found : %s", device);
            } else if (TextUtils.equals(action, IBluetoothLe.ACTION_STATE_UPDATED)) {
                String data = intent.getStringExtra(IBluetoothLe.EXTRA_DATA);
                Logger.d(TAG, "got data : %s", data);
            }
        }
    };


    public BluetoothLeService getBluttoothLeService() {
        return bluetoothLeService;
    }

    private void bindLeService() {
        Intent intent = new Intent(this, BluetoothLeService.class);
        bindService(intent, mServiceConnection, Service.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        startService(new Intent(this, BluetoothLeService.class));
        bindLeService();
    }

    @Override
    protected void onStop() {
        if (mBound) {
            unbindService(mServiceConnection);
            mBound = false;
        }
        super.onStop();
    }
}
