package in.mings.littledog.bt;

import android.bluetooth.BluetoothProfile;

/**
 * Created by wangming on 10/17/14.
 */
public interface IBluetoothLe {
    public static final String ACTION_STATE_UPDATED = "state_updated";
    public static final String ACTION_DEVICE_FOUND = "device_found";

    public static final String EXTRA_DEVICE = "extra_device";
    public static final String EXTRA_DATA = "extra_data";

    public static final int STATE_CONNECTING = BluetoothProfile.STATE_CONNECTING;
    public static final int STATE_CONNECTED = BluetoothProfile.STATE_CONNECTED;
    public static final int STATE_DISCONNECTED = BluetoothProfile.STATE_DISCONNECTED;
    public static final int STATE_DISCONNECTING = BluetoothProfile.STATE_DISCONNECTING;
}
