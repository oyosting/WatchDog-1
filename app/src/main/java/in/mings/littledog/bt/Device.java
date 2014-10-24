package in.mings.littledog.bt;

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/**
 * Created by wangming on 10/17/14.
 */
public class Device implements Parcelable {
    public String name; // device name
    public String alias; // device alias
    public String address; // device mac address
    public String desc;
    public BluetoothDevice btDevice;
    public int rssi;
    public byte[] scanRecord;

    public static final Parcelable.Creator<Device> CREATOR = new Creator<Device>() {

        @Override
        public Device createFromParcel(Parcel source) {
            Device device = new Device();
            device.name = source.readString();
            device.alias = source.readString();
            device.address = source.readString();
            device.desc = source.readString();
            device.btDevice = source.readParcelable(BluetoothDevice.class.getClassLoader());
            device.rssi = source.readInt();
            device.scanRecord = new byte[source.readInt()];
            source.readByteArray(device.scanRecord);
            return device;
        }

        @Override
        public Device[] newArray(int size) {
            return new Device[size];
        }
    };

    private Device() {
    }

    public Device(BluetoothDevice device, int rssi, byte[] scanRecord) {
        btDevice = device;
        if (btDevice != null) {
            name = btDevice.getName();
            address = btDevice.getAddress();
        }

        this.rssi = rssi;
        this.scanRecord = scanRecord;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(alias);
        dest.writeString(address);
        dest.writeString(desc);
        dest.writeParcelable(btDevice, flags);
        dest.writeInt(rssi);
        dest.writeInt(scanRecord.length);
        dest.writeByteArray(scanRecord);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(address).append(" : ").append(name);
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        return TextUtils.equals(address,((Device)o).address);
    }
 }
