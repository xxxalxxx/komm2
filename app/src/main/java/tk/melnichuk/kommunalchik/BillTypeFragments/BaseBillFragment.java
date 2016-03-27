package tk.melnichuk.kommunalchik.BillTypeFragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import tk.melnichuk.kommunalchik.R;

/**
 * Created by al on 26.03.16.
 */
public class BaseBillFragment extends Fragment {
    View mView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mView =  inflater.inflate(R.layout.frag_bills_settings, container, false);


        return mView;
    }


}
