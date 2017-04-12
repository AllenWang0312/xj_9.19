package measurement.color.com.xj_919.and.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;

import measurement.color.com.xj_919.R;
import measurement.color.com.xj_919.and.fragment.Test.PartData;
import measurement.color.com.xj_919.and.fragment.Test.ResultExpAdapter;

/**
 * Created by wpc on 2016/11/18.
 */

public class PagerAdapterForTestGuide extends PagerAdapter {

    Context context;
    LayoutInflater inflater;
    ArrayList<View> pagers;
    //    ListView result;
    ExpandableListView exp;
    HashSet<String> data;
    LinearLayout ll;
    ImageView iv;
    TextView tv;
    ProgressBar pb;

    ImageView mImageView;
    TextView mTextView;
    int pb_max;

    int[] ImgIds = {R.mipmap.unknow_pic, R.mipmap.unknow_pic, R.mipmap.unknow_pic, R.mipmap.unknow_pic};
    String[] strArrays;

    public PagerAdapterForTestGuide(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        pagers = new ArrayList<>();
        strArrays = context.getResources().getStringArray(R.array.gride_str_array);
    }

    public ArrayList<PartData> getResult() {
        return adapter.getDatas();
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
            exp = (ExpandableListView) v.findViewById(R.id.epl_result_test);
            ll = (LinearLayout) v.findViewById(R.id.ll_bg_title);

            iv = (ImageView) v.findViewById(R.id.iv_warning_result);
//            result = (ListView) v.findViewById(R.id.lv_result_test);
//            result.setAdapter(new ArrayAdapter<String>(context, R.layout.simple_list_item_1, resultString));
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

    ResultExpAdapter adapter;

    public void onRefesh(ArrayList<PartData> datas) {
        adapter = new ResultExpAdapter(context, datas);
        exp.setAdapter(adapter);
        if (!HasDangerous()) {
            setText(tv, Color.WHITE, Color.GREEN, "没有发现爆炸物");
            iv.setVisibility(View.GONE);
        } else {
            setText(tv, Color.WHITE, Color.RED, "发现爆炸物");
            iv.setVisibility(View.VISIBLE);
        }
    }


    public boolean HasDangerous() {
        return adapter.getDataSize() != 0;
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
                setText(tv, Color.YELLOW, "请录入白板信息");
            } else if (progress == -3) {
                setText(tv, Color.YELLOW, "设备未打开");
            } else if (progress == -2) {
                //测试不可用 请检查连接并正确放置被测物品//请正确放置被测物品
                setText(tv, Color.YELLOW, "请正确放置被测物品");
            } else if (progress == -1) {
                setText(tv, Color.WHITE, "点击测试,开始检测");
                pb.setProgress(0);
            } else if (progress == pb_max) {
                setText(tv, Color.WHITE, "检测完成");
                pb.setProgress(progress);
            } else {
                setText(tv, Color.WHITE, 0x00000000, "正在检测...");
                iv.setVisibility(View.GONE);
                pb.setProgress(progress);
            }
        }
    }

    void setText(TextView tv, int color, String content) {
        tv.setText(content);
        tv.setTextColor(color);
    }

    void setText(TextView tv, int color, int bg_color, String content) {
        tv.setText(content);
        ll.setBackgroundColor(bg_color);
        tv.setTextColor(color);
    }
}