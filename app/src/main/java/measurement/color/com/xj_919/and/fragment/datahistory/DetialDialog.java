package measurement.color.com.xj_919.and.fragment.datahistory;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import measurement.color.com.xj_919.R;
import measurement.color.com.xj_919.and.Utils.soft.ImageUtils;

/**
 * Created by wpc on 2016/10/24.
 */

public class DetialDialog extends DialogFragment {

    private Context context;
    private int id;
    private ImageView result;
    ListView lv;
    //    private RecyclerView rv;
    SQLiteDatabase db;

    public DetialDialog(Context context, SQLiteDatabase db, int id) {
        this.context = context;
        this.db = db;
        this.id = id;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("详细信息");
        View v = LayoutInflater.from(context).inflate(R.layout.detialdialog_layout, null);
        result = (ImageView) v.findViewById(R.id.iv_small_img_detial);
        lv = (ListView) v.findViewById(R.id.lv_detial);
        lv.setAdapter(new SimpleAdapter(context,  getArray(id), R.layout.item_detialdialog, new String[]{"area", "detial", "result"}, new int[]{R.id.index_item_detial, R.id.detial_item_detial, R.id.result_item_detial}));
//        rv = (RecyclerView) v.findViewById(R.id.rv_detial);
//        rv.setLayoutManager(new LinearLayoutManager(context));
//        //        mRecyclerView.setLayoutManager(new GridLayoutManager(context,4));
//        //        rv.setLayoutManager(layout);
//        //        rv.setAdapter(adapter);
//        rv.setItemAnimator(new DefaultItemAnimator());
//        rv.setHasFixedSize(true);
//        rv.setAdapter(new MyRecycleViewAdapter());
        builder.setView(v);
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", null);
        return builder.create();
    }

//    ArrayList<DetialItemData> getArray() {
//        mDatas = new ArrayList<>();
//        Cursor c = db.rawQuery("select * from rgb where id = ?", new String[]{id + ""});
//        if (c.moveToFirst()) {
//            do {
//                String AbsPath = c.getString(c.getColumnIndex("PNGpath"));
//                File file = new File(AbsPath+".png");
//                if (file.exists()) {
//                    Bitmap small = ImageUtils.scale(BitmapFactory.decodeFile(AbsPath+".png"), 0.3f, 0.3f);
//                    result.setImageBitmap(small);
//                } else {
//                    Toast.makeText(context, "图像文件丢失", Toast.LENGTH_SHORT).show();
//                }
//
//                for (int i = 1; i < 7; i++) {
//                    String detial = c.getString(c.getColumnIndex("detial" + i));
//                    String result = c.getString(c.getColumnIndex("result" + i));
//                    DetialItemData data = new DetialItemData(i, detial, result);
//                    mDatas.add(data);
//                }
//
//
//            } while (c.moveToNext());
//        }
//        return mDatas;
//    }

    ArrayList<HashMap<String, String>> getArray(int id) {
        ArrayList<HashMap<String, String>>   mDatas = new ArrayList<>();
        Cursor c = db.rawQuery("select * from rgb where id = ?", new String[]{id + ""});
        if (c.moveToFirst()) {
            do {
                String AbsPath = c.getString(c.getColumnIndex("PNGpath"));
                File file = new File(AbsPath + ".png");
                if (file.exists()) {
                    Bitmap small = ImageUtils.scale(BitmapFactory.decodeFile(AbsPath + ".png"), 0.3f, 0.3f);
                    result.setImageBitmap(small);
                } else {
                    Toast.makeText(context, "图像文件丢失", Toast.LENGTH_SHORT).show();
                }

                for (int i = 1; i < 6; i++) {
                    HashMap<String, String> data = new HashMap<>();
                    data.put("area", i + "");
                    data.put("detial", c.getString(c.getColumnIndex("detial" + i)));
                    data.put("result", c.getString(c.getColumnIndex("result" + i)));
                    mDatas.add(data);
                }
            } while (c.moveToNext());
        }
        Log.i("detial_data",mDatas.toString());
        return mDatas;
    }

//    class MyRecycleViewAdapter extends RecyclerView.Adapter<MyRecycleViewAdapter.ViewHolder> {
//
//        @Override
//        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            View view = LayoutInflater.from(context).inflate(R.layout.item_detialdialog, parent, false);
//            ViewHolder holder = new ViewHolder(view);
//            return holder;
//        }
//
//        @Override
//        public void onBindViewHolder(ViewHolder holder, int position) {
//            DetialItemData data = mDatas.get(position);
//            holder.index.setText(data.getPosition() + "");
//            holder.detial.setText(data.getDetial());
//            holder.result.setText(data.getResult());
//        }
//
//        @Override
//        public int getItemCount() {
//            return mDatas.size();
//        }
//
//        class ViewHolder extends RecyclerView.ViewHolder {
//
//            TextView index, detial, result;
//
//            public ViewHolder(View itemView) {
//                super(itemView);
//                index = (TextView) itemView.findViewById(R.id.index_item_detial);
//                detial = (TextView) itemView.findViewById(R.id.detial_item_detial);
//                result = (TextView) itemView.findViewById(R.id.result_item_detial);
//            }
//        }
//
//    }
//
//    class DetialItemData {
//        int position;
//        String detial, result;
//
//        public DetialItemData(int position, String detial, String result) {
//            this.position = position;
//            this.detial = detial;
//            this.result = result;
//        }
//
//        public String getDetial() {
//            return detial;
//        }
//
//        public void setDetial(String detial) {
//            this.detial = detial;
//        }
//
//        public int getPosition() {
//            return position;
//        }
//
//        public void setPosition(int position) {
//            this.position = position;
//        }
//
//        public String getResult() {
//            return result;
//        }
//
//        public void setResult(String result) {
//            this.result = result;
//        }
//
//    }

}
