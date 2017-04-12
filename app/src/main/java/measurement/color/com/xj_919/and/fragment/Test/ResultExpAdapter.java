package measurement.color.com.xj_919.and.fragment.Test;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import measurement.color.com.xj_919.R;

/**
 * Created by swant on 2017/4/8.
 */

public class ResultExpAdapter implements ExpandableListAdapter {
    Context mContext;
    public static int[] colors = {
            R.color.part_1_bg, R.color.part_2_bg,
            R.color.part_3_bg, R.color.part_4_bg,
            R.color.part_5_bg, R.color.part_6_bg
    };

    public ArrayList<PartData> getDatas() {
        return mdatas;
    }

    ArrayList<PartData> mdatas;
    boolean fond_gmsj;

    public ResultExpAdapter(Context context, ArrayList<PartData> datas) {
        fond_gmsj = false;
        mContext = context;
        mdatas = datas;
    }

    public int getDataSize() {
        return mdatas.size();
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public int getGroupCount() {
        return mdatas.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return 1;
    }

    @Override
    public Object getGroup(int i) {
        return mdatas.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return mdatas.get(i).getResults().get(0);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        PartData part = mdatas.get(i);
        View v = LayoutInflater.from(mContext).inflate(R.layout.group_expand, null);
        CardView cv = (CardView) v.findViewById(R.id.cv_group_result);
        Log.i("index", part.getResults().get(0).getIndex() + "");
//        cv.setCardBackgroundColor(colors[part.getResults().get(0).getIndex()]);
//        cv.setBackgroundColor(colors[part.getResults().get(0).getIndex()]);
        TextView tv = (TextView) v.findViewById(R.id.result_group_expand);
//        TextView tv2 = (TextView) v.findViewById(R.id.result_group_exact_expand);

        tv.setText(part.getCategroy_name());
//        tv2.setText(part.getFindList());
        return v;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        JudgeCondition item = mdatas.get(i).getResults().get(0);
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_expand, null);
        TextView tv = (TextView) v.findViewById(R.id.result_item_expand);
        tv.setText(item.getName()
//                + "(" + item.getFind_num() + "/" + item.getNum() + ")"
        );
        return v;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void onGroupExpanded(int i) {

    }

    @Override
    public void onGroupCollapsed(int i) {

    }

    @Override
    public long getCombinedChildId(long l, long l1) {
        return 0;
    }

    @Override
    public long getCombinedGroupId(long l) {
        return 0;
    }

}
