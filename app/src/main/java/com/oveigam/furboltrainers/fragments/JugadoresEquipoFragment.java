package com.oveigam.furboltrainers.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Oscarina on 25/04/2017.
 */
public class JugadoresEquipoFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return null;
    }


    @Override
    public void onRefresh() {

    }

    @Override
    public void onResume() {
        super.onResume();
        onRefresh();
    }
}