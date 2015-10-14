package com.hamilton.joel.wallpaper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
 * Created by joel on 06/10/15.
 */
public class ImageGalleryActivity extends AppCompatActivity {
    private final String TAG = "LOGImageGalleryActivity";
    private final String PAGE_NUM = "pageNum";
    private final int PAGE_COUNT = 12; //TODO set this programatically?

    private ViewPager pager;
    private ImageViewPagerAdapter adapter;
    private Button setWallpaper;
    private Button cancel;
    private Tracker imageGalleyActivityTracker;
    private LinearLayout sliderCirclesLayout;
    private int mostRecentPage;

    @Override
    public void onCreate(Bundle savedInstanceState) {


        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        imageGalleyActivityTracker = application.getDefaultTracker();
        imageGalleyActivityTracker.setScreenName(TAG);
        imageGalleyActivityTracker.send(new HitBuilders.ScreenViewBuilder().build());

        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mostRecentPage = savedInstanceState.getInt(PAGE_NUM);
        }
        setContentView(R.layout.image_gallery_image_pager);

        if ((findViewById(R.id.fragment_container) != null)) {
            Log.i(TAG, "onCreate VIEW EXISTS");
            View v = findViewById(R.id.fragment_container);
            v.setBackgroundResource(R.drawable.rounded_corners_background);
        }


        adapter = new ImageViewPagerAdapter(ImageGalleryActivity.this);

        sliderCirclesLayout = (LinearLayout) findViewById(R.id.imgslider_circle_layout);
        changeSliderCircles(mostRecentPage);

        pager = (ViewPager) findViewById(R.id.pager);
        pager.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(PAGE_COUNT - 1);
        pager.setCurrentItem(mostRecentPage);
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


        setWallpaper = (Button) findViewById(R.id.set_background_button);
        setWallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("image_uri", "-1");
                editor.putBoolean("load_stream", false);
                editor.putInt("image_picker", pager.getCurrentItem());
                editor.commit();
                finish();

            }
        });

        cancel = (Button) findViewById(R.id.cancel_button);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(PAGE_NUM, pager.getCurrentItem());
    }

    private void changeSliderCircles(int position) {
        int margins = (int) (5 * ImageGalleryActivity.this.getResources().getDisplayMetrics().density);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(margins, margins, margins, margins);
        sliderCirclesLayout.removeAllViews();

        for (int i = 0, a = adapter.getCount(); i < a; i++) {
            ImageView temp = new ImageView(ImageGalleryActivity.this);
            if (i == position) {
                temp.setImageDrawable(getResources().getDrawable(R.drawable.slider_circle_dark));
            } else {
                temp.setImageDrawable(getResources().getDrawable(R.drawable.slider_circle_light));
            }
            temp.setId(i);
            temp.setLayoutParams(params);
            sliderCirclesLayout.addView(temp);
        }
    }

    //--------------------------------------------
    private class ImageViewPagerAdapter extends PagerAdapter {

        Context mContext;
        LayoutInflater mLayoutInflater;

        public ImageViewPagerAdapter(Context context) {
            mContext = context;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
//TODO get num programatically
            return PAGE_COUNT;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            View itemView = mLayoutInflater.inflate(R.layout.pager_item, container, false);
            ImageView imageView = (ImageView) itemView.findViewById(R.id.image_view);
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_launcher));
            Point size = new Point();
            Display display = getWindowManager().getDefaultDisplay();
            display.getSize(size);
            int width = size.x/2; //TODO scaled down image


            String pageNumberString = "p" + String.valueOf(position);
            int bitmapResourceInt = getResources().getIdentifier(pageNumberString, "drawable", getPackageName());

            Integer[] intArray = new Integer[]{bitmapResourceInt, width};
            ImageLoader loader = new ImageLoader();
            loader.execute(intArray);
            try {
                imageView.setImageBitmap(loader.get());
            } catch (Exception e) {
                Log.e(TAG, "onCreateView EXCEPTION", e);
            }

            container.addView(itemView);

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }
    }
    //------------------------------------------------
    private class ImageLoader extends AsyncTask<Integer, Void, Bitmap> {


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
