package practices.my.countstep.common.db;

import android.content.Context;

/**
 * Created by user on 2016/08/02.
 */
public class ContextSaver {
    private static Context context;

    public static void setContext(Context ct) {
        context = ct;
    }

    public static Context getContext() {
        return context;
    }
}
