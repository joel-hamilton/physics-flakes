package com.hamilton.joel.wallpaper;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;
/**
 * Created by joel on 08/10/15.
 */

/**
 * This is a subclass of {@link Application} used to provide shared objects for this app, such as
 * the {@link Tracker}.
 */
public class AnalyticsApplication extends Application {
    private static final String TAG = "LOGAnalyticsApplication";
    private Tracker mTracker;
    private static Context context;

    public void onCreate() {
        super.onCreate();
        context = this;
    }

    private static Context getContext() {
        return context;
    }

    public static Bitmap getBitmapFromResource(int resource, int width, int height) {
        Bitmap background;
        background = decodeSampledBitmapFromResource(getContext().getResources(), resource, width, height);

        return background;
    }

    public static Bitmap getScaledBitmap(int resId, int reqWidth) {
        Bitmap background;
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(), resId, options);
        double width =  options.outWidth;
        double height = options.outHeight;
        double whRatio = width/height;
        int newHeight = (int) (reqWidth / whRatio);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, newHeight);
        options.inJustDecodeBounds = false;
        return Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), resId, options), reqWidth, newHeight, true);
    }


    private static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                          int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        Log.i(TAG, "decodeSampledBitmapFromResource SAMPLESIZE = " + options.inSampleSize);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, resId, options), reqWidth, reqHeight, true);
//            return BitmapFactory.decodeResource(res, resId, options);
    }

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     * @return tracker
     */
    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(R.xml.analytics_config);
//            mTracker = analytics.newTracker(R.xml.global_tracker);
        }
        return mTracker;
    }
}
