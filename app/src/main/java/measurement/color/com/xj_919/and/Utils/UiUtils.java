package measurement.color.com.xj_919.and.Utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.inputmethod.InputMethodManager;

import measurement.color.com.xj_919.R;


/**
 * Created by wpc on 2016/9/10.
 */
public class UiUtils {

    //切换输入法显示状态
    public static void hiddenSoftInput(Context context) {
        //得到InputMethodManager的实例
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        //如果开启
        if (imm.isActive()) {
            //关闭软键盘，开启方法相同，这个方法是切换开启与关闭状态的
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    public static int getToolbarHeight(Context context) {
        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
                new int[]{R.attr.actionBarSize});
        int toolbarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        return toolbarHeight;
    }

    public static int getTabsHeight(Context context) {
        return (int) context.getResources().getDimension(R.dimen.tabsHeight);
    }
}
