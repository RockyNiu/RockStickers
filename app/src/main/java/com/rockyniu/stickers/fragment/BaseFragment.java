package com.rockyniu.stickers.fragment;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Lei on 2015/2/19.
 */
public class BaseFragment extends Fragment{
    protected SharedPreferences getPreferences(int mode) {
        return getActivity().getPreferences(mode);
    }

    protected Context getApplicationContext() {
        return getActivity().getApplicationContext();
    }

}
