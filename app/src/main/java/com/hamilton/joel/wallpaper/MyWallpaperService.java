package com.hamilton.joel.wallpaper;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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
    private final String TAG = "MyWallPaperService";
    public static final int INT_DEFAULT_QUANTITY = 3;
    public static final int INT_DEFAULT_GRAVITY = 4;
    public static final int INT_DEFAULT_QUALITY = 1;
    private SharedPreferences prefs;
    private Tracker wallpaperServiceTracker;


    @Override
    public Engine onCreateEngine() {

        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        wallpaperServiceTracker = application.getDefaultTracker();
        wallpaperServiceTracker.setScreenName(TAG);
        wallpaperServiceTracker.send(new HitBuilders.ScreenViewBuilder().build());

        return new MyWallpaperEngine();
    }

    private class MyWallpaperEngine extends Engine {
        private final Handler handler = new Handler();
        private final Runnable drawRunner = new Runnable() {
            @Override
            public void run() {
                draw();
            }
        };

        private List<Flake> flakes;
        private Paint paint = new Paint();
        private Paint defaultPaint = new Paint();
        private Deque<Point> pointList = new LinkedList<>();
        private Deque<Long> timeList = new LinkedList<>();
        public int width;
        public int height;
        private Rect screenSize;

        private boolean shakeOnVisible;
        private int maxNumber;
        private double gravity;
        private int flakeQuality;
        private int flakeFactor;
        private String imagePosition;
        private boolean visible = true;
        private boolean touchEnabled;
        private boolean shakeEnabled;
        private boolean useAccelerometer;
        private SensorManager mgr;
        private Sensor accelerometer;
        private SensorEventListener sensorEventListener;
        private float x, y, z; //sensor data
        private float prev_x, prev_y, prev_z; //sensor data
        private float pitch;
        private float roll;
        private long shakeLastTime;
        private long shakeCurrentTime;
        Bitmap background;
        SharedPreferences.OnSharedPreferenceChangeListener prefListener;
        private Display display = ((WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        private int rotation;
        private float[] gravityEvent;

        public MyWallpaperEngine() {
            Log.i(TAG, "MyWallpaperEngine Created");
            prefs = PreferenceManager.getDefaultSharedPreferences(MyWallpaperService.this);

            getPrefs(prefs);

            prefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

                    if (
                            key.equals("touch_enabled") ||
                            key.equals("enable_shake") ||
                            key.equals("antigravity_enabled") ||
                            key.equals("shake_on_visible")) {
                        Log.i(TAG, "BOOLEAN CHANGED");
                        wallpaperServiceTracker.send(new HitBuilders.EventBuilder()
                                .setCategory("Prefs Changed")
                                .setAction(key)
                                .setLabel(String.valueOf(sharedPreferences.getBoolean(key, false)))
                                .build());
                    } else {
                        Log.i(TAG, "INT CHANGED");
                        wallpaperServiceTracker.send(new HitBuilders.EventBuilder()
                                .setCategory("Prefs Changed")
                                .setAction(key)
                                .setLabel(String.valueOf(sharedPreferences.getInt(key, 0)))
                                .build());
                    }
                    getPrefs(prefs);
                    int bitmapResourceInt = getResources().getIdentifier(imagePosition, "drawable", getPackageName());
                    Log.i(TAG, "bitmapResourceInt = " + bitmapResourceInt);
                    if (bitmapResourceInt == 0) {
                        bitmapResourceInt = R.drawable.p0;
                        Log.e(TAG, "BITMAPRESOURCEINT not parsed");
                    }

                    background = AnalyticsApplication.getBitmapFromResource(bitmapResourceInt, width, height);
                    addFlakesToArray();
                }
            };

            handler.post(drawRunner);

            if (flakes != null) {
                flakes.clear();
            }
        }

        private void getPrefs(SharedPreferences prefs) {
            int temp   = prefs.getInt("number_of_flakes", INT_DEFAULT_QUANTITY);
            int temp2 = 6 - temp;
                flakeFactor = temp2 * 1500;

            maxNumber = calculateFlakeNumber(flakeFactor);
            imagePosition = "p" + prefs.getInt("image_picker", 0);
            shakeOnVisible = prefs.getBoolean("shake_on_visible", false);
            gravity = prefs.getInt("gravity_strength", INT_DEFAULT_GRAVITY);
            flakeQuality = 1 + prefs.getInt("flake_quality", INT_DEFAULT_QUALITY);
            touchEnabled = prefs.getBoolean("touch_enabled", true);
            shakeEnabled = prefs.getBoolean("enable_shake", true);
            useAccelerometer = prefs.getBoolean("antigravity_enabled", true);

            Log.i(TAG, "imagePosition = " + imagePosition);
            Log.i(TAG, "maxNumber = " + maxNumber);
            Log.i(TAG, "flakeFactor = " + flakeFactor);
            Log.i(TAG, "gravity = " + gravity);
            Log.i(TAG, "quality = " + flakeQuality);
            Log.i(TAG, "touch = " + touchEnabled);
            Log.i(TAG, "shake = " + shakeEnabled);
            Log.i(TAG, "shakeOnVisible = " + shakeOnVisible);
            Log.i(TAG, "useAccelerometer = " + useAccelerometer);

        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            this.visible = visible;

            prefs.registerOnSharedPreferenceChangeListener(prefListener);
            rotation = display.getRotation();

            if (visible) {
                if (shakeOnVisible) {
                    shakeEvent(30);
                }
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
                        if (shakeEnabled && (shake > 20)) {
                            Log.i(TAG, "calling shakeevent: strength = " + shake);
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

            int bitmapResourceInt = getResources().getIdentifier(imagePosition , "drawable", getPackageName());
            Log.i(TAG, "bitmapResourceInt = " + bitmapResourceInt);
            if (bitmapResourceInt == 0) {
                bitmapResourceInt = R.drawable.p0;
                Log.e(TAG, "BITMAPRESOURCEINT not parsed");
            }

            background = AnalyticsApplication.getBitmapFromResource(bitmapResourceInt, this.width, this.height);

            maxNumber = calculateFlakeNumber(flakeFactor);
            addFlakesToArray();
        }

        @Override
        public void onTouchEvent(MotionEvent event) {
            if (touchEnabled) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    pointList.clear();
                    timeList.clear();
                    pointList.addFirst(new Point((int) event.getX(), (int) event.getY()));
                    timeList.addFirst(System.currentTimeMillis());
                }

                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    double distanceFromTouch;

                    for (Flake flake : flakes) {
                        distanceFromTouch = (Math.abs(flake.x - event.getX()) + Math.abs(flake.y - event.getY())) / 200;

                        if (distanceFromTouch < 5) {
                            flake.toFling = true;
                            flake.distanceFromTouch = distanceFromTouch;
                        }
                    }
                    pointList.addFirst(new Point((int) event.getX(), (int) event.getY()));
                    timeList.addFirst(System.currentTimeMillis());

                    if (pointList.size() > 5) {
                        pointList.removeLast();
                    }
                    if (timeList.size() > 5) {
                        timeList.removeLast();
                    }
                }

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (pointList.isEmpty()) {
                        Log.e(TAG, "onTouchEvent POINTSLIST IS EMPTY");
                        return;
                    }

                    double dx = pointList.peekFirst().x - pointList.peekLast().x; //LAST touch (first element), then FIRST touch
                    double dy = pointList.peekFirst().y - pointList.peekLast().y; //LAST touch (first element), then FIRST touch
                    long timeInterval = timeList.peekFirst() - timeList.peekLast();
                    double flakeSpeed;
                    double distance = Math.sqrt(((dx * dx) + (dy * dy)) + .1);
                    double rads = Math.atan2(dy, dx);
                    double speed = distance / (timeInterval + .001);

                    for (Flake flake : flakes) {
                        if (flake.toFling) {
                            flakeSpeed = (speed / (flake.distanceFromTouch + .1) * (flake.size * 3)) / 5;

                            if (flakeSpeed > (flake.size * 5)) {
                                flake.velocity = (flake.size * 5);
                            } else {
                                flake.velocity = flakeSpeed;
                            }
                            flake.launchAngle = rads + (Math.random() - .5);
                            flake.toFling = false;
                            flake.distanceFromTouch = 1;
                        }
                    }
                }
                super.onTouchEvent(event);
            }
        }

        private void draw() {
            SurfaceHolder holder = getSurfaceHolder();
            Canvas canvas = null;
            double randomizer;
            try {
                canvas = holder.lockCanvas();
                if (canvas != null) {
                    for (Flake flake : flakes) {

                        //-----------------------FLING-----------------

                        flake.x += Math.cos(flake.launchAngle) * flake.velocity * 2;
                        flake.y += Math.sin(flake.launchAngle) * flake.velocity * 2;
                        if (flake.velocity > 10) {
                            flake.velocity -= 0.5;
                        } else if (flake.velocity > 1) {
                            flake.velocity -= 0.05;
                        } else if (flake.velocity < .02) {
                            flake.velocity = 0.05;
                        }
                        //-----------------------GRAVITY-----------------

                        if (!useAccelerometer) {
                            pitch = 0;
                            roll = 1;
                        }

                        randomizer = (Math.random() - .5);
                        pitch += randomizer/1000;
                        randomizer = (Math.random() - .5);
                        roll += randomizer/1000;
                        flake.x -= ( pitch  ) * (gravity/3) * flake.size;
                        flake.y += (gravity/5) + ( roll )  * (gravity/3) * flake.size; //default to slow +y gravity direction

                        //-----------------------------------------------

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

            float relativeFlakeSize = getBaseContext().getResources().getDimension(R.dimen.maxSnowSize);

            if (flakes == null) {
                flakes = SnowflakeList.getSnowflakeList();
            } else {
                flakes.clear();
            }
            for (int i = 0; i < maxNumber; i++) {

                float x = (float) (width * Math.random());
                float y = (float) (height * Math.random());
                float size = (float) (relativeFlakeSize * Math.random() + 2);

                flakes.add(new Flake(x, y, size)); //todo
            }
        }

        private void drawFlakes(Canvas canvas, List<Flake> flakeList) {

            int passes = flakeQuality;// / 2;
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

