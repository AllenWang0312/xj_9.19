package measurement.color.com.xj_919.and.fragment.usb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import measurement.color.com.xj_919.and.Utils.clsPublic;
import measurement.color.com.xj_919.and.Utils.toast;

import static measurement.color.com.xj_919.and.fragment.usb.USBDatas.*;

/**
 * Created by wpc on 2016/9/19.
 */
public class USBManager {

    private static USBManager instance;
    private final String tag = "USBManager";


    private final int i = Toast.LENGTH_SHORT;
    //-1设备不支持usbhost，0usbmanager可用1derive可用2interface可用3endpoint可用
    private static int state = -1;


    static Context mContext;
    static private UsbManager mUsbManager;
    private usbBroadcastReceiver myUSBReceiver;
    private UsbDevice mUsbDevice;
    private UsbInterface mInterface;
    private UsbDeviceConnection mUsbDeviceConnection;

    private int ret = -100;

    ArrayList<String> USBDeviceList;

    private UsbEndpoint epBulkOut, epControl, epIntEndpointOut, epIntEndpointIn, epBulkIn;


    private byte[] Receiveytes;  //接收信息字节


    public static USBManager getInstance(Context context) {
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

    public void setState(int i) {
        this.state = i;
    }

    public void sendRequest() {
//        findIntfAndEpt();
        if (mUsbDeviceConnection == null) {
            mUsbDeviceConnection = mUsbManager.openDevice(mUsbDevice);
        }

//                // 1,发送准备命令
        ret = mUsbDeviceConnection.bulkTransfer(epBulkOut, buffer, buffer.length, 5000);
        Log.i(tag, "已经发送!");
        // 2,接收发送成功信息
        Receiveytes = new byte[60];
        requestAbyteStream(buffer1result);
        requestAbyteStream(buffer2result);
        requestAbyteStream(buffer3result);
        requestAbyteStream(buffer4result);
    }

    void requestAbyteStream(byte[] save) {

        ret = mUsbDeviceConnection.bulkTransfer(epBulkIn, Receiveytes, Receiveytes.length, 10000);
        Log.i(tag, "接收返回值:" + String.valueOf(ret));
        if (ret != 60) {
            Toast.makeText(mContext, "接收返回值" + String.valueOf(ret), i).show();
            return;
        } else {
            //查看返回值
            Toast.makeText(mContext, clsPublic.bytesToHexString(Receiveytes), i).show();
            Log.i(tag, clsPublic.bytesToHexString(Receiveytes));
            saveData(Receiveytes, save);
            Log.i(tag, clsPublic.bytesToHexString(save));
        }
    }

    //用UsbDeviceConnection 与 UsbInterface 进行端点设置和通讯
    private void getEndpoint(UsbDeviceConnection connection, UsbInterface intf) {
        if (intf.getEndpoint(1) != null) {
            epBulkOut = intf.getEndpoint(1);
        }
        if (intf.getEndpoint(0) != null) {
            epBulkIn = intf.getEndpoint(0);
        }
    }

    void saveData(byte[] from, byte[] to) {
        int j = 0;
        for (int i = 0; i < 60; i++) {
            if (i >= 6 && i < 58) {
                to[j] = from[i];
                j++;
            }
        }
    }

    void registerReceiver() {
        myUSBReceiver = new usbBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.setPriority(800);
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
                USBworkspace.setButtonEnable(true);
            } else {
                USBworkspace.setButtonEnable(false);
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
