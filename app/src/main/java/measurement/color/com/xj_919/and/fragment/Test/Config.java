package measurement.color.com.xj_919.and.fragment.Test;

import java.util.ArrayList;

import measurement.color.com.xj_919.and.Utils.soft.clsPublic;

import static measurement.color.com.xj_919.and.fragment.Test.Consts.judge_name;
import static measurement.color.com.xj_919.and.fragment.Test.Consts.reagent_names;

/**
 * Created by wpc on 2016/12/3.
 */

public class Config {

    public static PositionSets mPositionSets;
    public static boolean has_init = false;
    public static Config instance;

    public static Config getInstance() {
        if (instance == null) {
            instance = new Config();
        }
        return instance;
    }

    private Config() {

    }

    String model_number; //2byte
    String version;// 1 byte

    public static ArrayList<ArrayList<JudgeCondition>> settings;

//    55 00 07 5500
// 9101
// F700
// 9A00
// 3600

// 04 00 64 00  4B 00  4B 00  0A 000A 000A 0032 0000 0000 0000 0000 0000 0000   0000 0000 0000 0000 0000 0000

// 01 00 37 00  2F 00  2F 00  05 0007 0007 00C8 0000 0000 0000 0000 0000 0000   0000 0000 0000 0000 0000 0000
// 02 00 2D 00  3C 00  46 00  0F 000A 000A 00C8 0000 0000 0000 0000 0000 0000   0000 0000 0000 0000 0000 0000
// 03 00 55 00  42 00  31 00  05 000A 0006 0090 0100 0000 0000 0000 0000 0000   0000 0000 0000 0000 0000 0000
// 09 00 75 00  42 00  35 00  0A 000A 000A 002C 0100 0000 0000 0000 0000 0000   0000 0000 0000 0000 0000 0000

// 06 00 1E 00  1E 00  28 00  1E 001E 001E 002C 0100 0000 0000 0000 0000 0000   0000 0000 0000 0000 0000 0000
// 06 00 82 00  5A 00  46 00  0A 000A 000A 002C 0100 0000 0000 0000 0000 0000   0000 0000 0000 0000 0000 0000
// 07 00 3C 00  3C 00  5A 00  3C 003C 003C 00C8 00FD FF00 0000 0000 0000 000A   0000 0000 0000 0000 0000 0000


    public static void initSettings(byte[] data) {
        if (data[0] != 0x55) {
            return;
        }
        byte[] position = new byte[8];
        System.arraycopy(data, 5, position, 0, 8);
        mPositionSets = PositionSets.getPositionSetsWitdShortArray(clsPublic.toShortArray(position));
        int index = 13;
        byte[] bytes = new byte[40];
        settings = new ArrayList<>();
        ArrayList<JudgeCondition> results = new ArrayList<>();
        index = 13;
        System.arraycopy(data, index, bytes, 0, 40);//4
        results.add(getResultDataFromShortArray(reagent_names[4], clsPublic.toShortArray(bytes)));
        settings.add(results);

        results = new ArrayList<>();
        index += 40;
        System.arraycopy(data, index, bytes, 0, 40);//黑
        results.add(getResultDataFromShortArray(judge_name[0], clsPublic.toShortArray(bytes)));
        index += 40;
        System.arraycopy(data, index, bytes, 0, 40);//浅红
        results.add(getResultDataFromShortArray(judge_name[1], clsPublic.toShortArray(bytes)));
        index += 40;
        System.arraycopy(data, index, bytes, 0, 40);//浅绿
        results.add(getResultDataFromShortArray(judge_name[2], clsPublic.toShortArray(bytes)));
        index += 40;
        System.arraycopy(data, index, bytes, 0, 40);//pa
        results.add(getResultDataFromShortArray(judge_name[3], clsPublic.toShortArray(bytes)));
        index += 40;
        System.arraycopy(data, index, bytes, 0, 40);//深红 硫磺
        results.add(getResultDataFromShortArray(judge_name[4], clsPublic.toShortArray(bytes)));
        index += 40;
        System.arraycopy(data, index, bytes, 0, 40);//浅红 硫磺
        results.add(getResultDataFromShortArray(judge_name[5], clsPublic.toShortArray(bytes)));
        settings.add(results);


        results = new ArrayList<>();
        index += 40;
        System.arraycopy(data, index, bytes, 0, 40);
        results.add(getResultDataFromShortArray(reagent_names[6], clsPublic.toShortArray(bytes)));
//        results.add(new ResultData(6,     30, 30, 40,         30, 30, 30,      300, null));//0xff1e1e28
        settings.add(results);

        results = new ArrayList<>();
        index += 40;
        System.arraycopy(data, index, bytes, 0, 40);
        results.add(getResultDataFromShortArray(reagent_names[0], clsPublic.toShortArray(bytes)));
//        results.add(new ResultData(6,      160, 110, 80,        30, 30, 30,  300, null));//0xffa06e50
//        settings.add(results);
//
//        results = new ArrayList<>();
        index += 40;
        System.arraycopy(data, index, bytes, 0, 40);
        results.add(getResultDataFromShortArray(reagent_names[12], clsPublic.toShortArray(bytes)));
//        results.add(new ResultData(6,      160, 110, 80,        30, 30, 30,  300, null));//0xffa06e50
        settings.add(results);

        results = new ArrayList<>();
        index += 40;
        System.arraycopy(data, index, bytes, 0, 40);
        results.add(getResultDataFromShortArray(reagent_names[8], clsPublic.toShortArray(bytes)));
//        results.add(new ResultData(8,     40, 40, 40,         30, 30, 30,        1000, null));// 0xff282828
        settings.add(results);

        results = new ArrayList<>();
        index += 40;
        System.arraycopy(data, index, bytes, 0, 40);
        results.add(getResultDataFromShortArray(reagent_names[7], clsPublic.toShortArray(bytes)));
//        results.add(new ResultData(7,      60, 70, 90,       10, 10, 10,         500, null));//0xff3c465a
        settings.add(results);
        has_init = true;
    }

    public boolean isHas_init() {
        return has_init;
    }

    public void setHas_init(boolean has_init) {
        this.has_init = has_init;
    }

    public static void setInstance(Config instance) {
        Config.instance = instance;
    }


    public String getModel_number() {
        return model_number;
    }

    public void setModel_number(String model_number) {
        this.model_number = model_number;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }


    public static ArrayList<ArrayList<JudgeCondition>> getSettings() {
            initSettings(USBManager.getInstance().TransceiverInstance.sendTakeConfigRequest());
        return settings;
    }

    public PositionSets getPositionSets() {
        if (mPositionSets == null) {
//            if (USBManager.getInstance().TransceiverInstance.checkPermission()) {
//                initSettings(USBManager.getInstance().TransceiverInstance.sendTakeConfigRequest());
//            }
        }
        return mPositionSets;
    }

    public void setPositionSets(PositionSets positionSets) {
        mPositionSets = positionSets;
    }


    public static JudgeCondition getResultDataFromShortArray(String name, Short[] shorts) {
        Short[] diff = new Short[6];
        System.arraycopy(shorts, 8, diff, 0, 6);
//        if (shorts[0] == 3) {
//            return new JudgeCondition(shorts[0], (short) (shorts[1] - 5), shorts[2], (short) (shorts[3] + 3), (short) (shorts[4] + 5), (short) (shorts[5] + 3), (short) (shorts[6] + 5), shorts[7], diff);
//        } else {
        return new JudgeCondition(name, shorts[0], shorts[1], shorts[2], shorts[3], shorts[4], shorts[5], shorts[6], shorts[7], diff);
//        }
    }
}
