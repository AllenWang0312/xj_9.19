package measurement.color.com.xj_919.and.Utils.soft;

import java.util.ArrayList;

/**
 * Created by wpc on 2016/9/28.
 */

public class MathUtil {

    public static boolean hasTrueValue(boolean[] bools){
        for(boolean boo:bools){
            if(boo){
                return true;
            }
        }
        return false;
    }
    public static boolean allTrueValue(boolean[] bools){
        for(boolean boo:bools){
            if(!boo){

                return false;
            }
        }
        return true;
    }

    public static int getIntegersMean(int[] ints) {

        return (int) (getIntegersTotal(ints) / ints.length);
    }

    public static long getIntegersTotal(int[] ints) {
        long total = 0;
        for (int i = 0; i < ints.length; i++) {
            total += ints[i];
        }
        return total;
    }

    public static byte getBytesMean(byte[] bytes) {

        return (byte) (getBytesTotal(bytes) / bytes.length);
    }

    public static long getBytesTotal(byte[] bytes) {
        long total = 0;
        for (int i = 0; i < bytes.length; i++) {
            total += bytes[i];
        }
        return total;
    }

    public static short getShortsMean(short[] shorts ) {

        return (short) (getBytesTotal(shorts) / shorts.length);
    }

    public static long getBytesTotal(short[] shorts) {
        long total = 0;
        for (int i = 0; i < shorts.length; i++) {
            total += shorts[i];

        }
        return total;
    }

    public static int getArrayTotal(ArrayList<Integer> arr) {
        int total = 0;
        for (Integer i : arr) {
            total += i;
        }
        return total;
    }

    public static float getArrayMean(ArrayList<Integer> arr) {

        return (float) getArrayTotal(arr) / arr.size();
    }

    public static float getArrayWc(ArrayList<Integer> arr) {
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
}
