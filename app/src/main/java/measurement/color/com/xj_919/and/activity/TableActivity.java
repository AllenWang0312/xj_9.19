package measurement.color.com.xj_919.and.activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
    private ArrayList<SimpleData> mData;
    private MyDBHelper dbHelper;
    private SQLiteDatabase db;

    private TextView
            wcR1, wcG1, wcB1, wcR2, wcG2, wcB2, wcR3, wcG3, wcB3, wcR4, wcG4, wcB4, wcR5, wcG5, wcB5, wcR6, wcG6, wcB6, wcR7, wcG7, wcB7, wcR8, wcG8, wcB8, wcR9, wcG9, wcB9;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.table, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_remove) {
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
//        meanR = (TextView) findViewById(R.id.tv_R_mean);
//        meanG = (TextView) findViewById(R.id.tv_G_mean);
//        meanB = (TextView) findViewById(R.id.tv_B_mean);
        wcR1 = (TextView) findViewById(R.id.tv_R1_wc);
        wcG1 = (TextView) findViewById(R.id.tv_G1_wc);
        wcB1 = (TextView) findViewById(R.id.tv_B1_wc);
        wcR2 = (TextView) findViewById(R.id.tv_R2_wc);
        wcG2 = (TextView) findViewById(R.id.tv_G2_wc);
        wcB2 = (TextView) findViewById(R.id.tv_B2_wc);
        wcR3 = (TextView) findViewById(R.id.tv_R3_wc);
        wcG3 = (TextView) findViewById(R.id.tv_G3_wc);
        wcB3 = (TextView) findViewById(R.id.tv_B3_wc);
        wcR4 = (TextView) findViewById(R.id.tv_R4_wc);
        wcG4 = (TextView) findViewById(R.id.tv_G4_wc);
        wcB4 = (TextView) findViewById(R.id.tv_B4_wc);
        wcR5 = (TextView) findViewById(R.id.tv_R5_wc);
        wcG5 = (TextView) findViewById(R.id.tv_G5_wc);
        wcB5 = (TextView) findViewById(R.id.tv_B5_wc);
        wcR6 = (TextView) findViewById(R.id.tv_R6_wc);
        wcG6 = (TextView) findViewById(R.id.tv_G6_wc);
        wcB6 = (TextView) findViewById(R.id.tv_B6_wc);
        wcR7 = (TextView) findViewById(R.id.tv_R7_wc);
        wcG7 = (TextView) findViewById(R.id.tv_G7_wc);
        wcB7 = (TextView) findViewById(R.id.tv_B7_wc);
        wcR8 = (TextView) findViewById(R.id.tv_R8_wc);
        wcG8 = (TextView) findViewById(R.id.tv_G8_wc);
        wcB8 = (TextView) findViewById(R.id.tv_B8_wc);
        wcR9 = (TextView) findViewById(R.id.tv_R9_wc);
        wcG9 = (TextView) findViewById(R.id.tv_G9_wc);
        wcB9 = (TextView) findViewById(R.id.tv_B9_wc);
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
//        double totalR = 0, totalG = 0, totalB = 0;
        int maxR1 = 0, maxG1 = 0, maxB1 = 0, maxR2 = 0, maxG2 = 0, maxB2 = 0, maxR3 = 0, maxG3 = 0, maxB3 = 0, maxR4 = 0, maxG4 = 0, maxB4 = 0, maxR5 = 0, maxG5 = 0, maxB5 = 0, maxR6 = 0, maxG6 = 0, maxB6 = 0, maxR7 = 0, maxG7 = 0, maxB7 = 0, maxR8 = 0, maxG8 = 0, maxB8 = 0, maxR9 = 0, maxG9 = 0, maxB9 = 0;
        int minR1 = Integer.MAX_VALUE, minG1 = Integer.MAX_VALUE, minB1 = Integer.MAX_VALUE;
        int minR2 = Integer.MAX_VALUE, minG2 = Integer.MAX_VALUE, minB2 = Integer.MAX_VALUE;
        int minR3 = Integer.MAX_VALUE, minG3 = Integer.MAX_VALUE, minB3 = Integer.MAX_VALUE;
        int minR4 = Integer.MAX_VALUE, minG4 = Integer.MAX_VALUE, minB4 = Integer.MAX_VALUE;
        int minR5 = Integer.MAX_VALUE, minG5 = Integer.MAX_VALUE, minB5 = Integer.MAX_VALUE;
        int minR6 = Integer.MAX_VALUE, minG6 = Integer.MAX_VALUE, minB6 = Integer.MAX_VALUE;
        int minR7 = Integer.MAX_VALUE, minG7 = Integer.MAX_VALUE, minB7 = Integer.MAX_VALUE;
        int minR8 = Integer.MAX_VALUE, minG8 = Integer.MAX_VALUE, minB8 = Integer.MAX_VALUE;
        int minR9 = Integer.MAX_VALUE, minG9 = Integer.MAX_VALUE, minB9 = Integer.MAX_VALUE;
        Cursor cursor = db.query("rgb", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                int R1 = cursor.getInt(cursor.getColumnIndex("R1T"));
                int G1 = cursor.getInt(cursor.getColumnIndex("G1T"));
                int B1 = cursor.getInt(cursor.getColumnIndex("B1T"));

                int R2 = cursor.getInt(cursor.getColumnIndex("R2T"));
                int G2 = cursor.getInt(cursor.getColumnIndex("G2T"));
                int B2 = cursor.getInt(cursor.getColumnIndex("B2T"));

                int R3 = cursor.getInt(cursor.getColumnIndex("R3T"));
                int G3 = cursor.getInt(cursor.getColumnIndex("G3T"));
                int B3 = cursor.getInt(cursor.getColumnIndex("B3T"));

                int R4 = cursor.getInt(cursor.getColumnIndex("R4T"));
                int G4 = cursor.getInt(cursor.getColumnIndex("G4T"));
                int B4 = cursor.getInt(cursor.getColumnIndex("B4T"));

                int R5 = cursor.getInt(cursor.getColumnIndex("R5T"));
                int G5 = cursor.getInt(cursor.getColumnIndex("G5T"));
                int B5 = cursor.getInt(cursor.getColumnIndex("B5T"));

                int R6 = cursor.getInt(cursor.getColumnIndex("R6T"));
                int G6 = cursor.getInt(cursor.getColumnIndex("G6T"));
                int B6 = cursor.getInt(cursor.getColumnIndex("B6T"));
                int R7 = cursor.getInt(cursor.getColumnIndex("R7T"));
                int G7 = cursor.getInt(cursor.getColumnIndex("G7T"));
                int B7 = cursor.getInt(cursor.getColumnIndex("B7T"));
                int R8 = cursor.getInt(cursor.getColumnIndex("R8T"));
                int G8 = cursor.getInt(cursor.getColumnIndex("G8T"));
                int B8 = cursor.getInt(cursor.getColumnIndex("B8T"));
                int R9 = cursor.getInt(cursor.getColumnIndex("R9T"));
                int G9 = cursor.getInt(cursor.getColumnIndex("G9T"));
                int B9 = cursor.getInt(cursor.getColumnIndex("B9T"));


                if (R1 > maxR1) {maxR1 = R1;}
                if (R1 < minR1) {minR1 = R1;}

                if (G1 > maxG1) { maxG1 = G1;}
                if (G1 < minG1) {minG1 = G1;}

                if (B1 > maxB1) {maxB1 = B1;}
                if (B1 < minB1) {minB1 = B1; }

                if (R2 > maxR2) {maxR2 = R2;}
                if (R2 < minR2) {minR2 = R2;}

                if (G2 > maxG2) { maxG2 = G2;}
                if (G2 < minG2) {minG2 = G2;}

                if (B2 > maxB2) {maxB2 = B2;}
                if (B2 < minB2) {minB2 = B2; }

                if (R3 > maxR3) {maxR3 = R3;}
                if (R3 < minR3) {minR3 = R3;}

                if (G3 > maxG3) { maxG3 = G3;}
                if (G3 < minG3) {minG3 = G3;}

                if (B3 > maxB3) {maxB3 = B3;}
                if (B3 < minB3) {minB3 = B3; }

                if (R4 > maxR4) {maxR4 = R4;}
                if (R4 < minR4) {minR4 = R4;}

                if (G4 > maxG4) { maxG4 = G4;}
                if (G4 < minG4) {minG4 = G4;}

                if (B4 > maxB4) {maxB4 = B4;}
                if (B4 < minB4) {minB4 = B4; }

                if (R5 > maxR5) {maxR5 = R5;}
                if (R5 < minR5) {minR5 = R5;}

                if (G5 > maxG5) { maxG5 = G5;}
                if (G5 < minG5) {minG5 = G5;}

                if (B5 > maxB5) {maxB5 = B5;}
                if (B5 < minB5) {minB5 = B5; }

                if (R6 > maxR6) {maxR6 = R6;}
                if (R6 < minR6) {minR6 = R6;}

                if (G6 > maxG6) { maxG6 = G6;}
                if (G6 < minG6) {minG6 = G6;}

                if (B6 > maxB6) {maxB6 = B6;}
                if (B6 < minB6) {minB6 = B6; }

                if (R7 > maxR7) {maxR7 = R7;}
                if (R7 < minR7) {minR7 = R7;}

                if (G7 > maxG7) { maxG7 = G7;}
                if (G7 < minG7) {minG7 = G7;}

                if (B7 > maxB7) {maxB7 = B7;}
                if (B7 < minB7) {minB7 = B7; }

                if (R8 > maxR8) {maxR8 = R8;}
                if (R8 < minR8) {minR8 = R8;}

                if (G8 > maxG8) { maxG8 = G8;}
                if (G8 < minG8) {minG8 = G8;}

                if (B8 > maxB8) {maxB8 = B8;}
                if (B8 < minB8) {minB8 = B8; }


                if (R9 > maxR9) {maxR9 = R9;}
                if (R9 < minR9) {minR9 = R9;}

                if (G9 > maxG9) { maxG9 = G9;}
                if (G9 < minG9) {minG9 = G9;}

                if (B9 > maxB9) {maxB9 = B9;}
                if (B9 < minB9) {minB9 = B9; }

                mData.add(new SimpleData(
                        cursor.getString(cursor.getColumnIndex("time")),
                        R1, G1, B1, R2, G2, B2, R3, G3, B3, R4, G4, B4, R5, G5, B5, R6, G6, B6, R7, G7, B7, R8,G8,B8, R9,G9,B9
                ));
//                String time = cursor.getString(cursor.
//                        getColumnIndex("time"));
//
//                float R = cursor.getFloat(cursor.
//                        getColumnIndex("R"));
//
//                if (R > maxR) {
//                    maxR = R;
//                }
//                if (R < minR) {
//                    minR = R;
//                }
//                totalR += R;
//
//                float G = cursor.getFloat(cursor.getColumnIndex
//                        ("G"));
//                if (R > maxG) {
//                    maxG = G;
//                }
//                if (G < minG) {
//                    minG = G;
//                }
//                totalG += G;
//
//                float B = cursor.getFloat(cursor.getColumnIndex
//                        ("B"));
//                if (B > maxB) {
//                    maxB = B;
//                }
//                if (B < minB) {
//                    minB = B;
//                }
//                totalB += B;
//                RGBdata data = new RGBdata(time, R, G, B);
//                mData.add(data);
            } while (cursor.moveToNext());
//            Log.i("MaxR,MaxG,MaxB", maxR + "  " + maxG + "  " + maxB);
//            Log.i("minR,minG,minB", minR + "  " + minG + "  " + minB);
        }
        cursor.close();
//        for (SimpleData data : mData) {
//
//        }
//        MathUtil.getArrayMean()
        DecimalFormat df = new java.text.DecimalFormat("#.##");
//
//        double fR = totalR / mData.size();
//        meanR.setText(df.format(fR));
//        double fG = totalG / mData.size();
//        meanG.setText(df.format(fG));
//        double fB = totalB / mData.size();
//        meanB.setText(df.format(fB));
//        df = new java.text.DecimalFormat("#.#" +
//                "###");

        ((TextView)findViewById(R.id.tv_R1_wc)).setText(1.0f * (maxR1 - minR1) / maxR1 + "");
        wcG1.setText(1.0f * (maxG1 - minG1) / maxG1 + "");
        wcB1.setText(1.0f * (maxB1 - minB1) / maxB1 + "");
        wcR2.setText(1.0f * (maxR2 - minR2) / maxR2 + "");
        wcG2.setText(1.0f * (maxG2 - minG2) / maxG2 + "");
        wcB2.setText(1.0f * (maxB2 - minB2) / maxB2 + "");
        wcR3.setText(1.0f * (maxR3 - minR3) / maxR3 + "");
        wcG3.setText(1.0f * (maxG3 - minG3) / maxG3 + "");
        wcB3.setText(1.0f * (maxB3 - minB3) / maxB3 + "");
        wcR4.setText(1.0f * (maxR4 - minR4) / maxR4 + "");
        wcG4.setText(1.0f * (maxG4 - minG4) / maxG4 + "");
        wcB4.setText(1.0f * (maxB4 - minB4) / maxB4 + "");
        wcR5.setText(1.0f * (maxR5 - minR5) / maxR5 + "");
        wcG5.setText(1.0f * (maxG5 - minG5) / maxG5 + "");
        wcB5.setText(1.0f * (maxB5 - minB5) / maxB5 + "");
        wcR6.setText(1.0f * (maxR6 - minR6) / maxR6 + "");
        wcG6.setText(1.0f * (maxG6 - minG6) / maxG6 + "");
        wcB6.setText(1.0f * (maxB6 - minB6) / maxB6 + "");
        wcR7.setText(1.0f * (maxR7 - minR7) / maxR7 + "");
        wcG7.setText(1.0f * (maxG7 - minG7) / maxG7 + "");
        wcB7.setText(1.0f * (maxB7 - minB7) / maxB7 + "");
        wcR8.setText(1.0f * (maxR8 - minR8) / maxR8 + "");
        wcG8.setText(1.0f * (maxG8 - minG8) / maxG8 + "");
        wcB8.setText(1.0f * (maxB8 - minB8) / maxB8 + "");
        wcR9.setText(1.0f * (maxR9 - minR9) / maxR9 + "");
        wcG9.setText(1.0f * (maxG9 - minG9) / maxG9 + "");
        wcB9.setText(1.0f * (maxB9 - minB9) / maxB9 + "");

        Log.i("max_min", maxR1 + " " + minR1);
        Log.i("wc", 1.0f * (maxR1 - minR1) / maxR1 + "");
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

            SimpleData data = mData.get(position);
            holder.tv_time.setText(data.getTime());

            holder.tv_R1.setText(data.getR1T() + "");
            holder.tv_G1.setText(data.getG1T() + "");
            holder.tv_B1.setText(data.getB1T() + "");


            holder.tv_R2.setText(data.getR2T() + "");
            holder.tv_G2.setText(data.getG2T() + "");
            holder.tv_B2.setText(data.getB2T() + "");


            holder.tv_R3.setText(data.getR3T() + "");
            holder.tv_G3.setText(data.getG3T() + "");
            holder.tv_B3.setText(data.getB3T() + "");


            holder.tv_R4.setText(data.getR4T() + "");
            holder.tv_G4.setText(data.getG4T() + "");
            holder.tv_B4.setText(data.getB4T() + "");


            holder.tv_R5.setText(data.getR5T() + "");
            holder.tv_G5.setText(data.getG5T() + "");
            holder.tv_B5.setText(data.getB5T() + "");


            holder.tv_R6.setText(data.getR6T() + "");
            holder.tv_G6.setText(data.getG6T() + "");
            holder.tv_B6.setText(data.getB6T() + "");


            holder.tv_R7.setText(data.getR7T() + "");
            holder.tv_G7.setText(data.getG7T() + "");
            holder.tv_B7.setText(data.getB7T() + "");


            holder.tv_R8.setText(data.getR8T() + "");
            holder.tv_G8.setText(data.getG8T() + "");
            holder.tv_B8.setText(data.getB8T() + "");

            holder.tv_R9.setText(data.getR9T() + "");
            holder.tv_G9.setText(data.getG9T() + "");
            holder.tv_B9.setText(data.getB9T() + "");


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

            TextView tv_R1, tv_G1, tv_B1, tv_R2, tv_G2, tv_B2, tv_R3, tv_G3, tv_B3, tv_R4, tv_G4, tv_B4, tv_R5, tv_G5, tv_B5, tv_R6, tv_G6, tv_B6, tv_R7, tv_G7, tv_B7, tv_R8, tv_G8, tv_B8, tv_R9, tv_G9, tv_B9;


            public MyViewHolder(View v) {
                super(v);
                tv_time = (TextView) v.findViewById(R.id.tv_time_item_table);

                tv_R1 = (TextView) v.findViewById(R.id.tv_R1_item_table);
                tv_G1 = (TextView) v.findViewById(R.id.tv_G1_item_table);
                tv_B1 = (TextView) v.findViewById(R.id.tv_B1_item_table);

                tv_R2 = (TextView) v.findViewById(R.id.tv_R2_item_table);
                tv_G2 = (TextView) v.findViewById(R.id.tv_G2_item_table);
                tv_B2 = (TextView) v.findViewById(R.id.tv_B2_item_table);

                tv_R3 = (TextView) v.findViewById(R.id.tv_R3_item_table);
                tv_G3 = (TextView) v.findViewById(R.id.tv_G3_item_table);
                tv_B3 = (TextView) v.findViewById(R.id.tv_B3_item_table);

                tv_R4 = (TextView) v.findViewById(R.id.tv_R4_item_table);
                tv_G4 = (TextView) v.findViewById(R.id.tv_G4_item_table);
                tv_B4 = (TextView) v.findViewById(R.id.tv_B4_item_table);

                tv_R5 = (TextView) v.findViewById(R.id.tv_R5_item_table);
                tv_G5 = (TextView) v.findViewById(R.id.tv_G5_item_table);
                tv_B5 = (TextView) v.findViewById(R.id.tv_B5_item_table);

                tv_R6 = (TextView) v.findViewById(R.id.tv_R6_item_table);
                tv_G6 = (TextView) v.findViewById(R.id.tv_G6_item_table);
                tv_B6 = (TextView) v.findViewById(R.id.tv_B6_item_table);

                tv_R7 = (TextView) v.findViewById(R.id.tv_R7_item_table);
                tv_G7 = (TextView) v.findViewById(R.id.tv_G7_item_table);
                tv_B7 = (TextView) v.findViewById(R.id.tv_B7_item_table);

                tv_R8 = (TextView) v.findViewById(R.id.tv_R8_item_table);
                tv_G8 = (TextView) v.findViewById(R.id.tv_G8_item_table);
                tv_B8 = (TextView) v.findViewById(R.id.tv_B8_item_table);

                tv_R9 = (TextView) v.findViewById(R.id.tv_R9_item_table);
                tv_G9 = (TextView) v.findViewById(R.id.tv_G9_item_table);
                tv_B9 = (TextView) v.findViewById(R.id.tv_B9_item_table);


            }
        }
    }

    class SimpleData {
        String time;

        int R1T, G1T, B1T, R2T, G2T, B2T, R3T, G3T, B3T, R4T, G4T, B4T, R5T, G5T, B5T, R6T, G6T, B6T, R7T, G7T, B7T, R8T, G8T, B8T, R9T, G9T, B9T;

        public SimpleData(String time, int r1T, int g1T, int b1T, int r2T, int g2T, int b2T, int r3T, int g3T, int b3T, int r4T, int g4T, int b4T, int r5T, int g5T, int b5T, int r6T, int g6T, int b6T, int r7T, int g7T, int b7T, int r8T, int g8T, int b8T, int r9T, int g9T, int b9T) {
            this.time = time;
            R1T = r1T;
            G1T = g1T;
            B1T = b1T;
            R2T = r2T;
            G2T = g2T;
            B2T = b2T;
            R3T = r3T;
            G3T = g3T;
            B3T = b3T;
            R4T = r4T;
            G4T = g4T;
            B4T = b4T;
            R5T = r5T;
            G5T = g5T;
            B5T = b5T;
            R6T = r6T;
            G6T = g6T;
            B6T = b6T;
            R7T = r7T;
            G7T = g7T;
            B7T = b7T;
            R8T = r8T;
            G8T = g8T;
            B8T = b8T;
            R9T = r9T;
            G9T = g9T;
            B9T = b9T;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public int getR1T() {
            return R1T;
        }


        public int getG1T() {
            return G1T;
        }


        public int getB1T() {
            return B1T;
        }


        public int getR2T() {
            return R2T;
        }


        public int getG2T() {
            return G2T;
        }


        public int getB2T() {
            return B2T;
        }


        public int getR3T() {
            return R3T;
        }


        public int getG3T() {
            return G3T;
        }


        public int getB3T() {
            return B3T;
        }


        public int getR4T() {
            return R4T;
        }


        public int getG4T() {
            return G4T;
        }


        public int getB4T() {
            return B4T;
        }

        public int getR5T() {
            return R5T;
        }


        public int getG5T() {
            return G5T;
        }


        public int getB5T() {
            return B5T;
        }


        public int getR6T() {
            return R6T;
        }


        public int getG6T() {
            return G6T;
        }


        public int getB6T() {
            return B6T;
        }

        public int getR7T() {
            return R7T;
        }


        public int getG7T() {
            return G7T;
        }


        public int getB7T() {
            return B7T;
        }


        public int getR8T() {
            return R8T;
        }


        public int getG8T() {
            return G8T;
        }


        public int getB8T() {
            return B8T;
        }


        public int getR9T() {
            return R9T;
        }


        public int getG9T() {
            return G9T;
        }


        public int getB9T() {
            return B9T;
        }

    }

}
