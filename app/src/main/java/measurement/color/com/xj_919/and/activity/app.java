package measurement.color.com.xj_919.and.activity;

import android.app.Activity;
import android.app.Application;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wpc on 2016/9/19.
 */
public class app extends Application {


    private static List<Activity> activities = new ArrayList<Activity>();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static void addActivity(Activity activity) {
        activities.add(activity);
    }

    public static void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    public static void finishAll() {
        for (Activity activity : activities) {
            activity.finish();
        }
    }
}
