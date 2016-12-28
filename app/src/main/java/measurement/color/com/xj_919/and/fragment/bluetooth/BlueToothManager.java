package measurement.color.com.xj_919.and.fragment.bluetooth;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import measurement.color.com.xj_919.and.Utils.T;
import measurement.color.com.xj_919.and.activity.app;
import measurement.color.com.xj_919.and.Utils.SP.Consts;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by wpc on 2016/9/20.
 */
public class BlueToothManager {

    private static Activity mContext;

    public static BlueToothPrinter mBlueToothPrinter;

    private static BlueToothManager instance;
    public static BluetoothAdapter mAdapter;
    static BluetoothDevice mmDevice;

    public static BluetoothSocket client = null;// 打印机操作类
    static OutputStream mmOutputStream;
    static InputStream mmInputStream;

    static volatile boolean connect_is_stoped;
    private static final String tag = "BlueToothManager";
    private final int t = Toast.LENGTH_SHORT;


    private static Button mPrint;
    private static ChoseDeveiceDialog dialog;

    static SharedPreferences sharedPreferences;


    private BlueToothManager() {
    }

    public static BlueToothManager getInstance(@Nullable Activity context, @Nullable Button print) {
        if (instance != null) {
            return instance;
        } else {
            mPrint = print;
            mContext = context;
            mAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mAdapter == null) {
                Toast.makeText(mContext, "设备不支持蓝牙", Toast.LENGTH_SHORT).show();
            }
//            if (!mAdapter.isEnabled()) {
//                mAdapter.enable();
//            }
            sharedPreferences = mContext.getSharedPreferences(Consts.SYSTEM_SETTING_PREFERENCE, MODE_PRIVATE);
        }
        return instance;
    }

    public static BlueToothManager getInstance() {
        if (instance != null) {
            return instance;
        }
        return new BlueToothManager();
    }

    public boolean startDescovery() {
        if (mAdapter == null) {
            mAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        mAdapter.startDiscovery();
        return true;
    }

    //已连接过的列表
    public static ArrayList<BluetoothDevice> getDevices() {
        ArrayList<BluetoothDevice> boundedlist = new ArrayList<>();
        if (mAdapter != null) {
            Set<BluetoothDevice> Devices = mAdapter.getBondedDevices();
            if (Devices.size() != 0) {
                Log.i("getDevices_size", Devices.size() + "");
                Iterator<BluetoothDevice> iterator = Devices.iterator();
                while (iterator.hasNext()) {
                    BluetoothDevice device = iterator.next();
                    boundedlist.add(device);
                }
            }
        } else {
            Log.i(tag, "madapter为空");
            return null;
        }
        return boundedlist;
    }

    //搜索
    public void findDevices() {

        mAdapter.startDiscovery();
    }

    public void stopDiscover() {
        mAdapter.cancelDiscovery();
    }

    public void removeDeveiceFromBoundList(BluetoothDevice device) {
        try {
            Method m = device.getClass()
                    .getMethod("removeBond", (Class[]) null);
            m.invoke(device, (Object[]) null);
        } catch (Exception e) {
            Log.e("removeBound", e.getMessage());
        }
    }

    /**
     * 外部使用 manager实例调用
     * 打开蓝牙  搜索某个设备  找到即打开
     *
     * @param print
     */
    public void connect(Button print) {
        if (findBT()) {
            openBT(mmDevice, print);
            if (dialog.connectState == 12) {
                mBlueToothPrinter = new BlueToothPrinter(mContext, client, mmOutputStream);
            }
        } else {
            showChoseDeveiceDialog();
        }
    }

    public boolean connectBlueToothPrinter(Button print) {
        if (findBT()) {
            openBT(mmDevice, print);
            if (dialog.connectState == 12) {
                mBlueToothPrinter = new BlueToothPrinter(mContext, client, mmOutputStream);
                return true;
            }
        } else {
            showChoseDeveiceDialog();
            return false;
        }
        return false;
    }

    public static void showChoseDeveiceDialog() {
        if (dialog == null) {
            dialog = new ChoseDeveiceDialog(mContext, mPrint);
        }
        dialog.show(((Activity) mContext).getFragmentManager(), "DescoverDriver");
    }

    public static boolean findBT() {
        try {
            mAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mAdapter == null) {
                Log.i(tag, "No bluetooth adapter available");
            }
            if (!mAdapter.isEnabled()) {
                Intent enableBluetooth = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE);
                ((Activity) mContext).startActivityForResult(enableBluetooth, 0);
            }
            if (findDeveiceInBoundList(app.PRINTER_NAME, app.ADDRESS) != null) {
                Log.i("findBT", "found");
                return true;
            } else {
                Log.i("findBT", "no found any deveice name is" + app.PRINTER_NAME);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 在已连接的 蓝牙设备里 搜索是否有 以下设备
     *
     * @param deveiceName  设备名
     * @param deveiceAcess 地址  为空则返回列表最后一个 同名设备
     * @return
     */
    static BluetoothDevice findDeveiceInBoundList(String deveiceName, @Nullable String deveiceAcess) {

        Set<BluetoothDevice> pairedDevices = mAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                if (device.getName().equals(deveiceName)) {
                    if (deveiceAcess != null) {
                        if (device.getAddress().equals(deveiceAcess)) {
                            mmDevice = device;
                            Log.i("发现设备", device.getName() + device.getAddress());
                            return mmDevice;
                        } else {
                            Log.i("发现同名设备", device.getName() + device.getAddress());
                        }
                    }
                    mmDevice = device;
                }
            }
            return mmDevice;
        }
        return null;
    }

    public void openBT(final BluetoothDevice deveice, @Nullable final Button bt) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
        Log.i(tag, deveice.getName());
        if (deveice.getName().equals(app.PRINTER_NAME)) {
            UUID uuid = UUID.fromString(app.uuid);

            try {
                client = deveice.createRfcommSocketToServiceRecord(uuid);
                client.connect();
                mmOutputStream = client.getOutputStream();
                mmInputStream = client.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
                dialog.connectState = 10;
                T.show(mContext, "设备未打开");
                if (bt != null) {
                    bt.setText("连接出错");
                }
            }
            if (client.isConnected()) {
                beginListenForData();
                dialog.connectState = 12;
                dialog.dismiss();
                Log.i(tag, "Bluetooth Opened");
                if (bt != null) {
                    bt.setText("打印");
                }
            } else {
                showChoseDeveiceDialog();
                try {
                    closeBT();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }


//            }
//        }).start();


    @TargetApi(Build.VERSION_CODES.ECLAIR)
    public static void closeBT() throws IOException {
        try {
            connect_is_stoped = true;
            mmOutputStream.close();
            mmInputStream.close();
            client.close();
            Log.i(tag, "Bluetooth Closed");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static byte[] readBuffer;
    static int readBufferPosition;

    static void beginListenForData() {

        final Thread workerThread;
        try {
            final Handler handler = new Handler();
            // this is the ASCII code for a newline character
            final byte delimiter = 10;

            connect_is_stoped = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];

            workerThread = new Thread(new Runnable() {
                public void run() {

                    while (!Thread.currentThread().isInterrupted()
                            && !connect_is_stoped) {

                        try {

                            int bytesAvailable = mmInputStream.available();

                            if (bytesAvailable > 0) {

                                byte[] packetBytes = new byte[bytesAvailable];
                                mmInputStream.read(packetBytes);

                                for (int i = 0; i < bytesAvailable; i++) {

                                    byte b = packetBytes[i];
                                    if (b == delimiter) {

                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(readBuffer, 0,
                                                encodedBytes, 0,
                                                encodedBytes.length);

                                        // specify US-ASCII encoding
                                        final String data = new String(
                                                encodedBytes, "ISO8859_1");
                                        readBufferPosition = 0;

                                        // tell the user data were sent to
                                        // bluetooth printer device
                                        handler.post(new Runnable() {
                                            public void run() {
                                                Log.i(tag, data);
                                            }
                                        });

                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }

                        } catch (IOException ex) {
                            connect_is_stoped = true;
                        }

                    }
                }
            });

            workerThread.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class BlueToothPrinter {

        private Context context;
        private OutputStream mOutputStream;
        BluetoothSocket printersocker;

        BlueToothPrinter(Context context, BluetoothSocket socket, OutputStream outputStream) {
            this.printersocker = socket;
            this.context = context;
            mOutputStream = outputStream;
        }


        // 给打印机 发送数据
        @SuppressLint("NewApi")
        private void setPrinterFormet(byte[] mess) {
            if (printersocker == null || !printersocker.isConnected()) {
                Toast.makeText(context, "没有连接", Toast.LENGTH_SHORT)
                        .show();
                return;
            }

            try {
                OutputStream os = printersocker.getOutputStream();
                os.write(mess);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ArrayList<String> getPrintListWithSetting(SQLiteDatabase db, int id, boolean withSetting) {
            ArrayList<String> list = new ArrayList<>();
            Cursor c = db.query(app.SQLite_LOG_rgb, null, "id = ?", new String[]{id + ""}, null, null, null);
            Log.i("Cursor count", c.getCount() + "");
            if (c.moveToFirst()) {
                do {
                    if (withSetting) {
                        list.add(sharedPreferences.getString(Consts.KEY_PRINT_START, "<<--Start-->>"));
                    } else {
                        list.add("<<--Start-->>");
                    }

                    list.add("序列号:" + c.getInt(c.getColumnIndex("id")) + "");
                    if (withSetting) {
                        if (sharedPreferences.getBoolean(Consts.KEY_TIME, false)) {
                            list.add("检测时间:" + c.getString(c.getColumnIndex("time")));
                        }
                    } else {
                        list.add("检测时间:" + c.getString(c.getColumnIndex("time")));
                    }
                    list.add("数据:");
                    if (withSetting) {
                        if (sharedPreferences.getBoolean(Consts.KEY_R1, false)) {
                            list.add("R1:" + c.getInt(c.getColumnIndex("R1T")) + "");
                        }
                    } else {
                        list.add("R1:" + c.getInt(c.getColumnIndex("R1T")) + "");
                    }
                    if (withSetting) {
                        if (sharedPreferences.getBoolean(Consts.KEY_G1, false)) {
                            list.add("G1:" + c.getInt(c.getColumnIndex("G1T")) + "");
                        }
                    } else {
                        list.add("G1:" + c.getInt(c.getColumnIndex("G1T")) + "");
                    }
                    if (withSetting && sharedPreferences.getBoolean(Consts.KEY_B1, false)) {
                        list.add("B1:" + c.getInt(c.getColumnIndex("B1T")) + "");
                    }
//            sendData(c.getString(c.getColumnIndex("result")));
                    if (withSetting) {
                        list.add(sharedPreferences.getString(Consts.KEY_PRINT_START, "<<--End-->>"));
                    } else {
                        list.add("<<--End-->>");
                    }
                } while (c.moveToNext());
            }

            return list;
        }

        public void printDataByID(SQLiteDatabase db, int id, @Nullable Button bt, boolean withSetting) {
            try {
                printArray(getPrintListWithSetting(db, id, withSetting));
            } catch (IOException e) {
                e.printStackTrace();
                if (bt != null) {
                    bt.setText("发现设备");
                }
            }
        }

        public void print(String str) throws IOException {
            synchronized (str) {
                try {
                    str += "\n";
                    mOutputStream.write(str.getBytes("gb2312"));
                    // tell the user data were sent
                    Log.i(tag, "Data sent.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        public void printArray(ArrayList<String> str) throws IOException {
            synchronized (str) {
                try {
                    for (String string : str) {
                        string += "\n";
                        mOutputStream.write(string.getBytes("gb2312"));
                        // tell the user data were sent
                        Log.i(tag, "Data sent.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    closeBT();
                }
            }
        }

        //蓝牙打印机 指令
        static final int DY_Default = 0;//默认设置
        static final int DY_Vison = 1; //获取版本号
        static final int DY_BigText = 2;//大字体
        static final int DY_SmallText = 3;//小字体
        static final int DY_NotBold = 4;//不加粗
        static final int DY_Bold = 5;//加粗
        static final int DY_Left = 6;//左对齐
        static final int DY_Right = 7;//右对齐
        static final int DY_Center = 8;//居中对齐
        static final int DY_Pic = 9;//打印图片
        static final int DY_Hdouble = 10;//纵向拉伸两倍
        static final int DY_Alldouble = 11;//字体整体放大两倍
        static final int DY_Linespace = 12;//行间距

        private byte[] getByteArrayForFormat(int tag) {
            byte[] returnbuf = new byte[1];
            returnbuf[0] = 0x00;
            if (tag == DY_Default) {
                byte[] sendbuff = new byte[2];
                sendbuff[0] = 0x1B;
                sendbuff[1] = 0x40;
                return sendbuff;
            } else if (tag == DY_Vison) {//获取蓝牙打印版本号
                byte[] sendbuff = new byte[2];
                sendbuff[0] = 0x1B;
                sendbuff[1] = 0x2C;
                return sendbuff;
            } else if (tag == DY_BigText) {//设置打印字体为 大字体
                byte[] sendbuff = new byte[3];
                sendbuff[0] = 0x1B;
                sendbuff[1] = 0x4D;
                sendbuff[2] = 0x00;
                return sendbuff;
            } else if (tag == DY_SmallText) {//设置打印字体为 小字体
                byte[] sendbuff = new byte[3];
                sendbuff[0] = 0x1B;
                sendbuff[1] = 0x4D;
                sendbuff[2] = 0x01;
                return sendbuff;
            } else if (tag == DY_NotBold) {//不加粗
                byte[] sendbuff = new byte[3];
                sendbuff[0] = 0x1B;
                sendbuff[1] = 0x45;
                sendbuff[2] = 0x00;
                return sendbuff;
            } else if (tag == DY_Bold) {//加粗
                byte[] sendbuff = new byte[3];
                sendbuff[0] = 0x1B;
                sendbuff[1] = 0x45;
                sendbuff[2] = 0x01;
                return sendbuff;
            } else if (tag == DY_Left) {
                byte[] sendbuff = new byte[3];
                sendbuff[0] = 0x1B;
                sendbuff[1] = 0x61;
                sendbuff[2] = 0x00;
                return sendbuff;
            } else if (tag == DY_Right) {
                byte[] sendbuff = new byte[3];
                sendbuff[0] = 0x1B;
                sendbuff[1] = 0x61;
                sendbuff[2] = 0x02;
                return sendbuff;
            } else if (tag == DY_Center) {
                byte[] sendbuff = new byte[3];
                sendbuff[0] = 0x1B;
                sendbuff[1] = 0x61;
                sendbuff[2] = 0x01;
                return sendbuff;
            } else if (tag == DY_Pic) {
                byte[] sendbuff = new byte[4];
                sendbuff[0] = 0x1F;
                sendbuff[1] = 0x10;
                sendbuff[2] = 0x30;
                sendbuff[3] = 0x00;
                return sendbuff;
            } else if (tag == DY_Hdouble) {
                byte[] sendbuff = new byte[3];
                sendbuff[0] = 0x1D;
                sendbuff[1] = 0x21;
                sendbuff[2] = 0x01;
                return sendbuff;
            } else if (tag == DY_Alldouble) {
                byte[] sendbuff = new byte[3];
                sendbuff[0] = 0x1D;
                sendbuff[1] = 0x21;
                sendbuff[2] = 0x11;
                return sendbuff;
            } else if (tag == DY_Linespace) {
                byte[] sendbuff = new byte[3];
                sendbuff[0] = 0x1B;
                sendbuff[1] = 0x33;
                sendbuff[2] = 0x02;
                return sendbuff;
            }
            return returnbuf;
        }
    }


}