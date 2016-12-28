package measurement.color.com.xj_919.and.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class LogoSplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (AppPref.isFirstRunning(this)) {
////            IntroduceActivity.launch(this);
//        }
//        else {
//            MainActivity.launch(this);
//        }
        startActivity(new Intent(LogoSplashActivity.this, MainActivity.class));
        finish();
    }
}
