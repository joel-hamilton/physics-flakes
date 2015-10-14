package com.hamilton.joel.wallpaper;

import android.app.ProgressDialog;
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

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by joel on 11/10/15.
 */
public class SecondPrefsActivity extends PreferenceActivity {
    private final String TAG = "SecondPrefsActivity";
    private Tracker advancedSettingsActivityTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        advancedSettingsActivityTracker = application.getDefaultTracker();
        advancedSettingsActivityTracker.setScreenName(TAG);
        advancedSettingsActivityTracker.send(new HitBuilders.ScreenViewBuilder().build());
        addPreferencesFromResource(R.xml.prefs2);

        Preference goBack = getPreferenceScreen().findPreference("go_back");
        Preference clearPrefs = getPreferenceScreen().findPreference("clear_prefs");


        goBack.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                onBackPressed();
                return false;
            }
        });


        clearPrefs = getPreferenceScreen().findPreference("clear_prefs");
        clearPrefs.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(final Preference preference) {


                AlertDialog.Builder dialog = new AlertDialog.Builder(new ContextThemeWrapper(SecondPrefsActivity.this, R.style.AlertDialogTheme));
                dialog.setTitle(getResources().getString(R.string.reset_ask));
                dialog.setPositiveButton(getResources().getString(R.string.reset), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SecondPrefsActivity.this);
                        SharedPreferences.Editor editor = preferences.edit();
                        int oldImage = preferences.getInt("image_picker", 0);
                        boolean oldLoadStream = preferences.getBoolean("load_stream", false);
                        String oldUri = preferences.getString("image_uri", "-1");
                        editor.clear();
                        editor.putBoolean("load_image", oldLoadStream);
                        editor.putString("image_uri", oldUri);
                        editor.putInt("image_picker", oldImage);
                        editor.commit();

                        advancedSettingsActivityTracker.send(new HitBuilders.EventBuilder()
                                .setCategory("Action")
                                .setAction("Settings Reset")
                                .build());

                        dialog.dismiss();
                        Intent i = new Intent(SecondPrefsActivity.this, SecondPrefsActivity.class);
                        SecondPrefsActivity.this.recreate();
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
