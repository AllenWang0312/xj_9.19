package measurement.color.com.xj_919.and.fragment.Test;

import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by wpc on 2016/12/3.
 */

public class ResultData {

    String names;

    short index;
    short num;
    short r, g, b, r_range, g_range, b_range;
    Short[] differences;

    boolean hasfound = false;

    public static ResultData getResultDataFromShortArray(Short[] shorts) {
        Short[] diff = new Short[6];
        System.arraycopy(shorts, 8, diff, 0, 6);
        return new ResultData(shorts[0], shorts[1], shorts[2], shorts[3], shorts[4], shorts[5], shorts[6], shorts[7], diff);
    }

    public ResultData(Short index,
                      @Nullable Short r, @Nullable Short g, @Nullable Short b,
                      @Nullable Short r_range, @Nullable Short g_range, @Nullable Short b_range,
                      Short num, @Nullable Short[] differences) {
        this.index = index;
        names = Config.names[(int) this.getIndex()];
        this.r = r;
        this.g = g;
        this.b = b;

        this.r_range = r_range;
        this.g_range = g_range;
        this.b_range = b_range;
        this.num = num;
        this.differences = differences;
        Log.i("ResultData"+index,detial());
    }

    public Short[] getDifferences() {
        return differences;
    }

    public void setDifferences(Short[] differences) {
        this.differences = differences;
    }

    public short getIndex() {
        return index;
    }

    public void setIndex(short index) {
        this.index = index;
    }

    public boolean isHasfound() {
        return hasfound;
    }

    public void setHasfound(boolean hasfound) {
        this.hasfound = hasfound;
    }

    public short getNum() {
        return num;
    }

    public void setNum(short num) {
        this.num = num;
    }

    public short getR() {
        return r;
    }

    public void setR(short r) {
        this.r = r;
    }

    public short getG() {
        return g;
    }

    public void setG(short g) {
        this.g = g;
    }

    public short getB() {
        return b;
    }

    public void setB(short b) {
        this.b = b;
    }

    public short getR_range() {
        return r_range;
    }

    public void setR_range(short r_range) {
        this.r_range = r_range;
    }

    public short getG_range() {
        return g_range;
    }

    public void setG_range(short g_range) {
        this.g_range = g_range;
    }

    public short getB_range() {
        return b_range;
    }

    public void setB_range(short b_range) {
        this.b_range = b_range;
    }

    public String getNames() {
        return names;
    }

    public void setNames(String names) {
        this.names = names;
    }

    public String detial() {
        return"r:"+ r + "g:" + g + "b:" + b + "r_range:" + r_range + "g_range:" + g_range + "b_range:" + b_range;
    }

    @Override
    public String toString() {
        return (this.isHasfound() ? "发现[" : "未发现[") + (int) this.getIndex() + "]号样品" + names;
    }
}