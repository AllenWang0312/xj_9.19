package measurement.color.com.xj_919.and.Utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by wpc on 2016/11/7.
 */

public class T {


    public static void show(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static void showInTop(Context context, String text) {
        Toast toast = Toast.makeText(context, text,
                Toast.LENGTH_SHORT);
        //可以控制toast显示的位置
        toast.setGravity(Gravity.TOP, 0, 10);
        toast.show();
    }
}
