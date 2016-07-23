package practices.my.countstep.DBManager;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by user on 2016/07/20.
 */
public class TraceLogDB {
    /**
     * Content provider authority.
     */
    public static final String CONTENT_AUTHORITY = "com.example.android.basicsyncadapter";

    /**
     * Base URI. (content://com.example.android.basicsyncadapter)
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Path component for "entry"-type resources..
     */
    private static final String PATH_ENTRIES = "entries";

    /**
     * Columns supported by "entries" records.
     */
    public static class Entry implements BaseColumns{



            public static final String TABLE_NAME = "TraceLog";
            public static final String COLUMN_NAME_CNT13 = "cnt13";
            public static final String COLUMN_NAME_CNT12 = "cnt12";
            public static final String COLUMN_NAME_CNT11 = "cnt11";
            public static final String COLUMN_NAME_CNT10 = "cnt10";
            public static final String COLUMN_NAME_START = "start_time";
            public static final String COLUMN_NAME_END = "end_time";
            public static final String COLUMN_NAME_INSERT_DATE = "insrt_date";
            public static final String COLUMN_NAME_DEL = "del_flg";


    }
}
