package in.mings.littledog.db;

import android.provider.BaseColumns;

/**
 * Created by wangming on 10/13/14.
 */
public final class DeviceColumns implements BaseColumns {

    public static final String TABLE_NAME = "device";
    public static final String COLUMN_NAME_NAME = "name";
    public static final String COLUMN_NAME_ALIAS = "alias";
    public static final String COLUMN_NAME_ADDRESS = "address";
    public static final String COLUMN_NAME_DESC = "desc";
    public static final String COLUMN_NAME_DISCONNECT_ON_PURPOSE = "on_purpose_disconnect";

    /**
     * The profile is in disconnected state
     * public static final int STATE_DISCONNECTED  = 0;
     * public static final int STATE_CONNECTING    = 1;
     * public static final int STATE_CONNECTED     = 2;
     * public static final int STATE_DISCONNECTING = 3;
     */
    public static final String COLUMN_NAME_STATE = "state";
    public static final String COLUMN_NAME_UPDATED_DATE = "updated_time";
    public static final String COLUMN_NAME_CREATED_DATE = "created_time";
}
