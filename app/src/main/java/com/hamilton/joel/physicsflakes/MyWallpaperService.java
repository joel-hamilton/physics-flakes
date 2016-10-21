package com.hamilton.joel.physicsflakes;

import java.io.InputStream;
import java.io.StreamCorruptedException;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.WindowManager;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by joel on 29/09/15.
 */

public class MyWallpaperService extends WallpaperService {
    private final String TAG = "LOGMyWallPaperService";
    public static final int INT_DEFAULT_QUANTITY = 3;
    public static final int INT_DEFAULT_GRAVITY = 3;
    public static final String STR_DEFAULT_QUALITY = "1";
    private long startTime;
    private SharedPreferences prefs;
    private Tracker wallpaperServiceTracker;
    private int rotation;
    Display display;

    @Override
    public Engine onCreateEngine() {

        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        wallpaperServiceTracker = application.getDefaultTracker();
        wallpaperServiceTracker.setScreenName(TAG);
        wallpaperServiceTracker.send(new HitBuilders.ScreenViewBuilder().build());
        startTime = System.currentTimeMillis();

        display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        return new MyWallpaperEngine();
    }
    public void onConfigurationChanged (Configuration newConfig) {
        Log.i(TAG, "onConfigurationChange CALLED");
        rotation = display.getRotation();
        Log.i(TAG, "onConfigurationChanged: rotation = " + display.getRotation());
    }

    private class MyWallpaperEngine extends Engine {
        private final Handler handler = new Handler();
        private final Runnable drawRunner = new Runnable() {
            @Override
            public void run() {
                draw();
            }
        };

        //draw & dimens
        Bitmap background;
        private Paint paint = new Paint();
        private Paint defaultPaint = new Paint();
        public int width;
//        private Display display = ((WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        public int height;
        private Rect screenSize;
        private List<Flake> flakes;
        private Deque<Point> pointList = new LinkedList<>();
        private Deque<Long> timeList = new LinkedList<>();

        //prefs
        SharedPreferences.OnSharedPreferenceChangeListener prefListener;
        private boolean visible = true;
        private boolean touchEnabled;
        private boolean shakeEnabled;
        private boolean useAccelerometer;
        private boolean shakeOnVisible;
        private int maxNumber;
        private int flakeQuality;
        private int flakeFactor;
        private int imgCount;
        private double gravity;
        private long autoChangeTime;
        private String imagePosition;
        private Uri imageUri;

        //sensors
        private SensorManager mgr;
        private Sensor accelerometer;
        private SensorEventListener sensorEventListener;
        private float x, y, z;
        private float prev_x, prev_y, prev_z;
        private float pitch;
        private float roll;
        private long shakeLastTime;
        private long shakeCurrentTime;
//        private int rotation;
        private float[] gravityEvent;


        public MyWallpaperEngine() {
            Log.i(TAG, "LOGMyWallpaperEngine Created");
            prefs = PreferenceManager.getDefaultSharedPreferences(MyWallpaperService.this);

            getPrefs(prefs);

            prefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                    Log.i(TAG, "onSharedPreferenceChanged CALLED: key = " + key);
                    if (key.equals("auto_change_image") && !sharedPreferences.getString(key, "0").equals("0")) {
                        Log.i(TAG, "onSharedPreferenceChanged AUTOCHANGE != 0");
                        prefs.edit().putString("image_uri", "-1").commit();
                        getBackgroundBitmapFromRes();
                    }

                    try {
                        if (
                                key.equals("touch_enabled") ||
                                        key.equals("enable_shake") ||
                                        key.equals("load_stream") ||
                                        key.equals("antigravity_enable") ||
                                        key.equals("shake_on_visible")
                                ) {
                            Log.i(TAG, "BOOLEAN CHANGED");
                            wallpaperServiceTracker.send(new HitBuilders.EventBuilder()
                                    .setCategory("Prefs Changed")
                                    .setAction(key)
                                    .setLabel(String.valueOf(sharedPreferences.getBoolean(key, false)))
                                    .build());
                        } else if (key.equals("image_uri") ||
                                (key.equals("auto_change_image")) ||
                                (key.equals("flake_quality"))
                                ) {
                            Log.i(TAG, "STRING CHANGED");
                            wallpaperServiceTracker.send(new HitBuilders.EventBuilder()
                                    .setCategory("Prefs Changed")
                                    .setAction(key)
                                    .setLabel(sharedPreferences.getString(key, "-1"))
                                    .build());
                        } else {
                            Log.i(TAG, "INT CHANGED");
                            wallpaperServiceTracker.send(new HitBuilders.EventBuilder()
                                    .setCategory("Prefs Changed")
                                    .setAction(key)
                                    .setLabel(String.valueOf(sharedPreferences.getInt(key, 0)))
                                    .build());
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Fix this, fool", e);
                    }
                    getPrefs(prefs);
                    getBackgroundBitmap();
                    addFlakesToArray();
                }
            };
        }

        private void getBackgroundBitmap() {
            String uriString = prefs.getString("image_uri", "-1");

            if (uriString.equals("-1") && prefs.getBoolean("load_stream", false)) {
                Log.i(TAG, "NUMBER ONE");
                prefs.edit().putBoolean("load_stream", false).commit();
//                popErrorDialog();
                getBackgroundBitmapFromRes();
            } else if (!uriString.equals("-1")){
                Log.i(TAG, "NUMBER TWO");

                imageUri = Uri.parse(uriString);
                getBackgroundBitmapFromStream();
            }else {
                Log.i(TAG, "NUMBER THREE");
                getBackgroundBitmapFromRes();
            }
        }


        private void getBackgroundBitmapFromStream() {
            Log.i(TAG, "trying to load image from stream");
            InputStream stream = null;
            try {
                stream = getContentResolver().openInputStream(imageUri);
                background  = AnalyticsApplication.getBitmapFromStream(stream, width, height);
                stream.close();
                if (background == null) {
                    throw new StreamCorruptedException("Couldn't open image stream");
                }
            //problem decoding stream
            } catch (Exception e) {
                Log.e(TAG, "couldn't decode bitmap from stream", e);
                prefs.edit().putString("image_uri", "-1").commit();
                popErrorDialog();
                getBackgroundBitmapFromRes();
            }
        }

        private void getBackgroundBitmapFromRes(){
            Log.i(TAG, "getBackgroundBitmapFromRes imagePosition = " + imagePosition);
            int bitmapResourceInt = getResources().getIdentifier(imagePosition, "drawable", getPackageName());
            background = AnalyticsApplication.getBitmapFromResource(bitmapResourceInt, width, height);
            Log.i(TAG, "background loaded from RES.DRAWABLE");
        }

        private void popErrorDialog() {
            MyDialog alert = new MyDialog(MyWallpaperService.this);
            alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            alert.setTitle(getResources().getString(R.string.cant_load_file));
            alert.show();
        }

        private void getPrefs(SharedPreferences prefs) {
            int temp   = prefs.getInt("number_of_flakes", INT_DEFAULT_QUANTITY);
            int temp2 = 6 - temp;
                flakeFactor = temp2 * 2000;

            imagePosition = "p" + prefs.getInt("image_picker", 0);
            imgCount = prefs.getInt("img_count", 0);
            shakeOnVisible = prefs.getBoolean("shake_on_visible", false);
            gravity = prefs.getInt("gravity_strength", INT_DEFAULT_GRAVITY);
            String autoChangeTimeString = prefs.getString("auto_change_image", "0");
            autoChangeTime = Integer.parseInt(autoChangeTimeString);
            String qualityString = prefs.getString("flake_quality", STR_DEFAULT_QUALITY);
            flakeQuality = 1 + Integer.parseInt(qualityString);
            touchEnabled = prefs.getBoolean("touch_enabled", true);
            shakeEnabled = prefs.getBoolean("enable_shake", true);
            useAccelerometer = prefs.getBoolean("antigravity_enabled", true);

//            Log.i(TAG, "img_count = " + imgCount);
//            Log.i(TAG, "auto_change_image = " + autoChangeTime);
//            Log.i(TAG, "image_uri = " + imageUri);
//            Log.i(TAG, "imagePosition = " + imagePosition);
//            Log.i(TAG, "flakeFactor = " + flakeFactor);
//            Log.i(TAG, "antigravity_enabled = " + gravity);
//            Log.i(TAG, "flake_quality = " + flakeQuality);
//            Log.i(TAG, "touch_enabled = " + touchEnabled);
//            Log.i(TAG, "enable_shake = " + shakeEnabled);
//            Log.i(TAG, "shakeOnVisible = " + shakeOnVisible);
//            Log.i(TAG, "useAccelerometer = " + useAccelerometer);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            Log.i(TAG, "onVisibilityChanged: rotation = " + display.getRotation());
            this.visible = visible;
            rotation = display.getRotation();
            prefs.registerOnSharedPreferenceChangeListener(prefListener);

            if (visible) {
                //Change background based on time
                long currentTime = System.currentTimeMillis();
                if ((autoChangeTime != 0) && ((currentTime - startTime) > autoChangeTime)) {
                    startTime = currentTime;
                    int currentImgPos = prefs.getInt("image_picker", 0);
                    if (currentImgPos < (imgCount - 1)) {
                        currentImgPos ++;
                        prefs.edit().putInt("image_picker", currentImgPos).commit();
                    } else {
                        prefs.edit().putInt("image_picker", 0).commit();
                    }
                    prefs.edit().putString("image_uri", "-1").commit();
                }

                //---------AutoShake-----------//
                if (shakeOnVisible) {
                    shakeEvent(30);
                }
                //-------Tilt/Shake Calculations-------//
                mgr = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
                accelerometer = mgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                sensorEventListener = new SensorEventListener() {
                    @Override
                    public void onSensorChanged(SensorEvent event) {
                        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                            gravityEvent = event.values;
                        }

                        if (rotation != Surface.ROTATION_0) {
                            float[] adjustedValues = adjustForRotation(rotation, gravityEvent);
                            x = adjustedValues[0];
                            y = -adjustedValues[1];
                            z = adjustedValues[2];
                        } else {
                            x = gravityEvent[0];
                            y = gravityEvent[1];
                            z = gravityEvent[2];
                        }

                        pitch = (float) Math.atan(x / Math.sqrt((y * y) + (z * z)));
                        roll = (float) Math.atan(y / Math.sqrt((x * x) + (z * z)));

                        int shake = (int) Math.abs(x + y + z - prev_x - prev_y - prev_z);
                        //lower threshold for shakeEvent
                        if (shakeEnabled && (shake > 30)) {
                            shakeEvent(shake);
                        }

                        prev_x = x;
                        prev_y = y;
                        prev_z = z;
                    }

                    @Override
                    public void onAccuracyChanged(Sensor sensor, int accuracy) {

                    }
                };
                    mgr.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_UI);

                handler.post(drawRunner);
            } else {
                mgr.unregisterListener(sensorEventListener);
                handler.removeCallbacks(drawRunner);
            }
        }


        private float[] adjustForRotation(int displayRotation, float[] eventValues) {
            float[] adjustedValues = new float[3];

            final int axisSwap[][] = {
                    {1, -1, 0, 1},     // ROTATION_0
                    {-1, -1, 1, 0},     // ROTATION_90
                    {-1, 1, 0, 1},     // ROTATION_180
                    {1, 1, 1, 0}}; // ROTATION_270

            final int[] as = axisSwap[displayRotation];
            adjustedValues[0] = (float) as[0] * eventValues[as[2]];
            adjustedValues[1] = (float) as[1] * eventValues[as[3]];
            adjustedValues[2] = eventValues[2];

            return adjustedValues;
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            this.visible = false;
            mgr.unregisterListener(sensorEventListener);
            prefs.unregisterOnSharedPreferenceChangeListener(prefListener);
            handler.removeCallbacks(drawRunner);
        }

        private int calculateFlakeNumber(int flakeFactor) {
            int max;
            if (width < height) {
                max = (height * width) / (flakeFactor + 1);
            } else {
                max = (width * height) / (flakeFactor + 1);
            }
            return max;
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format,
                                     int width, int height) {
            this.width = width;
            this.height = height;
            screenSize = new Rect(0, 0, width, height);
            Log.i(TAG, "SURFACE SET: height & width  = " + this.height + " " + this.width);
            super.onSurfaceChanged(holder, format, width, height);

            getBackgroundBitmap();
            addFlakesToArray();
        }

        @Override
        public void onTouchEvent(MotionEvent event) {
            if (touchEnabled) {
                double dx, dy;
                long timeInterval;

                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    pointList.clear();
                    timeList.clear();
                    pointList.addFirst(new Point((int) event.getX(), (int) event.getY()));
                    timeList.addFirst(System.currentTimeMillis());
                }

                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    double distanceFromTouch;
                    double eventX = event.getX();
                    double eventY = event.getY();

                    for (Flake flake : flakes) {
                        //distance calc not correct, but close enough & less exp.
                        distanceFromTouch = 0.01 + (Math.abs(flake.x - eventX) + Math.abs(flake.y - eventY)) / 200;

                        if (distanceFromTouch < 2) {
                            flake.distanceFromTouch = distanceFromTouch;
                        }
                    }
                    pointList.addFirst(new Point((int) eventX, (int) eventY));
                    timeList.addFirst(System.currentTimeMillis());

                    if (pointList.size() > 5) {
                        pointList.removeLast();
                    }
                    if (timeList.size() > 5) {
                        timeList.removeLast();
                    }
                    dx = eventX - pointList.peekLast().x; //LAST touch (first element), then FIRST touch
                    dy = eventY - pointList.peekLast().y; //LAST touch (first element), then FIRST touch
                    timeInterval = (timeList.peekFirst() - timeList.peekLast());
                    calculateFlakeVelocity(dx, dy, timeInterval);
                }

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (pointList.isEmpty()) {
                        Log.e(TAG, "onTouchEvent POINTSLIST IS EMPTY");
                        return;
                    }

                    dx = pointList.peekFirst().x - pointList.peekLast().x; //LAST touch (first element), then FIRST touch
                    dy = pointList.peekFirst().y - pointList.peekLast().y; //LAST touch (first element), then FIRST touch
                    timeInterval = timeList.peekFirst() - timeList.peekLast();
                    calculateFlakeVelocity(dx, dy, timeInterval);
                }
                super.onTouchEvent(event);
            }
        }

        public void calculateFlakeVelocity(double dx, double dy, long timeInterval) {
            double distance = Math.sqrt (((dx * dx) + (dy * dy)) + .1);

            if ( distance > Math.abs(5)) {
                double flakeSpeed;
                double rads = Math.atan2(dy, dx);
                double speed = distance / (timeInterval + .001);
                double threshold = distance / 500;

                for (Flake flake : flakes) {
                    if (flake.distanceFromTouch < threshold ) {
                        flakeSpeed = (speed / (flake.distanceFromTouch + .1) * (flake.size * 3)) / 5;

                        //flake max speed
                        if (flakeSpeed > (flake.size * 3)) {
                            flake.velocity = (flake.size * 3);
                        } else {
                            flake.velocity = flakeSpeed;
                        }
                        flake.launchAngle = rads + (Math.random() - .5);
                    }
                }
            }
        }

        private void draw() {
            SurfaceHolder holder = getSurfaceHolder();
            Canvas canvas = null;
            double randomizer;
            try {
                canvas = holder.lockCanvas();

                if (canvas != null) {

                    if (!useAccelerometer) {
                        pitch = 0;
                        roll = 1;
                    }

                    for (Flake flake : flakes) {

                        //---------------FLING----------------

                        flake.x += Math.cos(flake.launchAngle) * flake.velocity * gravity/2;// * 2;
                        flake.y += Math.sin(flake.launchAngle) * flake.velocity * gravity/2;//*  2;
                        if (flake.velocity > 8) { //max flake velocity
                            flake.velocity -= 0.5;
                        } else if (flake.velocity > 1) {
                            flake.velocity -= 0.05;
                        } else if (flake.velocity < .02) {
                            flake.velocity = 0.05; //min flake velocity
                        }
                        //----------------GRAVITY----------------

                        randomizer = (Math.random() - .5);
                        pitch += randomizer/1000;
                        randomizer = (Math.random() - .5);
                        roll += randomizer/1000;
                        flake.x -= ( pitch  ) * (gravity/3) * flake.size;
                        //defaults to slow downwards gravity direction
                        flake.y += (gravity/5) + ( roll )  * (gravity/3) * flake.size;

                        //----------------FLAKE WRAP---------------

                        if (flake.y > height + 10) {
                            flake.y = -10;
                        } else if (flake.y < -10) {
                            flake.y = height + 10;
                        }
                        if (flake.x > width + 10) {
                            flake.x = -10;
                        } else if (flake.x < -10) {
                            flake.x = width + 10;
                        }
                        //-----------------------------------------
                    }
                    drawFlakes(canvas, flakes);
                }
            } finally {
                if (canvas != null)
                    holder.unlockCanvasAndPost(canvas);
            }
            handler.removeCallbacks(drawRunner);
            if (visible) {
                handler.postDelayed(drawRunner, 10);
            }
        }

        private void shakeEvent(int shakeStrength) {
            shakeCurrentTime = System.currentTimeMillis();

            //time delay between shakes
            if ((shakeCurrentTime - shakeLastTime) < 500) {
                return;
            }
            shakeLastTime = shakeCurrentTime;

            for (Flake flake : flakes) {
                flake.launchAngle = ((4 * 3.14 * Math.random()) - (2 * 3.14 * Math.random()));
                flake.velocity += Math.random() * (shakeStrength/4) * flake.size;
            }
        }

        private void addFlakesToArray() {

            maxNumber = calculateFlakeNumber(flakeFactor);
//            Log.i(TAG, "maxNumber = " + maxNumber);
            float relativeFlakeSize = getBaseContext().getResources().getDimension(R.dimen.maxSnowSize);

            if (flakes == null) {
                flakes = SnowflakeList.getSnowflakeList();
            }

            flakes.clear();

            for (int i = 0; i < maxNumber; i++) {

                float x = (float) (width * Math.random());
                float y = (float) (height * Math.random());
                float size = (float) (relativeFlakeSize * Math.random() + 2);

                flakes.add(new Flake(x, y, size)); //todo
            }
        }

        private void drawFlakes(Canvas canvas, List<Flake> flakeList) {
            int passes = flakeQuality;
            if (passes == 0) {
                passes = 1;
            }

            int alpha = 200 / passes;

            paint.setAntiAlias(true);
            paint.setARGB(alpha, 255, 255, 255);

                canvas.drawBitmap(background, null, screenSize, defaultPaint);

                for (Flake flake : flakeList) {
                    for (int i = 0; (i < passes) && ((i * 2) < flake.size); i++) {
                            canvas.drawCircle(flake.x, flake.y, flake.size - (i * 2), paint);// (flake.size / (Math.sqrt(i))), paint);
                    }
                }
            }

        }
    }

