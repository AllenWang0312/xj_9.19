package measurement.color.com.xj_919.and.fragment.datahistory;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import measurement.color.com.xj_919.R;
import measurement.color.com.xj_919.and.Utils.DB.MyDBHelper;
import measurement.color.com.xj_919.and.Utils.SP.Consts;
import measurement.color.com.xj_919.and.Utils.io.ExcelHelper;
import measurement.color.com.xj_919.and.Utils.io.FileOpener.ChoseFileDialog;
import measurement.color.com.xj_919.and.Utils.io.FileOpener.FileInfo;
import measurement.color.com.xj_919.and.Utils.io.FileUtils;
import measurement.color.com.xj_919.and.Utils.T;
import measurement.color.com.xj_919.and.activity.app;
import measurement.color.com.xj_919.and.adapter.RecycleAdapterForHistory;
import measurement.color.com.xj_919.and.fragment.bluetooth.BlueToothManager;
import measurement.color.com.xj_919.and.view.Divider.DividerItemDecoration;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by wpc on 2016/9/30.
 */

public class DataHistory extends Fragment implements View.OnClickListener {

    static DataHistory instance;

    public static DataHistory getInstance() {
        if (instance == null) {
            instance = new DataHistory();
        }
        return instance;
    }

    public static SelectDialog.selectData selectdata = null;
    private Context context;
    private BlueToothManager mBlueToothManager;
    private RecycleAdapterForHistory.OnItemClickLitener mOnItemClickLitener;
    private View view;
    private static CheckBox v_top;
    private boolean selectAll = false;
    private SwipeRefreshLayout srl;

    private RecyclerView rv;
    private RecycleAdapterForHistory adapter;

    private ArrayList<SimpleData> mData;

    private Button print;
    private MyDBHelper dbHelper;
    private SQLiteDatabase db;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    refreshCheckState(selectAll);
                    setAdatper();
//        rv.addItemDecoration(new DividerGridItemDecoration(context));
                    break;
            }
        }
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.datahestroy_fragment, container, false);
        mData = new ArrayList<>();
        context = getActivity();
        findViewAndSetActions();
        mBlueToothManager = BlueToothManager.getInstance(getActivity(), print);
        refresh();
        return view;
    }

    private void findViewAndSetActions() {
        srl = (SwipeRefreshLayout) view.findViewById(R.id.sr_table);
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
                srl.setRefreshing(false);
            }
        });
        v_top = (CheckBox) view.findViewById(R.id.cb_top_hestory);
        v_top.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (v_top.isChecked()) {
                    selectAll = true;
                    refresh();
                } else {
                    selectAll = false;
                    refresh();
                }
            }
        });
        rv = (RecyclerView) view.findViewById(R.id.rv_table);
//        printfile = (Button) view.findViewById(R.id.printfile_hestroy);
        view.findViewById(R.id.outputEx_hestroy).setOnClickListener(this);
        view.findViewById(R.id.blue_print_hestroy).setOnClickListener(this);
        view.findViewById(R.id.delete_hestroy).setOnClickListener(this);
        view.findViewById(R.id.find_hestroy).setOnClickListener(this);


        rv.setLayoutManager(new LinearLayoutManager(context));
        //        mRecyclerView.setLayoutManager(new GridLayoutManager(context,4));
        //        rv.setLayoutManager(layout);
        //        rv.setAdapter(adapter);
        rv.setItemAnimator(new DefaultItemAnimator());

        rv.addItemDecoration(new DividerItemDecoration(
                getActivity(), DividerItemDecoration.HORIZONTAL_LIST));

        rv.setHasFixedSize(true);
        mOnItemClickLitener = new RecycleAdapterForHistory.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                if (app.showCheckBoxs) {
                    if (justdelete) {
                        mHandler.sendEmptyMessage(0);
//                        adapter.notifyDataSetChanged();
                    }
                    justdelete = false;
                    if (app.CheckState.get(position)) {
                        app.CheckState.set(position, false);
                    } else {
                        app.CheckState.set(position, true);
                    }
//                    mHandler.sendEmptyMessage(0);
                    adapter.notifyDataSetChanged();
                } else {
                    int id = mData.get(position).getId();
                    new DetialDialog(getActivity(), db, id).show(getActivity().getFragmentManager(), "DetialDialog");
                    Log.i("detial", "show");
                    /////
                }
            }

            @Override
            public void onItemLongClick(View view, final int position) {
//                switchCheckState();
//               Vibrator vibrator = (Vibrator)getActivity().getSystemService(Context.VIBRATOR_SERVICE);
//                long [] pattern = {100,400};   // 停止 开启 停止 开启
//                vibrator.vibrate(pattern,-1);           //重复两次上面的pattern 如果只想震动一次，index设为-1
            }
        };
    }

    boolean justdelete = false;
    ArrayList<FileInfo> files;
    ChoseFileDialog choseFileDialog;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.outputEx_hestroy:
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("选择操作");
                builder.setItems(new String[]{"导出Excel", "管理Excel文件"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        if (which == 0) {
                            FileUtils.createOrExistsDir(FileUtils.Excel);
                            int size = mData.size();
                            if (size != 0) {
                                String filename = mData.get(0).getId() + "_" + mData.get(size - 1).getId();
                                if (new File(FileUtils.Excel, filename + ".xls").exists()) {
                                    T.show(context, "文件已存在请勿重复创建");
                                } else {
                                    if (ExcelHelper.writeExcel(mData, ExcelHelper.title, FileUtils.Excel, filename, ExcelHelper.Excel.poi)) {
                                        T.show(context, "导出成功" + FileUtils.Excel + "/" + filename + ".xls");
                                    }
                                }
                            } else {
                                T.show(context, "没有数据");
                            }

                        } else {
                            files = FileUtils.getFileInfoListWithDirPathAndEnd(FileUtils.Excel, ".xls");
                            if (files.size() != 0) {
                                choseFileDialog = new ChoseFileDialog((Activity) context, files, new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        FileInfo select = files.get(position);
                                        String abs = select.getDirPath() + "/" + select.getName();
                                        FileUtils.playFileWithSystemSeveice((Activity) context, abs);
                                    }
                                }, new AdapterView.OnItemLongClickListener() {
                                    @Override
                                    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                                        choseFileDialog.dismiss();
                                        FileInfo select = files.get(position);
                                        final String abs = select.getDirPath() + "/" + select.getName();
                                        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                                        builder1.setTitle("删除文件");
                                        builder1.setMessage("是否删除文件" + abs);
                                        builder1.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (FileUtils.deleteFileIfExist(abs)) {
//                                                files = FileUtils.getFileInfoListWithDirPathAndEnd(FileUtils.EXCEL_OUTPUT_PATH, ".xls");
                                                    T.show(context, "删除成功");
                                                }
                                            }
                                        });
                                        builder1.setNegativeButton("取消", null);
                                        builder1.create().show();
                                        return false;
                                    }
                                });
                                choseFileDialog.show(((Activity) context).getFragmentManager(), "chosefiledialog");
                            } else {
                                T.show(context, "没有该类型的文件");
                            }

                        }
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.create().show();

                break;
//            case R.id.printfile_hestroy:
// 勿删
//        printfile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                FileUtils.showChoseFileToPlayDialog(FileUtils.FILE_PATH, ".pdf",
////                        getActivity());
//
//                FileUtils.MakeDirIfNotexist(FileUtils.FILE_PATH);
//                final ArrayList<FileInfo> datas = FileUtils.getFileInfoListWithDirPathAndEnd(FileUtils.FILE_PATH, ".pdf");
//                if (datas.size() != 0) {
//                    new ChoseFileDialog(getActivity(), datas, new AdapterView.OnItemClickListener() {
//                        @Override
//                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
////                            Log.i("system if", PhoneUtils.getPhoneStatus(context));
////                            SharedPreferences sharedPreferences = context.getSharedPreferences(Consts.SYSTEM_SETTING_PREFERENCE, MODE_PRIVATE);
////                            Log.i("test", sharedPreferences.getString(Consts.KEY_PRINT_START, "null"));
//
//                            //调用系统打开文件操作
//                            FileInfo info = datas.get(position);
////                            FileUtils.playFileWithSystemSeveice((Activity) context,info.getDirPath()+info.getName());
//
//                            //调用系统打印服务
//                            Printer printer = new Printer(getActivity());
//                            printer.PrintPDFwithAbsPath(FileUtils.FILE_PATH + datas.get(position).getName());
//
////                            final Bundle params = new Bundle();
////                            params.putString(QQShare.SHARE_TO_QQ_TITLE, info.getName());
////                            params.putString(QQShare.SHARE_TO_QQ_SUMMARY,"summary");//副标题
////
////
////                            params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, "/storage/emulated/0/xj_919/img/2016-10-19_14:00:48.png");
////                            params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, info.getDirPath()+info.getName());//点击相应的uri
//////                            params.putString(QQShare.SHARE_TO_QQ_AUDIO_URL, mEditTextAudioUrl.getText().toString());
//////                            params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, imageUrl.getText().toString());
////
////                            params.putString(QQShare.SHARE_TO_QQ_APP_NAME,"xj919");
////                            params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE,QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
//////                            params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, mExtarFlag);
////                            ((MainActivity)context).getHandler().post(new Runnable() {
////                                @Override
////                                public void run() {
////                                    app .mTencent.shareToQQ(getActivity(), params, new IUiListener() {
////                                        @Override
////                                        public void onComplete(Object o) {
////
////                                        }
////
////                                        @Override
////                                        public void onError(UiError uiError) {
////
////                                        }
////
////                                        @Override
////                                        public void onCancel() {
////
////                                        }
////                                    });
////                                }
////                            });
//
//                        }
//                    }).show(getActivity().getFragmentManager(), "ChoseFileDialog");
//                } else {
//                    T.show(context, "文件夹为空(" + FileUtils.FILE_PATH + ")");
//                }
//
//            }
//        });
//                break;
            case R.id.find_hestroy:
                if (mData.size() != 0) {
                    new SelectDialog(context, DataHistory.this).show(getActivity().getFragmentManager(), "selectdialog");
                } else {
                    T.show(context, "没有数据");
                }

                break;
            case R.id.blue_print_hestroy:

                if (getCheckedNum(app.CheckState) != 0) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            int index = 1;
                            for (boolean b : app.CheckState) {
                                if (b) {
                                    int id = mData.get(index - 1).getId();
//                                    mBlueToothManager.print(print);
//                                            printWithBL(id);
                                    printWithUSB(id);
                                }
                                index++;
                            }
                        }
                    }).start();

                } else {
                    T.show(context, "长按勾选需要打印的数据");
                }
//                switch (connectState) {
//                    case 10:
//                        if (mBlueToothManager.mAdapter.enable()) {
//
//                            mBlueToothManager.showChoseDeveiceDialog();
//                        }
//                        break;
//                    case 12:
//                        if (getCheckedNum(app.CheckState) != 0) {
//                            new Thread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    int index = 1;
//                                    for (boolean b : app.CheckState) {
//                                        if (b) {
//                                            int id = mData.get(index - 1).getId();
////                                    mBlueToothManager.print(print);
//                                            printWithBL(id);
//                                        }
//                                        index++;
//                                    }
//                                }
//                            }).start();
//
//                        } else {
//                            T.show(context, "长按勾选需要打印的数据");
//                        }
//                        break;
//                    default:
//
//                        break;
//                }
                break;
            case R.id.delete_hestroy:
                if (justdelete) {
                    T.show(context, "请选择数据");
                    break;
                }
                if (!app.showCheckBoxs) {
                    T.show(context, "请选择数据");
                    break;
                }
                if (getCheckedNum(app.CheckState) != 0) {
                    int num = 0;
                    for (int BAindex = 0; BAindex < app.CheckState.size(); BAindex++) {
                        Log.i("index", app.CheckState.indexOf(BAindex) + "");
                        if (app.CheckState.get(BAindex)) {
                            SimpleData data = mData.get(BAindex - num);
                            int id = data.getId();
                            String path = data.getAbsPath();
                            String cache = data.getCache();
                            FileUtils.deleteFileIfExist(path);
                            FileUtils.deleteFileIfExist(cache);
                            db.delete(app.SQLite_LOG_rgb, "id = ?", new String[]{id + ""});
                            mData.remove(BAindex - num);
                            adapter.notifyItemRemoved(BAindex - num);
                            num++;
                        }
                    }
                    justdelete = true;
//                    mHandler.sendEmptyMessage(0);
                } else {
                    T.show(context, "请勾选要删除的项");
                }
                break;
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        Log.i("DataHestory", "onPause");
    }


    @Override
    public void onResume() {
        super.onResume();
        refresh();
        Log.i("DataHestory", "onResume");
    }

    public void switchCheckState() {
        refreshCheckState(selectAll);
        if (!app.showCheckBoxs) {
            v_top.setVisibility(View.VISIBLE);
        } else {
            v_top.setVisibility(View.GONE);
        }
        setAdatper();
    }

    public void setAdatper() {
        adapter = new RecycleAdapterForHistory(context, mData, app.CheckState);
        adapter.setOnItemClickLitener(mOnItemClickLitener);
        rv.setAdapter(adapter);
    }

    void printWithBL(int id) {
        mBlueToothManager.mBlueToothPrinter.printDataByID(db, id, print, context.getSharedPreferences(Consts.SYSTEM_SETTING_PREFERENCE, MODE_PRIVATE).getBoolean(Consts.KEY_PRINT_ENABLE, false));
    }

    private void printWithUSB(int id) {

    }

    private int getCheckedNum(ArrayList<Boolean> checkState) {
        int i = 0;
        for (boolean b : checkState) {
            if (b) {
                i++;
            }
        }
        return i;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            mBlueToothManager.closeBT();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void refresh() {
        new RefeshRecycleView().execute();
    }

    private ArrayList<SimpleData> getData() {

        boolean withtime = false;
        boolean withresult = false;
        boolean isnull = selectdata == null;
        if (!isnull) {
            withtime = (selectdata.getData() != null) && (!selectdata.getData().equals(""));
            withresult = (selectdata.getResult() != -1);
        }
        dbHelper = new MyDBHelper(context, app.SQLite_LOG, null, 1);
        db = dbHelper.getWritableDatabase();
        mData = new ArrayList<>();

        Cursor cursor = db.query("rgb", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String result = cursor.getString(cursor.getColumnIndex("result"));
                if (withresult && selectdata.getResult() == 1) {
                    if (!result.equals("合格")) {
                        break;
                    }
                } else if (withresult && selectdata.getResult() == 2) {
                    if (!result.equals("不合格")) {
                        break;
                    }
                }

                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String data = cursor.getString(cursor.getColumnIndex("data"));
                String time = cursor.getString(cursor.getColumnIndex("time"));
                if (withtime) {
                    if (!data.equals(selectdata.getData())) {
                        break;
                    }
                }
                String tips = cursor.getString(cursor.getColumnIndex("tips"));
                String path = cursor.getString(cursor.getColumnIndex("PNGpath"));
                String cache = cursor.getString(cursor.getColumnIndex("cachePath"));


                mData.add(new SimpleData(
                        id, tips, data, time,
                        "合格", path, cache
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        selectdata = null;
        Log.i("mData size", mData.size() + "");
        return mData;
    }

    void refreshCheckState(boolean selectAll) {
        app.CheckState = new ArrayList<>();
        if (mData != null && mData.size() != 0) {
            for (int i = 0; i < mData.size(); i++) {
                app.CheckState.add(0, selectAll);
            }
        }
    }

    class RefeshRecycleView extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            mData = getData();

            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            mHandler.sendEmptyMessage(0);
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }


}
