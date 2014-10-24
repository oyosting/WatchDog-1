package in.mings.littledog.bt;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.mings.mingle.utils.ByteArrayUtils;
import in.mings.mingle.utils.Logger;

/**
 * Created by wangming on 10/13/14.
 */
public abstract class BluetoothLeService extends Service implements IBluetoothLe {
    private static final String TAG = BluetoothLeService.class.getSimpleName();

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mLeScanning = false;
    private int mLeState = STATE_DISCONNECTED;
    protected Ringtone mRingtone;
    private Map<String, BluetoothGatt> mBluetoothGattMap = new HashMap<String, BluetoothGatt>();

    private BluetoothGattCallback mBluetoothGattCallback = new BluetoothGattCallback() {
        private StringBuilder sBuffer = new StringBuilder();

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            Logger.i(TAG, "onServicesDiscovered... %d", status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                mBluetoothGattMap.put(gatt.getDevice().getAddress(), gatt);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            String value = characteristic.getStringValue(0);
            Logger.i(TAG, "onCharacteristicChanged... %s", value);
            sBuffer.append(value);
            if (!TextUtils.isEmpty(value) && value.endsWith(";")) {
                Intent intent = new Intent(ACTION_STATE_UPDATED);
                String data = sBuffer.toString();
                intent.putExtra(EXTRA_DATA, data);
                sendBroadcast(intent);
                sBuffer.delete(0, data.length() - 1);

                onDataChanged(gatt,data);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            Logger.i(TAG, "onCharacteristicRead... %s", characteristic.getStringValue(0));
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            Logger.i(TAG, "onCharacteristicWrite... %s", characteristic.getStringValue(0));
        }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            Logger.i(TAG, "onConnectionStateChange... %d", newState);
            mLeState = newState;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                boolean result = gatt.discoverServices();
                Logger.i(TAG, "onConnectionStateChange: Attempting to start service discovery :%s successfully!", result ? "" : " NOT");
            } else if (newState == STATE_DISCONNECTED) {
                playRingtone();
            }
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
            Logger.i(TAG, "onReadRemoteRssi... %d", rssi);
        }
    };

    public abstract void onDataChanged(BluetoothGatt gatt, String value);

    public void playRingtone() {
        if (mRingtone == null) {
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            mRingtone = RingtoneManager.getRingtone(getApplicationContext(), uri);
        }
        mRingtone.play();
    }

    public void stopRingtone() {
        if (mRingtone != null) {
            mRingtone.stop();
        }
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            Logger.v(TAG, "device ==> %s with %s", device.getAddress(), device.getName());
            Logger.v(TAG, "rssi ==> %d with %s", rssi, ByteArrayUtils.toHexString(scanRecord));

            Intent intent = new Intent(ACTION_DEVICE_FOUND);
            intent.putExtra(EXTRA_DEVICE, new Device(device, rssi, scanRecord));
            sendBroadcast(intent);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mBluetoothManager = (BluetoothManager) getApplication().getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
    }

    @Override
    public void unbindService(ServiceConnection conn) {
        Logger.d(TAG, "unbindService...");
        super.unbindService(conn);
    }

    @Override
    public void onDestroy() {
        Logger.d(TAG, "onDestroy...");
//        disconnect(); //TODO disconnect all GATT connections
        super.onDestroy();
    }

    public void startLeScan() {
        if (mBluetoothAdapter != null && !mLeScanning && mLeState == STATE_DISCONNECTED) {
            Logger.d(TAG, "Start le scanning...");
            mBluetoothAdapter.startLeScan(mLeScanCallback);
            mLeScanning = true;
        }
    }

    public void stopLeScan() {
        Logger.d(TAG, "Stop le scanning...");
        mLeScanning = false;
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    /**
     * Connects to the GATT server hosted on the BLE device by given address.
     *
     * @param address The address of the destination device.
     */
    public void connect(final String address) {
        if (mBluetoothAdapter == null || TextUtils.isEmpty(address)) {
            return;
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            return;
        }

        synchronized (this) {
            mLeState = STATE_CONNECTING;
            device.connectGatt(getApplication(), false, mBluetoothGattCallback);
        }
    }

    /**
     * Disconnect the existing connection of BLE device. The result is reported through the
     * {@link BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     */
    public void disconnect(String address) {
        BluetoothGatt gatt = mBluetoothGattMap.get(address);
        if (gatt != null) {
            gatt.disconnect();
        }
    }

    public int getConnectedState(Device device) {
        if (device == null) {
            return STATE_DISCONNECTED;
        }
        return mBluetoothManager.getConnectionState(device.btDevice, BluetoothProfile.GATT);
    }

    /**
     * Call this method to ensure resources are released properly, after using the given BLE device.
     */
    public void colse(String address) {
        BluetoothGatt gatt = mBluetoothGattMap.get(address);
        if (gatt != null) {
            gatt.close();
        }
    }

    public List<BluetoothGattService> getSupportedGattServices(String address) {
        BluetoothGatt gatt = mBluetoothGattMap.get(address);
        if (gatt != null) {
            return gatt.getServices();
        }
        return new ArrayList<BluetoothGattService>();
    }
}
