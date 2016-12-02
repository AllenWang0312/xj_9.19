package measurement.color.com.xj_919.and.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import measurement.color.com.xj_919.R;

/**
 * Created by wpc on 2016/11/18.
 */

public class PagerAdapterForTestGuide extends PagerAdapter {

    Context context;
    LayoutInflater inflater;
    ArrayList<View> pagers;
    ListView result;
    TextView tv;

    int[] ImgIds = {R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher};
    String[] tips = {"1", "2", "3", "4"};

    public PagerAdapterForTestGuide(Context context) {
        this.context=context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View v;
        if (position != pagers.size()) {
            v = inflater.inflate(R.layout.item_vp_test, null);
        } else {
            v = inflater.inflate(R.layout.enditem_vp_test, null);
            result = (ListView) v.findViewById(R.id.lv_result_test);
            tv = (TextView) v.findViewById(R.id.testing_test);
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

    //-1 点击检测 0 正在检测 1 检测完成
    public void initState(int state) {
        if (state == -1) {
            tv.setText("点击测试,开始检测");
        } else if (state == 0) {
            tv.setText("正在检测...");
        } else if (state == 1) {
            tv.setText("检测完成");
        }

    }

    public void onFinish(ArrayList<String> datas) {
        if (datas != null) {
            result.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, datas));
        }
    }
}