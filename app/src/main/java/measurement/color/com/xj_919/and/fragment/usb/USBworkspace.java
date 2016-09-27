package measurement.color.com.xj_919.and.fragment.usb;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
public class USBworkspace extends Fragment {


    private Activity context;
    private USBManager mUSBManager;
    private static Button bt, bt_ls;
    private ImageView mImageView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.usb_fragment, container, false);

        context = getActivity();
        mUSBManager = USBManager.getInstance(context);
        mUSBManager.registerReceiver();

        bt = (Button) view.findViewById(R.id.bt_usb);
        bt_ls = (Button) view.findViewById(R.id.bt_log);

        mImageView = (ImageView) view.findViewById(R.id.iv_usb_fragment);

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (mUSBManager.getState()) {
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
                        bt.setText("拍照");
                        break;
                    case 3:
                        for (int i = 0; i < 100; i++) {
                            new MyTask().execute();
                        }
                        break;
                }
            }
        });
        bt_ls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, TableActivity.class));
            }
        });

        if (mUSBManager.getState() == -1) {
            Toast.makeText(context, "你的手机不支持USB Host，无法驱动设备", Toast.LENGTH_SHORT).show();
        } else {
            bt.setEnabled(true);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        switch (mUSBManager.getState()) {
            case -1:
                setButton("请确保usb正常连接", false);
                break;
            case 0:
                setButton("连接设备", true);
                break;
        }
    }

    public static void setButton(CharSequence str, boolean bool) {
        bt.setText(str);
        bt.setEnabled(bool);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mUSBManager.unregisterReceiver();
    }


    private class MyTask extends AsyncTask<String, Integer, String> {

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
        protected String doInBackground(String... params) {
            mUSBManager.sendTakePhotoRequest((byte) 0x00);
            mUSBManager.LoadDate();
            bitmap = Bitmap.createBitmap(mUSBManager.arr, mUSBManager.length - 1, mUSBManager.height - 1, Bitmap.Config.ARGB_8888);
            return null;
        }

        //onProgressUpdate方法用于更新进度信息
        @Override
        protected void onProgressUpdate(Integer... progresses) {
        }

        //onPostExecute方法用于在执行完后台任务后更新UI,显示结果
        @Override
        protected void onPostExecute(String result) {

            setButton("拍照", true);
            dialog.dismiss();
            mImageView.setImageBitmap(bitmap);
            mUSBManager.arr = new int[(mUSBManager.length - 1) * (mUSBManager.height - 1)];
        }

        //onCancelled方法用于在取消执行中的任务时更改UI
        @Override
        protected void onCancelled() {

        }
    }
}
