package com.apps.seikoshadow.ganglanddealer;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Shaun on 23/08/2017.
 */

public class CustomDialog extends DialogFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.move, container, false);
        getDialog().setTitle(getResources().getString(R.string.move));

        return rootView;
    }
}

