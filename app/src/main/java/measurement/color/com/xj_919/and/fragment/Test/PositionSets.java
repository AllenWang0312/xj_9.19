package measurement.color.com.xj_919.and.fragment.Test;

/**
 * Created by wpc on 2016/12/8.
 */

class PositionSets {
    short cent_x, cent_y, cent_bew, r;

    PositionSets(Short cent_x, Short cent_y, Short cent_bew, Short r) {
        this.cent_x = cent_x;
        this.cent_y = cent_y;
        this.cent_bew = cent_bew;
        this.r = r;
    }

    @Override
    public String toString() {
        return
                 "cent_x" + String.valueOf(cent_x)
                + "cent_y" + String.valueOf(cent_y)
                + "cent_bew" + String.valueOf(cent_bew)
                + "r" + String.valueOf(r)
                ;
    }

    public static PositionSets getPositionSetsWitdShortArray(Short[] datas) {
        return new PositionSets(datas[0], datas[1], datas[2], datas[3]);
    }

    public short getCent_x() {
        return cent_x;
    }

    public void setCent_x(short cent_x) {
        this.cent_x = cent_x;
    }

    public short getCent_y() {
        return cent_y;
    }

    public void setCent_y(short cent_y) {
        this.cent_y = cent_y;
    }

    public short getCent_bew() {
        return cent_bew;
    }

    public void setCent_bew(short cent_bew) {
        this.cent_bew = cent_bew;
    }

    public short getR() {
        return r;
    }

    public void setR(short r) {
        this.r = r;
    }
}