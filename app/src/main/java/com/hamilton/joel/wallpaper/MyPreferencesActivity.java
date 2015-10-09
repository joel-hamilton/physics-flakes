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
import android.support.v7.app.AlertDialog;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
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
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        prefsActivityTracker = application.getDefaultTracker();
        prefsActivityTracker.setScreenName(TAG);
        prefsActivityTracker.send(new HitBuilders.ScreenViewBuilder().build());



        addPreferencesFromResource(R.xml.prefs);

        Preference contact;
        Preference clearPrefs;
        Preference applyWallpaper;
        final Preference imagePicker;

        imagePicker = getPreferenceScreen().findPreference("imagePicker");

        imagePicker.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent i = new Intent(MyPreferencesActivity.this, ImageGalleryActivity.class);
                startActivity(i);
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
//                i.putExtra(Intent.EXTRA_PHONE_NUMBER, "1-515-777-2459");
                i.putExtra(Intent.EXTRA_SUBJECT, "Get your act together!");
                startActivity(i);

                return false;
            }
        });

        clearPrefs = getPreferenceScreen().findPreference("clearPrefs");
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
                        int oldImage = preferences.getInt("imagePicker", 0);
                        editor.clear();
                        editor.putInt("imagePicker", oldImage);
                        editor.commit();


                        new Handler().post(new Runnable() {

                            @Override
                            public void run() {
                                Intent intent = MyPreferencesActivity.this.getIntent();
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                MyPreferencesActivity.this.finish();

                                startActivity(intent);
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

        applyWallpaper= getPreferenceScreen().findPreference("applyWallpaper");
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
                }
                return true;
            }
        });
//    }

    }



    @Override
    public void onBackPressed() {

        WallpaperManager mgr = WallpaperManager.getInstance(getApplicationContext());
//        Log.i(TAG, "mgr.getWallpaperInfo() = " + mgr.getWallpaperInfo());
        if ((mgr.getWallpaperInfo() == null)) {
            //|| (!mgr.getWallpaperInfo().toString().contains("MyWallpaperService"))) {
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
                    } catch (Exception e) {
                        Intent intent = new Intent();
                        intent.setAction(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER);
                        startActivity(intent);
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

    @Override
    public void onPause() {
        super.onPause();
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (resultCode != Activity.RESULT_OK) {
//            Log.i(TAG, "onActivityResult returned NOT OK");
//            return;
//        }
//        data.getIntExtra("IMAGE_POSITION", 0);
//
//    }
}
