package measurement.color.com.xj_919.and.fragment.Test;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import measurement.color.com.xj_919.and.Utils.DB.MyDBHelper;
import measurement.color.com.xj_919.and.Utils.SP.Consts;
import measurement.color.com.xj_919.and.Utils.SP.SharePreferenceHelper;
import measurement.color.com.xj_919.and.Utils.io.FileUtils;
import measurement.color.com.xj_919.and.Utils.soft.ImageUtils;
import measurement.color.com.xj_919.and.Utils.T;
import measurement.color.com.xj_919.and.activity.app;


/**
 * Created by wpc on 2016/9/19.
 */
public class USBManager {
    static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";// 权限
    public Bitmap bitmap;
    private static USBManager instance;
    public USBDataTransceiver TransceiverInstance;
    private final String tag = "USBManager";

    //-1设备不支持usbhost，
    // 0usbmanager可用
    // 1derive可用
    // 2interface可用
    // 3endpoint可用/可以发送拍照请求
    private static int state = -1;

    public static boolean printable = false;


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
        initUsbManager();
        return instance;
    }

    private USBManager(Activity context) {
        mContext = context;
        registerReceiver();
    }

    public int getState() {
        return state;
    }

    public boolean init() {
        initUsbManager();
        if (findDevice(1155, 22336)) {
            if (findInterface(10, 0, 0)) {
                if (findPoint()) {
                    return true;
                } else {
                    Toast.makeText(mContext, "没有发现端点 \n can not find endpoint", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(mContext, "没有发现接口 \n can not find interface", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(mContext, "没有发现设备 \n can not find device", Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    public static void initUsbManager() {
        if (mUsbManager == null) {
            mUsbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
        }
        if (mUsbManager == null) {
            T.show(mContext, "usb不可用");
        } else {
            state = 0;
        }
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

    //
//    private void reset() {
//        synchronized (this) {
//            if (mUsbDeviceConnection != null) {
//                // 复位命令的设置有USB Mass Storage的定义文档给出
//                int result = mUsbDeviceConnection.controlTransfer(0x21, 0xFF, 0x00, 0x00, null, 0, 1000);
//                if(result < 0) {                      // result<0说明发送失败
//                    Log.d("reset", "Send reset command failed!");
//                } else {
//                    Log.d("reset", "Send reset command succeeded!");
//                }
//            }
//        }
//    }
//
//    private void getMaxLnu() {
//        synchronized (this) {
//            if (mUsbDeviceConnection != null) {
//                // 接收的数据只有1个字节
//                byte[] message = new byte[1];
//                // 获取最大LUN命令的设置由USB Mass Storage的定义文档给出
//                int result = mUsbDeviceConnection.controlTransfer(0xA1, 0xFE, 0x00, 0x00, message, 1, 1000);
//                if(result < 0) {
//                    Log.d("getMaxLnu",  "Get max lnu failed!");
//                } else {
//                    Log.d("getMaxLnu", "Get max lnu succeeded! maxlnu="+Integer.toString(message[0]&0x00FF));
//                }
//            }
//        }
//    }
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
                mUsbDeviceConnection = null;
                TestFragment.setButton("连接错误", true);
            }
        }
    }

    static void registerReceiver() {
        myUSBReceiver = new usbBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.setPriority(800);
        intentFilter.addAction("android.hardware.usb.action.USB_STATE");
        intentFilter.addAction("android.hardware.usb.action.USB_DEVICE_ATTACHED");
        intentFilter.addAction("android.hardware.usb.action.USB_DEVICE_DETACHED");
        mContext.registerReceiver(myUSBReceiver, intentFilter);
    }

    void unregisterReceiver() {
        mContext.unregisterReceiver(myUSBReceiver);
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
        private int[] GRAY = new int[(length * height / content)];//源数据 10位
        private int[] gray = new int[(length * height / content)];//RGB支持的8位
        public int[] arr = new int[(length - 1) * (height - 1)];//计算过后的图片数组
        //        public int[] arr = new int[(length / 2) * (height / 2)];//计算过后的图片数组
        //数据库db相关
        private MyDBHelper dbHelper;
        private SQLiteDatabase db;


        private USBDataTransceiver() {
            dbHelper = new MyDBHelper(mContext, app.SQLite_LOG, null, 1);
            db = dbHelper.getWritableDatabase();
        }


        /**
         * 根据 下面 位置信息 返回拍照的 命令byte数组
         *
         * @param arr 位置信息
         * @return 对应命令byte数组
         */

        public byte[] getTakePhotoOrder(byte[] arr, int delay) {
            byte[] ml = new byte[55];
            ml[0] = 0x55;
            ml[2] = 0x01;
            ml[4] = (byte) delay;//190
            ml[5] = 0x01;
            if (arr == null) {
                return ml;
            } else {
                for (int i = 0; i < arr.length; i++) {
                    ml[i + 10] = arr[i];
                }
            }
            return ml;
        }


        public void sendTakePhotoRequest(byte position, int delay) {
//
            // 判断是否有权限
            if (mUsbManager.hasPermission(mUsbDevice)) {
                Log.i("has permission", "true");
                // 打开设备，获取 UsbDeviceConnection 对象，连接设备，用于后面的通讯
                mUsbDeviceConnection = mUsbManager.openDevice(mUsbDevice);
                if (mUsbDeviceConnection == null) {
                    Log.i("conn", "not null");
                }
                if (mUsbDeviceConnection.claimInterface(mInterface, true)) {
                    //用UsbDeviceConnection 与 UsbInterface 进行端点设置和通讯
                    Log.i("claimInterface", "true");
                } else {
                    mUsbDeviceConnection.close();
                }
            } else {
                Log.i("has permission", "没有权限");
            }
            printable = false;
            byte[] arr = getTakePhotoOrder(getPosition(position), delay);
            int i = mUsbDeviceConnection.bulkTransfer(epBulkOut, arr, arr.length, 0);
            Receiveytes = new byte[55];
            int j = mUsbDeviceConnection.bulkTransfer(epBulkIn, Receiveytes, Receiveytes.length, 0);//55成功
        }

        public void sendGetDataRequest(short index) {
            mUsbDeviceConnection.bulkTransfer(epBulkOut, getDataOrder(index), 55, 200);
            Receiveytes = new byte[4020];
            mUsbDeviceConnection.bulkTransfer(epBulkIn, Receiveytes, 4020, 0);
            byte[] data = new byte[4000];
            System.arraycopy(Receiveytes, 9, data, 0, 4000);
            System.arraycopy(data, 0, part1, index * 4000, 4000);
        }

        public void LoadDate(boolean isTest, int lightheight) {
            syncInt();
            for (short i = (short) 0; i < times; i++) {
                sendGetDataRequest(i);
            }
//            mUsbDeviceConnection.releaseInterface(mInterface);
//            mUsbDeviceConnection.close();

            for (int i = 0; i < part1.length; i += 2) {
                GRAY[i / 2] = part1[i] + ((part1[i + 1] & 0xff) << 8);
                gray[i / 2] = (((part1[i] & 0xff) >> 2) + ((part1[i + 1] & 0xff) << 6));
            }
            //白平衡---
            long R = 0, G = 0, B = 0;
            for (short i = 0; i < 480; i++) {
                for (short j = 0; j < 640; j++) {
                    int a = i * 640 + j;
                    if (i % 2 == 0) {
                        if (j % 2 == 0) {
                            B += gray[a];
                        } else {
                            G += gray[a];
                        }
                    } else {
                        if (j % 2 == 0) {
                            G += gray[a];
                        } else {
                            R += gray[a];
                        }
                    }
                }
            }
            float g_ = ((float) G) / 163200;
            float r_p = g_ / (((float) R) / 81600);
            float b_p = g_ / (((float) B) / 81600);
            for (short i = 0; i < 480; i++) {
                for (short j = 0; j < 640; j++) {
                    int a = i * 640 + j;
                    if (i % 2 == 0) {
                        if (j % 2 == 0) {
                            gray[a] *= b_p;
                        }
                    } else {
                        if (j % 2 != 0) {
                            gray[a] *= r_p;
                        }
                    }
                }
            }

            //缺一行一列 //曝光平衡
            boolean ph = SharePreferenceHelper.getInstance().getBoolean(Consts.EXPOSURE, false);
            for (short i = 0; i < 479; i++) {
                for (short j = 0; j < 639; j++) {
                    int p = i * 639 + j;
                    int a = p + i, b = a + 1, c = a + 640, d = c + 1;
                    if (ph) {
                        float f = (float) (Math.pow((Math.pow(j - 80, 2) + Math.pow(i - 240, 2) + Math.pow(j - 560, 2) + Math.pow(i - 240, 2) + 2 * Math.pow(lightheight, 2)), 0.5) / Math.pow((2 * Math.pow(240, 2) + 2 * Math.pow(lightheight, 2)), 0.5));
                        if (i % 2 == 0) {
                            if (j % 2 == 0) {
                                arr[p] = 0xff000000 + (((int) (gray[d] * f)) << 16) + (((int) (((gray[b] + gray[c]) / 2) * f)) << 8) + (int) (gray[a] * f);
                            } else {
                                arr[p] = 0xff000000 + (((int) (gray[c] * f)) << 16) + (((int) (((gray[a] + gray[d]) / 2) * f)) << 8) + (int) (gray[b] * f);
                            }
                        } else {
                            if (j % 2 == 0) {
                                arr[p] = 0xff000000 + (((int) (gray[b] * f)) << 16) + (((int) (((gray[a] + gray[d]) / 2) * f)) << 8) + (int) (gray[c] * f);
                            } else {
                                arr[p] = 0xff000000 + (((int) (gray[a] * f)) << 16) + (((int) (((gray[b] + gray[c]) / 2) * f)) << 8) + (int) (gray[d] * f);
                            }
                        }
                    } else {
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
                    }

                }
            }
            printable = true;
            bitmap = Bitmap.createBitmap(TransceiverInstance.arr, TransceiverInstance.length - 1, TransceiverInstance.height - 1, Bitmap.Config.ARGB_8888);
            if (isTest) {
                bitmap = drawLines(bitmap, getPoints(centerX, centerY, between));
            }
        }

        private Bitmap drawLines(Bitmap bit, ArrayList<Point> findpoints) {
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

        ArrayList<ArrayList<ResultData>> judgeresult;

        public ArrayList<ArrayList<ResultData>> Judge() {
            judgeresult = checkData(getPartData());
            return judgeresult;
        }

        /**
         * 利用bitmap 拿到对应区域像素色值数组
         *
         * @return 保存6个区域数组的集合
         */
        private ArrayList<int[]> getPartData() {
            ArrayList<int[]> datalist = new ArrayList<>();
            int[] arr1 = new int[(between + 2 * r + 1) * (2 * r + 1)];
            bitmap.getPixels(arr1, 0, (between + 2 * r + 1), (centerX - between - r), (centerY - between - r), (between + 2 * r + 1), (2 * r + 1));
            datalist.add(arr1);

            int[] arr2 = new int[(2 * r + 1) * (2 * r + 1)];
            bitmap.getPixels(arr2, 0, (2 * r + 1), (centerX + between - r), (centerY - between - r), (2 * r + 1), (2 * r + 1));
            datalist.add(arr2);

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

        /**
         * 检查所有区域
         *
         * @param datalist
         * @return
         */
        private ArrayList<ArrayList<ResultData>> checkData(ArrayList<int[]> datalist) {
            ArrayList<ArrayList<ResultData>> results = new ArrayList<>();
            int index = 0;
            for (int[] arr : datalist) {
                results.add(getResult(arr, index));
                index++;
            }
            return results;
        }

        /**
         * 检查某个区域
         *
         * @param arr
         * @param index
         * @return
         */
        private ArrayList<ResultData> getResult(int[] arr, int index) {
            ArrayList<ResultData> results;
            switch (index) {
                case 0:
                    results = new ArrayList<>();
                    results.add(new ResultData(4, 0xff835255, 10));
                    results.add(new ResultData(11, 0xff8a595f, 10));
                    return foundSimilarColor(arr, results);
                case 1:
                    results = new ArrayList<>();
                    results.add(new ResultData(1, 0xff463231, 100));
                    results.add(new ResultData(2, 0xff1f232f, 100));
                    results.add(new ResultData(3, 0xff5c4235, 100));
                    results.add(new ResultData(9, 0xff754a39, 100));
                    return foundSimilarColor(arr, results);
                case 2:
                    results = new ArrayList<>();
                    results.add(new ResultData(6, 0xff444455, 100));
                    return foundSimilarColor(arr, results);
                case 3:
                    results = new ArrayList<>();
                    results.add(new ResultData(6, 0xff724e3e, 100));
                    return foundSimilarColor(arr, results);
                case 4:
                    results = new ArrayList<>();
                    results.add(new ResultData(8, 0xff484e4d, 50));
                    return foundSimilarColor(arr, results);
                case 5:
                    results = new ArrayList<>();
                    results.add(new ResultData(7, 0xff54546d, 10));
                    return foundSimilarColor(arr, results);
                default:
                    return null;
            }
        }

        int fudong = 5;

        /**
         * @param arr
         * @param kinds
         * @return
         */
        private ArrayList<ResultData> foundSimilarColor(int[] arr, ArrayList<ResultData> kinds) {
            byte[] fondlist = new byte[kinds.size()];

            for (int i = 0; i < arr.length; i++) {
                int pix = arr[i];
                for (int j = 0; j < kinds.size(); j++) {

                    if (fondlist != null && fondlist[j] > kinds.get(j).getNum()) {
                        kinds.get(j).setHasfound(true);
                        continue;
                    } else {
                        int c = kinds.get(j).getColor();
                        byte R = (byte) ((c & 0x00ff0000) >> 16);
                        byte G = (byte) ((c & 0x0000ff00) >> 8);
                        byte B = (byte) (c & 0x000000ff);

                        byte r = (byte) ((pix & 0x00ff0000) >> 16);
                        byte g = (byte) ((pix & 0x0000ff00) >> 8);
                        byte b = (byte) (pix & 0x000000ff);
                        int ind = kinds.get(j).getIndex();
                        if (Math.abs(r - R) < fudong && Math.abs(g - G) < fudong && Math.abs(b - B) < fudong) {
                            switch (ind) {
                                case 1:
                                    if (r - g > 45) {
                                        fondlist[j]++;
                                    }
                                    break;
                                case 2:
                                    if (g - r > 30) {
                                        fondlist[j]++;
                                    }
                                    break;
                                case 3:
                                    if (r - g > 60) {
                                        fondlist[j] += 1;
                                    }
                                    break;
                                case 4:
                                    if (r - g > 60 && r - b > 60) {
                                        fondlist[j]++;
                                    }
                                    break;
                                case 6:
                                    if (b - r > 60 && r - g < 15) {
                                        fondlist[j]++;
                                    }
                                    if (r - g > 45 && g - b > 45) {
                                        fondlist[j]++;
                                    }
                                    break;
                                case 7:
                                    if (b - r > 30 && b - g > 30) {
                                        fondlist[j]++;
                                    }
                                    break;
                                case 8:
                                    if (r + g + b < 120) {
                                        fondlist[j]++;
                                    }
                                    break;
                                case 9:
                                    if (r - g >= 80 && g - b >= 60) {
                                        fondlist[j]++;
                                    }
                                    break;
                                case 11:
                                    if (r - g > 60 && r - b > 60) {
                                        fondlist[j]++;
                                    }
                                    break;

                            }
                        }
                    }
                }

            }
            return kinds;
        }


        private ArrayList<Point> getPoints(int centX, int centY, int bet) {
            ArrayList<Point> points = new ArrayList<>();
            points.add(new Point(centX - bet, centY - bet));
            points.add(new Point(centX, centY - bet));
            points.add(new Point(centX + bet, centY - bet));

            points.add(new Point(centX - bet, centY));
            points.add(new Point(centX, centY));
            points.add(new Point(centX + bet, centY));

            points.add(new Point(centX - bet, centY + bet));
            points.add(new Point(centX, centY + bet));
            points.add(new Point(centX + bet, centY + bet));
            return points;
        }

        String names[] = {"", "", "", "", "", "", "", "", "", "", ""};

        public boolean saveData(String tips, Boolean saveImg) {

            ContentValues values = new ContentValues();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
            String data_time = df.format(new Date());
            String[] d_t = data_time.split(" ");

            values.put("data", d_t[0]);
            values.put("time", d_t[1]);

            if (judgeresult != null) {
                for (int i = 0; i < judgeresult.size(); i++) {
                    ArrayList<ResultData> part = judgeresult.get(i);
                    int num = 0;
                    for (int j = 0; j < part.size(); j++) {
                        if (part.get(j).hasfound) {
                            num++;
                            values.put("result" + (i + 1), "阳性");
                            values.put("detial" + (i + 1),names[part.get(j).getIndex()] );
                        } else {
                            values.put("result" + (i + 1), "阴性");
                            values.put("detial" + (i + 1),"");
                        }
                    }
                    if (num > 0) {
                        values.put("result", "阳性");
                    } else {
                        values.put("result", "阴性");
                    }

                }
            }
            values.put("tips", tips);

            String fileName = data_time + ".png";
            String fileABSpath = FileUtils.IMG + "/" + fileName;
            String cachePath = FileUtils.IMG_NATIVE_DATA + data_time + ".cache";

            values.put("PNGpath", fileABSpath);
            values.put("cachePath", cachePath);

            if (saveImg) {
                FileUtils.writeByteArrayToFile(part1, FileUtils.IMG_NATIVE_DATA, data_time + ".cache");
                ImageUtils.saveBitmapToBmp(bitmap, FileUtils.IMG, data_time + ".bmp");
                if (ImageUtils.savaBitmapAsPNG(bitmap, FileUtils.IMG, fileName)) {
                    Log.i("图片已保存", FileUtils.IMG + fileName);
                } else {
                    Log.i("图片保存出错", String.valueOf(new File(FileUtils.IMG, fileName).exists()));
                }
            }
            db.insert("rgb", null, values);
            return true;
        }

        //中心校验
        short centerX;
        //                = length / 2 - 5;
        short centerY;
//        = height / 2 + 3;

        short between;
        //                = 145;//135px(9mm)
        short r;
        //        = 40;//45px(3mm)
        int rr;
//        = (int) Math.pow(r, 2);

        /**
         * 根据 获取  第几个   data返回对应命令byte数组
         *
         * @param index 第几个data
         * @return 对应命令byte数组
         */
        byte[] gdo = new byte[55];

        public byte[] getDataOrder(short index) {
            gdo[0] = 0x55;
            gdo[2] = 0x02;
            gdo[4] = (byte) (index >> 8);
            gdo[5] = (byte) (index & 0x00ff);
            return gdo;
        }

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

    class ResultData {
        int index;
        boolean hasfound;
        int num;
        int color;

        public ResultData(int index, int color, int num) {
            this.index = index;
            this.hasfound = false;
            this.color = color;
            this.num = num;
        }

        public int getIndex() {
            return index;
        }

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color;
        }

        public boolean isHasfound() {
            return hasfound;
        }

        public void setHasfound(boolean hasfound) {
            this.hasfound = hasfound;
        }

        @Override
        public String toString() {
            return (hasfound ? "发现[" : "未发现[") + index + "]号样品,[color]" + color;
        }
    }
}
