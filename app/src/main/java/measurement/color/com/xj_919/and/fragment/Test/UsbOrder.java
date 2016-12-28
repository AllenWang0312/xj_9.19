package measurement.color.com.xj_919.and.fragment.Test;

/**
 * Created by wpc on 2016/12/3.
 */

public class UsbOrder {

    /**
     * 根据 下面 位置信息 返回拍照的 命令byte数组
     *
     * @param arr 位置信息
     * @return 对应命令byte数组
     */

    public static byte[] getTakePhotoOrder(byte[] arr, int delay) {
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
    public static byte[] getVolentOrder() {
        byte[] ml = new byte[55];
        ml[0] = 0x55;
        ml[1] = 0x00;
        ml[2] = 0x09;
//        ml[53] = (byte) 0xff;
        return ml;
    }
    public static byte[] getTurnDownOrder() {
        byte[] ml = new byte[55];
        ml[0] = 0x55;
        ml[1] = 0x00;
        ml[2] = 0x0a;
//        ml[53] = (byte) 0xff;
        return ml;
    }

    public static byte[] getTakeConfigOrder() {
        byte[] ml = new byte[55];
        ml[0] = 0x55;
        ml[1] = 0x00;
        ml[2] = 0x07;
        ml[53] = (byte) 0xff;
        return ml;
    }

    public static byte[] getNotifyUserOrder(boolean redLight) {
        byte[] nu = new byte[55];
        nu[0] = 0x55;
        nu[1] = 0x00;
        nu[2] = 0x08;
        if(redLight){
            nu[3] =  0x01;
        }else {
            nu[3] =  0x00;
        }

//        nu[53] = (byte) 0xff;
        return nu;
    }

    /**
     * 根据 获取  第几个   data返回对应命令byte数组
     *
     * @param index 第几个data
     * @return 对应命令byte数组
     */
    public static byte[] getDataOrder(short index) {
        byte[] gdo = new byte[55];
        gdo[0] = 0x55;
        gdo[2] = 0x02;
        gdo[4] = (byte) (index >> 8);
        gdo[5] = (byte) (index & 0x00ff);
        return gdo;
    }
}
