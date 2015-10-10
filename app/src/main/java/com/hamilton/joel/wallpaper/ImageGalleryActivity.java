package com.hamilton.joel.wallpaper;

import android.content.Context;
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
    private final String TAG = "ImageGalleryActivity";

    private ViewPager pager;
    private ImageViewPagerAdapter adapter;
    private Button setWallpaper;
    private Button cancel;
    private Tracker imageGalleyActivityTracker;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        imageGalleyActivityTracker = application.getDefaultTracker();
        imageGalleyActivityTracker.setScreenName(TAG);
        imageGalleyActivityTracker.send(new HitBuilders.ScreenViewBuilder().build());

        super.onCreate(savedInstanceState);

        setContentView(R.layout.image_gallery_image_pager);



        if ((findViewById(R.id.fragment_container) != null)) {
            Log.i(TAG, "onCreate VIEW EXISTS");
            View v = findViewById(R.id.fragment_container);
            v.setBackgroundResource(R.drawable.image_picker_drawable);
        }


        adapter = new ImageViewPagerAdapter(ImageGalleryActivity.this);
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(3);



        setWallpaper = (Button) findViewById(R.id.set_background_button);
        setWallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = prefs.edit();
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
            return 3;
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
            int width = size.x;


            String pageNumberString = "p" + String.valueOf(position);
            int bitmapResourceInt = getResources().getIdentifier(pageNumberString, "drawable", getPackageName());

            Integer[] intArray = new Integer[]{bitmapResourceInt, width};
            ImageLoader loader = new ImageLoader();
            loader.execute(intArray);
            try {
                imageView.setImageBitmap(loader.get()); //TODO
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
            Bitmap background = AnalyticsApplication.getScaledBitmap(params[0], params[1]);
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
