package com.hamilton.joel.wallpaper;

import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.util.Log;

/**
 * Created by joel on 12/10/15.
 */
public class PreferenceFrag extends PreferenceFragment {
    private final String TAG = "PREFS1";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate CALLED");
        addPreferencesFromResource(R.xml.prefs);

    Preference advancedPrefs;
    Preference rate;
    Preference upgrade;
    Preference contact;
    Preference clearPrefs;
    Preference applyWallpaper;
    final Preference imagePicker;


    advancedPrefs = getPreferenceScreen().findPreference("advanced_prefs");
    advancedPrefs.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            Intent i = new Intent(getActivity(), Prefs2Activity.class);
            startActivity(i);
            getActivity().finish();
            return true;
        }
    });


    upgrade = getPreferenceScreen().findPreference("upgrade");
    upgrade.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
        @Override//temp implementation
        public boolean onPreferenceClick(Preference preference) {
            Intent i = new Intent(getActivity(), UnlockPagerActivity.class);
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
            Intent i = new Intent(getActivity(), ImageGalleryActivity.class);
            startActivity(i);
            getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

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

            try {
                Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
                intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                        new ComponentName(getActivity(), MyWallpaperService.class));
                startActivity(intent);
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER);
                startActivity(intent);
                getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
            return true;
        }
    });

}

//
//    @Override
//    public void onBackPressed() {
//
//        WallpaperManager mgr = WallpaperManager.getInstance(getContext());
//        if ((mgr.getWallpaperInfo() == null)) {
//            AlertDialog.Builder dialog = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AlertDialogTheme));
//            dialog.setTitle("Would you like to apply this wallpaper?");
//            dialog.setPositiveButton("Apply", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    try {
//                        Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
//                        intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
//                                new ComponentName(getActivity(), MyWallpaperService.class));
//                        startActivity(intent);
//                        getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//
//                    } catch (Exception e) {
//                        Intent intent = new Intent();
//                        intent.setAction(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER);
//                        startActivity(intent);
//                        getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//
//                    } finally {
//                        onBackPressed();
////                        PreferenceFrag.super.onBackPressed();//TODO
//                    }
//                }
//            });
//            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    onBackPressed();//MyPreferencesActivity.super.onBackPressed();
//                }
//            });
//            dialog.show();
//        } else {
//            onBackPressed();
////            super.onBackPressed();
//        }
//    }

    private void checkFirstRun() {

        final String PREFS_NAME = "MyPrefsFile";
        final String PREF_VERSION_CODE_KEY = "version_code";
        final String PREF_RUN_COUNT_KEY = "run_count";
        final int DOESNT_EXIST = -1;


        // Get current version code
        int currentVersionCode = 0;
        try {
            currentVersionCode = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionCode;
        } catch (android.content.pm.PackageManager.NameNotFoundException e) {
            // handle exception
            e.printStackTrace();
            return;
        }

        // Get saved version code
        SharedPreferences prefs = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int savedVersionCode = prefs.getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST);
        int runCount = prefs.getInt(PREF_RUN_COUNT_KEY, 0);

        // Check for first run or upgrade
        if (currentVersionCode == savedVersionCode) {
            //Normal run
            runCount++;
            if (runCount == 10) { // TODO Implement intent to pop up give rating/buy pro activity
                Intent i = new Intent(getActivity(), RatingActivity.class);
                startActivity(i);
            }

        } else if (savedVersionCode == DOESNT_EXIST) {
            //New install (or prefs cleared)//TODO welcome screen

            //Reset shared preferences
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
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
