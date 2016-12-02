package measurement.color.com.xj_919.and.fragment.datahistory;

import java.util.ArrayList;

/**
 * Created by wpc on 2016/10/8.
 */

public class SimpleData {


    String tips;
    String data;
    String time;
    int id;
    String result;
    String absPath;
    String cache;

    public SimpleData(int id,  String tips, String data, String time, String result,String absPath,String cache) {

        this.data = data;
        this.tips = tips;
        this.id = id;
        this.time = time;
        this.result = result;
        this.absPath=absPath;
        this.cache=cache;
    }

    public String getCache() {
        return cache;
    }

    public void setCache(String cache) {
        this.cache = cache;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getId() {
        return id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getAbsPath() {
        return absPath;
    }

    public void setAbsPath(String absPath) {
        this.absPath = absPath;
    }
    //    public static ArrayList<SimpleData> getTestData(int size) {
//        ArrayList<SimpleData> test = new ArrayList<>();
//        for (int i = 0; i < size; i++) {
//            test.add(new SimpleData(false,i, "projectName", "Tips", "2016-9-20", "12:00:00", 0, 0, 0, "合格"));
//        }
//        return test;
//    }

    public static ArrayList<String> getArray(SimpleData data) {
//        {"编号", "日期", "时间", "R1", "G1", "B1", "结果", "备注"};
        ArrayList<String> list = new ArrayList<>();
        list.add(data.getId() + "");
        list.add(data.getData());
        list.add(data.getTime());
        list.add(data.getResult());
        list.add(data.getTips());

        return list;
    }
}