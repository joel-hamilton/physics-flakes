package com.hamilton.joel.physicsflakes;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.internal.view.ContextThemeWrapper;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by joel on 11/10/15.
 */
public class SecondPrefsActivity extends PreferenceActivity {
    private final String TAG = "LOGSecondPrefsActivity";
    private Tracker advancedSettingsActivityTracker;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        advancedSettingsActivityTracker = application.getDefaultTracker();
        advancedSettingsActivityTracker.setScreenName(TAG);
        advancedSettingsActivityTracker.send(new HitBuilders.ScreenViewBuilder().build());
        addPreferencesFromResource(R.xml.prefs2);

        prefs = PreferenceManager.getDefaultSharedPreferences(SecondPrefsActivity.this);

        Preference goBack = getPreferenceScreen().findPreference(Keys.PREFS_RETURN_TO_MAIN);
        Preference clearPrefs = getPreferenceScreen().findPreference("clear_prefs");

        goBack.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                onBackPressed();
                return false;
            }
        });


        clearPrefs = getPreferenceScreen().findPreference(Keys.PREFS_CLEAR_PREFS);
        clearPrefs.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(final Preference preference) {


                AlertDialog.Builder dialog = new AlertDialog.Builder(new ContextThemeWrapper(SecondPrefsActivity.this, R.style.AlertDialogTheme));
                dialog.setTitle(getResources().getString(R.string.reset_ask));
                dialog.setPositiveButton(getResources().getString(R.string.reset), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

//                        SharedPreferences.Editor editor = preferences.edit();
                        SharedPreferences.Editor editor = prefs.edit();

//                        String imagePosition = "p" + prefs.getInt("image_picker", 0);
//                        int imgCount = prefs.getInt("img_count", 0);
//                        boolean shakeOnVisible = prefs.getBoolean("shake_on_visible", false);
//                        int gravity = prefs.getInt("gravity_strength", 3);
//                        String autoChangeTimeString = prefs.getString("auto_change_image", "0");
//                        int autoChangeTime = Integer.parseInt(autoChangeTimeString);
//                        String qualityString = prefs.getString("flake_quality", "2");
//                        int flakeQuality = 1 + Integer.parseInt(qualityString);
//                        boolean touchEnabled = prefs.getBoolean("touch_enabled", true);
//                        boolean shakeEnabled = prefs.getBoolean("enable_shake", true);
//                        boolean useAccelerometer = prefs.getBoolean("antigravity_enabled", true);
//
//                        Log.i(TAG, "auto_change_image = " + autoChangeTime);
//                        Log.i(TAG, "antigravity_enabled = " + gravity);
//                        Log.i(TAG, "flake_quality = " + flakeQuality);
//                        Log.i(TAG, "touch_enabled = " + touchEnabled);
//                        Log.i(TAG, "enable_shake = " + shakeEnabled);
//                        Log.i(TAG, "shakeOnVisible = " + shakeOnVisible);
//                        Log.i(TAG, "useAccelerometer = " + useAccelerometer);


                        int oldImage = prefs.getInt(Keys.PREFS_IMG_FROM_VIEWPAGER, 0);
                        boolean oldLoadStream = prefs.getBoolean(Keys.PREFS_LOAD_STREAM, false);
                        String oldUri = prefs.getString(Keys.PREFS_IMAGE_URI, "-1");
                        int imgCount = prefs.getInt(Keys.PREFS_IMAGE_COUNT, 0);

                        editor.clear();
//                        editor.commit();//TODO fix this so prefs refresh themselves
//

//                        PreferenceManager.setDefaultValues(getApplicationContext(), R.xml.prefs2, true);
//                        PreferenceManager.setDefaultValues(getApplicationContext(), R.xml.prefs, true);

                        editor.putInt(Keys.PREFS_IMG_FROM_VIEWPAGER, oldImage);
                        editor.putBoolean(Keys.PREFS_LOAD_STREAM, oldLoadStream);
                        editor.putString(Keys.PREFS_IMAGE_URI, oldUri);
                        editor.putInt(Keys.PREFS_IMAGE_COUNT, imgCount);
                        editor.commit();

//                        imagePosition = "p" + prefs.getInt("image_picker", 0);
//                         imgCount = prefs.getInt("img_count", 0);
//                         shakeOnVisible = prefs.getBoolean("shake_on_visible", false);
//                         gravity = prefs.getInt("gravity_strength", 3);
//                         autoChangeTimeString = prefs.getString("auto_change_image", "0");
//                         autoChangeTime = Integer.parseInt(autoChangeTimeString);
//                         qualityString = prefs.getString("flake_quality", "2");
//                         flakeQuality = 1 + Integer.parseInt(qualityString);
//                         touchEnabled = prefs.getBoolean("touch_enabled", true);
//                         shakeEnabled = prefs.getBoolean("enable_shake", true);
//                         useAccelerometer = prefs.getBoolean("antigravity_enabled", true);
//
//                        Log.i(TAG, "auto_change_image = " + autoChangeTime);
//                        Log.i(TAG, "antigravity_enabled = " + gravity);
//                        Log.i(TAG, "flake_quality = " + flakeQuality);
//                        Log.i(TAG, "touch_enabled = " + touchEnabled);
//                        Log.i(TAG, "enable_shake = " + shakeEnabled);
//                        Log.i(TAG, "shakeOnVisible = " + shakeOnVisible);
//                        Log.i(TAG, "useAccelerometer = " + useAccelerometer);

                        advancedSettingsActivityTracker.send(new HitBuilders.EventBuilder()
                                .setCategory("Action")
                                .setAction("Settings Reset")
                                .build());

                        dialog.dismiss();
                        SecondPrefsActivity.this.recreate();//TODO
//                        SecondPrefsActivity.this.finish();
//
//                        new Handler().post(new Runnable() {
//
//                            @Override
//                            public void run() {
//                                Intent intent = SecondPrefsActivity.this.getIntent();
////                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                                startActivity(intent);
//                                SecondPrefsActivity.this.finish();
//
//                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//                            }
//                        });

                    }
                });
                dialog.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                return true;
            }
        });
    }
}
