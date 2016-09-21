package measurement.color.com.xj_919.and.fragment.first;

import android.app.Fragment;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import measurement.color.com.xj_919.R;

/**
 * Created by wpc on 2016/9/19.
 */
public class FirstFragment extends Fragment {

    private Context context=getActivity();
    private int t=Toast.LENGTH_SHORT;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_firstfragment, null);
        Button bt1=(Button)view.findViewById(R.id.blueButton_fragment1);
        Button bt2=(Button)view.findViewById(R.id.usbbutton_fragment1);

        if(!bt1.isEnabled()&&!bt2.isEnabled()){
            Toast.makeText(context,"请连接usb或打开蓝牙",t).show();
        }

        return view;
    }
}
