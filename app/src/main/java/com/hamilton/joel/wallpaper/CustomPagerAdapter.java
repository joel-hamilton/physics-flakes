package com.hamilton.joel.wallpaper;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by joel on 07/10/15.
 */

public class CustomPagerAdapter extends FragmentPagerAdapter {
    private final String TAG = "LOGPagerAdapter";

    protected Context mContext;

    public CustomPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
    }





    @Override
    public Fragment getItem(int position) {
        Fragment fragment = new ImageGalleryFragment();
        Bundle args = new Bundle();
        args.putInt("page_position", position);
        fragment.setArguments(args);
        Log.i(TAG, "PAGE # = " + position);
        return fragment;
    }

    @Override
    public int getCount() {
        return 10;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return false;
    }

}
