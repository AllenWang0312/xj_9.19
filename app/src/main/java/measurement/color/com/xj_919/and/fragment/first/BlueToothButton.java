package measurement.color.com.xj_919.and.fragment.first;

import android.app.Activity;
import android.app.FragmentManager;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by wpc on 2016/9/20.
 */
public class BlueToothButton extends Button {


    private BlueToothManager mBlueToothManager;
    private Activity mActivity;

    public BlueToothButton(Context context) {
        this(context, null);
    }

    public BlueToothButton(final Context context, AttributeSet attrs) {
        super(context, attrs);
        mActivity = (Activity) context;
        mBlueToothManager =BlueToothManager.getInstance(context);
        if (mBlueToothManager.openBlueTooth()) {
            setEnabled(true);
            setText("搜索设备");
        }
        setOnClickListener(new OnClickListener() {
            public void onClick(View view) {

                switch (mBlueToothManager.getConnectState()) {
                    case BluetoothDevice.BOND_NONE:
                        ChoseDeveiceDialog dialog = new ChoseDeveiceDialog();
                        FragmentManager fm = mActivity.getFragmentManager();
                        dialog.show(fm, "ChoseDeveiceDialog");
                        setText("正在连接");
                        break;
                    case BluetoothDevice.BOND_BONDED:
                        Toast.makeText(context,"设备已连接，请勿重复操作",Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothDevice.BOND_BONDING:
                        Toast.makeText(context,"设备正在连接，请稍后",Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }



}
