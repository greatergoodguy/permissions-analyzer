package eagleeyenetworks.permissionsanalyzer.util;

import android.content.Context;
import android.net.wifi.WifiManager;

/**
 * Created by xuejianyu on 2/23/15.
 */
public class UtilConnectivity {

    public static boolean isConnectedWifi(Context context) {
        if(context == null) {
            return false;}

        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return wifiManager.isWifiEnabled();
    }

}
