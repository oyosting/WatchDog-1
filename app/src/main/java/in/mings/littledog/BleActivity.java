package in.mings.littledog;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import in.mings.mingle.utils.Logger;

/**
 * Created by wangming on 10/17/14.
 */
public abstract class BleActivity extends Activity {
    private static final String TAG = BleActivity.class.getSimpleName();
    private boolean mBound;
    protected BleService bluetoothLeService;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Logger.d(TAG, "Service connected");
            mBound = true;
            BleService.BluetoothLeBinder binder = (BleService.BluetoothLeBinder) service;
            bluetoothLeService = binder.getService();
            bluetoothLeService.startLeScan();
            onBleServiceConnected(bluetoothLeService);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Logger.d(TAG, "Service onServiceDisconnected");
            bluetoothLeService = null;
            mBound = false;
        }
    };

    public BleService getBluetoothLeService() {
        return bluetoothLeService;
    }

    private void bindLeService() {
        Logger.d(TAG, "start to bind le service....");
        if (mBound) {
            return;
        }
        Intent intent = new Intent(this, BleService.class);
        bindService(intent, mServiceConnection, Service.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        startService(new Intent(this, BleService.class));
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

    public abstract void onBleServiceConnected(BleService service);
}
