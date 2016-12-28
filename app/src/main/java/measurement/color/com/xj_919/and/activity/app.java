package measurement.color.com.xj_919.and.activity;

import android.app.Application;

import java.util.ArrayList;

/**
 * Created by wpc on 2016/9/19.
 */
public class app extends Application {

    public static int cent_X, cent_Y, cent_bet, round_r;

    public static boolean showCheckBoxs = false;
    public static boolean hasNewHestroy=false;
    public static ArrayList<Boolean> CheckState;

    public static int select_mod = -1;

    public static int from = 0;
    public static int to = 0;
    public static int id = 0;

    public static final String uuid = "00001101-0000-1000-8000-00805F9B34FB";
    public static final String PRINTER_NAME = "Qsprinter";
    public static String ADDRESS = "00:19:5D:31:85:BC";

    public static final String SQLite_LOG = "LOG.db";
    public static final String SQLite_LOG_rgb = "rgb";

    private static boolean isNightMode = false;

    @Override
    public void onCreate() {
        super.onCreate();
    }
    public static boolean isNightMode() {
        return isNightMode;
    }

    public static void setNightMode(boolean nightMode) {
        isNightMode = nightMode;
    }

}
