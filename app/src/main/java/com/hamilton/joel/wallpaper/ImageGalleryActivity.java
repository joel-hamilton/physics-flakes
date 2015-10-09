package com.hamilton.joel.wallpaper;

import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;

import javax.security.auth.login.LoginException;

/**
 * Created by joel on 06/10/15.
 */
public class ImageGalleryActivity extends AppCompatActivity {
    private final String TAG = "ImageGalleryActivity";

    private ViewPager pager;
    private ImageViewPagerAdapter adapter;
//    private CustomPagerAdapter adapter;
    private Button setWallpaper;
    private Button cancel;
//    private ProgressBar pBar;
    private Tracker imageGalleyActivityTracker;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        imageGalleyActivityTracker = application.getDefaultTracker();
        imageGalleyActivityTracker.setScreenName(TAG);
        imageGalleyActivityTracker.send(new HitBuilders.ScreenViewBuilder().build());

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Log.i(TAG, "onCreate CALLED");
        setContentView(R.layout.image_gallery_image_pager);

        if ((findViewById(R.id.fragment_container) != null)) {
            Log.i(TAG, "onCreate VIEW EXISTS");
            View v = findViewById(R.id.fragment_container);
            v.setBackgroundColor(getResources().getColor(R.color.backgroundColorLight));
        }
//
//        pBar = (ProgressBar) findViewById(R.id.progressBar);
//        pBar.setVisibility(View.VISIBLE);

        adapter = new ImageViewPagerAdapter(ImageGalleryActivity.this);
//        adapter = new CustomPagerAdapter(getSupportFragmentManager(), this);


        pager = (ViewPager) findViewById(R.id.pager);
        pager.setBackgroundColor(getResources().getColor(R.color.backgroundColor));
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(3);
//
//
//        final Thread thread = new Thread() {
//            @Override
//            public void run() {
//
//
//                Handler handler = new Handler();
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        pager.setOffscreenPageLimit(10);
//                        Log.i(TAG, "OFFSCREEN LIMIT " + pager.getOffscreenPageLimit());
//                        pBar.setVisibility(View.INVISIBLE);
//                    }
//                }, 3000);
//            }
//        };
//        thread.run();

        setWallpaper = (Button) findViewById(R.id.set_background_button);
        setWallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("imagePicker", pager.getCurrentItem()); //TODO this works; set prefs according to image selected
                editor.commit();
                Intent i = new Intent(ImageGalleryActivity.this, MyPreferencesActivity.class);
                startActivity(i);
                finish();
            }
        });

        cancel = (Button) findViewById(R.id.cancel_button);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.i(TAG, "" + pager.getCurrentItem());
                Intent i = new Intent(ImageGalleryActivity.this, MyPreferencesActivity.class);
                startActivity(i);
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
//            return mResources.length;
            return 3;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((LinearLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            View itemView = mLayoutInflater.inflate(R.layout.pager_item, container, false);
            ImageView imageView = (ImageView) itemView.findViewById(R.id.image_view);
            Point size = new Point();
            Display display = getWindowManager().getDefaultDisplay();
            display.getSize(size);
            int width = size.x;

            String pageNumberString = "p" + String.valueOf(position);
            int bitmapResourceInt = getResources().getIdentifier(pageNumberString, "drawable", getPackageName());

//            ArrayList<Integer> intList = new ArrayList<>();
            Integer[] intArray = new Integer[]{bitmapResourceInt, width};
//            intList.add(bitmapResourceInt);
//            intList.add(width);
            ImageLoader loader = new ImageLoader();
            loader.execute(intArray);
//            loader.execute(intList);
            try {
                imageView.setImageBitmap(loader.get()); //TODO
            } catch (Exception e) {
                Log.e(TAG, "onCreateView EXCEPTION", e);
            }


//            imageView.setImageBitmap(background);
//            imageView.setImageResource(mResources[position]);

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
//    private class ImageLoader extends AsyncTask<ArrayList<Integer>, Void, Bitmap> {


        @Override
        protected Bitmap doInBackground(Integer... params) {
//        protected Bitmap doInBackground(ArrayList<Integer>... params) {
            Bitmap background = AnalyticsApplication.getScaledBitmap(params[0], params[1]);
//            Bitmap background = BitmapMethods.getScaledBitmap(params[0], params[1]);
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
