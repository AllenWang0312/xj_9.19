package measurement.color.com.xj_919.and.fragment.first;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by wpc on 2016/9/20.
 */
public class USBButton extends Button {
    private Context context;
    private USBManager mUSBManager;
    private usbBroadcastReceiver myUSBReceiver;

    public USBButton(Context context) {
        this(context, null);
    }

    public USBButton(final Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        mUSBManager = USBManager.getInstance(context);
        if (mUSBManager.getState() == -1) {
            Toast.makeText(context, "你的手机不支持USB Host，无法驱动设备", Toast.LENGTH_SHORT).show();
        } else {
            setEnabled(true);
        }

        setOnClickListener(new OnClickListener() {
                               @Override
                               public void onClick(View view) {
                                   synchronized (view) {
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
                                               setText("获取数据");
                                               break;
                                           case 1:
                                               break;
                                           case 2:
                                               break;
                                           case 3:
                                               mUSBManager.sendRequest();
                                               break;
                                       }
                                   }
                               }
                           }
        );

        myUSBReceiver = new usbBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.setPriority(800);
        intentFilter.addAction("android.hardware.usb.action.USB_DEVICE_ATTACHED");
        intentFilter.addAction("android.hardware.usb.action.USB_DEVICE_DETACHED");
        context.registerReceiver(myUSBReceiver, intentFilter);

    }

    class usbBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //作为host设备检测从设备是否连接
            if (intent.getAction().equals("android.hardware.usb.action.USB_DEVICE_ATTACHED")) {
                setEnabled(true);
            } else {
                setEnabled(false);
            }

//作为从设备连接和断开的判断   android.hardware.usb.action.USB_STATE
//        if (intent.getExtras().getBoolean("connected")) {
//            USBConectLayout.changeUSBButtonState(true);
//        } else {
//            USBConectLayout.changeUSBButtonState(false);
//        }
        }

    }


}
