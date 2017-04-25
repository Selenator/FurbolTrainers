package com.oveigam.furboltrainers.fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Oscarina on 25/04/2017.
 */
public class PagerAdapterEquipo extends FragmentStatePagerAdapter {
    private int mNumOfTabs;

    public PagerAdapterEquipo(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new EventosEquipoFragment();
            case 1:
                return new JugadoresEquipoFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}