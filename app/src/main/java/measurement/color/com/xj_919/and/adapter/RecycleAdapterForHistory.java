package measurement.color.com.xj_919.and.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import measurement.color.com.xj_919.R;
import measurement.color.com.xj_919.and.activity.app;
import measurement.color.com.xj_919.and.fragment.datahistory.SimpleData;

/**
 * Created by wpc on 2016/10/8.
 */

public class RecycleAdapterForHistory extends RecyclerView.Adapter<RecycleAdapterForHistory.MyViewHolder> {

    private List<Boolean> listCheck;
    private static List<Integer> checkedID = new ArrayList<>();
    private ArrayList<SimpleData> mData;
    private Context context;

    public RecycleAdapterForHistory(Context context, ArrayList<SimpleData> mData, @Nullable List<Boolean> listCheck) {
        if (listCheck != null) {
            this.listCheck = listCheck;
        }
        this.context = context;
        this.mData = mData;
    }



    public interface OnItemClickLitener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_table_layout, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
//        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        SimpleData data = mData.get(position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getLayoutPosition();
                mOnItemClickLitener.onItemClick(holder.itemView, pos);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int pos = holder.getLayoutPosition();
                mOnItemClickLitener.onItemLongClick(holder.itemView, pos);
//                removeData(pos);
                return false;
            }
        });
//        holder.tv_time.setTag(position);
        holder.position = position;
        if (listCheck != null) {
            holder.cb.setChecked(listCheck.get(position));
        }
//        holder.cb.setClickable(false);
        holder.cb.setVisibility(app.showCheckBoxs?View.VISIBLE:View.INVISIBLE);
        holder.tv_data.setText(data.getData());
        holder.tv_result.setText(data.getResult());
        holder.tv_tips.setText(data.getTips());

        holder.tv_time.setText(data.getTime());
//            holder.iv.setImageResource(R.drawable.uu);
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
//          implements View.OnLongClickListener ,View.OnClickListener
    {

        int position;
        CheckBox cb;
        TextView tv_data, tv_time, tv_result, tv_tips;

        public MyViewHolder(View v) {
            super(v);
            cb = (CheckBox) v.findViewById(R.id.cb_item_table);
            tv_result = (TextView) v.findViewById(R.id.tv_result_item_table);
            tv_tips = (TextView) v.findViewById(R.id.tv_tips_item_table);
            tv_data = (TextView) v.findViewById(R.id.tv_data_item_table);
            tv_data = (TextView) v.findViewById(R.id.tv_data_item_table);
            tv_time = (TextView) v.findViewById(R.id.tv_time_item_table);

        }
    }

    public List<Boolean> getListCheck() {
        return listCheck;
    }

    public void setListCheck(List<Boolean> listCheck) {
        this.listCheck = listCheck;
    }

    public static List<Integer> getCheckedID() {
        return checkedID;
    }

    public static void setCheckedID(List<Integer> checkedID) {
        RecycleAdapterForHistory.checkedID = checkedID;
    }

    public void addCheckedItem(Integer i) {
        checkedID.add(0, i);
    }
}
