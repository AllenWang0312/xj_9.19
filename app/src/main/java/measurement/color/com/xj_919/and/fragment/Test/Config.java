package measurement.color.com.xj_919.and.fragment.Test;

import java.util.ArrayList;

import measurement.color.com.xj_919.and.Utils.soft.clsPublic;

/**
 * Created by wpc on 2016/12/3.
 */

public class Config {

    PositionSets mPositionSets;
    boolean has_init = false;
    static Config instance;

    public static Config getInstance() {
        if (instance == null) {
            instance = new Config();
        }
        return instance;
    }
//    区域1阳性结果: 硝酸甘油酯类（液体炸药）、硝胺类(黑索金、奥克托金)、亚硝酸盐类
//
//    区域3与区域4同时显阳性报硝酸铵，区域3单独显色报硝酸盐，区域4单独显色报铵盐
//
//    区域5单独显色报：过氧化物类（TATP、HMTD、过硫酸盐）
//
//    区域6单独显色报：氯酸盐


    public static String names[] = {
            "铵盐",
            "TNT (三硝基甲苯)",//1  黑红 浅红
            "DNT (二硝基甲苯)",//2  黑绿
            "硫磺",//3
            "RDX(黑索金)",//4 浅红   硝酸甘油酯类（液体炸药）、硝胺类(黑索金、奥克托金)、亚硝酸盐类
            "尿素", //5
            "硝酸盐", //6
            "氯酸盐", //7
            "过氧化物类", //8  （TATP、HMTD、过硫酸盐）
            "PA(苦味酸)", //9
            "pNT(对硝基甲苯)", //10
            "HMX(奥克托金)"//11
    };

    private Config() {

    }

    String model_number; //2byte
    String version;// 1 byte

    ArrayList<ArrayList<ResultData>> settings;

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


    public void initSettings(byte[] data) {
        if (data[0] != 0x55) {
            return;
        }
        byte[] position = new byte[8];
        System.arraycopy(data, 5, position, 0, 8);
        mPositionSets = PositionSets.getPositionSetsWitdShortArray(clsPublic.toShortArray(position));
        int index = 13;
        byte[] bytes = new byte[40];
        settings = new ArrayList<>();
        ArrayList<ResultData> results = new ArrayList<>();
//        results.add(new ResultData(4,      100, 75, 75,     10, 10, 10,     50, null));//0xff644b4b
//        results.add(new ResultData(11,     100, 75, 75,     10, 10, 10,     50, null));

        index += 40;
        System.arraycopy(data, index, bytes, 0, 40);//1
        results.add(ResultData.getResultDataFromShortArray(clsPublic.toShortArray(bytes)));
        index += 40;
        System.arraycopy(data, index, bytes, 0, 40);//2
        results.add(ResultData.getResultDataFromShortArray(clsPublic.toShortArray(bytes)));
        index += 40;
        System.arraycopy(data, index, bytes, 0, 40);//3
        results.add(ResultData.getResultDataFromShortArray(clsPublic.toShortArray(bytes)));
        index += 40;
        System.arraycopy(data, index, bytes, 0, 40);//9
        results.add(ResultData.getResultDataFromShortArray(clsPublic.toShortArray(bytes)));

        index = 13;
        System.arraycopy(data, index, bytes, 0, 40);//4
        results.add(ResultData.getResultDataFromShortArray(clsPublic.toShortArray(bytes)));

//        System.arraycopy(data,index,bytes,0,40);index+=40;
//        results.add(ResultData.getResultDataFromShortArray(clsPublic.toShortArray(bytes)));
        settings.add(results);


        index = 53;
//        results = new ArrayList<>();
//        System.arraycopy(data, index, bytes, 0, 40);
//        results.add(ResultData.getResultDataFromShortArray(clsPublic.toShortArray(bytes)));
        index += 40;
//        System.arraycopy(data, index, bytes, 0, 40);
//        results.add(ResultData.getResultDataFromShortArray(clsPublic.toShortArray(bytes)));
        index += 40;
//        System.arraycopy(data, index, bytes, 0, 40);
//        results.add(ResultData.getResultDataFromShortArray(clsPublic.toShortArray(bytes)));
        index += 40;
//        System.arraycopy(data, index, bytes, 0, 40);
//        results.add(ResultData.getResultDataFromShortArray(clsPublic.toShortArray(bytes)));
//        results.add(new ResultData(1,     130, 100, 100,     10, 10, 10,    500, null));//0xff823231
//        results.add(new ResultData(2,     20, 30, 30,         10,10, 10,       500, null));//0xff141e1e
//        results.add(new ResultData(3,      92, 66, 53,       10, 10, 10,      300, null));// 0xff5c4235
//        results.add(new ResultData(9,     117, 74, 57,         10, 10, 10,    300, null));//0xff754a39
//        settings.add(results);

        results = new ArrayList<>();
        index += 40;
        System.arraycopy(data, index, bytes, 0, 40);
        results.add(ResultData.getResultDataFromShortArray(clsPublic.toShortArray(bytes)));
//        results.add(new ResultData(6,     30, 30, 40,         30, 30, 30,      300, null));//0xff1e1e28
        settings.add(results);

        results = new ArrayList<>();
        index += 40;
        System.arraycopy(data, index, bytes, 0, 40);
        results.add(ResultData.getResultDataFromShortArray(clsPublic.toShortArray(bytes)));
//        results.add(new ResultData(6,      160, 110, 80,        30, 30, 30,  300, null));//0xffa06e50
        settings.add(results);

        results = new ArrayList<>();
        index += 40;
        System.arraycopy(data, index, bytes, 0, 40);
        results.add(ResultData.getResultDataFromShortArray(clsPublic.toShortArray(bytes)));
//        results.add(new ResultData(8,     40, 40, 40,         30, 30, 30,        1000, null));// 0xff282828
        settings.add(results);

        results = new ArrayList<>();
        index += 40;
        System.arraycopy(data, index, bytes, 0, 40);
        results.add(ResultData.getResultDataFromShortArray(clsPublic.toShortArray(bytes)));
//        results.add(new ResultData(7,      60, 70, 90,       10, 10, 10,         500, null));//0xff3c465a
        settings.add(results);
        has_init = true;

//        Log.i("positionsets",mPositionSets.toString
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

    public static String[] getNames() {
        return names;
    }

    public static void setNames(String[] names) {
        Config.names = names;
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


    public ArrayList<ArrayList<ResultData>> getSettings() {
        if (settings == null) {
            initSettings(USBManager.getInstance().TransceiverInstance.sendTakeConfigRequest());
        }
        for (int i = 0; i < settings.size(); i++) {
            for (int j = 0; j < settings.get(i).size(); j++) {
                settings.get(i).get(j).setHasfound(false);
            }
        }
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

    public void setSettings(ArrayList<ArrayList<ResultData>> settings) {
        this.settings = settings;
    }
}
