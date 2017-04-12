package measurement.color.com.xj_919.and.fragment.Test;

import android.util.Log;

/**
 * Created by swant on 2017/4/8.
 */

public class JudgeCondition {

    //一定存在 1 可能存在0 一定不存在-1;
    Integer state;

    String name;

    int index;

    int num;
    int find_num;

    short a_r, a_g, a_b, i_r, i_g, i_b;
    Short[] differences;

    public boolean isHasfound() {
        Log.i("jud",toString());
        if (state != null) {
            if (state == 1) {
                return true;
            } else {
                return false;
            }
        }
        return find_num > num;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public int getFind_num() {
        return find_num;
    }

    public void setFind_num(short find_num) {
        this.find_num = find_num;
    }

    public JudgeCondition(String name, int index, int num, int find_num) {
        this.name = name;
        this.index = index;
        this.num = num;
        this.find_num = find_num;
    }

    public JudgeCondition(String name, int index, short a_r, short a_g, short a_b, short i_r, short i_g, short i_b, int num, Short[] differences) {
        this.name = name;
        this.index = index;
        this.num = num;
        this.a_r = a_r;
        this.a_g = a_g;
        this.a_b = a_b;
        this.i_r = i_r;
        this.i_g = i_g;
        this.i_b = i_b;
        find_num = 0;
        this.differences = differences;
    }

    public Short[] getDifferences() {
        return differences;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDifferences(Short[] differences) {
        this.differences = differences;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(short index) {
        this.index = index;
    }

    public boolean isEnough() {
        return find_num > num;
    }


    public int getNum() {
        return num;
    }

    public void setNum(short num) {
        this.num = num;
    }

    public short getA_r() {
        return a_r;
    }

    public void setA_r(short a_r) {
        this.a_r = a_r;
    }

    public short getA_g() {
        return a_g;
    }

    public void setA_g(short a_g) {
        this.a_g = a_g;
    }

    public short getA_b() {
        return a_b;
    }

    public void setA_b(short a_b) {
        this.a_b = a_b;
    }

    public short getI_r() {
        return i_r;
    }

    public void setI_r(short i_r) {
        this.i_r = i_r;
    }

    public short getI_g() {
        return i_g;
    }

    public void setI_g(short i_g) {
        this.i_g = i_g;
    }

    public short getI_b() {
        return i_b;
    }

    public void setI_b(short i_b) {
        this.i_b = i_b;
    }


    public String detial() {
        return "r:" + a_r + "g:" + a_g + "b:" + a_b
                + "r_:" + i_r + "g_:" + i_g + "b_:" + i_b;
    }

    @Override
    public String toString() {

        return name + String.valueOf(state)
                + "r:" + a_r + "g:" + a_g + "b:" + a_b
                + "r_:" + i_r + "g_:" + i_g + "b_:" + i_b
                + "(" + find_num + "/" + num + ")"
                ;
    }

    public void incress() {
        find_num += 1;
    }
}
