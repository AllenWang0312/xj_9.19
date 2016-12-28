package measurement.color.com.xj_919.and.fragment.bluetooth;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;

import measurement.color.com.xj_919.R;

/**
 * Created by wpc on 2016/9/20.
 */
public class ChoseDeveiceDialog extends DialogFragment {

    private static final String tag = "ChoseDeveiceDialog";
    private Context context;
    private static BlueToothManager manager;
    //10蓝牙未连接 12已连接 11正在搜索蓝牙
    public static int connectState = 10;
    static ArrayList<BluetoothDevice> boundedlist;
    static ArrayList<BluetoothDevice> deveicesDescoved = new ArrayList<>();
    private BlueToothBroadcastReceiver blueToothBroadcastReceiver;
    private boolean isDescovering = false;

    private ListView lv1;
    private Button bt, print;
    static ListView lv2;
    static ProgressBar mProgressBar;
    static AlertDialog dialog;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    lv1.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, getNames(manager.getDevices())));
                    break;
                case 2:
                    lv2.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, getNames(deveicesDescoved)));
                    break;
            }
        }
    };

    public ChoseDeveiceDialog() {

    }

    public ChoseDeveiceDialog(Activity context, Button print) {
        this.context = context;
        this.print = print;
        manager = BlueToothManager.getInstance();
        boundedlist = manager.getDevices();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerReceiver(context);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_chose_bluetooth, null);
        lv1 = (ListView) view.findViewById(R.id.lv1_dialog_bluetooth);
        lv2 = (ListView) view.findViewById(R.id.lv2_dialog_bluetooth);

        bt = (Button) view.findViewById(R.id.bt_dialog_bluetooth);

        mProgressBar = (ProgressBar) view.findViewById(R.id.pb_dialog_bluetooth);

        if (manager.getDevices().size() != 0) {
            mHandler.sendEmptyMessage(1);
        }


        lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                manager.openBT(manager.getDevices().get(i), print);
            }
        });
        lv1.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(context).setTitle("删除").setMessage("从已连接设备中删除该设备?").setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        manager.removeDeveiceFromBoundList(manager.getDevices().get(position));
                        mHandler.sendEmptyMessage(1);
                    }
                }).setNegativeButton("取消", null).create().show();
                return false;
            }
        });
//        lv2.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, manager.getDeveicesDescovedNames()));

        lv2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (isDescovering) {
                    manager.stopDiscover();
                }
                manager.openBT(deveicesDescoved.get(i), print);
            }
        });
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!manager.mAdapter.isDiscovering()) {
                    manager.startDescovery();
                }
                if (isDescovering) {
                    manager.stopDiscover();
                    mProgressBar.setVisibility(View.INVISIBLE);
                    bt.setText("搜索设备");
                    isDescovering = false;
                } else {
                    manager.findDevices();
                    mProgressBar.setVisibility(View.VISIBLE);
                    bt.setText("停止");
                    isDescovering = true;
                }
            }
        });
        builder.setView(view);
        builder.setTitle("选择你要连接的设备");

        dialog = builder.create();
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        return dialog;
    }

    public static ArrayList<String> getNames(ArrayList<BluetoothDevice> devices) {
        ArrayList<String> names = new ArrayList<>();
        for (BluetoothDevice device : devices) {
            names.add(device.getName() + "(" + device.getAddress() + ")");
        }
        return names;
    }

    public void dimissProgressBar() {
        mProgressBar.setVisibility(View.INVISIBLE);
        mHandler.sendEmptyMessage(2);
        Log.i(tag, deveicesDescoved.toString());
    }

    //注册接受搜索结果的广播
    public boolean registerReceiver(Context context) {
        blueToothBroadcastReceiver = new BlueToothBroadcastReceiver();
        IntentFilter startFilter = new IntentFilter(
                BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        IntentFilter foundFilter = new IntentFilter(
                BluetoothDevice.ACTION_FOUND);
        IntentFilter endFilter = new IntentFilter(
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
//        IntentFilter changeFilter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);

        context.registerReceiver(blueToothBroadcastReceiver, startFilter);
        context.registerReceiver(blueToothBroadcastReceiver, foundFilter);
        context.registerReceiver(blueToothBroadcastReceiver, endFilter);
//        mContext.registerReceiver(blueToothBroadcastReceiver, changeFilter);
//        IntentFilter startFilter = new IntentFilter(
//                BluetoothAdapter.ACTION_DISCOVERY_STARTED);
//        IntentFilter foundFilter = new IntentFilter(
//                BluetoothDevice.ACTION_FOUND);
//        IntentFilter endFilter = new IntentFilter(
//                BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
//        mContext.registerReceiver(blueToothBroadcastReceiver, startFilter);
//        mContext.registerReceiver(blueToothBroadcastReceiver, foundFilter);
//        mContext.registerReceiver(blueToothBroadcastReceiver, endFilter);
        Log.i("Receicer", "register");
        return true;
    }

    //记得unregister
    public boolean unregisterReceiver() {
        context.unregisterReceiver(blueToothBroadcastReceiver);
        Log.i("Receicer", "unregister");
        return true;
    }

    class BlueToothBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i("action", action);
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                deveicesDescoved = new ArrayList<>();

            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.i("action found", device.getName() + device.getAddress());
                if (deveicesDescoved == null) {
                    deveicesDescoved = new ArrayList<BluetoothDevice>();
                }
                if (!(boundedlist.contains(device))) {
                    deveicesDescoved.add(device);
                    mHandler.sendEmptyMessage(2);
                    Log.i("add deveice" + device.getName(), deveicesDescoved.size() + "");
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
                    .equals(action)) {
                dimissProgressBar();
            }
        }

    }
}
