package measurement.color.com.xj_919.and.ui.myview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import measurement.color.com.xj_919.R;
import measurement.color.com.xj_919.and.Manager.USBManager;

/**
 * Created by wpc on 2016/9/19.
 */
public class USBConectLayout extends RelativeLayout {

    private String tag = "USBConectLayout";
    private int i = Toast.LENGTH_SHORT;


    private Context context;
    private USBManager mUSBManager;

    public USBConectLayout(final Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.usbconect, this);

        final Button usbButton = (Button) findViewById(R.id.usbbt_conect);
        Button BlueButton = (Button) findViewById(R.id.bluebt_conect);

        mUSBManager = USBManager.getInstance(context);

        if (mUSBManager.getState() == -1) {
            Toast.makeText(context, "你的手机不支持USB Host，无法驱动设备", i).show();
        } else {
            usbButton.setEnabled(true);
        }

        usbButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (mUSBManager.getState()) {

                    case 0:
                        usbButton.setText("查找驱动");
                        boolean findDevice = mUSBManager.findDevice(1155, 22336);
                        if (!findDevice) {
                            Toast.makeText(context, "请确认连接", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 1:
                        usbButton.setText("查找接口");
                       boolean findInterface= mUSBManager.findInterface(10, 0, 0);
                        if(!findInterface){
                            Toast.makeText(context, "请确认连接", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 2:
                        usbButton.setText("查找端点");
                       boolean findPoint= mUSBManager.findPoint();
                        if(!findPoint){
                            Toast.makeText(context, "请确认连接", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 3:
                        usbButton.setText("获取数据");
                        mUSBManager.sendRequest();
                        break;
                }
            }
        });


    }

}
