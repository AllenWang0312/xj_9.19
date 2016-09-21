package measurement.color.com.xj_919.and.fragment.first;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import measurement.color.com.xj_919.R;

/**
 * Created by wpc on 2016/9/20.
 */
public class ChoseDeveiceDialog extends DialogFragment {

    private static final String tag = "ChoseDeveiceDialog";
    private static Context context;
    private static BlueToothManager manager;

    private ListView lv1;
    private Button bt;
    static ListView lv2;
    static ProgressBar mProgressBar;

    static android.support.v7.app.AlertDialog dialog;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        context = getActivity();
        manager = BlueToothManager.getInstance(context);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_chose_bluetooth, null);
        lv1 = (ListView) view.findViewById(R.id.lv1_dialog_bluetooth);
        lv2 = (ListView) view.findViewById(R.id.lv2_dialog_bluetooth);
        bt = (Button) view.findViewById(R.id.bt_dialog_bluetooth);
        mProgressBar = (ProgressBar) view.findViewById(R.id.pb_dialog_bluetooth);

        if (manager.getDeveicesBondedNames().size() != 0) {
            lv1.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, manager.getDeveicesBondedNames()));
        }


        lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    manager.connect(manager.getDeveicesBonded().get(i));
                } catch (IOException e) {

                }

            }
        });
//        lv2.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, manager.getDeveicesDescovedNames()));

        lv2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    manager.connect(manager.getDeveicesDescoved().get(i));
                } catch (IOException e) {

                }
            }
        });

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                manager.registerReceiver();
                manager.findDevices();
                mProgressBar.setVisibility(View.VISIBLE);
            }
        });

        builder.setView(view);
        builder.setTitle("选择你要连接的设备");
        builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//                manager.setDeveicesBondedNames(new ArrayList<String>());
            }
        });
        builder.setCancelable(false);
        dialog = builder.create();
        return dialog;
    }

    public static void dismissDialog() {
        dialog.dismiss();
    }

    public static void dimissProgressBar() {
        mProgressBar.setVisibility(View.GONE);
        lv2.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, manager.getDeveicesDescovedNames()));
        Log.i(tag, manager.getDeveicesDescovedNames().toString());
    }

}
