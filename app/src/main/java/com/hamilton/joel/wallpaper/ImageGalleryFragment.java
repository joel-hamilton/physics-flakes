package com.hamilton.joel.wallpaper;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

/**
 * Created by joel on 06/10/15.
 */
public class ImageGalleryFragment extends Fragment {
    public static final String TAG = "LOGImageGalleryFragment";

    private ImageView iView;
    private View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        Bundle args = getArguments();
        int pageNumber = args.getInt("page_position");
        String pageNumberString = "p" + String.valueOf(pageNumber);
        Log.i(TAG, "onCreateView pageNumber = " + pageNumber);

        v = inflater.inflate(R.layout.image_gallery_fragment, container, false);
        iView = (ImageView) v.findViewById(R.id.image_view);
        if (v == null) {
            Log.i(TAG, "onCreateView iView  == null");
            return super.onCreateView(inflater, container, savedInstanceState);
        }

        int bitmapResourceInt = getResources().getIdentifier(pageNumberString , "drawable", getActivity().getPackageName());
        Log.i(TAG, "bitmapResourceInt = " + bitmapResourceInt);
        if (bitmapResourceInt == 0) {
            bitmapResourceInt = R.drawable.p0;
            Log.e(TAG, "BITMAPRESOURCEINT not parsed");
        }

//        iView.setImageBitmap(BitmapMethods.getScaledBitmap(bitmapResourceInt, 200)); //TODO

        ArrayList<Integer> intList = new ArrayList<>();
        intList.add(bitmapResourceInt);
        intList.add(200);
        ImageLoader loader = new ImageLoader();
        loader.execute(intList);
        try {
            iView.setImageBitmap(loader.get()); //TODO
        } catch (Exception e) {
            Log.e(TAG, "onCreateView EXCEPTION", e);
        }

        return v;
    }


    private class ImageLoader extends AsyncTask <ArrayList<Integer>, Void, Bitmap> {


        @Override
        protected Bitmap doInBackground(ArrayList<Integer>... params) {
            Bitmap background = BitmapMethods.getScaledBitmap(params[0].get(0), params[0].get(1));
            Log.i(TAG, "ASyncTask loading Bitmap  " + background.getWidth() + " " + background.getHeight());
            return background;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
        }
    }
}
