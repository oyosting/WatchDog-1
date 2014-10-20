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
import android.os.Binder;
import android.os.IBinder;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import in.mings.littledog.db.Device;
import in.mings.mingle.utils.ByteArrayUtils;
import in.mings.mingle.utils.Logger;

/**
 * Created by wangming on 10/13/14.
 */
public class BluetoothLeService extends Service implements IBluetoothLe {
    private static final String TAG = BluetoothLeService.class.getSimpleName();

    public static final String SERIAL_PORT_UUID = "0000dfb1-0000-1000-8000-00805f9b34fb";
    public static final String COMMAND_UUID = "0000dfb2-0000-1000-8000-00805f9b34fb";
    public static final String MODEL_NUMBER_STRING_UUID = "00002a24-0000-1000-8000-00805f9b34fb";

    public static final int STATE_CONNECTING = BluetoothProfile.STATE_CONNECTING;
    public static final int STATE_CONNECTED = BluetoothProfile.STATE_CONNECTED;
    public static final int STATE_DISCONNECTED = BluetoothProfile.STATE_DISCONNECTED;
    public static final int STATE_DISCONNECTING = BluetoothProfile.STATE_DISCONNECTING;

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt mBluetoothGatt;
    private boolean mLeScanning = false;
    private int mLeState = STATE_DISCONNECTED;
    //    private List<List<BluetoothGattCharacteristic>> mBluetoothGattCharacteristics = new ArrayList<List<BluetoothGattCharacteristic>>();
    private BluetoothGattCharacteristic mModelNumberCharacteristic, mSerialPortCharacteristic, mCommandCharacteristic;

    private BluetoothGattCallback mBluetoothGattCallback = new BluetoothGattCallback() {
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            mBluetoothGatt = gatt;
            Logger.i(TAG, "onServicesDiscovered... %d", status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                loadBluetoothGattCharacteristics();
                if (mSerialPortCharacteristic != null) {
                    mBluetoothGatt.setCharacteristicNotification(mSerialPortCharacteristic, true);
//                    mSerialPortCharacteristic.setValue("<RGBLED>0,0,0;");
//                    mBluetoothGatt.writeCharacteristic(mSerialPortCharacteristic);
                }
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            Logger.i(TAG, "onCharacteristicChanged... %s", characteristic.getStringValue(0));
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
            }
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
            Logger.i(TAG, "onReadRemoteRssi... %d", rssi);
        }
    };


    private void loadBluetoothGattCharacteristics() {
        List<BluetoothGattService> list = getSupportedGattServices();
        for (BluetoothGattService service : list) {
            for (BluetoothGattCharacteristic bgc : service.getCharacteristics()) {
                if (TextUtils.equals(bgc.getUuid().toString(), SERIAL_PORT_UUID)) {
                    mSerialPortCharacteristic = bgc;
                } else if (TextUtils.equals(bgc.getUuid().toString(), COMMAND_UUID)) {
                    mCommandCharacteristic = bgc;
                } else if (TextUtils.equals(bgc.getUuid().toString(), MODEL_NUMBER_STRING_UUID)) {
                    mModelNumberCharacteristic = bgc;
                }
            }
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

    public class BluetoothLeBinder extends Binder {
        public BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mBluetoothManager = (BluetoothManager) getApplication().getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new BluetoothLeBinder();
    }

    @Override
    public void unbindService(ServiceConnection conn) {
        Logger.d(TAG, "unbindService...");
        super.unbindService(conn);
    }

    @Override
    public void onDestroy() {
        Logger.d(TAG, "onDestroy...");
        disconnect();
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
            mBluetoothGatt = device.connectGatt(getApplication(), false, mBluetoothGattCallback);
        }
    }

    /**
     * Disconnect the existing connection of BLE device. The result is reported through the
     * {@link BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     */
    public void disconnect() {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.disconnect();
        }
        mLeState = STATE_DISCONNECTED;
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
    public void colse() {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.close();
            mBluetoothGatt = null;
        }
    }

    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt != null) {
            return mBluetoothGatt.getServices();
        }
        return new ArrayList<BluetoothGattService>();
    }
}
