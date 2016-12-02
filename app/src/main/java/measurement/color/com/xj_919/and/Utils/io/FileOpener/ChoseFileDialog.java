package measurement.color.com.xj_919.and.Utils.io.FileOpener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import java.util.ArrayList;

import measurement.color.com.xj_919.R;


@SuppressLint({"NewApi", "ValidFragment"})
public class ChoseFileDialog extends DialogFragment {

//	FileUtils.showChoseFileToPlayDialog(FileUtils.FILE_PATH, ".ppt",
//	SoftUpdateActivity.this);

    ListView lv;
    ArrayList<FileInfo> mData;
    Activity activity;
//    onRefeshListener mOnRefeshListener;

    OnItemClickListener mClickListener;
    AdapterView.OnItemLongClickListener mOnItemLongClickListener;

    public ChoseFileDialog(Activity activity, ArrayList<FileInfo> items,
                           OnItemClickListener mClickListener, @Nullable AdapterView.OnItemLongClickListener onItemLongClickListener) {
        this.activity = activity;
        this.mClickListener = mClickListener;
        this.mOnItemLongClickListener = onItemLongClickListener;
        this.mData = items;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //1 通过样式定义
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Mdialog);
        //2代码设置 无标题 无边框
        //setStyle(DialogFragment.STYLE_NO_TITLE|DialogFragment.STYLE_NO_FRAME,0);
    }

    @Override
    public void onStart() {
        super.onStart();
//        getDialog().getWindow().getAttributes().width=getResources().getDisplayMetrics().widthPixels;
//        getDialog().getWindow().setGravity(Gravity.BOTTOM);//对齐方式
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        //3 在此处设置 无标题 对话框背景色
        //getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        // //对话框背景色
        //getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.RED));
        //getDialog().getWindow().setDimAmount(0.5f);//背景黑暗度

        //不能在此处设置style
        // setStyle(DialogFragment.STYLE_NORMAL,R.style.Mdialog);//在此处设置主题样式不起作用

        Builder builder = new Builder(getActivity());
        builder.setTitle("选择文件");
        View view = LayoutInflater.from(getActivity()).inflate(
                R.layout.chosefiledialog_layout, null);
        lv = (ListView) view.findViewById(R.id.lv_chosefiledialog);
        lv.setAdapter(new MyAdapter(mData, getActivity()));
        lv.setOnItemClickListener(mClickListener);
        lv.setOnItemLongClickListener(mOnItemLongClickListener);
        builder.setView(view);
        builder.setCancelable(false);
        builder.setNegativeButton("取消", null);
        return builder.create();
    }

    public void refeshList(ArrayList<FileInfo> newData) {
        lv.setAdapter(new MyAdapter(newData, getActivity()));
    }

//    interface onRefeshListener{
//        void onRefesh();
//    }

}
