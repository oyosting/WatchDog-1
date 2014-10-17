package in.mings.littledog;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import in.mings.littledog.bt.BluetoothLeService;
import in.mings.mingle.utils.Logger;

/**
 * Created by wangming on 10/17/14.
 */
public class BleActivity extends Activity {
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
