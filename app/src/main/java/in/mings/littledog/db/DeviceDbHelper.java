package in.mings.littledog.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by wangming on 10/13/14.
 */
public class DeviceDbHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DB_NAME = "little_dog.db";

    private static final String CREATE_TABLE = "CREATE TABLE " + Device.TABLE_NAME + " ("
            + Device._ID + " INTEGER PRIMARY KEY,"
            + Device.COLUMN_NAME_NAME + " TEXT,"
            + Device.COLUMN_NAME_ALIAS + " TEXT,"
            + Device.COLUMN_NAME_ADDRESS + " TEXT,"
            + Device.COLUMN_NAME_DESC + " TEXT,"
            + Device.COLUMN_NAME_CREATED_DATE + " INTEGER,"
            + Device.COLUMN_NAME_UPDATED_DATE + " INTEGER"
            + ")";

    public DeviceDbHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + Device.TABLE_NAME);
            onCreate(db);
        }
    }
}
