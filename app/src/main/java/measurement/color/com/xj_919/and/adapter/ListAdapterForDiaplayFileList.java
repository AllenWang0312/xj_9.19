package measurement.color.com.xj_919.and.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import measurement.color.com.xj_919.R;
import measurement.color.com.xj_919.and.Utils.io.FileOpener.FileInfo;

/**
 * Created by wpc on 2016/11/17.
 */

public class ListAdapterForDiaplayFileList extends BaseAdapter {

    ViewHolder vh = null;
    ArrayList<FileInfo> mDatas;
    Context mContext;
    LayoutInflater inflater;
   public ListAdapterForDiaplayFileList(ArrayList<FileInfo> datas, Context context) {
        mDatas = datas;
        mContext=context;
        inflater=LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {

        if (convertView == null) {
            vh = new ViewHolder();
            convertView =inflater.inflate(
                    R.layout.chose_file_item, arg2, false);
            // TODO Auto-generated method stub
            vh.name = (TextView) convertView
                    .findViewById(R.id.filename_chosefiledialogitem);
            vh.dirpath = (TextView) convertView
                    .findViewById(R.id.dirpath_chosefiledialogitem);
            vh.secondinfo = (TextView) convertView
                    .findViewById(R.id.secondinfo_chosefiledialogitem);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        vh.name.setText(mDatas.get(position).getName());
        vh.dirpath.setText(mDatas.get(position).getDirPath());
        vh.secondinfo.setText(mDatas.get(position).getSecondInfo());

        return convertView;
    }

    @Override
    public long getItemId(int index) {
        // TODO Auto-generated method stub
        return index;
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return mDatas.get(arg0);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mDatas.size();
    }

    class ViewHolder {
        TextView name;
        TextView secondinfo;
        TextView dirpath;

        // public ViewHolder(TextView name, TextView secondinfo, TextView
        // dirpath) {
        // super();
        // this.name = name;
        // this.secondinfo = secondinfo;
        // this.dirpath = dirpath;
        // }

    }
}
