package measurement.color.com.xj_919.and.Utils;

import android.text.format.Time;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static java.util.Calendar.getInstance;

/**
 * Created by wpc on 2016/10/11.
 */

public class TimeUtils {

    static Time t = new Time();
    static Calendar c = getInstance();

    public static String getTimeStamp() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(date);
    }
    //Data
    public static String getTime() {
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);
        return str;
    }
    public static String getTimeWithOutZero(){
        t.setToNow();
        return t.year+"-"+t.month+"-"+t.monthDay;
    }

    public static String getHourMin() {
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(curDate);
    }
    public static String getHourMinSecond() {
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return format.format(curDate);
    }


    public static void LogTime() {
        t.setToNow();
        Log.i("Time", t.year + "-" + t.month + "-" + t.monthDay);
        Log.i("Calendar", c.get(Calendar.YEAR) + "-" + c.get(Calendar.MONTH) + "-" + c.get(Calendar.DAY_OF_MONTH));
        Log.i("Date", getTime());
    }
}
