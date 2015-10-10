package com.hamilton.joel.wallpaper;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.v7.app.AlertDialog;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by joel on 29/09/15.
 */

public class MyPreferencesActivity extends PreferenceActivity {
    private final String TAG = "PreferencesActivity";
    private Tracker prefsActivityTracker;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkFirstRun();
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        prefsActivityTracker = application.getDefaultTracker();
        prefsActivityTracker.setScreenName(TAG);
        prefsActivityTracker.send(new HitBuilders.ScreenViewBuilder().build());
        addPreferencesFromResource(R.xml.prefs);


        Preference contact;
        Preference clearPrefs;
        Preference applyWallpaper;
        final Preference imagePicker;





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

        contact = getPreferenceScreen().findPreference("contact");
        contact.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                String[] email = new String[] {"joelhamilton5@gmail.com"};

                Intent i = new Intent(Intent.ACTION_SENDTO);
                i.setType("text/plain");
                i.setData(Uri.parse("mailto:"));
                i.putExtra(Intent.EXTRA_EMAIL, email);
                i.putExtra(Intent.EXTRA_SUBJECT, "Get your act together!");
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);


                return false;
            }
        });

        clearPrefs = getPreferenceScreen().findPreference("clear_prefs");
        clearPrefs.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                prefsActivityTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("Click")
                        .setLabel("ClearPrefs Opened")
                        .build());

                AlertDialog.Builder dialog = new AlertDialog.Builder(new ContextThemeWrapper(MyPreferencesActivity.this, R.style.AlertDialogTheme));
                dialog.setTitle("Reset all settings?");
                dialog.setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyPreferencesActivity.this);
                        SharedPreferences.Editor editor = preferences.edit();
                        int oldImage = preferences.getInt("image_picker", 0);
                        editor.clear();
                        editor.putInt("image_picker", oldImage);
                        editor.commit();


                        new Handler().post(new Runnable() {

                            @Override
                            public void run() {
                                Intent intent = MyPreferencesActivity.this.getIntent();
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                MyPreferencesActivity.this.finish();

                                startActivity(intent);
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            }
                        });

                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                return true;
            }
        });

        applyWallpaper= getPreferenceScreen().findPreference("apply_wallpaper");
        applyWallpaper.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                try {
                    Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
                    intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                            new ComponentName(MyPreferencesActivity.this, MyWallpaperService.class));
                    startActivity(intent);
                } catch (Exception e) {
                    Intent intent = new Intent();
                    intent.setAction(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
                return true;
            }
        });

    }



    @Override
    public void onBackPressed() {

        WallpaperManager mgr = WallpaperManager.getInstance(getApplicationContext());
        if ((mgr.getWallpaperInfo() == null)) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogTheme));
            dialog.setTitle("Would you like to apply this wallpaper?");
            dialog.setPositiveButton("Apply", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
                        intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                                new ComponentName(MyPreferencesActivity.this, MyWallpaperService.class));
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                    } catch (Exception e) {
                        Intent intent = new Intent();
                        intent.setAction(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                    } finally {
                        MyPreferencesActivity.super.onBackPressed();
                    }
                }
            });
            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    MyPreferencesActivity.super.onBackPressed();
                    MyPreferencesActivity.super.onBackPressed();
                }
            });
            dialog.show();
        } else {
            super.onBackPressed();
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
            if (runCount == 5) { // TODO Implement intent to pop up give rating/buy pro activity
                Log.i(TAG, "PLEASE BUY MY APP, BRO");
            }

        } else if (savedVersionCode == DOESNT_EXIST) {
            //New install (or prefs cleared)//TODO welcome screen

            //Reset shared preferences
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyPreferencesActivity.this);
            SharedPreferences.Editor editor = preferences.edit();
            int oldImage = preferences.getInt("image_picker", 0);
            editor.clear();
            editor.putInt("image_picker", oldImage);
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
