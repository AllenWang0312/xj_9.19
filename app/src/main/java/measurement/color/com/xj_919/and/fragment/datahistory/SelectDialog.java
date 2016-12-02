package measurement.color.com.xj_919.and.fragment.datahistory;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;

import java.util.Calendar;

import measurement.color.com.xj_919.R;
import measurement.color.com.xj_919.and.Utils.soft.StringUtils;
import measurement.color.com.xj_919.and.activity.app;

/**
 * Created by wpc on 2016/10/11.
 */

public class SelectDialog extends DialogFragment {


    private int checked_year, checked_mouth, checked_day;
    private selectData mSelectData;
    private DataHestroy parent;

    Context mContext;
    EditText data;
//    EditText from, to;
    RadioGroup result;

    public SelectDialog(Context context, DataHestroy parent) {
        mContext = context;
        this.parent = parent;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        mSelectData = new selectData();

        builder.setTitle("添加筛选条件");
        View view = LayoutInflater.from(mContext).inflate(R.layout.select_dialog, null);
        data = (EditText) view.findViewById(R.id.et_select_dialog);
        result=(RadioGroup)view.findViewById(R.id.rg_select_dialog) ;

        data.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                Calendar date = Calendar.getInstance();
                int c_year = date.get(Calendar.YEAR);
                int c_month = date.get(Calendar.MONTH);
                int c_day = date.get(Calendar.DAY_OF_MONTH);
                new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        app.select_mod = 0;
                        checked_year = year;
                        checked_mouth = month;
                        checked_day = day;
                        data.setText(year + "-" + (month + 1) + "-" + day);
                        Log.i("checked_date", checked_year + ":" + checked_mouth + ":" + checked_day);
                    }
                }, c_year, c_month,
                        c_day).show();
            }
        });
//        from = (EditText) view.findViewById(R.id.et1_select_dialog);
//        to = (EditText) view.findViewById(R.id.et2_select_dialog);
        result = (RadioGroup) view.findViewById(R.id.rg_select_dialog);

        builder.setView(view);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String str1 = data.getText().toString();
                Log.i("data", str1);
                if (!StringUtils.isEmpty(str1)) {
                    mSelectData.setData(str1);
                } else {
                    mSelectData.setData("");
                }
//                String str2 = from.getText().toString();
//                Log.i("from", str2);
//                if (!StringUtils.isEmpty(str2)) {
//                    mSelectData.setFrom(Integer.valueOf(str2));
//                } else {
//                    mSelectData.setFrom(0);
//                }
//                String str3 = to.getText().toString();
//                Log.i("to", str3);
//                if (!StringUtils.isEmpty(str3)) {
//                    mSelectData.setTo(Integer.valueOf(str3));
//                } else {
//                    mSelectData.setTo(Integer.MAX_VALUE);
//                }

                if (result.getCheckedRadioButtonId() == R.id.rb1_select_dialog) {
                    mSelectData.setResult(1);

                } else if (result.getCheckedRadioButtonId() == R.id.rb2_select_dialog) {
                    mSelectData.setResult(2);
                } else {
                    mSelectData.setResult(-1);
                }
                Log.i("result",mSelectData.getResult()+"");

                parent.selectdata = mSelectData;
                parent.refresh();
            }


        });
        builder.setNegativeButton("取消", null);

        return builder.create();
    }

    public class selectData {

        String data = null;

//        int from = -1, to = -1;
        //-1未选择  1合格  2不合格
        int result = -1;

        public selectData() {

        }

        public selectData(String data, int from, int to, int result) {
            this.data = data;
//            this.from = from;
//            this.to = to;
            this.result = result;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

//        public int getFrom() {
//            return from;
//        }

//        public void setFrom(int from) {
//            this.from = from;
//        }

        public int getResult() {
            return result;
        }

        public void setResult(int result) {
            this.result = result;
        }

//        public int getTo() {
//            return to;
////        }

//        public void setTo(int to) {
//            this.to = to;
//        }
    }
}
