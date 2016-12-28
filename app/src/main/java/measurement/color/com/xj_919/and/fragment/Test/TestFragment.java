package measurement.color.com.xj_919.and.fragment.Test;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import measurement.color.com.xj_919.R;
import measurement.color.com.xj_919.and.Utils.SP.Consts;
import measurement.color.com.xj_919.and.Utils.SP.SharePreferenceHelper;
import measurement.color.com.xj_919.and.Utils.hard.VibratorHelper;
import measurement.color.com.xj_919.and.Utils.io.FileAndPath;
import measurement.color.com.xj_919.and.Utils.io.FileOpener.FileInfo;
import measurement.color.com.xj_919.and.Utils.io.FileUtils;
import measurement.color.com.xj_919.and.adapter.ListAdapterForDiaplayFileList;
import measurement.color.com.xj_919.and.adapter.PagerAdapterForTestGuide;
import measurement.color.com.xj_919.and.adapter.RecycleAdapterForHistory;
import measurement.color.com.xj_919.and.fragment.bluetooth.BlueToothManager;
import measurement.color.com.xj_919.and.view.CustomViewPager;

import static measurement.color.com.xj_919.and.activity.app.cent_X;
import static measurement.color.com.xj_919.and.activity.app.cent_Y;
import static measurement.color.com.xj_919.and.activity.app.cent_bet;
import static measurement.color.com.xj_919.and.activity.app.round_r;

/**
 * Created by wpc on 2016/9/21.
 */
public class TestFragment extends Fragment implements View.OnClickListener {

    static TestFragment instance;

    public static TestFragment getInstance() {
        if (instance == null) {
            instance = new TestFragment();
        }
        return instance;
    }

//    String filePath = FileAndPath.getSDcardPath() + "/xj_919/img/";
//    String fileName;

    View view;
    CustomViewPager vp;
    Button last, next, skep;
    private static boolean hasSaved = false;

    private static final int USB_STATE_UNENABLE = -1;
    private static final int USB_STATE_TAKEPHOTO = 2;
    private static final int SAVE_STATE_ENABLE = 5;
    private static final int SAVE_STATE_GONE = 6;
    private static final int REFRESH_RECYC = 7;
    private static final int SHOW_DIALOG = 8;
    private static final int DISMISS_DIALOG = 9;
    private static final int START_ASY_TASK = 10;
    private static final int TEST_FINISH = 11;
    private static final int TEST_REFESH = 12;
    private static final int SET_PROGRESS=13;

    private BlueToothManager mBlueToothManager;
    private Activity context;
    private USBManager mUSBManager;

    ProgressDialog dialog;
    private SharedPreferences mSharedPreferences;

    PagerAdapterForTestGuide adapter;
    ArrayList<String> datas;

    private RecycleAdapterForHistory.OnItemClickLitener mOnItemClickLitener;
    private TextView center, between, r;
    private Button lift, right, top, bottom, bet_del, bet_add, r_del, r_add;
    ListView lv;

    private static Button bt, bt_save, bt_print;
    private CheckBox cb, position_selecter;
    private EditText et_tips;
    private ImageView mImageView;
    Timer timer = new Timer();

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case USB_STATE_UNENABLE:
                    bt.setText("usb已断开");
                    bt.setEnabled(true);
                    break;
                case USB_STATE_TAKEPHOTO:
                    bt.setText("检测");
                    bt.setEnabled(true);
                    break;
                case SAVE_STATE_ENABLE:
                    bt_save.setVisibility(View.VISIBLE);
                    break;
                case SAVE_STATE_GONE:
                    bt_save.setVisibility(View.GONE);
                    break;
                case START_ASY_TASK:
                    new MyTask(cb.isChecked(), msg.arg2, msg.arg1).execute();
                    break;
//                case TEST_FINISH:
//                    adapter.onFinish(datas);
//                    datas=new ArrayList<>();
//                    break;
                case TEST_REFESH:
                    adapter.onRefesh(resultData);
                    break;
                case SET_PROGRESS:
                    adapter.setProgress(msg.arg1, msg.arg2);
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        view = inflater.inflate(R.layout.usb_fragment, container, false);
//        PhoneUtils.isUsbHostEnable(context);
        initData();
        findView();
        setListener();

        mOnItemClickLitener = new RecycleAdapterForHistory.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.i("item", "click");
            }

            @Override
            public void onItemLongClick(View view, int position) {
                Log.i("item", "longclick");
            }
        };

        return view;
    }


    void initData() {
        mSharedPreferences = SharePreferenceHelper.getInstance();
        mUSBManager = USBManager.getInstance(context);
        mBlueToothManager = BlueToothManager.getInstance();
    }

    int pagerindex = 0;

    private void findView() {
        view.findViewById(R.id.bblr_test).setOnClickListener(this);

        view.findViewById(R.id.bt_turn_down_light).setOnClickListener(this);
//        view.findViewById(R.id.set_test).setOnClickListener(this);
        bt = (Button) view.findViewById(R.id.bt_usb);
        bt_save = (Button) view.findViewById(R.id.bt_save_usb);
        cb = (CheckBox) view.findViewById(R.id.cb_usb);
        bt_print = (Button) view.findViewById(R.id.bt_print_usb);

        position_selecter = (CheckBox) view.findViewById(R.id.cb_use_under_position);

        mImageView = (ImageView) view.findViewById(R.id.iv_usb_fragment);
        et_tips = (EditText) view.findViewById(R.id.et_tips_usb);

        center = (TextView) view.findViewById(R.id.center_location);
        between = (TextView) view.findViewById(R.id.tv_between);
        r = (TextView) view.findViewById(R.id.tv_r_location);

        lift = (Button) view.findViewById(R.id.bt_left_location);
        right = (Button) view.findViewById(R.id.bt_right_location);
        top = (Button) view.findViewById(R.id.bt_up_location);
        bottom = (Button) view.findViewById(R.id.bt_down_location);
        lv = (ListView) view.findViewById(R.id.lv_cache_list);

        bet_del = (Button) view.findViewById(R.id.bt_bew_down);
        bet_add = (Button) view.findViewById(R.id.bt_bew_up);
        r_del = (Button) view.findViewById(R.id.bt_r_down);
        r_add = (Button) view.findViewById(R.id.bt_r_up);
        vp = (CustomViewPager) view.findViewById(R.id.vp_test);
        last = (Button) view.findViewById(R.id.bt_last_test);
        next = (Button) view.findViewById(R.id.bt_next_test);
        skep = (Button) view.findViewById(R.id.bt_skep_test);
        final ArrayList<FileInfo> fileInfos = FileUtils.getFileInfoListWithDirPathAndEnd(FileAndPath.IMG_NATIVE_DATA, ".cache");
        lv.setAdapter(new ListAdapterForDiaplayFileList(fileInfos, context));
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                byte[] arr = FileUtils.decodeFileToByteArray(fileInfos.get(position).getDirPath(), fileInfos.get(position).getName());
                Log.i("byte arr length", arr.length + "");
            }
        });
        adapter = new PagerAdapterForTestGuide(context);
        vp.setAdapter(adapter);
        vp.setCurrentItem(0);
        vp.setOffscreenPageLimit(5);
        vp.setPagingEnabled(false);
        vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                pagerindex = position;
                if (pagerindex == 4) {
                    skep.setText("开始测试");
                    adapter.setProgress(mSharedPreferences.getInt(Consts.TakePhotoTimes, 1), 0);
                    if (cb.isChecked()) {
                        handler.sendEmptyMessage(SAVE_STATE_GONE);
                    } else {
                        handler.sendEmptyMessage(SAVE_STATE_ENABLE);
                    }
                } else {
                    skep.setText("跳过");
                    handler.sendEmptyMessage(SAVE_STATE_GONE);
                }

                if (pagerindex == 0) {
                    last.setVisibility(View.INVISIBLE);
                } else {
                    last.setVisibility(View.VISIBLE);
                }

                if (pagerindex == 4) {
                    next.setVisibility(View.INVISIBLE);
                } else {
                    next.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    //testMod 2下位机固定位置校准 1位置调整校准  0正常测试  -1白板录入
    private void test(final int testMod, final int times) {
        Log.i("state", mUSBManager.getState() + "");
        switch (mUSBManager.getState()) {
            default:
                if (!mUSBManager.init()) {
//                    adapter.init(false);
                    break;
                }
            case 3:
                adapter.setProgress(null, 0);
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    int time = 0;

                    @Override
                    public void run() {
                        Log.i("times", times + "");
                        time++;
                        Message message = new Message();
                        message.what = START_ASY_TASK;
                        message.arg1 = time;
                        message.arg2 = testMod;
                        handler.sendMessage(message);

                        if (time == times) {
                            timer.cancel();
                        }
                    }
                }, 0, 1000 * (mSharedPreferences.getInt(Consts.TakePhotoWait, 1) + 3));
//                if (!fixPox) {
//                    bt_print.setVisibility(View.VISIBLE);
//                }
                break;
        }
    }

    private void setListener() {

//        mImageView.setOnClickListener(this);
        lift.setOnClickListener(this);
        right.setOnClickListener(this);
        top.setOnClickListener(this);
        bottom.setOnClickListener(this);

        bet_del.setOnClickListener(this);
        bet_add.setOnClickListener(this);
        r_del.setOnClickListener(this);
        r_add.setOnClickListener(this);

        last.setOnClickListener(this);
        next.setOnClickListener(this);
        skep.setOnClickListener(this);

        bt.setOnClickListener(this);
        bt_save.setOnClickListener(this);
        bt_print.setOnClickListener(this);

        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.i("isChecked", String.valueOf(isChecked));
                if (vp.getCurrentItem() == 4) {
                    if (cb.isChecked()) {
                        handler.sendEmptyMessage(SAVE_STATE_GONE);
                    } else {
                        handler.sendEmptyMessage(SAVE_STATE_ENABLE);
                    }
                }
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        cent_X = mSharedPreferences.getInt(Consts.KEY_CENTER_X, 320);
        cent_Y = mSharedPreferences.getInt(Consts.KEY_CENTER_Y, 240);
        cent_bet = mSharedPreferences.getInt(Consts.KEY_CENTER_BETWEEN, 145);
        round_r = mSharedPreferences.getInt(Consts.KEY_CENTER_R, 40);
        setText(R.id.center_location);
        setText(R.id.tv_between);
        setText(R.id.tv_r_location);

        if (mSharedPreferences.getBoolean(Consts.ROOT_PROMISSION, false)) {
            view.findViewById(R.id.cv1_test).setVisibility(View.VISIBLE);
            view.findViewById(R.id.cv2_test).setVisibility(View.VISIBLE);
        } else {
            view.findViewById(R.id.cv1_test).setVisibility(View.GONE);
            view.findViewById(R.id.cv2_test).setVisibility(View.GONE);
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
//            case R.id.iv_usb_fragment:
//                if (filePath == null || fileName == null) {
//                    break;
//                } else {
//                    FileUtils.openFile(context, new File(filePath, fileName));
//                }
//                break;
//            case R.id.set_test:
//               initConfig();
//                break;
            case R.id.bt_turn_down_light:
                if (mUSBManager.TransceiverInstance != null) {
                    mUSBManager.TransceiverInstance.sendTurnDownRequest();
                }
                break;
            case R.id.bt_usb:
                if (position_selecter.isChecked()) {
                    test(2, 1);
                } else {
                    test(1, 1);
                }

                break;
            case R.id.bblr_test:
                test(-1, 1);
                break;
            case R.id.bt_save_usb:
                if (mUSBManager.TransceiverInstance != null) {
                    mUSBManager.TransceiverInstance.saveData(0, et_tips.getText().toString(), true, resultData);
                } else {
                    Toast.makeText(context, "请先测试样品", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.bt_print_usb:
//                Log.i("bt_print_usb", "click printable=" + String.valueOf(mUSBManager.printable));
                if (mUSBManager.printable) {

//                    if (mBlueToothManager.mBlueToothPrinter != null) {
//                        try {
//                            mBlueToothManager.mBlueToothPrinter.printArray(getPrintStringArray());
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    } else {
//                        Toast.makeText(context, "请先连接蓝牙打印设备", Toast.LENGTH_SHORT).show();
//                    }

                } else {
                    Toast.makeText(context, "请先测试样品", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.bt_left_location:
                cent_X--;
                setText(R.id.center_location);
                break;
            case R.id.bt_right_location:
                cent_X++;
                setText(R.id.center_location);
                break;
            case R.id.bt_up_location:
                cent_Y++;
                setText(R.id.center_location);
                break;
            case R.id.bt_down_location:
                cent_Y--;
                setText(R.id.center_location);
                break;
            case R.id.bt_bew_down:
                cent_bet--;
                setText(R.id.tv_between);
                break;
            case R.id.bt_bew_up:
                cent_bet++;
                setText(R.id.tv_between);
                break;
            case R.id.bt_r_down:
                round_r--;
                setText(R.id.tv_r_location);
                break;
            case R.id.bt_r_up:
                round_r++;
                setText(R.id.tv_r_location);
                break;

            case R.id.bt_last_test:

                if (pagerindex > 0 && pagerindex < 5) {
                    vp.setCurrentItem(pagerindex - 1, true);
                }
                break;
            case R.id.bt_next_test:
                if (pagerindex >= 0 && pagerindex < 4) {
                    vp.setCurrentItem(pagerindex + 1, true);
                }
                break;
            case R.id.bt_skep_test:
                if (pagerindex == 4) {
                    bt_print.setVisibility(View.GONE);
                    adapter.CleanListView();
                    test(0, mSharedPreferences.getInt(Consts.TakePhotoTimes, 1));
                    adapter.setProgress(mSharedPreferences.getInt(Consts.TakePhotoTimes, 1), 0);
                } else {
                    vp.setCurrentItem(4, true);
                }
                break;
        }
    }

    private void initConfig() {
        if (mUSBManager.init()) {
//            if (mUSBManager.TransceiverInstance.checkPermission()) {

//            }
        }
    }

    private void setText(int id) {
        switch (id) {
            case R.id.center_location:
                center.setText("(" + cent_X + "," + cent_Y + ")");
                break;
            case R.id.tv_between:
                between.setText(cent_bet + "");
                break;
            case R.id.tv_r_location:
                r.setText(round_r + "");
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public static void setButton(String str, boolean b) {
        bt.setText(str);
        bt.setEnabled(b);
    }


    boolean busy = false;


    ArrayList<ArrayList<ResultData>> resultData;

    private class MyTask extends AsyncTask<String, Integer, Integer> {
//1 成功 0 数据不可用  -1连接不可用  -2没有白板
        int resultful;
        boolean save;
        int test_mod;
        int times;
        String tips;

        MyTask(boolean save, int testMod, int time) {
            this.save = save;
            test_mod = testMod;
            this.times = time;
        }

        //onPreExecute方法用于在执行后台任务前做一些UI操作
        @Override
        protected void onPreExecute() {
            tips = et_tips.getText().toString();
            if (times == 1) {
                dialog = new ProgressDialog(context);
                dialog.setCancelable(false);
                dialog.show();
            }
            dialog.setMessage("测试中["+times+"]...");
//            if (dialog == null) {
//                dialog = new ProgressDialog(context);
//                dialog.setCancelable(false);
////            int flags
////            各种锁的类型对CPU 、屏幕、键盘的影响：
////            PARTIAL_WAKE_LOCK :保持CPU 运转，屏幕和键盘灯有可能是关闭的。
////            SCREEN_DIM_WAKE_LOCK ：保持CPU 运转，允许保持屏幕显示但有可能是灰的，允许关闭键盘灯
////            SCREEN_BRIGHT_WAKE_LOCK ：保持CPU 运转，允许保持屏幕高亮显示，允许关闭键盘灯
////            FULL_WAKE_LOCK ：保持CPU 运转，保持屏幕高亮显示，键盘灯也保持亮度
//                dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//            }
//            if (!dialog.isShowing()) {
//                dialog.setMessage("第" + times + "次取样...");
//                dialog.show();
//            }
        }

        //doInBackground方法内部执行后台任务,不可在此方法内修改UI
        @Override
        protected Integer doInBackground(String... params) {

            busy = true;
            if (mUSBManager != null) {
//                mUSBManager.TransceiverInstance.checkPermission();
                if (test_mod != 1) {
                    if (Config.getInstance().getSettings() == null) {
                        Config.getInstance().initSettings(mUSBManager.TransceiverInstance.sendTakeConfigRequest());
                    }
                }
                if (mUSBManager.TransceiverInstance.sendTakePhotoRequest((byte) 0x00, mSharedPreferences.getInt(Consts.TakePhotoDelay,SharePreferenceHelper.getInstance().getInt(Consts.TakePhotoDelay,180)))) {//190
                    //                mUSBManager.TransceiverInstance.initData();
                    resultful = mUSBManager.TransceiverInstance.LoadDate(test_mod, mSharedPreferences.getInt(Consts.LIGHT_HIGHT, 20) * 10);
                    if (resultful==1) {
                        if (test_mod == 0) {
                            resultData = mUSBManager.TransceiverInstance.checkData();
                            Log.i("result",resultData.toString());
                            if (save) {
                                mUSBManager.TransceiverInstance.saveData(0, tips, true, resultData);
                                hasSaved = true;
                            }
                            notifityUser(resultData);
                        } else if (test_mod == -1) {
                            mUSBManager.TransceiverInstance.saveData(-1, null, null, null);
                        }
                    }
                } else {
                    resultful=-1;
//                    Toast.makeText(context, "连接错误", Toast.LENGTH_SHORT).show();
//                    Message m=new Message();
//                    m.arg1=0;m.arg2=-3;
//                    handler.sendMessage(m);
                    dialog.cancel();
                    timer.cancel();
                }


            } else {
                Toast.makeText(context, "连接错误", Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        private void notifityUser(ArrayList<ArrayList<ResultData>> resultData) {
            boolean hasFound = false;
            for (int i = 0; i < resultData.size(); i++) {
                for (int j = 0; j < resultData.get(i).size(); j++) {
                    if (resultData.get(i).get(j).isHasfound()) {
                        hasFound = true;
                        break;
                    }
                }
            }
            mUSBManager.TransceiverInstance.sendNotifyUserRequest(hasFound);
        }

        //onPostExecute方法用于在执行完后台任务后更新UI,显示结果
        @Override
        protected void onPostExecute(Integer result) {
            if (resultful!=1) {
                dialog.cancel();
                adapter.setProgress(null, resultful-2);
                timer.cancel();
//                Toast.makeText(context, "请正确放置被测物位置", Toast.LENGTH_SHORT).show();
            }else if(resultful==-2){
                dialog.cancel();
                adapter.setProgress(null, resultful-2);
                timer.cancel();
            }
            else {
                mImageView.setImageBitmap(mUSBManager.bitmap);
                adapter.setProgress(null, times);
                if (test_mod == 1) {
                    saveChange();
                    dialog.cancel();
                } else if (test_mod == 0) {
//                   getResultReport(judge);
                    if (times == mSharedPreferences.getInt(Consts.TakePhotoTimes, 1)) {
                        if (VibratorHelper.isOpen()) {
                            VibratorHelper.getInstance(context).vibrate(new long[]{100, 100, 100, 100}, -1);
                        }
                        dialog.cancel();
                    }
                } else {
                    dialog.cancel();
                }
//            handler.sendEmptyMessage(USB_STATE_TAKEPHOTO)  handler.sendEmptyMessage(TEST_FINISH);;
                mUSBManager.TransceiverInstance.arr = new int[(mUSBManager.TransceiverInstance.length - 1) * (mUSBManager.TransceiverInstance.height - 1)];
                busy = false;
//            mUSBManager.TransceiverInstance.arr = new int[(mUSBManager.TransceiverInstance.length / 2) * (mUSBManager.TransceiverInstance.height / 2)];
//            mUSBManager.bitmap.recycle();
                if (test_mod == 0) {
                    handler.sendEmptyMessage(TEST_REFESH);
                }

//                if (times != mSharedPreferences.getInt(Consts.TakePhotoTimes, 1)) {
//
//                } else {
////                    handler.sendEmptyMessage(TEST_FINISH);
//                    adapter.setProgress(null, times);
//                    mUSBManager.printable = true;
//                }
            }
        }
    }

    /**
     * 根据6个区域返回数据的集合得出报告list
     *
     * @param judge
     * @return
     */

    private ArrayList<String> getResultReport(ArrayList<ArrayList<ResultData>> judge) {
        ArrayList<String> results = new ArrayList<>();
        for (int i = 0; i < judge.size(); i++) {
            ArrayList<ResultData> datas = judge.get(i);
            for (int j = 0; j < datas.size(); j++) {
                if (datas.get(j).hasfound) {
                    results.add(datas.get(j).toString());
                }
            }
        }
        return results;
    }

    /**
     * 将当前位置参数存入sharedpreference
     */
    private void saveChange() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(Consts.KEY_CENTER_X, cent_X);
        editor.putInt(Consts.KEY_CENTER_Y, cent_Y);
        editor.putInt(Consts.KEY_CENTER_BETWEEN, cent_bet);
        editor.putInt(Consts.KEY_CENTER_R, round_r);
        editor.commit();
    }

}
