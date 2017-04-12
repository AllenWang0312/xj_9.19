package measurement.color.com.xj_919.and.fragment.Test;

import android.util.Log;

import java.util.ArrayList;

import static measurement.color.com.xj_919.and.fragment.Test.Consts.groupNames;
import static measurement.color.com.xj_919.and.fragment.Test.Consts.reagent_names;

/**
 * Created by swant on 2017/4/8.
 */

public class PartData {
    String categroy_name;
    ArrayList<JudgeCondition> results;

    public ArrayList<JudgeCondition> getResults() {
        return results;
    }

    public void setResults(ArrayList<JudgeCondition> results) {
        this.results = results;
    }

    public void setResult(JudgeCondition results) {
        if (results == null) {
            this.results = null;
        } else {
            this.results = new ArrayList<JudgeCondition>();
            this.results.add(results);
        }
    }

    public PartData(int area, ArrayList<JudgeCondition> found) {
        categroy_name =
                "区域[" + (area + 1) + "]" +
                        groupNames[area];
        results = getResultFromJudgeCondition(found, area);
    }


    private ArrayList<JudgeCondition> getResultFromJudgeCondition(ArrayList<JudgeCondition> found, int area) {
        ArrayList<JudgeCondition> results;
        switch (area) {
            case 1:
                JudgeCondition res = new JudgeCondition("null", 1,200, 100);
                boolean[] result = getResult(found);
                Log.i("result[0]", String.valueOf(result[0]));
                JudgeCondition jud0 = found.get(0);
                JudgeCondition jud = found.get(1);
                JudgeCondition jud2 = found.get(2);
                JudgeCondition jud3 = found.get(3);
                JudgeCondition jud4 = found.get(4);
                JudgeCondition jud5 = found.get(5);
                if (result[0]) {
                    res = new JudgeCondition(reagent_names[1] + "或" + reagent_names[2], (short) 1, jud0.getNum(), jud0.getFind_num());
//                    jud.getNum() + jud2.getNum()
//                    jud.getFind_num() + jud2.getFind_num()
                    if (result[1] && !result[2]) {
                        res = new JudgeCondition(reagent_names[1], (short) 1, jud.getNum(), jud.getFind_num());
                    } else if (!result[1] && result[2]) {
                        res = new JudgeCondition(reagent_names[2], (short) 1, jud2.getNum(), jud2.getFind_num());
                    }
                } else {
                    if (result[1]) {
                        res = new JudgeCondition(reagent_names[1], 1, jud.getNum(), jud.getFind_num());
                        if (result[4]) {
                            res = new JudgeCondition(reagent_names[1] + "或" + reagent_names[3], (short) 1, jud.getNum(), jud.getFind_num());
                        }
                    }
                    if (result[2]) {
                        res = new JudgeCondition(reagent_names[2], 1, jud2.getNum(), jud2.getFind_num());
                    }
                    if (result[3]) {
                        res = new JudgeCondition(reagent_names[9], 1, jud3.getNum(), jud3.getFind_num());
                        if (result[5]) {
                            res = new JudgeCondition(reagent_names[9] + "或" + reagent_names[3], 1, jud3.getNum() + jud5.getNum(), jud3.getFind_num() + jud5.getFind_num());
                        }
                    }
                    if (result[4]) {
                        res = new JudgeCondition(reagent_names[3], 1, jud4.getNum(), jud4.getFind_num());
                    }
                    if (result[5]) {
                        res = new JudgeCondition(reagent_names[3], 1, jud5.getNum(), jud5.getFind_num());
                    }
                }
//                Log.i("resut", res.getResult_string());
                results = new ArrayList<>();
                results.add(res);
                return results;


            case 3:
                results = new ArrayList<>();
                for (int i = 0; i < found.size(); i++) {
                    JudgeCondition judge = found.get(i);
                    results.add(new JudgeCondition(judge.getName(), 3, judge.getNum(), judge.getFind_num()));
                }

                return results;

            default:
                JudgeCondition j = found.get(0);
                results = new ArrayList<>();
                results.add(new JudgeCondition(j.getName(), j.getIndex(), j.getNum(), j.getFind_num()));
                return results;
        }
    }

    private boolean[] getResult(ArrayList<JudgeCondition> found) {
        boolean[] bs = new boolean[found.size()];
        for (int i = 0; i < found.size(); i++) {
            bs[i] = found.get(i).isEnough();
        }
        return bs;
    }

    public boolean isHasFound() {
        if (results != null && results.size() > 0) {
            for (JudgeCondition jud : results) {
                if (jud.isHasfound()) {
                    return true;
                }
            }
        }
        return false;
    }


    public String getCategroy_name() {
        return categroy_name;
    }

    public void setCategroy_name(String categroy_name) {
        this.categroy_name = categroy_name;
    }

    //
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(categroy_name + String.valueOf(isHasFound()));
        sb.append(results.toString());
        return sb.toString();
    }
}
