package measurement.color.com.xj_919.and.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import measurement.color.com.xj_919.R;

/**
 * Created by wpc on 2016/9/19.
 */
public class FirstFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_firstfragment, null);
        return view;
    }
}
