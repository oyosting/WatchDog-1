package in.mings.littledog.db;

import android.provider.BaseColumns;

/**
 * Created by wangming on 10/13/14.
 */
public final class Device implements BaseColumns {

    public static final String TABLE_NAME = "device";
    public static final String COLUMN_NAME_NAME = "name";
    public static final String COLUMN_NAME_ALIAS = "alias";
    public static final String COLUMN_NAME_ADDRESS = "address";
    public static final String COLUMN_NAME_DESC = "desc";
    public static final String COLUMN_NAME_UPDATED_DATE = "updated_time";
    public static final String COLUMN_NAME_CREATED_DATE = "created_time";



    public String name; // device name
    public String alias; // device alias
    public String address; // device mac address
    public String desc;
}
