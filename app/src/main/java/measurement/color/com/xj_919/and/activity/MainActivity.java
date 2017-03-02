package measurement.color.com.xj_919.and.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.lang.reflect.Field;

import measurement.color.com.xj_919.R;
import measurement.color.com.xj_919.and.Utils.SP.Consts;
import measurement.color.com.xj_919.and.Utils.SP.SharePreferenceHelper;
import measurement.color.com.xj_919.and.Utils.TimeUtils;
import measurement.color.com.xj_919.and.Utils.hard.VibratorHelper;
import measurement.color.com.xj_919.and.Utils.io.FileAndPath;
import measurement.color.com.xj_919.and.Utils.io.FileUtils;
import measurement.color.com.xj_919.and.adapter.ViewPagerAdapter;
import measurement.color.com.xj_919.and.fragment.Setting.SettingFragment;
import measurement.color.com.xj_919.and.fragment.Test.TestFragment;
import measurement.color.com.xj_919.and.fragment.Test.USBManager;
import measurement.color.com.xj_919.and.fragment.datahistory.DataHistory;
import measurement.color.com.xj_919.and.view.CustomViewPager;

public class MainActivity extends AppCompatActivity {
    USBManager manager;
    private Menu menu;
    private TimerChanger mTimerChanger;
    private BatteryChanger mBatteryChanger;

    String time, battery;
    Toolbar toolbar;
    SharedPreferences mSharedPreference;

    TabLayout tl;
    private CustomViewPager vp;
    private TestFragment mTestFragment;
    private DataHistory mDataHistory;
    private SettingFragment mSettingFragment;
    ImageView iv_exit, iv_setting;
    ViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manager = USBManager.init(MainActivity.this);
        manager.registerReceiver(MainActivity.this);

        mSharedPreference = SharePreferenceHelper.init(this);
        if (mSharedPreference.getBoolean(Consts.KEY_NIGHT_MOD, true)) {
            setTheme(R.style.AppTheme_night);
        } else {
            setTheme(R.style.AppTheme_day);
        }
        setContentView(R.layout.app_bar_main);
        time = "12:00";
        battery = "50%";
        vp = (CustomViewPager) findViewById(R.id.vp_main);
        iv_exit = (ImageView) findViewById(R.id.iv_exit);
        iv_setting = (ImageView) findViewById(R.id.iv_setting);
        iv_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(MainActivity.this, v);
                try {
                    Field field = popup.getClass().getDeclaredField("mPopup");
                    field.setAccessible(true);
                    MenuPopupHelper mHelper = (MenuPopupHelper) field.get(popup);
                    mHelper.setForceShowIcon(true);
                } catch (IllegalAccessException | NoSuchFieldException e) {
                    e.printStackTrace();
                }


                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.settings, popup.getMenu());
//                if (vp.getCurrentItem() == 0) {
//                    popup.getMenu().findItem(R.id.light_mod).setVisible(true);
//                } else {
//                    popup.getMenu().findItem(R.id.light_mod).setVisible(false);
//                }
                if (SharePreferenceHelper.getInstance().getBoolean(Consts.KEY_VIBRATE, false)) {
                    popup.getMenu().findItem(R.id.vibrate_enable).setChecked(true);
                }
                if (SharePreferenceHelper.getInstance().getBoolean(Consts.KEY_NIGHT_MOD, false)) {
                    popup.getMenu().findItem(R.id.light_mod).setChecked(true);
                }
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.vibrate_enable:
                                if (mSharedPreference.getBoolean(Consts.KEY_VIBRATE, false)) {
                                    SharePreferenceHelper.writeBoolToSP(Consts.KEY_VIBRATE, false);
                                } else {
                                    SharePreferenceHelper.writeBoolToSP(Consts.KEY_VIBRATE, true);
                                }
                                break;
                            case R.id.light_mod:
                                if (mSharedPreference.getBoolean(Consts.KEY_NIGHT_MOD, false)) {
                                    SharePreferenceHelper.writeBoolToSP(Consts.KEY_NIGHT_MOD, false);
                                } else {
                                    SharePreferenceHelper.writeBoolToSP(Consts.KEY_NIGHT_MOD, true);
                                }
                                recreate();
                                break;
                            case R.id.clean_cache:
                                FileUtils.deleteDirectory(FileAndPath.IMG);
                                FileUtils.deleteDirectory(FileAndPath.IMG_NATIVE_DATA);
                                break;
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });
        iv_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("确认退出");
                builder.setMessage("点击确定退出程序");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.create().show();
            }
        });
        iv_exit.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mSharedPreference.getBoolean(Consts.ROOT_PROMISSION, false)) {
                    SharePreferenceHelper.writeBoolToSP(Consts.ROOT_PROMISSION, false);
                    initView();
//                    onResume();

                } else {
                    SharePreferenceHelper.writeBoolToSP(Consts.ROOT_PROMISSION, true);
                    initView();
//                    onResume();
                }
                return true;
            }
        });
        initView();
        vp.setOffscreenPageLimit(3);
        vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    menu.findItem(R.id.action_chose).setVisible(true);
                    menu.findItem(R.id.action_chose).setIcon(app.showCheckBoxs ? R.drawable.ic_done_all_white_24dp : R.drawable.ic_done_white_24dp);
//                    mDataHistory.refreshData();
                } else {
                    menu.findItem(R.id.action_chose).setVisible(false);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        regeisterReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        manager.unregisterReceiver(MainActivity.this);
        unregisterReceiver(mTimerChanger);
        unregisterReceiver(mBatteryChanger);
    }

    void regeisterReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        mTimerChanger = new TimerChanger();
        registerReceiver(mTimerChanger, filter);
        mBatteryChanger = new BatteryChanger();
        IntentFilter buttery = new IntentFilter();
        buttery.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(mBatteryChanger, buttery);
    }

    void initView() {
        tl = (TabLayout) findViewById(R.id.tl_main);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        mDataHistory=DataHistory.getInstance();
        adapter.addFragment(mDataHistory, "记录");
        mTestFragment=TestFragment.getInstance();
        adapter.addFragment(mTestFragment, "测试");
        if (mSharedPreference.getBoolean(Consts.ROOT_PROMISSION, false)) {
            adapter.addFragment(SettingFragment.getInstance(), "设置");
            vp.setPagingEnabled(true);
        } else {
            vp.setPagingEnabled(false);
        }
        vp.setAdapter(adapter);
        tl.setupWithViewPager(vp);

    }

    //back键退出检测
    private long exitTime = 0;

    @Override
    public void onBackPressed() {

        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(getApplicationContext(), "再按一次退出程序",
                    Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            this.finish();
        }
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.test_menu, menu);
        menu.findItem(R.id.action_time).setTitle(TimeUtils.getHourMin());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_chose) {
            DataHistory.getInstance().switchCheckState();
            if (app.showCheckBoxs) {
                item.setIcon(R.drawable.ic_done_white_24dp);
                app.showCheckBoxs = false;
            } else {
                item.setIcon(R.drawable.ic_done_all_white_24dp);
                app.showCheckBoxs = true;
            }
            if (VibratorHelper.isOpen()) {
                VibratorHelper.getInstance(this).vibrate(new long[]{100, 100}, -1);
            }

        }

        return super.onOptionsItemSelected(item);
    }


    class TimerChanger extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("onReceive", "receive");
            time = TimeUtils.getHourMin();
            refreshTimeBattery();
        }
    }

    private void refreshTimeBattery() {
        menu.findItem(R.id.action_time).setTitle(time + "\n" + battery);
    }

    class BatteryChanger extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra("level", -1);
            int scale = intent.getIntExtra("scale", -1);
            if (menu != null) {
                battery = ((level * 100) / scale) + "%";
                refreshTimeBattery();
            }
        }
    }
}

