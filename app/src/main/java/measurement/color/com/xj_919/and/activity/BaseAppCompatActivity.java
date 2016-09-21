package measurement.color.com.xj_919.and.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * Created by wpc on 2016/9/20.
 */
public class BaseAppCompatActivity extends AppCompatActivity {


    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        app.addActivity(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        app.removeActivity(this);
    }


    void showToast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }
}
