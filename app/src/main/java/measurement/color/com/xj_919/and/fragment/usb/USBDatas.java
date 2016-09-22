package measurement.color.com.xj_919.and.fragment.usb;

import android.util.Log;

import measurement.color.com.xj_919.and.Utils.clsPublic;

/**
 * Created by wpc on 2016/9/14.
 */
public class USBDatas {

    public static byte[] buffer = {
            (byte) 0xbb, 0x01, 0x02, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0xff, 0x00
    };

    public static byte[] buffer1result = new byte[52];
    public static byte[] buffer2result = new byte[52];
    public static byte[] buffer3result = new byte[52];
    public static byte[] buffer4result = new byte[52];


    //2163
    public static short length = 640;
    public static short height = 480;
    public static byte content = 8;

    public static byte[] part1 = new byte[length * (height / content) * 2];
    public static byte[] gray = new byte[length * (height / content)];

    public static byte partH = (byte) (height / content);

    public static short times = (short) (length * (height / content) / 100);


    /**
     * 根据 下面 位置信息 返回拍照的 命令byte数组
     *
     * @param arr 位置信息
     * @return 对应命令byte数组
     */

    public static byte[] getTakePhotoOrder(byte[] arr) {
        byte[] ml = new byte[55];
        Log.i("ml", ml.toString());
        ml[0] = 0x55;
//        ml[1]=0x00;
        ml[2] = 0x01;
        for (int i = 0; i < arr.length; i++) {
            ml[i + 10] = arr[i];
        }
        Log.i("ml_", clsPublic.bytesToHexString(ml));
        return ml;
    }

    /**
     * 根据 position计算 图片起始位置
     *
     * @param position 第几张图片
     * @return 8位byte  strXL strXH strYL strYH
     * endXL endXH endYL endYH
     */

    public static byte[] getPosition(byte position) {
        byte[] arr = new byte[8];

//        byte[0]=
//        byte[1]=

        short strY = (short) (partH * (position - 1));
        arr[2] = (byte) (strY & 0x00ff);
        arr[3] = (byte) (strY >> 8);


        arr[5] = (byte) (length >> 8);
        arr[4] = (byte) (length & 0x00ff);

        short endY = (short) (partH * position);
        arr[7] = (byte) (endY >> 8);
        arr[6] = (byte) (endY & 0x00ff);
        Log.i("getPosition", clsPublic.bytesToHexString(arr));
        return arr;
    }

    /**
     * 根据 获取  第几个   data返回对应命令byte数组
     *
     * @param index 第几个data
     * @return 对应命令byte数组
     */

    public static byte[] getDataOrder(short index) {
        byte[] ml = new byte[55];
        Log.i("ml", ml.toString());
        ml[0] = 0x55;
//        ml[1]=0x00;
        ml[2] = 0x02;
        ml[4] = (byte) (index >> 8);
        ml[5] = (byte) (index & 0x00ff);
        Log.i("ml_", clsPublic.bytesToHexString(ml));
        return ml;
    }
}
