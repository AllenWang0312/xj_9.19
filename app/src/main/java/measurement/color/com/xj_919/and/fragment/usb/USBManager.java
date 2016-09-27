package measurement.color.com.xj_919.and.fragment.usb;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.provider.ContactsContract;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import measurement.color.com.xj_919.and.Utils.MyDBHelper;
import measurement.color.com.xj_919.and.Utils.toast;


/**
 * Created by wpc on 2016/9/19.
 */
public class USBManager {

    private static USBManager instance;
    private final String tag = "USBManager";

    //-1设备不支持usbhost，
    // 0usbmanager可用
    // 1derive可用
    // 2interface可用
    // 3endpoint可用
    private static int state = -1;

    //-1 啥都没干
    // 0 拍了照了
    // 1 正在取数据
    // 2 数据取完了
    private static int datastate = -1;

    static Activity mContext;
    static private UsbManager mUsbManager;
    private usbBroadcastReceiver myUSBReceiver;
    private UsbDevice mUsbDevice;
    private UsbInterface mInterface;
    private UsbDeviceConnection mUsbDeviceConnection;

    private int ret = -100;
    ArrayList<String> USBDeviceList;
    private UsbEndpoint epBulkOut, epControl, epIntEndpointOut, epIntEndpointIn, epBulkIn;
    private byte[] Receiveytes;  //接收信息字节

    public final short length = 640;
    public final short height = 480;
    private final byte content = 1;

    private final short times = (short) ((length * height / content) / 100);

    private byte[] part1 = new byte[(length * height / content) * 2];

    private int[] GRAY = new int[(length * height / content)];
    private int[] gray = new int[(length * height / content)];

    //    public static int[] grayARGB = new int[pixsNum];
    public int[] arr = new int[(length - 1) * (height - 1)];

    public int index = 0; //选择的像素点坐标
    public ArrayList<Integer> chose_colors = new ArrayList<>();

    private static MyDBHelper dbHelper;
    private static SQLiteDatabase db;

    public static USBManager getInstance(Activity context) {
        if (instance == null) {
            instance = new USBManager();
        }
        mContext = context;
        mUsbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
        if (mUsbManager == null) {
            toast.makeText(mContext, "usb不可用");
        } else {
            state = 0;
        }
        dbHelper = new MyDBHelper(mContext, "LOG.db", null, 1);
        db = dbHelper.getWritableDatabase();
        return instance;
    }

    private USBManager() {

    }


    public boolean findDevice(int Vendorid, int productid) {
        if (mUsbManager != null) {
            final HashMap<String, UsbDevice> mdrivers = mUsbManager.getDeviceList();
            Iterator<UsbDevice> deviceIterator = mdrivers.values().iterator();
            USBDeviceList = new ArrayList<>();
            while (deviceIterator.hasNext()) {
                UsbDevice device = deviceIterator.next();
                if (device.getVendorId() == Vendorid && device.getProductId() == productid) {
                    this.mUsbDevice = device;
                    state = 1;
                    Log.i(tag, "找到设备");
                    return true;
                }
                USBDeviceList.add(String.valueOf(device.getVendorId()));
                USBDeviceList.add(String.valueOf(device.getProductId()));
            }
            Log.i(tag, USBDeviceList.toString());
        } else {
            return false;
        }
        return false;
    }

    public boolean findInterface(int interfaceClass, int interfaceSubclass, int interfaceProtocol) {
        if (mUsbDevice != null) {
            int cont = mUsbDevice.getInterfaceCount();
            for (int i = 0; i < cont; i++) {
                UsbInterface mInterface = mUsbDevice.getInterface(i);
                if (mInterface.getInterfaceClass() == interfaceClass && mInterface.getInterfaceSubclass() == interfaceSubclass && mInterface.getInterfaceProtocol() == interfaceProtocol) {
                    this.mInterface = mInterface;
                    Log.i("findInterface", mInterface.toString());
                    state = 2;
                    return true;
                }
            }
        } else {
            return false;
        }
        return false;
    }


    public boolean findPoint() {
        if (mInterface != null) {
            for (int j = 0; j < mInterface.getEndpointCount(); j++) {
                UsbEndpoint ep = mInterface.getEndpoint(j);
                if (ep.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                    if (ep.getDirection() == UsbConstants.USB_DIR_OUT) {
                        Log.i(tag, "Find the BulkEndpointOut,index:" + j + ",使用断点号:" + ep.getEndpointNumber());
                        epBulkOut = ep;

                    } else {
                        epBulkIn = ep;
                        Log.i(tag, "Find the BulkEndpointIn,index:" + j + ",使用断点号:" + ep.getEndpointNumber());

                    }
                }
                if (ep.getType() == UsbConstants.USB_ENDPOINT_XFER_CONTROL) {
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
                        epIntEndpointIn = ep;
                        Log.i(tag, "find the InterruptEndpointIn:"
                                + "index:" + j + ","
                                + epIntEndpointIn.getEndpointNumber());
                    }
                }

            }
            if (epBulkOut != null && epBulkIn != null) {
                state = 3;
                return true;
            }
//
//            if (epBulkOut == null && epBulkIn == null && epControl == null
//                    && epIntEndpointOut == null && epIntEndpointIn == null) {
//                throw new IllegalArgumentException("not all endpoints found");
//            }else {
//
//            }
        }
        return false;
    }


    public int getState() {
        return state;
    }


    public int getDatastate() {
        return datastate;
    }


    public void sendTakePhotoRequest(byte position) {
        if (mUsbDeviceConnection == null) {
            mUsbDeviceConnection = mUsbManager.openDevice(mUsbDevice);
        }
        byte[] arr = getTakePhotoOrder(getPosition(position));
        ret = mUsbDeviceConnection.bulkTransfer(epBulkOut, arr, 55, 5000);
        Log.i(tag, "拍照请求已经发送!");
        Receiveytes = new byte[220];
        ret = mUsbDeviceConnection.bulkTransfer(epBulkIn, Receiveytes, 220, 10000);
    }

    public void sendGetDataRequest(short index) {
        ret = mUsbDeviceConnection.bulkTransfer(epBulkOut, getDataOrder(index), 55, 5000);
//        Log.i(tag, "已经发送!" + index);
        Receiveytes = new byte[220];
        ret = mUsbDeviceConnection.bulkTransfer(epBulkIn, Receiveytes, 220, 10000);
//            Log.i(tag, "返回值为" + clsPublic.bytesToHexString(Receiveytes));
        byte[] data = new byte[200];
        System.arraycopy(Receiveytes, 9, data, 0, 200);
        System.arraycopy(data, 0, part1, index * 200, 200);
    }

//        findIntfAndEpt();
//                // 1,发送准备命令
//        ret = mUsbDeviceConnection.bulkTransfer(epBulkOut, buffer, buffer.length, 5000);
    // 2,接收发送成功信息
//        Receiveytes = new byte[];
//        requestAbyteStream(buffer1result);
//        requestAbyteStream(buffer2result);
//        requestAbyteStream(buffer3result);
//        requestAbyteStream(buffer4result);


//    void requestAbyteStream(byte[] save) {
//
//        ret = mUsbDeviceConnection.bulkTransfer(epBulkIn, Receiveytes, Receiveytes.length, 10000);
//        Log.i(tag, "接收返回值:" + String.valueOf(ret));
//        if (ret != 60) {
//            Toast.makeText(mContext, "接收返回值" + String.valueOf(ret), i).show();
//            return;
//        } else {
//            //查看返回值
//            Toast.makeText(mContext, clsPublic.bytesToHexString(Receiveytes), i).show();
//            Log.i(tag, clsPublic.bytesToHexString(Receiveytes));
//            saveData(Receiveytes, save);
//            Log.i(tag, clsPublic.bytesToHexString(save));
//        }
//    }

    //用UsbDeviceConnection 与 UsbInterface 进行端点设置和通讯
//    private void getEndpoint(UsbDeviceConnection connection, UsbInterface intf) {
//        if (intf.getEndpoint(1) != null) {
//            epBulkOut = intf.getEndpoint(1);
//        }
//        if (intf.getEndpoint(0) != null) {
//            epBulkIn = intf.getEndpoint(0);
//        }
//    }
//
//    void saveData(byte[] from, byte[] to) {
//        int j = 0;
//        for (int i = 0; i < 60; i++) {
//            if (i >= 6 && i < 58) {
//                to[j] = from[i];
//                j++;
//            }
//        }
//    }

    void registerReceiver() {
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

    class usbBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //作为host设备检测从设备是否连接
            if (intent.getAction().equals("android.hardware.usb.action.USB_DEVICE_ATTACHED")) {
                USBworkspace.setButton("连接设备", true);
            } else {
                USBworkspace.setButton("请确保usb正常连接", false);
            }

//作为从设备连接和断开的判断   android.hardware.usb.action.USB_STATE
//        if (intent.getExtras().getBoolean("connected")) {
//            USBConectLayout.changeUSBButtonState(true);
//        } else {
//            USBConectLayout.changeUSBButtonState(false);
//        }
        }
    }


    /**
     * 根据 下面 位置信息 返回拍照的 命令byte数组
     *
     * @param arr 位置信息
     * @return 对应命令byte数组
     */

    public static byte[] getTakePhotoOrder(byte[] arr) {
        byte[] ml = new byte[55];
//        Log.i("ml", ml.toString());
        ml[0] = 0x55;
//        ml[1]=0x00;
        ml[2] = 0x01;
        ml[4] = (byte) 0xE0;
        ml[5] = 0x01;
        if (arr == null) {
            return ml;
        } else {
            for (int i = 0; i < arr.length; i++) {
                ml[i + 10] = arr[i];
            }
        }
//        Log.i("ml_", clsPublic.bytesToHexString(ml));
        return ml;
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
//        byte[0]=
//        byte[1]=
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

//        Log.i("getPosition", clsPublic.bytesToHexString(arr));
        return arr;
    }

    /**
     * 根据 获取  第几个   data返回对应命令byte数组
     *
     * @param index 第几个data
     * @return 对应命令byte数组
     */
    static byte[] gdo = new byte[55];

    public static byte[] getDataOrder(short index) {
//        Log.i("ml", ml.toString());
        gdo[0] = 0x55;
//        ml[1]=0x00;
        gdo[2] = 0x02;
        gdo[4] = (byte) (index >> 8);
        gdo[5] = (byte) (index & 0x00ff);
        return gdo;
    }

    public void LoadDate() {
        ArrayList<Integer> gray1R = new ArrayList<>();
        ArrayList<Integer> gray1G = new ArrayList<>();
        ArrayList<Integer> gray1B = new ArrayList<>();

        ArrayList<Integer> gray2R = new ArrayList<>();
        ArrayList<Integer> gray2G = new ArrayList<>();
        ArrayList<Integer> gray2B = new ArrayList<>();

        ArrayList<Integer> gray3R = new ArrayList<>();
        ArrayList<Integer> gray3G = new ArrayList<>();
        ArrayList<Integer> gray3B = new ArrayList<>();

        ArrayList<Integer> gray4R = new ArrayList<>();
        ArrayList<Integer> gray4G = new ArrayList<>();
        ArrayList<Integer> gray4B = new ArrayList<>();

        ArrayList<Integer> gray5R = new ArrayList<>();
        ArrayList<Integer> gray5G = new ArrayList<>();
        ArrayList<Integer> gray5B = new ArrayList<>();

        ArrayList<Integer> gray6R = new ArrayList<>();
        ArrayList<Integer> gray6G = new ArrayList<>();
        ArrayList<Integer> gray6B = new ArrayList<>();

        ArrayList<Integer> gray7R = new ArrayList<>();
        ArrayList<Integer> gray7G = new ArrayList<>();
        ArrayList<Integer> gray7B = new ArrayList<>();

        ArrayList<Integer> gray8R = new ArrayList<>();
        ArrayList<Integer> gray8G = new ArrayList<>();
        ArrayList<Integer> gray8B = new ArrayList<>();

        ArrayList<Integer> gray9R = new ArrayList<>();
        ArrayList<Integer> gray9G = new ArrayList<>();
        ArrayList<Integer> gray9B = new ArrayList<>();
//        mContext.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
        if (mUsbDeviceConnection == null) {
            mUsbDeviceConnection = mUsbManager.openDevice(mUsbDevice);
        }
        for (short i = (short) 0; i < times; i++) {
//                    Log.i("-->>", i + "of");
            sendGetDataRequest(i);
        }
        datastate = 1;
        Log.i("get data end", "begin trainslate");
        for (int i = 0; i < part1.length; i += 2) {
//                                            byte a = (byte) (part1[i] >> 2);
//                                            byte b = (byte)(part1[i + 1] << 6);
//                                            gray[1/2]=(byte) (a+b);
            //0xCF 02  1100  1111  0000  0010

            //1011 0011
            GRAY[i / 2] = part1[i] + ((part1[i + 1] & 0x0ff) << 8);
            gray[i / 2] = (((part1[i] & 0x0ff) >> 2) + ((part1[i + 1] & 0x0ff) << 6));
//                    Log.i("gray", gray[1 / 2]+"");

            //0xFF2C2C2C

//                                            grayARGB[i/ 2] = (0xff000000 + (g << 16) +  (g << 8) + g);
//                                            grayARGB[2 * i] = (byte) 0xff;
//                                            grayARGB[2 * i + 1] = g;
//                                            grayARGB[2 * i + 2] = g;
//                                            grayARGB[2 * i + 3] = g;
        }
//i 行数(y)j列数(x)
        for (short i = 0; i < 479; i++) {
            for (short j = 0; j < 639; j++) {
                int p = i * 639 + j;
                //a=i*640+j
                int a = p + i, b = a + 1, c = a + 640, d = c + 1;
                if (i % 2 == 0) {
                    if (j % 2 == 0) {
                        int chose = 0xff000000 + (gray[d] << 16) + (((gray[b] + gray[c]) / 2) << 8) + gray[a];
//                                arr[p] = chose;
                        switch (inSample(j, i)) {
                            case 0:
                                arr[p] = chose;
                                break;
                            case 1:
                                gray1B.add(GRAY[a]);
                                arr[p] = 0xff000000;
                                break;
                            case 2:
                                gray2B.add(GRAY[a]);
                                arr[p] = 0xff000000;
                                break;
                            case 3:
                                gray3B.add(GRAY[a]);
                                arr[p] = 0xff000000;
                                break;
                            case 4:
                                gray4B.add(GRAY[a]);
                                arr[p] = 0xff000000;
                                break;
                            case 5:
                                gray5B.add(GRAY[a]);
                                arr[p] = 0xff000000;
                                break;
                            case 6:
                                gray6B.add(GRAY[a]);
                                arr[p] = 0xff000000;
                                break;
                            case 7:
                                gray7B.add(GRAY[a]);
                                arr[p] = 0xff000000;
                                break;
                            case 8:
                                gray8B.add(GRAY[a]);
                                arr[p] = 0xff000000;
                                break;
                            case 9:
                                gray9B.add(GRAY[a]);
                                arr[p] = 0xff000000;
                                break;
                        }
//                        if (inSample(j, i)) {
////                                    chose_colors.add(chose);
//                            grayB.add(GRAY[a]);
//                            arr[p] = 0xff000000;
//                        } else {
//                            arr[p] = chose;
//                        }
                    } else {
                        int chose = 0xff000000 + (gray[c] << 16) + (((gray[a] + gray[d]) / 2) << 8) + gray[b];
//                                arr[p] = chose;
                        switch (inSample(j, i)) {
                            case 0:
                                arr[p] = chose;
                                break;
                            case 1:
                                gray1G.add(GRAY[a]);
                                arr[p] = 0xff000000;
                                break;
                            case 2:
                                gray2G.add(GRAY[a]);
                                arr[p] = 0xff000000;
                                break;
                            case 3:
                                gray3G.add(GRAY[a]);
                                arr[p] = 0xff000000;
                                break;
                            case 4:
                                gray4G.add(GRAY[a]);
                                arr[p] = 0xff000000;
                                break;
                            case 5:
                                gray5G.add(GRAY[a]);
                                arr[p] = 0xff000000;
                                break;
                            case 6:
                                gray6G.add(GRAY[a]);
                                arr[p] = 0xff000000;
                                break;
                            case 7:
                                gray7G.add(GRAY[a]);
                                arr[p] = 0xff000000;
                                break;
                            case 8:
                                gray8G.add(GRAY[a]);
                                arr[p] = 0xff000000;
                                break;
                            case 9:
                                gray9G.add(GRAY[a]);
                                arr[p] = 0xff000000;
                                break;
                        }
//                        if (inSample(j, i)) {
//                            grayG.add(GRAY[a]);
//                            arr[p] = 0xff000000;
////                                    chose_colors.add(chose);
//                        } else {
//                            arr[p] = chose;
//                        }
                    }
                } else {
                    if (j % 2 == 0) {
                        int chose = 0xff000000 + (gray[b] << 16) + (((gray[a] + gray[d]) / 2) << 8) + gray[c];
//                                arr[p] = chose;
                        switch (inSample(j, i)) {
                            case 0:
                                arr[p] = chose;
                                break;
                            case 1:
                                gray1G.add(GRAY[a]);
                                arr[p] = 0xff000000;
                                break;
                            case 2:
                                gray2G.add(GRAY[a]);
                                arr[p] = 0xff000000;
                                break;
                            case 3:
                                gray3G.add(GRAY[a]);
                                arr[p] = 0xff000000;
                                break;
                            case 4:
                                gray4G.add(GRAY[a]);
                                arr[p] = 0xff000000;
                                break;
                            case 5:
                                gray5G.add(GRAY[a]);
                                arr[p] = 0xff000000;
                                break;
                            case 6:
                                gray6G.add(GRAY[a]);
                                arr[p] = 0xff000000;
                                break;
                            case 7:
                                gray7G.add(GRAY[a]);
                                arr[p] = 0xff000000;
                                break;
                            case 8:
                                gray8G.add(GRAY[a]);
                                arr[p] = 0xff000000;
                                break;
                            case 9:
                                gray9G.add(GRAY[a]);
                                arr[p] = 0xff000000;
                                break;
                        }
//                        if (inSample(j, i)) {
//                            grayG.add(GRAY[a]);
//                            arr[p] = 0xff000000;
////                                    chose_colors.add(chose);
//                        } else {
//                            arr[p] = chose;
//                        }
                    } else {
                        int chose = 0xff000000 + (gray[a] << 16) + (((gray[b] + gray[c]) / 2) << 8) + gray[d];
//                                arr[p] = chose;
                        switch (inSample(j, i)) {
                            case 0:
                                arr[p] = chose;
                                break;
                            case 1:
                                gray1R.add(GRAY[a]);
                                arr[p] = 0xff000000;
                                break;
                            case 2:
                                gray2R.add(GRAY[a]);
                                arr[p] = 0xff000000;
                                break;
                            case 3:
                                gray3R.add(GRAY[a]);
                                arr[p] = 0xff000000;
                                break;
                            case 4:
                                gray4R.add(GRAY[a]);
                                arr[p] = 0xff000000;
                                break;
                            case 5:
                                gray5R.add(GRAY[a]);
                                arr[p] = 0xff000000;
                                break;
                            case 6:
                                gray6R.add(GRAY[a]);
                                arr[p] = 0xff000000;
                                break;
                            case 7:
                                gray7R.add(GRAY[a]);
                                arr[p] = 0xff000000;
                                break;
                            case 8:
                                gray8R.add(GRAY[a]);
                                arr[p] = 0xff000000;
                                break;
                            case 9:
                                gray9R.add(GRAY[a]);
                                arr[p] = 0xff000000;
                                break;
                        }
//                        if (inSample(j, i)) {
//                            grayR.add(GRAY[a]);
//                            arr[p] = 0xff000000;
////                                    chose_colors.add(chose);
//                        } else {
//                            arr[p] = chose;
//                        }
                    }
                }
            }
        }
//        Log.i("grayR" + grayR.size() + "平均值", getArrayMean(grayR) + "");
//        Log.i("grayG" + grayG.size() + "平均值", getArrayMean(grayG) + "");
//        Log.i("grayB" + grayB.size() + "平均值", getArrayMean(grayB) + "");

        ContentValues values = new ContentValues();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        values.put("time", df.format(new Date()));

        values.put("R1Size", gray1R.size());
        values.put("G1Size", gray1G.size());
        values.put("B1Size", gray1B.size());

        values.put("R2Size", gray2R.size());
        values.put("G2Size", gray2G.size());
        values.put("B2Size", gray2B.size());

        values.put("R3Size", gray3R.size());
        values.put("G3Size", gray3G.size());
        values.put("B3Size", gray3B.size());

        values.put("R4Size", gray4R.size());
        values.put("G4Size", gray4G.size());
        values.put("B4Size", gray4B.size());

        values.put("R5Size", gray5R.size());
        values.put("G5Size", gray5G.size());
        values.put("B5Size", gray5B.size());

        values.put("R6Size", gray6R.size());
        values.put("G6Size", gray6G.size());
        values.put("B6Size", gray6B.size());

        values.put("R7Size", gray7R.size());
        values.put("G7Size", gray7G.size());
        values.put("B7Size", gray7B.size());

        values.put("R8Size", gray8R.size());
        values.put("G8Size", gray8G.size());
        values.put("B8Size", gray8B.size());

        values.put("R9Size", gray9R.size());
        values.put("G9Size", gray9G.size());
        values.put("B9Size", gray9B.size());

        values.put("R1Mean", getArrayMean(gray1R));
        values.put("G1Mean", getArrayMean(gray1G));
        values.put("B1Mean", getArrayMean(gray1B));

        values.put("R2Mean", getArrayMean(gray2R));
        values.put("G2Mean", getArrayMean(gray2G));
        values.put("B2Mean", getArrayMean(gray2B));

        values.put("R3Mean", getArrayMean(gray3R));
        values.put("G3Mean", getArrayMean(gray3G));
        values.put("B3Mean", getArrayMean(gray3B));

        values.put("R4Mean", getArrayMean(gray4R));
        values.put("G4Mean", getArrayMean(gray4G));
        values.put("B4Mean", getArrayMean(gray4B));

        values.put("R5Mean", getArrayMean(gray5R));
        values.put("G5Mean", getArrayMean(gray5G));
        values.put("B5Mean", getArrayMean(gray5B));

        values.put("R6Mean", getArrayMean(gray6R));
        values.put("G6Mean", getArrayMean(gray6G));
        values.put("B6Mean", getArrayMean(gray6G));

        values.put("R7Mean", getArrayMean(gray7R));
        values.put("G7Mean", getArrayMean(gray7G));
        values.put("B7Mean", getArrayMean(gray7B));

        values.put("R8Mean", getArrayMean(gray8R));
        values.put("G8Mean", getArrayMean(gray8G));
        values.put("B8Mean", getArrayMean(gray8B));

        values.put("R9Mean", getArrayMean(gray9R));
        values.put("G9Mean", getArrayMean(gray9G));
        values.put("B9Mean", getArrayMean(gray9B));

        values.put("R1Mean", getArrayMean(gray1R));
        values.put("G1Mean", getArrayMean(gray1G));
        values.put("B1Mean", getArrayMean(gray1B));

        values.put("R2Mean", getArrayMean(gray2R));
        values.put("G2Mean", getArrayMean(gray2G));
        values.put("B2Mean", getArrayMean(gray2B));

        values.put("R3Mean", getArrayMean(gray3R));
        values.put("G3Mean", getArrayMean(gray3G));
        values.put("B3Mean", getArrayMean(gray3B));

        values.put("R4Mean", getArrayMean(gray4R));
        values.put("G4Mean", getArrayMean(gray4G));
        values.put("B4Mean", getArrayMean(gray4B));

        values.put("R5Mean", getArrayMean(gray5R));
        values.put("G5Mean", getArrayMean(gray5G));
        values.put("B5Mean", getArrayMean(gray5B));

        values.put("R6Mean", getArrayMean(gray6R));
        values.put("G6Mean", getArrayMean(gray6G));
        values.put("B6Mean", getArrayMean(gray6G));

        values.put("R7Mean", getArrayMean(gray7R));
        values.put("G7Mean", getArrayMean(gray7G));
        values.put("B7Mean", getArrayMean(gray7B));

        values.put("R8Mean", getArrayMean(gray8R));
        values.put("G8Mean", getArrayMean(gray8G));
        values.put("B8Mean", getArrayMean(gray8B));

        values.put("R9Mean", getArrayMean(gray9R));
        values.put("G9Mean", getArrayMean(gray9G));
        values.put("B9Mean", getArrayMean(gray9B));


        db.insert("RGB", null, values);
//                Log.i("chose_colors", chose_colors.size() + "");
        //将目标区域绘制为黑色,用于校准
//                chose_colors.clear();

        datastate = -1;
        Log.i("arr", Integer.toHexString(arr[1]));
//                Log.i("gray length", gray.length + "");
//                Log.i("gtsyARGB", grayARGB.toString() + "");
//            }
//        });

    }

    float getArrayMean(ArrayList<Integer> arr) {
        float total = 0;
        for (Integer i : arr) {
            total += i;
        }
        return total / arr.size();
    }

    float getArryWc(ArrayList<Integer> arr) {
        int max = 0, min = 1024;
        for (Integer i : arr) {
            if (i > max) {
                max = i;
            }
            if (i < min) {
                min = i;
            }
        }
        return (max - min) / getArrayMean(arr);
    }

    //
    short centerX = length / 2;
    short centerY = height / 2;

    short between = 145;//135px(9mm)
    short r = 40;//45px(3mm)
    int rr = (int) Math.pow(r, 2);

    /**
     * 判断一个像素点是否在取样区域
     *
     * @param x 横坐标
     * @param y 纵坐标
     * @return true false
     */
    int inSample(short x, short y) {

        if (y > centerY - between - r && y < centerY - between + r) {
            if (Math.pow((x - (centerX - between)), 2) + Math.pow((y - (centerY - between)), 2) < rr) {
                return 1;
            } else if (Math.pow((x - centerX), 2) + Math.pow((y - (centerY - between)), 2) < rr) {
                return 2;
            } else if (Math.pow((x - (centerX + between)), 2) + Math.pow((y - (centerY - between)), 2) < rr) {
                return 3;
            }
        } else if (y > centerY - r && y < centerY + r) {
            if (Math.pow((x - (centerX - between)), 2) + Math.pow((y - centerY), 2) < rr) {
                return 4;
            } else if (Math.pow((x - centerX), 2) + Math.pow((y - centerY), 2) < rr) {
                return 5;
            } else if (Math.pow((x - (centerX + between)), 2) + Math.pow((y - centerY), 2) < rr) {
                return 6;
            }
        } else if (y > centerY + between - r && y < centerY + between + r) {
            if (Math.pow((x - (centerX - between)), 2) + Math.pow((y - (centerY + between)), 2) < rr) {
                return 7;
            } else if (Math.pow((x - centerX), 2) + Math.pow((y - (centerY + between)), 2) < rr) {
                return 8;
            } else if (Math.pow((x - (centerX + between)), 2) + Math.pow((y - (centerY + between)), 2) < rr) {
                return 9;
            }
        }
        return 0;
    }

    class SimpleData {
        private String time;
        private ArrayList<MathData> Datas;

        public SimpleData(String time, ArrayList<MathData> datas) {
            this.time = time;
            Datas = datas;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public ArrayList<MathData> getDatas() {
            return Datas;
        }

        public void setDatas(ArrayList<MathData> datas) {
            Datas = datas;
        }

        class MathData {
            int num;
            float mean;
            float wc;

            public MathData(int num, float wc, float mean) {
                this.num = num;
                this.wc = wc;
                this.mean = mean;
            }

            public int getNum() {
                return num;
            }

            public void setNum(int num) {
                this.num = num;
            }

            public float getWc() {
                return wc;
            }

            public void setWc(float wc) {
                this.wc = wc;
            }

            public float getMean() {
                return mean;
            }

            public void setMean(float mean) {
                this.mean = mean;
            }
        }
    }
}
