package in.mings.littledog.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import in.mings.littledog.bt.Device;

/**
 * Created by wangming on 10/27/14.
 */
public class DeviceStore {
    private Context mContext;
    private static final Object LOCKER = new Object();
    private DeviceDbHelper mDeviceDbHelper;
    private static DeviceStore mInstance;

    public static DeviceStore newInstance(Context context) {
        synchronized (LOCKER) {
            if (mInstance == null) {
                mInstance = new DeviceStore(context);
            }
        }
        return mInstance;
    }

    private DeviceStore(Context context) {
        mContext = context;
        mDeviceDbHelper = new DeviceDbHelper(mContext);
    }

    public void insert(Device device) {
        SQLiteDatabase db = mDeviceDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DeviceColumns.COLUMN_NAME_ADDRESS, device.address);
        values.put(DeviceColumns.COLUMN_NAME_NAME, device.name);
        values.put(DeviceColumns.COLUMN_NAME_ALIAS, device.alias);
        values.put(DeviceColumns.COLUMN_NAME_DESC, device.desc);
        values.put(DeviceColumns.COLUMN_NAME_STATE, device.state);
        long now = System.currentTimeMillis();
        values.put(DeviceColumns.COLUMN_NAME_CREATED_DATE, now);
        values.put(DeviceColumns.COLUMN_NAME_UPDATED_DATE, now);
        db.insert(DeviceColumns.TABLE_NAME, null, values);
    }

    public void update(Device device) {
        SQLiteDatabase db = mDeviceDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DeviceColumns.COLUMN_NAME_NAME, device.name);
        values.put(DeviceColumns.COLUMN_NAME_ALIAS, device.alias);
        values.put(DeviceColumns.COLUMN_NAME_DESC, device.desc);
        values.put(DeviceColumns.COLUMN_NAME_STATE, device.state);
        long now = System.currentTimeMillis();
        values.put(DeviceColumns.COLUMN_NAME_UPDATED_DATE, now);
        db.update(DeviceColumns.TABLE_NAME, values, DeviceColumns.COLUMN_NAME_ADDRESS + " = ?", new String[]{device.address});
    }

    public void upsert(Device device) {
        if (query(device.address) != null) {
            update(device);
        } else {
            insert(device);
        }
    }

    public Device query(String address) {
        SQLiteDatabase db = mDeviceDbHelper.getReadableDatabase();
        String[] columns = new String[]{DeviceColumns.COLUMN_NAME_ADDRESS, DeviceColumns.COLUMN_NAME_ALIAS, DeviceColumns.COLUMN_NAME_NAME, DeviceColumns.COLUMN_NAME_STATE};
        Cursor c = db.query(DeviceColumns.TABLE_NAME, columns, DeviceColumns.COLUMN_NAME_ADDRESS + " = ?", new String[]{address}, null, null, null);
        if (c.moveToFirst()) {
            Device device = new Device();
            device.address = address;
            device.alias = c.getString(1);
            device.name = c.getString(2);
            device.state = c.getInt(3);
            return device;
        }
        return null;
    }

    public Cursor queryAll() {
        SQLiteDatabase db = mDeviceDbHelper.getReadableDatabase();
        String[] columns = new String[]{DeviceColumns._ID, DeviceColumns.COLUMN_NAME_ADDRESS, DeviceColumns.COLUMN_NAME_ALIAS, DeviceColumns.COLUMN_NAME_NAME, DeviceColumns.COLUMN_NAME_STATE};
        return db.query(DeviceColumns.TABLE_NAME, columns, null, null, null, null, null);
    }

}
