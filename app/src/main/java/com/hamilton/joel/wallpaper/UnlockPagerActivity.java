package com.hamilton.joel.wallpaper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NotificationCompatSideChannelService;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by joel on 12/10/15.
 */
public class UnlockPagerActivity extends AppCompatActivity {
    private final String TAG = "LOGImageGalleryActivity";
    private final int PAGE_COUNT = 3;
    private final String PAGE_NUM = "currentPageNum";

    private ViewPager pager;
    private ImageViewPagerAdapter adapter;
    private Button upgrade;
    private int mostRecentPageNum;
    private LinearLayout circleLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.unlock_boss);

        if (savedInstanceState != null) {
            mostRecentPageNum = savedInstanceState.getInt(PAGE_NUM);
        }

        if ((findViewById(R.id.unlock_pager_container) != null)) {
            Log.i(TAG, "onCreate VIEW EXISTS");
            View v = findViewById(R.id.unlock_pager_container);
            v.setBackgroundResource(R.drawable.rounded_corners_background);
        }


        adapter = new ImageViewPagerAdapter(UnlockPagerActivity.this, getSupportFragmentManager());

        circleLayout = (LinearLayout) findViewById(R.id.slider_circle_layout);
        changeSliderCircles(mostRecentPageNum);

        pager = (ViewPager) findViewById(R.id.unlock_pager);
        pager.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        pager.setAdapter(adapter);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                changeSliderCircles(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });



        upgrade = (Button) findViewById(R.id.unlock_full_version_pager_activity_button);
        upgrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try { //TODO change packagename
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.aldiko.android")));// + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.aldiko.android")));// + appPackageName)));
                }            }
        });
    }


    private void changeSliderCircles(int position) {
        circleLayout.removeAllViews();
        int margins = (int) (5 * UnlockPagerActivity.this.getResources().getDisplayMetrics().density);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(margins, margins, margins, margins);

        for (int i = 0; i < adapter.getCount(); i++) {
            ImageView temp = new ImageView(UnlockPagerActivity.this);
            if (i == position) {
                temp.setImageDrawable(getResources().getDrawable(R.drawable.slider_circle_dark));
            } else {
                temp.setImageDrawable(getResources().getDrawable(R.drawable.slider_circle_light));
            }
            temp.setId(i);
            temp.setLayoutParams(params);
            circleLayout.addView(temp);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(PAGE_NUM, pager.getCurrentItem());
    }

    //--------------------------------------------
    private class ImageViewPagerAdapter extends FragmentPagerAdapter {

        public ImageViewPagerAdapter(Context context, FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
//            switch (position) {
//                case 0:
                    Log.i(TAG, "getItem called at position 0");
                    return UnlockPagerFragment.newInstance(position);
                    
//            }
//            return null;
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }
    }
    //------------------------------------------------
    private class FragmentGetter extends AsyncTask<Integer, Void, Bitmap> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Bitmap doInBackground(Integer... params) {
            Bitmap background = AnalyticsApplication.getScaledBitmapFromRes(params[0], params[1]);
            Log.i(TAG, "ASyncTask loading Bitmap  " + background.getWidth() + " " + background.getHeight());
            return background;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
        }
    }
    //------------------------------------------------------------
}

