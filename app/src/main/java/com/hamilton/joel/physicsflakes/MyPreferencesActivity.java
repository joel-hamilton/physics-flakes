package com.hamilton.joel.physicsflakes;

import android.app.Activity;
import android.app.Dialog;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by joel on 29/09/15.
 */

public class MyPreferencesActivity extends PreferenceActivity {
    private final String TAG = "PreferencesActivity";
    private final int PICK_PHOTO = 2;
    private Tracker prefsActivityTracker;
    private SharedPreferences prefs;
    private Dialog dialog;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        prefsActivityTracker = application.getDefaultTracker();
        prefsActivityTracker.setScreenName(TAG);
        prefsActivityTracker.send(new HitBuilders.ScreenViewBuilder().build());
        addPreferencesFromResource(R.xml.prefs);


        prefs = PreferenceManager.getDefaultSharedPreferences(MyPreferencesActivity.this);
        Preference advancedPrefs;
        Preference chooseFromGallery;
//        Preference rate;
        Preference about;
//        Preference upgrade;
//        Preference contact;
//        Preference clearPrefs;
        Preference autoChange;
        Preference applyWallpaper;
        final Preference imagePicker;

        checkFirstRun();
        countDrawables();

//                ---------------free version------------//TODO FLAG FREE VERSION (also switch in prefs.xml)
//        autoChange = getPreferenceScreen().findPreference("auto_change_image");
//        autoChange.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//            @Override
//            public boolean onPreferenceClick(Preference preference) {
//                Intent i = new Intent(MyPreferencesActivity.this, UnlockPagerActivity.class);
//                i.putExtra("page_num", 3);
//                startActivity(i);
//                return false;
//            }
//        });

        about = getPreferenceScreen().findPreference(Keys.PREFS_ABOUT);
        about.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent i = new Intent(MyPreferencesActivity.this, AboutActivity.class);
                startActivity(i);

                return false;
            }
        });

        chooseFromGallery = getPreferenceScreen().findPreference(Keys.PREFS_IMG_FROM_GALLERY);
        chooseFromGallery.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
//---------------free version------------// TODO FLAG FREE VERSION
//                Intent i = new Intent(MyPreferencesActivity.this, UnlockPagerActivity.class);
//                i.putExtra("page_num", 0);
//                startActivity(i);
//---------------pro version------------// TODO FLAG PRO VERSION
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_PHOTO);
                return true;
            }
        });



        advancedPrefs = getPreferenceScreen().findPreference(Keys.PREFS_ADVANCED_PREFS);
        advancedPrefs.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent i = new Intent(MyPreferencesActivity.this, SecondPrefsActivity.class);
                startActivity(i);
                return false;
            }
        });
//
//
//        upgrade = getPreferenceScreen().findPreference("upgrade");
//        upgrade.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//            @Override//temp implementation
//            public boolean onPreferenceClick(Preference preference) {
//                Intent i = new Intent(MyPreferencesActivity.this, UnlockPagerActivity.class);
////                Intent i = new Intent(MyPreferencesActivity.this, UpgradeActivity.class);
//                startActivity(i);
//                return true;
//            }
//        });

//        upgrade.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//            @Override
//            public boolean onPreferenceClick(Preference preference) {
////                //TODO direct this to play store paid app
//        try { //TODO change packagename
//            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.aldiko.android")));// + appPackageName)));
//        } catch (android.content.ActivityNotFoundException anfe) {
//            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.aldiko.android")));// + appPackageName)));
//        }
//                return true;
//            }
//        });



//        rate = getPreferenceScreen().findPreference("rate");
//        rate.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//            @Override
//            public boolean onPreferenceClick(Preference preference) {
//                try { //TODO change packagename
//                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.aldiko.android")));// + appPackageName)));
//                } catch (android.content.ActivityNotFoundException anfe) {
//                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.aldiko.android")));// + appPackageName)));
//                }
//                return true;
//            }
//        });

        imagePicker = getPreferenceScreen().findPreference(Keys.PREFS_IMG_FROM_VIEWPAGER);

        imagePicker.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                dialog = new Dialog(MyPreferencesActivity.this);
                dialog.setTitle(getResources().getString(R.string.loading_dialog));
                dialog.setContentView(new TextView(MyPreferencesActivity.this));
                dialog.setCancelable(true);
                dialog.show();

                Intent i = new Intent(MyPreferencesActivity.this, ImageGalleryActivity.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                Handler handler = new Handler(); //TODO not optimized
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    }
                }, 3000);

                return true;
            }
        });

        applyWallpaper= getPreferenceScreen().findPreference(Keys.PREFS_APPLY_WALLPAPER);
        applyWallpaper.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                setAsWallpaper();
                return true;
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume RESUMING");
    }


    @Override
    public void onActivityResult(int request, int result, Intent data) {

        super.onActivityResult(request, result, data);

        if ((request == PICK_PHOTO) && (result == Activity.RESULT_OK)) {
            if (data == null) {
                Log.e(TAG, "no photo returned");
                prefs.edit().putString(Keys.PREFS_IMAGE_URI, "-1").commit();
                prefs.edit().putBoolean(Keys.PREFS_LOAD_STREAM, false).commit();
                return;
            }
            Log.i(TAG, "photo returned");
            prefs.edit().putString(Keys.PREFS_AUTO_CHANGE_IMAGE, "0").commit();
            prefs.edit().putString(Keys.PREFS_IMAGE_URI, data.getDataString()).commit();
            prefs.edit().putBoolean(Keys.PREFS_LOAD_STREAM, true).commit();
        } else {
            Log.i(TAG, "onActivityResult result!=ACTIVITY_OK");
            prefs.edit().putString(Keys.PREFS_IMAGE_URI, "-1").commit();
            prefs.edit().putBoolean(Keys.PREFS_LOAD_STREAM, false).commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    @Override
    public void onBackPressed() {

        WallpaperManager mgr = WallpaperManager.getInstance(getApplicationContext());
        if ((mgr.getWallpaperInfo() == null)) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogTheme));
            dialog.setTitle(getResources().getString(R.string.apply_wallpaper_dialog));
            dialog.setPositiveButton(getResources().getString(R.string.apply), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    setAsWallpaper();
                    MyPreferencesActivity.super.onBackPressed();
                }
            });
            dialog.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    MyPreferencesActivity.super.onBackPressed();
                }
            });
            dialog.show();
        } else {
            super.onBackPressed();
        }
    }

    private void setAsWallpaper() {
        try {
            Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
            intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                    new ComponentName(getPackageName(), MyWallpaperService.class.getCanonicalName()));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            Intent intent = new Intent();
            intent.setAction(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }

    public void countDrawables() {
        Pattern imgPattern = Pattern.compile("(p)(\\d)");

        Field[] fields = R.drawable.class.getFields();
        int count = 0;
        for (int i = 0, f = fields.length; i < f; i++) {
            String name = fields[i].getName();
            Matcher m = imgPattern.matcher(name);
            if (m.find()) {
                Log.i(TAG, "MATCH FOUND " + fields[i].getName());
                count++;
            }
//            if (fields[i].getName().startsWith("p")) {
//                count++;
//            }
        }
        prefs.edit().putInt(Keys.PREFS_IMAGE_COUNT, count).commit();
    }

    private void checkFirstRun() {

        // Get current version code
        int currentVersionCode = 0;
        try {
            currentVersionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (android.content.pm.PackageManager.NameNotFoundException e) {
            // handle exception
            e.printStackTrace();
            return;
        }

        // Get saved version code
        SharedPreferences prefs = getSharedPreferences(Keys.PREFS_PREFS_FILE_NAME, MODE_PRIVATE);
        int savedVersionCode = prefs.getInt(Keys.PREFS_VERSION_CODE, Keys.PREFS_DOESNT_EXIST);
        int runCount = prefs.getInt(Keys.PREFS_RUN_COUNT, 0);

        // Check for first run or upgrade
        if (currentVersionCode == savedVersionCode) {
            //Normal run
            runCount++;
            if (runCount == 10) { // TODO Implement intent to pop up give rating/buy pro activity
                Intent i = new Intent(MyPreferencesActivity.this, RatingActivity.class);
                startActivity(i);
            }

        } else if (savedVersionCode == Keys.PREFS_DOESNT_EXIST) {
            //New install (or prefs cleared)//TODO welcome screen

            //Reset shared preferences
            SharedPreferences.Editor editor = this.prefs.edit();
            editor.clear();
            editor.commit();

            prefs.edit().putInt(Keys.PREFS_VERSION_CODE, currentVersionCode).commit();

        } else if (currentVersionCode > savedVersionCode) {
            //Upgraded, first run
            SharedPreferences.Editor editor = this.prefs.edit();
            editor.clear();
            editor.commit();

            prefs.edit().putInt(Keys.PREFS_VERSION_CODE, currentVersionCode).commit();

            prefs.edit().putInt(Keys.PREFS_VERSION_CODE, currentVersionCode).commit();

        }

        prefs.edit().putInt(Keys.PREFS_RUN_COUNT, runCount).commit();

        Log.i(TAG, "CURRENT RUNCOUNT = " + prefs.getInt(Keys.PREFS_RUN_COUNT, Keys.PREFS_DOESNT_EXIST));
        Log.i(TAG, "CURRENT VERSIONCODE = " + prefs.getInt(Keys.PREFS_VERSION_CODE, Keys.PREFS_DOESNT_EXIST));

    }
}
