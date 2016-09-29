package measurement.color.com.xj_919.and.fragment.usb;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import measurement.color.com.xj_919.R;
import measurement.color.com.xj_919.and.activity.TableActivity;

/**
 * Created by wpc on 2016/9/21.
 */
public class USBworkspace extends Fragment implements View.OnClickListener {


    private static final int BUTTON_STATE_UNENABLE = -1;
    private static final int BUTTON_STATE_CONNECT = 0;
    private static final int BUTTON_STATE_TAKEPHOTO = 2;
    private static final int BUTTON_STATE_LOAD = 3;


    private Activity context;
    private USBManager mUSBManager;
    private static Button bt, bt_ls;
    private ImageView mImageView;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BUTTON_STATE_UNENABLE:
                    setButton("请确保usb连接正常", false);
                    break;
                case BUTTON_STATE_CONNECT:
                    setButton("连接设备", true);
                    break;
                case BUTTON_STATE_TAKEPHOTO:
                    setButton("拍照", true);
                    break;
                case BUTTON_STATE_LOAD:
                    setButton("正在获取数据", false);
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.usb_fragment, container, false);
        context = getActivity();
        mUSBManager = USBManager.getInstance(context);

        bt = (Button) view.findViewById(R.id.bt_usb);
        bt_ls = (Button) view.findViewById(R.id.bt_log);
        mImageView = (ImageView) view.findViewById(R.id.iv_usb_fragment);

        bt.setOnClickListener(this);
        bt_ls.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_usb:
                Log.i("state", mUSBManager.getState() + "");
                switch (mUSBManager.getState()) {
                    case -1:

                        break;
                    case 0:
                        boolean findDevice = mUSBManager.findDevice(1155, 22336);
                        if (!findDevice) {
                            Toast.makeText(context, "请确认连接", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        boolean findInterface = mUSBManager.findInterface(10, 0, 0);
                        if (!findInterface) {
                            Toast.makeText(context, "请确认连接", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        boolean findPoint = mUSBManager.findPoint();
                        if (!findPoint) {
                            Toast.makeText(context, "请确认连接", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        handler.sendEmptyMessage(BUTTON_STATE_TAKEPHOTO);
                        break;
                    case 3:
                        for (int i = 0; i < 30; i++) {
                            new MyTask().execute();
                        }
                        break;
                }
                break;
            case R.id.bt_log:
                startActivity(new Intent(context, TableActivity.class));
                break;

        }

    }

    @Override
    public void onStart() {
        super.onStart();
        mUSBManager.registerReceiver();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mUSBManager.getState() == 3) {
            handler.sendEmptyMessage(BUTTON_STATE_TAKEPHOTO);
        } else if (mUSBManager.getState() == 0) {
            handler.sendEmptyMessage(BUTTON_STATE_CONNECT);
        } else if (mUSBManager.getState() == -1) {
            Toast.makeText(context, "你的手机不支持USB Host，无法驱动设备", Toast.LENGTH_SHORT).show();
        }
        Log.i("onResume", mUSBManager.getState() + "");
    }

    @Override
    public void onStop() {
        super.onStop();
        mUSBManager.unregisterReceiver();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public static void setButton(CharSequence str, boolean bool) {
        bt.setText(str);
        bt.setEnabled(bool);
    }

    private class MyTask extends AsyncTask<String, Integer, Integer> {

        ProgressDialog dialog;
        Bitmap bitmap;

        //onPreExecute方法用于在执行后台任务前做一些UI操作
        @Override
        protected void onPreExecute() {
            setButton("正在读取数据", false);
            dialog = ProgressDialog.show(context, "请稍后", "正在读取数据", false, false);
//            int flags
//            各种锁的类型对CPU 、屏幕、键盘的影响：
//            PARTIAL_WAKE_LOCK :保持CPU 运转，屏幕和键盘灯有可能是关闭的。
//            SCREEN_DIM_WAKE_LOCK ：保持CPU 运转，允许保持屏幕显示但有可能是灰的，允许关闭键盘灯
//            SCREEN_BRIGHT_WAKE_LOCK ：保持CPU 运转，允许保持屏幕高亮显示，允许关闭键盘灯
//            FULL_WAKE_LOCK ：保持CPU 运转，保持屏幕高亮显示，键盘灯也保持亮度
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            dialog.show();
        }

        //doInBackground方法内部执行后台任务,不可在此方法内修改UI
        @Override
        protected Integer doInBackground(String... params) {
            mUSBManager.sendTakePhotoRequest((byte) 0x00);
            mUSBManager.LoadDate();
            bitmap = Bitmap.createBitmap(mUSBManager.arr, mUSBManager.length - 1, mUSBManager.height - 1, Bitmap.Config.ARGB_8888);
            return null;
        }

        //onProgressUpdate方法用于更新进度信息
        @Override
        protected void onProgressUpdate(Integer... progresses) {
//            dialog.setProgress(progresses);
        }

        //onPostExecute方法用于在执行完后台任务后更新UI,显示结果
        @Override
        protected void onPostExecute(Integer result) {

            setButton("拍照", true);
            dialog.cancel();
            mImageView.setImageBitmap(bitmap);
            mUSBManager.arr = new int[(mUSBManager.length - 1) * (mUSBManager.height - 1)];
        }

        //onCancelled方法用于在取消执行中的任务时更改UI
        @Override
        protected void onCancelled() {

        }
    }
}
