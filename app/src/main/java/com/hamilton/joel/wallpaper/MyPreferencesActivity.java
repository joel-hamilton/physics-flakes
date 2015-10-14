package com.hamilton.joel.wallpaper;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by joel on 29/09/15.
 */

public class MyPreferencesActivity extends PreferenceActivity {
    private final String TAG = "PreferencesActivity";
    private final int PICK_PHOTO = 2;
    private Tracker prefsActivityTracker;

    public static MyPreferencesActivity getInstance() {
        return new MyPreferencesActivity();
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkFirstRun();
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        prefsActivityTracker = application.getDefaultTracker();
        prefsActivityTracker.setScreenName(TAG);
        prefsActivityTracker.send(new HitBuilders.ScreenViewBuilder().build());
        addPreferencesFromResource(R.xml.prefs);

        Preference advancedPrefs;
        Preference chooseFromGallery;
        Preference rate;
        Preference upgrade;
        Preference contact;
        Preference clearPrefs;
        Preference applyWallpaper;
        final Preference imagePicker;


        chooseFromGallery = getPreferenceScreen().findPreference("image_picker_from_gallery");
        chooseFromGallery.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_PHOTO);
                return true;
            }
        });



        advancedPrefs = getPreferenceScreen().findPreference("advanced_prefs");
        advancedPrefs.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent i = new Intent(MyPreferencesActivity.this, SecondPrefsActivity.class);
                startActivity(i);
                return false;
            }
        });


        upgrade = getPreferenceScreen().findPreference("upgrade");
        upgrade.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override//temp implementation
            public boolean onPreferenceClick(Preference preference) {
                Intent i = new Intent(MyPreferencesActivity.this, UnlockPagerActivity.class);
//                Intent i = new Intent(MyPreferencesActivity.this, UpgradeActivity.class);
                startActivity(i);
                return true;
            }
        });

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

        imagePicker = getPreferenceScreen().findPreference("image_picker");

        imagePicker.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent i = new Intent(MyPreferencesActivity.this, ImageGalleryActivity.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                return true;
            }
        });

//        contact = getPreferenceScreen().findPreference("contact");
//        contact.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//            @Override
//            public boolean onPreferenceClick(Preference preference) {
//
//                String[] email = new String[] {"joelhamilton5@gmail.com"};
//
//                Intent i = new Intent(Intent.ACTION_SENDTO);
//                i.setType("text/plain");
//                i.setData(Uri.parse("mailto:"));
//                i.putExtra(Intent.EXTRA_EMAIL, email);
//                i.putExtra(Intent.EXTRA_SUBJECT, "Get your act together!");
//                startActivity(i);
//                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//
//
//                return false;
//            }
//        });

        applyWallpaper= getPreferenceScreen().findPreference("apply_wallpaper");
        applyWallpaper.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                setAsWallpaper();
                return true;
            }
        });

    }


    @Override
    public void onActivityResult(int request, int result, Intent data) {
        super.onActivityResult(request, result, data);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MyPreferencesActivity.this);

        if ((request == PICK_PHOTO) && (result == Activity.RESULT_OK)) {
            if (data == null) {
                Log.e(TAG, "no photo returned");
                prefs.edit().putString("image_uri", "-1").commit();
                prefs.edit().putBoolean("load_stream", false).commit();
                return;
            }
            Log.i(TAG, "photo returned");
            prefs.edit().putString("image_uri", data.getDataString()).commit();
            prefs.edit().putBoolean("load_stream", true).commit();
        } else {
            Log.i(TAG, "onActivityResult result!=ACTIVITY_OK");
            prefs.edit().putString("image_uri", "-1").commit();
            prefs.edit().putBoolean("load_stream", false).commit();
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
                    new ComponentName(MyPreferencesActivity.this, MyWallpaperService.class));
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

    private void checkFirstRun() {

        final String PREFS_NAME = "MyPrefsFile";
        final String PREF_VERSION_CODE_KEY = "version_code";
        final String PREF_RUN_COUNT_KEY = "run_count";
        final int DOESNT_EXIST = -1;


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
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedVersionCode = prefs.getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST);
        int runCount = prefs.getInt(PREF_RUN_COUNT_KEY, 0);

        // Check for first run or upgrade
        if (currentVersionCode == savedVersionCode) {
            //Normal run
            runCount++;
            if (runCount == 10) { // TODO Implement intent to pop up give rating/buy pro activity
                Intent i = new Intent(MyPreferencesActivity.this, RatingActivity.class);
                startActivity(i);
            }

        } else if (savedVersionCode == DOESNT_EXIST) {
            //New install (or prefs cleared)//TODO welcome screen

            //Reset shared preferences
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyPreferencesActivity.this);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.commit();

            prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).commit();

        } else if (currentVersionCode > savedVersionCode) {
            //Upgraded, first run

            prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).commit();

        }

        prefs.edit().putInt(PREF_RUN_COUNT_KEY, runCount).commit();

        Log.i(TAG, "CURRENT RUNCOUNT = " + prefs.getInt(PREF_RUN_COUNT_KEY, DOESNT_EXIST));
        Log.i(TAG, "CURRENT VERSIONCODE = " + prefs.getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST));

    }
}
