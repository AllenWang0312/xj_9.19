package measurement.color.com.xj_919.and.activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;

import measurement.color.com.xj_919.R;
import measurement.color.com.xj_919.and.Divider.DividerGridItemDecoration;
import measurement.color.com.xj_919.and.Utils.MyDBHelper;
import measurement.color.com.xj_919.and.listener.MyItemClickListener;
import measurement.color.com.xj_919.and.listener.MyItemLongClickListener;

public class TableActivity extends BaseAppCompatActivity {

    private RecyclerView rv;
    private ArrayList<RGBdata> mData;
    private MyDBHelper dbHelper;
    private SQLiteDatabase db;

    private TextView meanR, meanG, meanB, wcR, wcG, wcB;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.table);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        meanR = (TextView) findViewById(R.id.tv_R_mean);
        meanG = (TextView) findViewById(R.id.tv_G_mean);
        meanB = (TextView) findViewById(R.id.tv_B_mean);
        wcR = (TextView) findViewById(R.id.tv_R_wc);
        wcG = (TextView) findViewById(R.id.tv_G_wc);
        wcB = (TextView) findViewById(R.id.tv_B_wc);

        initData();

        rv = (RecyclerView) findViewById(R.id.rv_table);
        MyRecycleViewAdapter adapter = new MyRecycleViewAdapter();
        adapter.setOnItemClickListener(new MyItemClickListener() {
            @Override
            public void onItemClick(View view, int postion) {
                Toast.makeText(TableActivity.this, "you click" + postion, Toast.LENGTH_SHORT).show();
            }
        });
        adapter.setOnItemLongClickListener(new MyItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int postion) {
                Toast.makeText(TableActivity.this, "you long click" + postion, Toast.LENGTH_SHORT).show();
            }
        });
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));
        //        mRecyclerView.setLayoutManager(new GridLayoutManager(context,4));
        //        rv.setLayoutManager(layout);
        //        rv.setAdapter(adapter);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.addItemDecoration(new DividerGridItemDecoration(this));

    }

    void initData() {
        dbHelper = new MyDBHelper(this, "LOG.db", null, 1);
        db = dbHelper.getWritableDatabase();
        mData = new ArrayList<>();
        double totalR = 0, totalG = 0, totalB = 0;
        float maxR = 0, maxG = 0, maxB = 0;
        float minR = 1024, minG = 1024, minB = 1024;
        Cursor cursor = db.query("RGB", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String time = cursor.getString(cursor.
                        getColumnIndex("time"));

                float R = cursor.getFloat(cursor.
                        getColumnIndex("R"));

                if (R > maxR) {
                    maxR = R;
                }
                if (R < minR) {
                    minR = R;
                }
                totalR += R;

                float G = cursor.getFloat(cursor.getColumnIndex
                        ("G"));
                if (R > maxG) {
                    maxG = G;
                }
                if (G < minG) {
                    minG = G;
                }
                totalG += G;

                float B = cursor.getFloat(cursor.getColumnIndex
                        ("B"));
                if (B > maxB) {
                    maxB = B;
                }
                if (B < minB) {
                    minB = B;
                }
                totalB += B;
                RGBdata data = new RGBdata(time, R, G, B);
                mData.add(data);
            } while (cursor.moveToNext());
            Log.i("MaxR,MaxG,MaxB",maxR+"  "+maxG+"  "+maxB);
            Log.i("minR,minG,minB",minR+"  "+minG+"  "+minB);
        }
        cursor.close();

        DecimalFormat df = new java.text.DecimalFormat("#.##");

        double fR =  totalR / mData.size();
        meanR.setText(df.format(fR));
        double fG = totalG / mData.size();
        meanG.setText(df.format(fG));
        double fB = totalB / mData.size();
        meanB.setText(df.format(fB));
        df = new java.text.DecimalFormat("#.#" +
                "###");
        wcR.setText(df.format((maxR - minR) / fR));
        wcG.setText(df.format((maxG - minG) / fG));
        wcB.setText(df.format((maxB - minB) / fB));
    }

    class MyRecycleViewAdapter extends RecyclerView.Adapter<MyRecycleViewAdapter.MyViewHolder> {


        private MyItemClickListener mItemClickListener;
        private MyItemLongClickListener mItemLongClickListener;

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder holder = new MyViewHolder(getLayoutInflater().inflate(R.layout.item_table_layout, parent, false));
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {

            RGBdata data = mData.get(position);
            holder.tv_time.setText(data.getTime());
            holder.tv_R.setText(data.getR() + "");
            holder.tv_G.setText(data.getG() + "");
            holder.tv_B.setText(data.getB() + "");
//            holder.iv.setImageResource(R.drawable.uu);
        }

        /**
         * 设置Item点击监听
         *
         * @param listener
         */
        public void setOnItemClickListener(MyItemClickListener listener) {
            this.mItemClickListener = listener;
        }

        public void setOnItemLongClickListener(MyItemLongClickListener listener) {
            this.mItemLongClickListener = listener;
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView tv_time;
            TextView tv_R;
            TextView tv_G;
            TextView tv_B;

            public MyViewHolder(View v) {
                super(v);
                tv_time = (TextView) v.findViewById(R.id.tv_time_item_table);
                tv_R = (TextView) v.findViewById(R.id.tv_R_item_table);
                tv_G = (TextView) v.findViewById(R.id.tv_G_item_table);
                tv_B = (TextView) v.findViewById(R.id.tv_B_item_table);

            }
        }
    }

    class RGBdata {
        String time;
        float R;
        float G;
        float B;

        public RGBdata(String time, float r, float g, float b) {
            this.time = time;
            R = r;
            G = g;
            B = b;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public float getR() {
            return R;
        }

        public void setR(int r) {
            R = r;
        }

        public float getG() {
            return G;
        }

        public void setG(int g) {
            G = g;
        }

        public float getB() {
            return B;
        }

        public void setB(int b) {
            B = b;
        }
    }
}
