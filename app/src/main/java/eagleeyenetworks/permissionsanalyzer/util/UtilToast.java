package eagleeyenetworks.permissionsanalyzer.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by xuejianyu on 2/23/15.
 */
public class UtilToast {

    public static void showToast(Context context, String text) {
        if(context == null)
            return;

        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

}
