package measurement.color.com.xj_919.and.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;

import measurement.color.com.xj_919.R;
import measurement.color.com.xj_919.and.fragment.Test.ResultData;

/**
 * Created by wpc on 2016/11/18.
 */

public class PagerAdapterForTestGuide extends PagerAdapter {

    Context context;
    LayoutInflater inflater;
    ArrayList<View> pagers;
    ListView result;
    ArrayList<String> resultString = new ArrayList<>();
    HashSet<String> data;
    TextView tv;
    ProgressBar pb;

    ImageView mImageView;
    TextView mTextView;
    int pb_max;

    int[] ImgIds = {R.mipmap.unknow_pic, R.mipmap.unknow_pic, R.mipmap.unknow_pic, R.mipmap.unknow_pic};
    String[] strArrays;
    String[] tips = {"1", "2", "3", "4"};

    public PagerAdapterForTestGuide(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        pagers = new ArrayList<>();
        strArrays = context.getResources().getStringArray(R.array.gride_str_array);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View v;
        if (position != ImgIds.length) {
            v = inflater.inflate(R.layout.item_vp_test, null);
            mImageView = (ImageView) v.findViewById(R.id.iv_test_gride);
            mTextView = (TextView) v.findViewById(R.id.tv_test_gride);
//            mImageView.setImageDrawable(context.getResources().getDrawable(ImgIds[position]));
            mTextView.setText(strArrays[position]);

        } else {
            data = new HashSet<>();
            v = inflater.inflate(R.layout.enditem_vp_test, null);
            result = (ListView) v.findViewById(R.id.lv_result_test);
            result.setAdapter(new ArrayAdapter<String>(context, R.layout.simple_list_item_1, resultString));
            tv = (TextView) v.findViewById(R.id.testing_test);
            pb = (ProgressBar) v.findViewById(R.id.pb_test);
        }
        container.addView(v);
        pagers.add(v);
        return v;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(pagers.get(position));
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public void CleanListView() {
        resultString = new ArrayList<>();
        result.setAdapter(new ArrayAdapter<String>(context, R.layout.simple_list_item_1, resultString));
    }

    public void onRefesh(ArrayList<ArrayList<ResultData>> datas) {
        Log.i("datas", datas.toString());
        resultString = new ArrayList<>();
        for (int i = 0; i < datas.size(); i++) {
            for (int j = 0; j < datas.get(i).size(); j++) {
                if (datas.get(i).get(j).isHasfound()) {
                    String res = "区域" + (i + 1) + datas.get(i).get(j).toString();
                    if (resultString.size() == 0 | !resultString.contains(res)) {
                        resultString.add(res);
                    }
                }
            }
        }
        if (resultString != null) {
            if (resultString.size() == 0) {
                resultString.add("没有检测到危险物品");
            }

        }
        Log.i("resultString", resultString.toString());
        result.setAdapter(new ArrayAdapter<String>(context, R.layout.simple_list_item_1, resultString));
    }

    public ArrayList<String> add(ArrayList<String> datas) {
        for (String str : datas) {
            data.add(str);
        }
        ArrayList<String> sum = new ArrayList<>();
        for (String str : data) {
            sum.add(str);
        }
        return sum;
    }

    //-1 progress
    public void setProgress(@Nullable Integer max, int progress) {
        if (max != null) {
            pb_max = max;
            pb.setMax(max);
            pb.setProgress(progress);
        } else {
            if (progress == -4) {
                tv.setText("请录入白板信息");
            } else if (progress == -3) {
                tv.setText("设备未打开");
            } else if (progress == -2) {
                tv.setText("请正确放置被测物品");//测试不可用 请检查连接并正确放置被测物品//请正确放置被测物品
            } else if (progress == -1) {
                tv.setText("点击测试,开始检测");
                pb.setProgress(0);
            } else if (progress == pb_max) {
                tv.setText("检测完成");
                pb.setProgress(progress);
            } else {
                tv.setText("正在检测...");
                pb.setProgress(progress);
            }
        }
    }
}