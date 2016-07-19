package practices.my.countstep;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by ysy20_000 on 2016/7/19.
 */
public final class TaceLogHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "TaceLog.db";

    public TaceLogHelper() {
        super(null, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public TaceLogHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public TaceLogHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public static abstract class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "TaceLog";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_CNT13 = "cnt13";
        public static final String COLUMN_NAME_CNT12 = "cnt12";
        public static final String COLUMN_NAME_CNT11 = "cnt11";
        public static final String COLUMN_NAME_CNT10 = "cnt10";
        public static final String COLUMN_NAME_START = "start_time";
        public static final String COLUMN_NAME_END = "end_time";
        public static final String COLUMN_NAME_DEL = "del_flg";

    }
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String BOOLEAN_TYPE = " BOOLEAN";
    private static final String DATETIME_TYPE = " DATETIME";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + FeedEntry.TABLE_NAME + " (" +
                    FeedEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    FeedEntry.COLUMN_NAME_ID + INTEGER_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_CNT13 + INTEGER_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_CNT12 + INTEGER_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_CNT11 + INTEGER_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_CNT10 + INTEGER_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_START + DATETIME_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_END + DATETIME_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_DEL + BOOLEAN_TYPE + COMMA_SEP +
            " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME;
}
