package measurement.color.com.xj_919.and.fragment.bluetooth;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import measurement.color.com.xj_919.R;

/**
 * Created by wpc on 2016/9/21.
 */
public class BlueToothworkspace extends Fragment {

    private Context mContext;
    private FragmentManager fm;
    private Button bt;
    private BlueToothManager mBlueToothManager;
    private ChoseDeveiceDialog dialog;

    @Override
    public void onResume() {
        super.onResume();

        ProgressDialog dl = ProgressDialog.show(mContext, "数据正在加载", "请稍后", true, true);
        dl.show();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bluetooth_fragment, container, false);

        mContext = getActivity();
        fm = getFragmentManager();
        mBlueToothManager = BlueToothManager.getInstance(mContext);
        dialog = new ChoseDeveiceDialog();

        bt = (Button) view.findViewById(R.id.bt_bluetooth);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (mBlueToothManager.getConnectState()) {
                    case BluetoothDevice.BOND_NONE:
                        dialog.show(fm, "ChoseDeveiceDialog");
                        bt.setText("正在连接");
                        break;
                    case BluetoothDevice.BOND_BONDED:
                        Toast.makeText(mContext, "设备已连接，请勿重复操作", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothDevice.BOND_BONDING:
                        Toast.makeText(mContext, "设备正在连接，请稍后", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        mBlueToothManager = BlueToothManager.getInstance(mContext);
        if (mBlueToothManager.openBlueTooth()) {
            bt.setEnabled(true);
            bt.setText("搜索设备");
        }
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBlueToothManager.unregisterReceiver();
    }
}
