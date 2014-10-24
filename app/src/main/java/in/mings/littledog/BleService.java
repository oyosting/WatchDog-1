package in.mings.littledog;

import android.bluetooth.BluetoothGatt;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import in.mings.littledog.bt.BluetoothLeService;

/**
 * Created by wangming on 10/24/14.
 */
public class BleService extends BluetoothLeService {

    @Override
    public void onDataChanged(BluetoothGatt gatt, String data) {
        if (data.contains("ROCKER") && !data.contains("ROCKER>0")) {
            if (mRingtone != null && mRingtone.isPlaying()) {
                stopRingtone();
            } else {
                playRingtone();
            }
        }
    }

    public class BluetoothLeBinder extends Binder {
        public BleService getService() {
            return BleService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new BluetoothLeBinder();
    }
}
