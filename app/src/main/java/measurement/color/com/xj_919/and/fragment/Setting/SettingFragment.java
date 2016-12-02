package measurement.color.com.xj_919.and.fragment.Setting;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import measurement.color.com.xj_919.R;
import measurement.color.com.xj_919.and.Utils.SP.Consts;
import measurement.color.com.xj_919.and.Utils.SP.SharePreferenceHelper;

/**
 * Created by wpc on 2016/10/21.
 */

public class SettingFragment extends Fragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    static SettingFragment instance;

    public static SettingFragment getInstance() {
        if (instance == null) {
            instance = new SettingFragment();
        }
        return instance;
    }

    SharedPreferences sharedPreferences;
    View view;
    Switch sw;
    SeekBar sb1, sb2, sb3;
    TextView tv, tv_z, tv_t;
    int pro, z, t;
    TextView cent, bet, r;
    int times = 1;
    TextView testtimes;
    String[] list;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.setting_layout, null);
        list = getActivity().getResources().getStringArray(R.array.list2_preference);
        sharedPreferences = SharePreferenceHelper.getInstance();

        findView();
        setListener();
        return view;
    }

    private void findView() {
        testtimes = (TextView) view.findViewById(R.id.tv_test_times);

        tv = (TextView) view.findViewById(R.id.tv_pgcs_setting);
        tv_z = (TextView) view.findViewById(R.id.tv_z_setting);
        tv_t = (TextView) view.findViewById(R.id.tv_time_between);

        sb1 = (SeekBar) view.findViewById(R.id.sb_pgcs_setting);
        sb2 = (SeekBar) view.findViewById(R.id.sb_z_setting);
        sb3 = (SeekBar) view.findViewById(R.id.sb_time_between);
        sw = (Switch) view.findViewById(R.id.sw_explore);
        cent = (TextView) view.findViewById(R.id.tv_cent_setting);
        bet = (TextView) view.findViewById(R.id.tv_r_setting);
        r = (TextView) view.findViewById(R.id.tv_r_setting);
    }

    private void setListener() {
        view.findViewById(R.id.bt_down_tt).setOnClickListener(this);
        view.findViewById(R.id.bt_up_tt).setOnClickListener(this);

        sb1.setOnSeekBarChangeListener(this);
        sb2.setOnSeekBarChangeListener(this);
        sb3.setOnSeekBarChangeListener(this);

        view.findViewById(R.id.bt_save_setting).setOnClickListener(this);
        view.findViewById(R.id.bt_tb_setting).setOnClickListener(this);

        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sb2.setEnabled(true);
                } else {
                    sb2.setEnabled(false);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        testtimes.setText(sharedPreferences.getInt(Consts.TakePhotoTimes, 1) + "");
        cent.setText("(" + sharedPreferences.getInt(Consts.KEY_CENTER_X, 0) + "," + sharedPreferences.getInt(Consts.KEY_CENTER_Y, 0) + ")");
        bet.setText(sharedPreferences.getInt(Consts.KEY_CENTER_BETWEEN, 0) + "");
        bet.setText(sharedPreferences.getInt(Consts.KEY_CENTER_R, 0) + "");
        sb1.setProgress(sharedPreferences.getInt(Consts.TakePhotoDelay, 180));
        sb2.setProgress(sharedPreferences.getInt(Consts.LIGHT_HIGHT, 80));
        sb3.setProgress(sharedPreferences.getInt(Consts.TakePhotoWait, 1));
        sw.setChecked(sharedPreferences.getBoolean(Consts.EXPOSURE,false));
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_save_setting:
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(Consts.TakePhotoDelay, pro);
                editor.putInt(Consts.LIGHT_HIGHT, z);
                editor.putInt(Consts.TakePhotoTimes, times);
                editor.putInt(Consts.TakePhotoWait, t);
                editor.putBoolean(Consts.EXPOSURE,sw.isChecked());
                editor.commit();
                break;


            case R.id.bt_down_tt:
                if (times > 1) {
                    times--;
                }
                testtimes.setText(times + "");
                break;
            case R.id.bt_up_tt:
                times++;
                testtimes.setText(times + "");
                break;
            case R.id.bt_tb_setting:
                onResume();
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.sb_pgcs_setting:
                pro = progress;
                tv.setText("补光参数:" + progress);
                break;
            case R.id.sb_time_between:
                t = progress;
                tv_t.setText("拍摄间隔:" + progress);
                break;
            case R.id.sb_z_setting:
                z = progress;
                tv_z.setText("z值:" + progress);
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
