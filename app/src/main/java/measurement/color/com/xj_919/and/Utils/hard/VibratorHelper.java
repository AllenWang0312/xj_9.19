package measurement.color.com.xj_919.and.Utils.hard;

import android.content.Context;
import android.os.Vibrator;

/**
 * Created by wpc on 2016/11/17.
 */

public class VibratorHelper {
   static Vibrator vibrator;
    public static Vibrator getInstance(Context context){
        if(vibrator==null){
            vibrator= (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        }
        return vibrator;
    }
}
