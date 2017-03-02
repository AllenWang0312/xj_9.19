package measurement.color.com.xj_919.and.fragment.Test;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import measurement.color.com.xj_919.and.Utils.DB.MyDBHelper;
import measurement.color.com.xj_919.and.Utils.SP.Consts;
import measurement.color.com.xj_919.and.Utils.SP.SharePreferenceHelper;
import measurement.color.com.xj_919.and.Utils.T;
import measurement.color.com.xj_919.and.Utils.io.FileAndPath;
import measurement.color.com.xj_919.and.Utils.io.FileUtils;
import measurement.color.com.xj_919.and.Utils.soft.ImageUtils;
import measurement.color.com.xj_919.and.Utils.soft.MathUtil;
import measurement.color.com.xj_919.and.Utils.soft.clsPublic;
import measurement.color.com.xj_919.and.activity.app;


/**
 * Created by wpc on 2016/9/19.
 */
public class USBManager {

    //-1设备不支持usbhost，
    // 0usbmanager可用
    // 1derive可用
    // 2interface可用
    // 3endpoint可用/可以发送拍照请求
    private static int state = -1;
    public static boolean printable = false;

    //    static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";// 权限
    private static USBManager instance;
    public USBDataTransceiver TransceiverInstance;
    private final String tag = "USBManager";

    public Bitmap bitmap;

    //usb相关
    static Activity mContext;
    static private UsbManager mUsbManager;
    private static usbBroadcastReceiver myUSBReceiver;
    private static UsbDevice mUsbDevice;
    private static UsbInterface mInterface;
    private static UsbDeviceConnection mUsbDeviceConnection;

    private UsbEndpoint epBulkOut, epControl, epIntEndpointOut, epIntEndpointIn, epBulkIn;

    public static USBManager getInstance(Activity context) {
        if (instance == null) {
            instance = new USBManager(context);
        }
        return instance;
    }

    public static USBManager getInstance() {
        return instance;
    }

    private USBManager(Activity context) {
        mContext = context;
    }

    public int getState() {
        return state;
    }

    boolean hasInit = false;

    public boolean init() {
        if (findDevice(1155, 22336)) {
            if (findInterface(10, 0, 0)) {
                if (findPoint()) {
                    printable = true;
                    hasInit = true;
                    return true;
                } else {
                    T.showInTop(mContext, "没有发现端点");
                }
            } else {
                T.showInTop(mContext, "没有发现接口");
            }
        } else {
            T.showInTop(mContext, "没有发现设备");
        }

        return false;
    }

    public boolean isHasInit() {
        return hasInit;
    }


    public static USBManager init(Context context) {
        if (mUsbManager == null) {
            mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        }
        if (mUsbManager == null) {
            T.showInTop(context, "usb不可用");
        } else {
            state = 0;
        }
        return instance;
    }

    public boolean findDevice(int Vendorid, int productid) {
        if (mUsbManager != null) {
            final HashMap<String, UsbDevice> mdrivers = mUsbManager.getDeviceList();
            Iterator<UsbDevice> deviceIterator = mdrivers.values().iterator();
            while (deviceIterator.hasNext()) {
                UsbDevice device = deviceIterator.next();
                if (device.getVendorId() == Vendorid && device.getProductId() == productid) {
                    this.mUsbDevice = device;
                    state = 1;
                    return true;
                }
            }
        }
        return false;
    }

    public boolean findInterface(int interfaceClass, int interfaceSubclass, int interfaceProtocol) {
        if (mUsbDevice != null) {
            int cont = mUsbDevice.getInterfaceCount();
            for (int i = 0; i < cont; i++) {
                UsbInterface Intf = mUsbDevice.getInterface(i);
                if (Intf.getInterfaceClass() == interfaceClass && Intf.getInterfaceSubclass() == interfaceSubclass && Intf.getInterfaceProtocol() == interfaceProtocol) {
                    mInterface = Intf;
                    Log.i("findInterface", "true");
                    state = 2;
                    return true;
                }
            }
        }
        return false;
    }

    public boolean findPoint() {
        if (mInterface != null) {
            for (int j = 0; j < mInterface.getEndpointCount(); j++) {
                UsbEndpoint ep = mInterface.getEndpoint(j);
                if (ep.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
//                    USB_ENDPOINT_XFER_BULK
//                            批量端点类型
                    if (ep.getDirection() == UsbConstants.USB_DIR_OUT) {
                        Log.i(tag, "Find the BulkEndpointOut,index:" + j + ",使用断点号:" + ep.getEndpointNumber());
                        epBulkOut = ep;
                        Log.i("epBulkOut", epBulkOut.toString());
                    } else {
                        epBulkIn = ep;
                        Log.i(tag, "Find the BulkEndpointIn,index:" + j + ",使用断点号:" + ep.getEndpointNumber());
                    }

                    if (epBulkOut != null && epBulkIn != null) {
                        state = 3;
                        Log.i("findPoint", "true");
                        if (TransceiverInstance == null) {
                            TransceiverInstance = new USBDataTransceiver();
                        }
                        return true;
                    } else {
                        state = 0;
                    }
                }
                if (ep.getType() == UsbConstants.USB_ENDPOINT_XFER_CONTROL) {
//                    USB_ENDPOINT_XFER_CONTROL
//                    控制端点类型（端点零）
                    epControl = ep;
                    Log.i(tag, "find the ControlEndPoint:" + "index:" + j
                            + "," + epControl.getEndpointNumber());
                }
                if (ep.getType() == UsbConstants.USB_ENDPOINT_XFER_INT) {
                    if (ep.getDirection() == UsbConstants.USB_DIR_OUT) {
                        epIntEndpointOut = ep;
                        Log.i(tag, "find the InterruptEndpointOut:"
                                + "index:" + j + ","
                                + epIntEndpointOut.getEndpointNumber());
                    }
                    if (ep.getDirection() == UsbConstants.USB_DIR_IN) {
//                        USB_ENDPOINT_XFER_INT
//                                中断端点类型
                        epIntEndpointIn = ep;
                        Log.i(tag, "find the InterruptEndpointIn:"
                                + "index:" + j + ","
                                + epIntEndpointIn.getEndpointNumber());
                    }
                }
            }
        }
        return false;
    }

    public static class usbBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("onReceive", intent.getAction());

            if (intent.getAction().equals("android.hardware.usb.action.USB_DEVICE_ATTACHED")) {
//                mUsbDevice = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                TestFragment.setButton("检测", true);
                state = 0;
            } else if (intent.getAction().equals("android.hardware.usb.action.USB_DEVICE_DETACHED")) {
                state = -1;
                printable = false;
                mUsbDeviceConnection = null;
                TestFragment.setButton("连接错误", true);
            }
        }
    }

    public static void registerReceiver(Context context) {
        myUSBReceiver = new usbBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.setPriority(800);
        intentFilter.addAction("android.hardware.usb.action.USB_STATE");
        intentFilter.addAction("android.hardware.usb.action.USB_DEVICE_ATTACHED");
        intentFilter.addAction("android.hardware.usb.action.USB_DEVICE_DETACHED");
        context.registerReceiver(myUSBReceiver, intentFilter);
    }

    public static void unregisterReceiver(Context context) {
        context.unregisterReceiver(myUSBReceiver);
    }


    public class USBDataTransceiver {

        private final String tag = "USBDataTransceiver";
        private byte[] Receiveytes;  //接收信息字节
        private int num_onceArrive = 4000;
        //图片数据
        public final short length = 640;
        public final short height = 480;
        private final byte content = 1;

        private final short times = (short) ((2 * length * height / content) / num_onceArrive);

        private byte[] part1 = new byte[(length * height / content) * 2];

        //        private int[] GRAY = new int[(length * height / content)];//源数据 10位
        private short[] gray = new short[(length * height / content)];//RGB支持的8位
        public int[] arr = new int[(length - 1) * (height - 1)];//计算过后的图片数组
        //        public int[] arr = new int[(length / 2) * (height / 2)];//计算过后的图片数组
        //数据库db相关
        private MyDBHelper dbHelper;
        private SQLiteDatabase db;


        private USBDataTransceiver() {
            dbHelper = new MyDBHelper(mContext, app.SQLite_LOG, null, 1);
            db = dbHelper.getWritableDatabase();
        }


        public boolean checkPermission() {
            // 判断是否有权限
            if (mUsbDeviceConnection == null) {
                if (mUsbManager.hasPermission(mUsbDevice)) {
                    Log.i("has permission", "true");
                    // 打开设备，获取 UsbDeviceConnection 对象，连接设备，用于后面的通讯
                    mUsbDeviceConnection = mUsbManager.openDevice(mUsbDevice);
                    if (mUsbDeviceConnection == null) {
                        T.showInTop(mContext, "连接出错");
                        return false;
                    }
                    if (mUsbDeviceConnection.claimInterface(mInterface, true)) {
                        //用UsbDeviceConnection 与 UsbInterface 进行端点设置和通讯'
//                        sendVolentRequest();
                        Log.i("claimInterface", "true");
                        return true;
                    } else {
                        mUsbDeviceConnection.close();
                    }
                }
                T.showInTop(mContext, "连接出错");
                Log.i("has permission", "没有权限");
            } else {
                return true;
            }
            return false;
        }

        public byte[] sendTakeConfigRequest() {
            if (checkPermission()) {
                printable = false;
                byte[] arr = UsbOrder.getTakeConfigOrder();
                int i = mUsbDeviceConnection.bulkTransfer(epBulkOut, arr, arr.length, 1000);
                Receiveytes = new byte[1000];
                int j = mUsbDeviceConnection.bulkTransfer(epBulkIn, Receiveytes, Receiveytes.length, 1000);
                Log.i("sendTakeConfigRequest" + i + "" + j + "" + Receiveytes.length, clsPublic.bytesToHexString(Receiveytes));
                return Receiveytes;
            } else {
                return null;
            }
        }

        public byte[] sendVolentRequest() {
//            checkPermission();
            printable = false;
            byte[] arr = UsbOrder.getVolentOrder();
            int i = mUsbDeviceConnection.bulkTransfer(epBulkOut, arr, arr.length, 1000);
            Receiveytes = new byte[220];
            int j = mUsbDeviceConnection.bulkTransfer(epBulkIn, Receiveytes, Receiveytes.length, 220);
//            Log.i("sendTakeConfigRequest" + i + "" + j + "" + Receiveytes.length, clsPublic.bytesToHexString(Receiveytes));
            return Receiveytes;
        }

        public boolean sendTurnDownRequest() {
//            checkPermission();
            byte[] arr = UsbOrder.getTurnDownOrder();
            int i = mUsbDeviceConnection.bulkTransfer(epBulkOut, arr, arr.length, 200);
            Receiveytes = new byte[220];
            int j = mUsbDeviceConnection.bulkTransfer(epBulkIn, Receiveytes, Receiveytes.length, 220);
            Log.i("sendTurnDownRequest", j + "-" + i + "->>" + clsPublic.bytesToHexString(Receiveytes));
            if (j == 220) {
                return true;
            }
            return false;
        }

        // 1 有权限 2连接断开
        public boolean sendTakePhotoRequest(byte position, int delay) {
            if (checkPermission()) {
                printable = false;
                byte[] arr = UsbOrder.getTakePhotoOrder(getPosition(position), delay);
                int i = mUsbDeviceConnection.bulkTransfer(epBulkOut, arr, arr.length, 0);
                if (i == -1) {
                    return false;
                }
                Receiveytes = new byte[55];
                int j = mUsbDeviceConnection.bulkTransfer(epBulkIn, Receiveytes, Receiveytes.length, 0);//55成功
                return true;
            } else {
                return false;
            }
        }

        public void sendNotifyUserRequest(boolean redLight) {
//            checkPermission();
            byte[] arr = UsbOrder.getNotifyUserOrder(redLight);
            int i = mUsbDeviceConnection.bulkTransfer(epBulkOut, arr, arr.length, 0);
            Receiveytes = new byte[220];
            int j = mUsbDeviceConnection.bulkTransfer(epBulkIn, Receiveytes, Receiveytes.length, 0);
            if (Receiveytes[4] == 1) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("提示").setMessage("电量不足").setPositiveButton("确定", null).create().show();
            }
        }

        public void sendGetDataRequest(short index) {
            checkPermission();
            mUsbDeviceConnection.bulkTransfer(epBulkOut, UsbOrder.getDataOrder(index), 55, 200);
            Receiveytes = new byte[4020];
            mUsbDeviceConnection.bulkTransfer(epBulkIn, Receiveytes, 4020, 0);
            byte[] data = new byte[4000];
            System.arraycopy(Receiveytes, 9, data, 0, 4000);
            System.arraycopy(data, 0, part1, index * 4000, 4000);
//            Log.i("4000" + (int) index, clsPublic.bytesToHexString(data));
        }

        public void printStringWithUSB(int id) {
            checkPermission();
            Print(getPrintStrings(id));
        }

        public ArrayList<String> getPrintStrings(int id) {
//            int lineLength = 60;
            ArrayList<String> strs = new ArrayList<>();
            Cursor c = db.rawQuery("select * from rgb where id = ?", new String[]{id + ""});
            if (c.moveToFirst()) {
                do {
                    strs.add("日期:" + c.getString(c.getColumnIndex("data")) + "\r\n");
                    strs.add("时间:" + c.getString(c.getColumnIndex("time")) + "\r\n");
                    for (int j = 6; j > 0; j--) {
                        if ((c.getString(c.getColumnIndex("result" + j))).equals("阳性")) {
                            String content = "区域" + j + ":发现" + c.getString(c.getColumnIndex("detial" + j)) + "\r\n";
//                            if (content.length() > lineLength) {
//                                for (int i = 0; (i + 1) * lineLength > content.length(); i++) {
//                                    strs.add(new String(content.getBytes(), i, lineLength));
////                                clsPublic.insertStringInParticularPosition(content, "\r\n", i * 16);
//                                }
//                            } else {
                            strs.add(content);
//                            }

//                            strs.add(content);
                        }
                    }
                    strs.add("结果:" + c.getString(c.getColumnIndex("result")) + "\r\n");
                } while (c.moveToNext());
            }
            Collections.reverse(strs);
            return strs;
        }

        public void Print(ArrayList<String> strs) {
            for (int i = strs.size() - 1; i >= 0; i--) {
                try {
                    byte[] arr = strs.get(i).getBytes("gbk");
                    mUsbDeviceConnection.bulkTransfer(epBulkOut, getAppendArray(arr, i), 55, 200);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Receiveytes = new byte[220];
                mUsbDeviceConnection.bulkTransfer(epBulkIn, Receiveytes, 220, 0);
                if (Receiveytes[3] == 0) {
                    Log.i("receiveytes[3]==0", "requeset fault");
                    break;
                }
            }
            mUsbDeviceConnection.bulkTransfer(epBulkOut, getPrintArray(strs.size()), 55, 200);
            Receiveytes = new byte[220];
            mUsbDeviceConnection.bulkTransfer(epBulkIn, Receiveytes, 220, 0);
            Log.i("receiveytes", clsPublic.bytesToHexString(Receiveytes));
        }

        byte[] getAppendArray(byte[] str_bytes, int i) {
            byte[] arr = new byte[55];
            arr[0] = 0x55;
            arr[1] = 0x00;
            arr[2] = 0x03;
            arr[3] = (byte) i;
            arr[4] = 0x00;//0 添加数据 1 打印
            Log.i("bytesLength", str_bytes.length + "");
            System.arraycopy(str_bytes, 0, arr, 5, str_bytes.length);
            return arr;
        }

        byte[] getPrintArray(int nums) {
            byte[] arr = new byte[55];
            arr[0] = 0x55;
            arr[1] = 0x00;
            arr[2] = 0x03;
            arr[3] = (byte) nums;//打印多少条
            arr[4] = 0x01;//添加数据 1 打印
            return arr;
        }

        boolean judgePhotoEnable(short[] gray, int G) {
            int mean = MathUtil.getShortsMean(gray);
            Log.i("mean", "灰度平均值" + mean);
            if (mean < G && mean > 0) {
                return false;
            }
            return true;
        }

        public int LoadDate(int test_mod, @Nullable int lightheight) {
            byte[] part0 = new byte[(length * height / content) * 2];
            syncInt();
            for (short i = (short) 0; i < times; i++) {
                sendGetDataRequest(i);
            }
//            mUsbDeviceConnection.releaseInterface(mInterface);
//            mUsbDeviceConnection.close();
            if (test_mod == 0) {
                try {
                    part0 = FileUtils.toByteArray(FileAndPath.IMG_NATIVE_DATA, FileAndPath.IMG_BAIBAN_NAME + ".cache");
                } catch (FileNotFoundException e) {

                    e.printStackTrace();
                }
                if (part0 != null) {
                    for (int i = 0; i < part1.length; i += 2) {
//                GRAY[i / 2] = part1[i] + ((part1[i + 1] & 0xff) << 8);
                        short bz = (short) (((part0[i] & 0xff) >> 2) + ((part0[i + 1] & 0xff) << 6));
                        if (bz != 0) {
                            gray[i / 2] = (short) (
                                    (((part1[i] & 0xff) >> 2) + ((part1[i + 1] & 0xff) << 6))
                                            * 230
                                            / bz

                            );
                            if (gray[i / 2] > 250) {
                                gray[i / 2] = 250;
                            }
                        }
//                        } else {
//                            gray[i / 2] = (short) (((part1[i] & 0xff) >> 2) + ((part1[i + 1] & 0xff) << 6));
//                        }
                    }
                } else {
                    for (int i = 0; i < part1.length; i += 2) {
//                GRAY[i / 2] = part1[i] + ((part1[i + 1] & 0xff) << 8);
                        gray[i / 2] = (short) (((part1[i] & 0xff) >> 2) + ((part1[i + 1] & 0xff) << 6));
                        if (gray[i / 2] > 250) {
                            gray[i / 2] = 250;
                        }
                    }
                    T.showInTop(mContext, "请先录入白板信息");
                    return -2;
                }
                if (!judgePhotoEnable(gray, 100)) {
                    return 0;
                }
            } else {
                for (int i = 0; i < part1.length; i += 2) {
//                GRAY[i / 2] = part1[i] + ((part1[i + 1] & 0xff) << 8);
                    gray[i / 2] = (short) (((part1[i] & 0xff) >> 2) + ((part1[i + 1] & 0xff) << 6));
                    if (gray[i / 2] > 250) {
                        gray[i / 2] = 250;
                    }
                }
            }

            //白平衡---
//            long R = 0, G = 0, B = 0;
//            for (short i = 0; i < 480; i++) {
//                for (short j = 0; j < 640; j++) {
//                    int a = i * 640 + j;
//                    if (i % 2 == 0) {
//                        if (j % 2 == 0) {
//                            B += gray[a];
//                        } else {
//                            G += gray[a];
//                        }
//                    } else {
//                        if (j % 2 == 0) {
//                            G += gray[a];
//                        } else {
//                            R += gray[a];
//                        }
//                    }
//                }
//            }
//            float g_ = ((float) G) / 163200;
//            float r_p = g_ / (((float) R) / 81600);
//            float b_p = g_ / (((float) B) / 81600);
//            for (short i = 0; i < 480; i++) {
//                for (short j = 0; j < 640; j++) {
//                    int a = i * 640 + j;
//                    if (i % 2 == 0) {
//                        if (j % 2 == 0) {
//                            gray[a] *= b_p;
//                        }
//                    } else {
//                        if (j % 2 != 0) {
//                            gray[a] *= r_p;
//                        }
//                    }
//                }
//            }

            //缺一行一列 //曝光平衡
//            boolean ph = SharePreferenceHelper.getInstance().getBoolean(Consts.EXPOSURE, false);
            boolean ph = false;
            for (short i = 0; i < 479; i++) {
                for (short j = 0; j < 639; j++) {
                    int p = i * 639 + j;
                    int a = p + i, b = a + 1, c = a + 640, d = c + 1;
//                    if (ph) {
//                        float f = (float) Math.pow((Math.pow(j - 320, 2) + Math.pow(i - 240, 2) + Math.pow(lightheight, 2)) / (Math.pow(80, 2) + Math.pow(lightheight, 2)), 0.5);
////                        float f = (float) (Math.pow((Math.pow(j - 80, 2) + Math.pow(i - 240, 2) + Math.pow(j - 560, 2) + Math.pow(i - 240, 2) + 2 * Math.pow(lightheight, 2)), 0.5) / Math.pow((2 * Math.pow(240, 2) + 2 * Math.pow(lightheight, 2)), 0.5));
//                        if (i % 2 == 0) {
//                            if (j % 2 == 0) {
//                                arr[p] = 0xff000000 + (((int) (gray[d] * f)) << 16) + (((int) (((gray[b] + gray[c]) / 2) * f)) << 8) + (int) (gray[a] * f);
//                            } else {
//                                arr[p] = 0xff000000 + (((int) (gray[c] * f)) << 16) + (((int) (((gray[a] + gray[d]) / 2) * f)) << 8) + (int) (gray[b] * f);
//                            }
//                        } else {
//                            if (j % 2 == 0) {
//                                arr[p] = 0xff000000 + (((int) (gray[b] * f)) << 16) + (((int) (((gray[a] + gray[d]) / 2) * f)) << 8) + (int) (gray[c] * f);
//                            } else {
//                                arr[p] = 0xff000000 + (((int) (gray[a] * f)) << 16) + (((int) (((gray[b] + gray[c]) / 2) * f)) << 8) + (int) (gray[d] * f);
//                            }
//                        }
//                    } else {
                    if (i % 2 == 0) {
                        if (j % 2 == 0) {
                            arr[p] = 0xff000000 + (gray[d] << 16) + (((gray[b] + gray[c]) / 2) << 8) + gray[a];
                        } else {
                            arr[p] = 0xff000000 + (gray[c] << 16) + (((gray[a] + gray[d]) / 2) << 8) + gray[b];
                        }
                    } else {
                        if (j % 2 == 0) {
                            arr[p] = 0xff000000 + (gray[b] << 16) + (((gray[a] + gray[d]) / 2) << 8) + gray[c];
                        } else {
                            arr[p] = 0xff000000 + (gray[a] << 16) + (((gray[b] + gray[c]) / 2) << 8) + gray[d];
                        }
                    }
//                    }

                }
            }

            bitmap = Bitmap.createBitmap(TransceiverInstance.arr, TransceiverInstance.length - 1, TransceiverInstance.height - 1, Bitmap.Config.ARGB_8888);
//            if (judgeExistBlackPoint()) {
//                return 0;
//            }
            if (test_mod == 1) {
                bitmap = drawLines(bitmap, getPoints(centerX, centerY, between), r);
            } else if (test_mod == 2) {
                PositionSets positionSets = Config.getInstance().getPositionSets();
                bitmap = drawLines(bitmap, getPoints(positionSets.getCent_x(), positionSets.getCent_y(), positionSets.getCent_bew()), positionSets.getR());
            }
            return 1;
        }

        private boolean judgeExistBlackPoint() {
            boolean[] exist_point = new boolean[5];
            initPartData();
            for (int i = 0; i < datas.size() - 1; i++) {
                exist_point[i] = findBlackPoint(datas.get(i + 1), 25, 25, 200);
                Log.i("exist_point" + i, String.valueOf(exist_point[i]));
            }

            if (MathUtil.allTrueValue(exist_point)) {
                return true;
            }
            return false;
        }

        private void initPartData() {
            PositionSets positionSets = Config.getInstance().getPositionSets();
//            datas = getPartData(getPoints(positionSets.getCent_x(), positionSets.getCent_y(), positionSets.getCent_bew()), positionSets.getR());
            datas = getPartData(positionSets.getCent_x(), positionSets.getCent_y(), positionSets.getCent_bew(), positionSets.getR());
        }

        private boolean findBlackPoint(int[] ints, int gray, int range, int min_num) {
            int num = 0;
            for (int i = 0; i < ints.length; i++) {
                int r = (ints[i] & 0x00ff0000) >> 16;
                int g = (ints[i] & 0x0000ff00) >> 8;
                int b = ints[i] & 0x000000ff;
                if (r > gray - range && r < gray + range && g > gray - range && g < gray + range && b > gray - range && b < gray + range) {
                    num++;
                }
                if (num > min_num) {
                    return true;
                }
            }
            return false;
        }


        private Bitmap drawLines(Bitmap bit, ArrayList<Point> findpoints, int r) {
            Bitmap b = bit.copy(Bitmap.Config.ARGB_8888, true);
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.BLACK);
            paint.setStrokeWidth(3);
            paint.setAntiAlias(true);
            Canvas c = new Canvas(b);
            for (int i = 0; i < findpoints.size(); i++) {
                Point p = findpoints.get(i);
                RectF rectf = new RectF(p.x - r, p.y - r, p.x + r, p.y + r);
                c.drawRect(rectf, paint);
            }
            return b;
        }

        /**
         * 利用bitmap 拿到对应区域像素色值数组
         *
         * @return 保存6个区域数组的集合
         */
        private ArrayList<int[]> getPartData(ArrayList<Point> centPoint, int r) {
            ArrayList<int[]> datalist = new ArrayList<>();
            for (int i = 0; i < centPoint.size(); i++) {
                Point p = centPoint.get(i);
                int[] arr1 = new int[(2 * r + 1) * (2 * r + 1)];
                bitmap.getPixels(arr1, 0, (2 * r + 1), p.x - r, p.y - r, (2 * r + 1), (2 * r + 1));
                datalist.add(arr1);
            }
            return datalist;
        }

        private ArrayList<int[]> getPartData(short centerX, short centerY, short between, short r) {
            ArrayList<int[]> datalist = new ArrayList<>();
            int[] arr1 = new int[(2*between + 2 * r + 3) * (2 * r + 1)];
            //存放的数组   从该数组的第几位存储  一行多少个像素 左上角X Y坐标   长 高
            bitmap.getPixels(arr1, 0, (2*between + 2 * r + 3), (centerX - between - r), (centerY - between - r), (2*between + 2 * r + 3), (2 * r + 1));
            datalist.add(arr1);

            int[] arr2;
//        int[] arr2 = new int[(2 * r + 1) * (2 * r + 1)];
//        bitmap.getPixels(arr2, 0, (2 * r + 1), (centerX + between - r), (centerY - between - r), (2 * r + 1), (2 * r + 1));
//        datalist.add(arr2);

            arr2 = new int[(2 * r + 1) * (2 * r + 1)];
            bitmap.getPixels(arr2, 0, (2 * r + 1), (centerX - between - r), (centerY - r), (2 * r + 1), (2 * r + 1));
            datalist.add(arr2);
            arr2 = new int[(2 * r + 1) * (2 * r + 1)];
            bitmap.getPixels(arr2, 0, (2 * r + 1), (centerX + between - r), (centerY - r), (2 * r + 1), (2 * r + 1));
            datalist.add(arr2);

            arr2 = new int[(2 * r + 1) * (2 * r + 1)];
            bitmap.getPixels(arr2, 0, (2 * r + 1), (centerX - between - r), (centerY + between - r), (2 * r + 1), (2 * r + 1));
            datalist.add(arr2);
            arr2 = new int[(2 * r + 1) * (2 * r + 1)];
            bitmap.getPixels(arr2, 0, (2 * r + 1), (centerX + between - r), (centerY + between - r), (2 * r + 1), (2 * r + 1));
            datalist.add(arr2);
            return datalist;
        }

        ArrayList<int[]> datas;

        /**
         * 检查所有区域
         *
         * @return
         */
        public ArrayList<ArrayList<ResultData>> checkData() {
            ArrayList<ArrayList<ResultData>> result_data = new ArrayList<>();
            ArrayList<ArrayList<ResultData>> results = Config.getInstance().getSettings();
            Log.i("result", results.toString());
            initPartData();
            for (int i = 0; i < datas.size(); i++) {
                result_data.add(foundSimilarColor(datas.get(i), results.get(i), i));
            }
            if (result_data.get(3).get(0).hasfound) {
                result_data.get(3).get(0).setNames("铵盐");
                if (result_data.get(2).get(0).hasfound) {
                    result_data.get(3).get(0).setHasfound(false);
                    result_data.get(2).get(0).setNames("硝酸铵");
                }

            }
            Log.i("result_data", result_data.toString());
            return result_data;
        }

        /**
         * @param arr
         * @param kinds
         * @return
         */

        private ArrayList<ResultData> foundSimilarColor(int[] arr, ArrayList<ResultData> kinds, int area) {

            ArrayList<ResultData> found = kinds;

            int[] fondlist = new int[found.size()];
            boolean[] boo = new boolean[found.size()];

            for (int j = 0; j < found.size(); j++) {
                for (int i = 0; i < arr.length; i++) {
                    int pix = arr[i];
                    if (fondlist[j] > found.get(j).getNum()) {
                        boo[j] = true;
                        found.get(j).setHasfound(true);
                        Log.i("foundlist" + found.get(j).getIndex(), fondlist[j] + "");
                        break;
                    } else {
                        if (judgeSimple(found.get(j), pix)) {
                            fondlist[j]++;
                        }
                    }
                }
            }
            if (area == 0) {
                if (boo[0]) {
                    if (boo[1]) {
                        found.get(0).setHasfound(true);
                        found.get(1).setHasfound(false);
                        found.get(2).setHasfound(false);
//                        found.get(3).setHasfound(false);
//                        found.get(4).setHasfound(false);
                    } else {
                        found.get(0).setHasfound(false);
                        found.get(1).setHasfound(true);
//                        found.get(3).setHasfound(false);
                        found.get(2).setHasfound(false);
//                        found.get(4).setHasfound(false);
                    }
                } else {
                    if(boo[2]){
                        found.get(0).setHasfound(false);
                        found.get(1).setHasfound(false);
                        found.get(2).setHasfound(true);
                    }else {
                        found.get(0).setHasfound(false);
                        found.get(1).setHasfound(false);
                        found.get(2).setHasfound(false);
                    }
//                    if (boo[2]) {
//                        if (boo[3]) {
//                            found.get(0).setHasfound(false);
//                            found.get(1).setHasfound(false);
//                            found.get(2).setHasfound(false);
////                            found.get(3).setHasfound(true);
////                            found.get(4).setHasfound(false);
//                        } else {
//                            found.get(1).setHasfound(false);
//                            found.get(0).setHasfound(false);
////                            found.get(3).setHasfound(false);
//                            found.get(2).setHasfound(true);
////                            found.get(4).setHasfound(false);
//
//                        }
//                    }
//                    else {
//                        if (boo[4]) {
//                            found.get(1).setHasfound(false);
//                            found.get(0).setHasfound(false);
////                            found.get(3).setHasfound(false);
//                            found.get(2).setHasfound(false);
////                            found.get(4).setHasfound(true);
////                        } else {
//                            found.get(0).setHasfound(false);
//                            found.get(1).setHasfound(false);
//                            found.get(2).setHasfound(false);
////                            found.get(3).setHasfound(false);
////                            found.get(4).setHasfound(false);
//                        }
//
//                    }

                }
            }
//            //对2号区域特殊判断
//            if (area == 1) {
//                if (boo[0]) {
//                    if (boo[1]) {
//                        found.get(1).setHasfound(false);
//                        found.get(0).setHasfound(true);
//                        found.get(3).setHasfound(false);
//                        found.get(2).setHasfound(false);
//                    } else {
//                        found.get(1).setHasfound(true);
//                        found.get(0).setHasfound(false);
//                        found.get(3).setHasfound(false);
//                        found.get(2).setHasfound(false);
//                    }
//                } else {
//                    if (boo[2]) {
//                        if (boo[3]) {
//                            found.get(1).setHasfound(false);
//                            found.get(0).setHasfound(false);
//                            found.get(3).setHasfound(true);
//                            found.get(2).setHasfound(false);
//                        } else {
//                            found.get(1).setHasfound(false);
//                            found.get(0).setHasfound(false);
//                            found.get(3).setHasfound(false);
//                            found.get(2).setHasfound(true);
//                        }
//                    } else {
//                        found.get(0).setHasfound(false);
//                        found.get(1).setHasfound(false);
//                        found.get(2).setHasfound(false);
//                        found.get(3).setHasfound(false);
//                    }
//
//                }
//            }
//            Log.i("foundlist", Arrays.asList(fondlist)+"");
            return found;
        }

        boolean judgeSimple(ResultData kinds, int pix) {

            if (colorIsInRange(kinds.getR(), kinds.getG(), kinds.getB(), kinds.getR_range(), kinds.getG_range(), kinds.getB_range(), pix) && colorHasSpeciality(pix, kinds.getDifferences())) {
                return true;
            }
            return false;
        }

        boolean colorIsInRange(@Nullable Short R, @Nullable Short G, @Nullable Short B,
                               @Nullable Short r_range, @Nullable Short g_range, @Nullable Short b_range,
                               int color) {
            int r = (color & 0x00ff0000) >> 16;
            int g = (color & 0x0000ff00) >> 8;
            int b = (color & 0x000000ff);
            if (r < R + r_range && r > R - r_range && g < G + g_range && g > G - g_range && b < B + b_range && b > B - b_range) {
                return true;
            }
            return false;
        }

        boolean colorHasSpeciality(int pix, @Nullable Short[] deference) {
            int r = (pix & 0x00ff0000) >> 16;
            int g = (pix & 0x0000ff00) >> 8;
            int b = (pix & 0x000000ff);

            if (deference == null) {
                return true;
            }
            boolean[] tag = new boolean[6];
            for (int i = 0; i < 6; i++) {
                if (deference[i] != 0) {
                    if (verdict(r, g, b, i, deference[i])) {
                        tag[i] = true;
                    } else {
                        tag[i] = false;
                    }
                } else {
                    tag[i] = true;
                }
            }
            for (Boolean bool : tag) {
                if (!bool) {
                    return false;
                }
            }
            return true;
        }

        boolean verdict(int r, int g, int b, int index, Short def) {
            switch (index) {
                case 0:
                    return r - g > def;
                case 1:
                    return r - b > def;
                case 2:
                    return g - r > def;
                case 3:
                    return g - b > def;
                case 4:
                    return b - r > def;
                case 5:
                    return b - g > def;
                default:
                    return false;
            }
        }


        private ArrayList<Point> getPoints(int centX, int centY, int bet) {
            ArrayList<Point> points = new ArrayList<>();
            points.add(new Point((2 * centX - bet) / 2, centY - bet));
            points.add(new Point(centX + bet, centY - bet));

            points.add(new Point(centX - bet, centY));
            points.add(new Point(centX + bet, centY));

            points.add(new Point(centX - bet, centY + bet));
            points.add(new Point(centX + bet, centY + bet));
            return points;
        }

        public boolean saveData(int test_mod, @Nullable String tips, @Nullable Boolean saveImg, @Nullable ArrayList<ArrayList<ResultData>> mDates) {
            if (test_mod == -1) {
//                FileUtils.deleteFile(FileAndPath.SD_xj, FileAndPath.IMG_BAIBAN_NAME + ".cache");
//                FileAndPath.writeByteArrayToFile(part1, FileAndPath.SD_xj, FileAndPath.IMG_BAIBAN_NAME + ".cache");
                FileUtils.deleteFile(FileAndPath.IMG_NATIVE_DATA, FileAndPath.IMG_BAIBAN_NAME + ".cache");
                FileAndPath.writeByteArrayToFile(part1, FileAndPath.IMG_NATIVE_DATA, FileAndPath.IMG_BAIBAN_NAME + ".cache");
            } else {
                if (test_mod == 0) {
                    ContentValues values = new ContentValues();
                    SimpleDateFormat df = new SimpleDateFormat("y-M-d HH:mm:ss");//设置日期格式
                    String data_time = df.format(new Date());
                    String[] d_t = data_time.split(" ");

                    values.put("data", d_t[0]);
                    values.put("time", d_t[1]);

                    if (mDates != null) {
//                        Log.i("save_data", mDates.toString());
                        int num = 0;
                        for (int i = 0; i < mDates.size(); i++) {
                            ArrayList<ResultData> part = mDates.get(i);
//                            Log.i("part", part.toString());
                            boolean[] found_list = new boolean[part.size()];
                            for (int j = 0; j < part.size(); j++) {
//                                Log.i("has_found", i + "" + j + String.valueOf(part.get(j).isHasfound()));
                                if (part.get(j).isHasfound()) {
                                    found_list[j] = true;
                                    num++;
                                }
                            }
                            if (MathUtil.hasTrueValue(found_list)) {
                                values.put("result" + (i + 1), "阳性");
                                StringBuffer str_found = new StringBuffer();
                                for (int k = 0; k < found_list.length; k++) {
                                    if (found_list[k]) {
                                        str_found.append(Config.names[(int) (part.get(k).getIndex())]);
                                    }
                                }
                                values.put("detial" + (i + 1), str_found.toString());
                            } else {
                                values.put("result" + (i + 1), "阴性");
                                values.put("detial" + (i + 1), "");
                            }
                        }
                        if (num > 0) {
                            values.put("result", "阳性");
                        } else {
                            values.put("result", "阴性");
                        }
                    }

                    values.put("tips", tips);

                    String fileName = data_time + getPG_Z();
                    String fileABSpath = FileAndPath.IMG + "/" + fileName;
                    String cachePath = FileAndPath.IMG_NATIVE_DATA + "/" + fileName + ".cache";

                    values.put("PNGpath", fileABSpath);
                    values.put("cachePath", cachePath);
                    if (saveImg) {
                        FileAndPath.writeByteArrayToFile(part1, FileAndPath.IMG_NATIVE_DATA, fileName + ".cache");
                        ImageUtils.saveBitmapToBmp(bitmap, FileAndPath.IMG, fileName + ".bmp");
                        if (ImageUtils.savaBitmapAsPNG(bitmap, FileAndPath.IMG, fileName + ".png")) {
                            Log.i("图片已保存", FileAndPath.IMG + fileName);
                        } else {
                            Log.i("图片保存出错", String.valueOf(new File(FileAndPath.IMG, fileName).exists()));
                        }
                    }
//                    Log.i("values", values.toString());
                    db.insert("rgb", null, values);
                }
            }
            app.hasNewHestroy = true;
            return true;
        }

        String getPG_Z() {
            SharedPreferences sharedPreferences = SharePreferenceHelper.getInstance();

            return sharedPreferences.getInt(Consts.TakePhotoDelay, 0) + "_" + sharedPreferences.getInt(Consts.LIGHT_HIGHT, 0);
        }

        //中心校验
        short centerX;
        short centerY;
        short between;
        short r;
        int rr;


        /**
         * 根据 position计算 图片起始位置
         *
         * @param position 第几张图片
         * @return 8位byte  strXL strXH strYL strYH
         * endXL endXH endYL endYH
         */

        public byte[] getPosition(byte position) {
            byte[] arr = new byte[8];
            if (position != 0) {
                short strY = (short) (height / content * (position - 1));
                arr[2] = (byte) (strY & 0x00ff);
                arr[3] = (byte) (strY >> 8);

                arr[5] = (byte) (length >> 8);
                arr[4] = (byte) (length & 0x00ff);

                short endY = (short) (height / content * position);
                arr[7] = (byte) (endY >> 8);
                arr[6] = (byte) (endY & 0x00ff);
            } else {
                return null;
            }
            return arr;
        }

        private void syncInt() {
            centerX = (short) app.cent_X;
            //                = length / 2 - 5;315  320
            centerY = (short) app.cent_Y;
//        = height / 2 + 3;243 240
            between = (short) app.cent_bet;
            //                = 145;//135px(9mm)
            r = (short) app.round_r;
            //        = 40;//45px(3mm)
            rr = (int) Math.pow(r, 2);
        }
    }
}
