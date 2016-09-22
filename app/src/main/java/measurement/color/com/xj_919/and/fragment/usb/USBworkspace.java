package measurement.color.com.xj_919.and.fragment.usb;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import measurement.color.com.xj_919.R;

import static measurement.color.com.xj_919.and.fragment.usb.USBManager.requestType;

/**
 * Created by wpc on 2016/9/21.
 */
public class USBworkspace extends Fragment {


    private Context context;
    private USBManager mUSBManager;
    private static Button bt;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.usb_fragment, container, false);
        context = getActivity();
        mUSBManager = USBManager.getInstance(context);
        bt = (Button) view.findViewById(R.id.bt_usb);

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
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        switch (mUSBManager.getDatastate()) {
                            case -1:
                                mUSBManager.sendRequest(requestType.TakePhoto, (byte) 0x01, null);
                                bt.setText("获取第一组数据");
                                break;
                            case 0:
                                getDataThread myThread=new getDataThread();
                                myThread.run();
                                break;
                            case 1:

                                break;
                            case 2:

                                break;
                        }
                        break;
                }
            }
        });
        if (mUSBManager.getState() == -1) {
            Toast.makeText(context, "你的手机不支持USB Host，无法驱动设备", Toast.LENGTH_SHORT).show();
        } else {
            bt.setEnabled(true);
        }
        mUSBManager.registerReceiver();

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mUSBManager.unregisterReceiver();
    }

    public static void setButtonEnable(boolean bool) {
        bt.setEnabled(bool);
        if (bool) {
            bt.setText("连接仪器");
        } else {
            bt.setText("请确保usb连接");
        }

    }

    class getDataThread extends Thread {
        @Override
        public void run() {
            super.run();
            for (short i = 0x0000; i < USBDatas.times; i++) {
                mUSBManager.sendRequest(requestType.GetData, null, i);
            }
            Log.i("getDataThread","finish");
        }
    }

}