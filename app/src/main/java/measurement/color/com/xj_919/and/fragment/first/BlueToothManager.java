package measurement.color.com.xj_919.and.fragment.first;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

/**
 * Created by wpc on 2016/9/20.
 */
public class BlueToothManager {

    private static final String tag = "BlueToothManager";


    private final int t = Toast.LENGTH_SHORT;

    private int connectState = BluetoothDevice.BOND_NONE;
    private static ArrayList<BluetoothDevice> deveicesBonded=new ArrayList<>();
    private static ArrayList<String> deveicesBondedNames=new ArrayList<>();

    private static ArrayList<BluetoothDevice> deveicesDescoved = new ArrayList<>();
    private static ArrayList<String> deveicesDescovedNames = new ArrayList<>();

    private static BlueToothManager instance;

    private BlueToothManager() {

    }

    private static Context mContext;
    private static BluetoothAdapter mAdapter;
    private BlueToothBroadcastReceiver blueToothBroadcastReceiver;
    private String deveicename = "152163LJT0010";//需要连接的设备名

    public static BlueToothManager getInstance(Context context) {
        if (instance == null) {
            instance = new BlueToothManager();
        }
        mContext = context;
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mAdapter == null) {
            Toast.makeText(context, "设备不支持蓝牙", Toast.LENGTH_SHORT).show();
        }
        return instance;
    }


    public boolean openBlueTooth() {
        if (!mAdapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            mContext.startActivity(intent);
            return true;
        } else {
            return true;
        }
    }

    //已连接过的列表
    public static ArrayList<String> getDevices() {
        if (mAdapter != null) {
            Set<BluetoothDevice> Devices = mAdapter.getBondedDevices();
            if (Devices.size() != 0) {
                Log.i(tag, Devices.size() + "");
                deveicesBonded = new ArrayList<BluetoothDevice>();
                deveicesBondedNames = new ArrayList<String>();
                Iterator<BluetoothDevice> iterator = Devices.iterator();
                while (iterator.hasNext()) {
                    BluetoothDevice device = iterator.next();
                    deveicesBonded.add(device);
                    deveicesBondedNames.add(device.getName());
                }
            }
        } else {
            Log.i(tag, "madapter为空");
            return null;
        }
        return deveicesBondedNames;

    }

    //注册接受搜索结果的广播
    public boolean registerReceiver() {
        deveicesDescoved = new ArrayList<>();
        deveicesDescovedNames=new ArrayList<>();
        blueToothBroadcastReceiver = new BlueToothBroadcastReceiver();

        IntentFilter startFilter = new IntentFilter(
                BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        IntentFilter foundFilter = new IntentFilter(
                BluetoothDevice.ACTION_FOUND);
        IntentFilter endFilter = new IntentFilter(
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        IntentFilter changeFilter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);

        mContext.registerReceiver(blueToothBroadcastReceiver, startFilter);
        mContext.registerReceiver(blueToothBroadcastReceiver, foundFilter);
        mContext.registerReceiver(blueToothBroadcastReceiver, endFilter);
        mContext.registerReceiver(blueToothBroadcastReceiver, changeFilter);
        return true;
    }

    //搜索
    public void findDevices() {
        mAdapter.startDiscovery();
    }

    //记得unregister
    public boolean unregisterReceiver() {
        mContext.unregisterReceiver(blueToothBroadcastReceiver);
        return true;
    }

    class BlueToothBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    deveicesDescoved = new ArrayList<>();
                    deveicesDescovedNames = new ArrayList<>();
                    Log.i(tag, "receiver action started");
                    break;
                case BluetoothDevice.ACTION_FOUND:
                    Log.i(tag, "receiver action found");
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    deveicesDescoved.add(device);
                    deveicesDescovedNames.add(device.getName());
//                    Log.i(tag, device.getName());
                    if (device.getName().equalsIgnoreCase(deveicename)) {
                        mAdapter.cancelDiscovery();
                        connectState = device.getBondState();
                        switch (connectState) {
                            // 未配对
                            case BluetoothDevice.BOND_NONE:
                                Log.i(tag, "found a unbond Deveice");
                                try {
                                    Method createBondMethod = BluetoothDevice.class.getMethod("createBond");
                                    createBondMethod.invoke(device);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            // 已配对
                            case BluetoothDevice.BOND_BONDED:
                                Log.i(tag, "found a bonded Deveice");
                                try {
                                    // 连接
                                    connect(device);
                                    ChoseDeveiceDialog.dismissDialog();
                                    unregisterReceiver();
                                    Toast.makeText(context, "蓝牙连接成功", t).show();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                break;
                        }
                    }
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    Log.i(tag, "receiver action finished");
                    ChoseDeveiceDialog.dimissProgressBar();
                    break;

                case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                    Log.i(tag, "receiver action bondstate Changed");
                    BluetoothDevice deviceChanged = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (deviceChanged.getName().equalsIgnoreCase(deveicename)) {
                        connectState = deviceChanged.getBondState();
                        switch (connectState) {
                            case BluetoothDevice.BOND_NONE:
                                break;
                            case BluetoothDevice.BOND_BONDING:
                                break;
                            case BluetoothDevice.BOND_BONDED:
                                try {
                                    // 连接
                                    connect(deviceChanged);

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                break;
                        }
                        break;
                    }
            }
        }

    }

    public void connect(BluetoothDevice device) throws IOException {
        // 固定的UUID
//            UUID uuid = aboutPhone.getUUID(mContext);
        String uuid = "00001101-0000-1000-8000-00805F9B34FB";
        BluetoothSocket socket = device.createRfcommSocketToServiceRecord(UUID.fromString(uuid));
        socket.connect();
    }

    int getConnectState() {
        return connectState;
    }

    public static ArrayList<BluetoothDevice> getDeveicesBonded() {
        return deveicesBonded;
    }

    public static void setDeveicesBonded(ArrayList<BluetoothDevice> deveicesBonded) {
        BlueToothManager.deveicesBonded = deveicesBonded;
    }

    public static ArrayList<String> getDeveicesBondedNames() {
        return deveicesBondedNames;
    }

    public static void setDeveicesBondedNames(ArrayList<String> deveicesBondedNames) {
        BlueToothManager.deveicesBondedNames = deveicesBondedNames;
    }

    public ArrayList<BluetoothDevice> getDeveicesDescoved() {
        return deveicesDescoved;
    }

    public ArrayList<String> getDeveicesDescovedNames() {
        return deveicesDescovedNames;
    }

    public void setDeveicesDescoved(ArrayList<BluetoothDevice> deveicesDescoved) {
        this.deveicesDescoved = deveicesDescoved;
    }

    public void setDeveicesDescovedNames(ArrayList<String> deveicesDescovedNames) {
        this.deveicesDescovedNames = deveicesDescovedNames;
    }

}