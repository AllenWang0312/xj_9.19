package measurement.color.com.xj_919.and.Utils.SP;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by wpc on 2016/11/14.
 */

public class SharePreferenceHelper {

    static SharedPreferences mSharedPreferences;

    public static SharedPreferences init(Context context) {
        if (mSharedPreferences == null) {
            mSharedPreferences = context.getSharedPreferences(Consts.SYSTEM_SETTING_PREFERENCE, MODE_PRIVATE);
        }
        return mSharedPreferences;
    }

    public static SharedPreferences getInstance(){
        return mSharedPreferences;
    }

    public static void writeBoolToSP(String key, boolean value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static void writeIntToSP(String key, int value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static void writeStringToSP(String key, String value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }
}
