package eagleeyenetworks.permissionsanalyzer.util;

import android.util.Log;

/**
 * Created by xuejianyu on 2/23/15.
 */
public class UtilLogger {

    public static void log(String tag, String info) {
        if(info.length() > 4000) {
            Log.d(tag, info.substring(0, 4000));
            log(tag, info.substring(4000));
        }
        else {
            Log.d(tag, info);
        }
    }
}
