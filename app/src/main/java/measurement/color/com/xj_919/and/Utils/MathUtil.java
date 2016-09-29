package measurement.color.com.xj_919.and.Utils;

import java.util.ArrayList;

/**
 * Created by wpc on 2016/9/28.
 */

public class MathUtil {

    public static int getArrayTotal(ArrayList<Integer> arr){
        int total = 0;
        for (Integer i : arr) {
            total += i;
        }
        return total ;
    }

    public static  float getArrayMean(ArrayList<Integer> arr) {

        return (float) getArrayTotal(arr) / arr.size();
    }

    public static  float getArrayWc(ArrayList<Integer> arr) {
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
