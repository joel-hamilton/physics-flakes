package com.hamilton.joel.wallpaper;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.internal.view.ContextThemeWrapper;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by joel on 12/10/15.
 */
public class PreferenceFrag2 extends PreferenceFrag {
    private final String TAG = "PREFS2";
//    private Tracker advancedSettingsActivityTracker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AnalyticsApplication application = (AnalyticsApplication) getActivity().getApplication();
//        advancedSettingsActivityTracker = application.getDefaultTracker();
//        advancedSettingsActivityTracker.setScreenName(TAG);
//        advancedSettingsActivityTracker.send(new HitBuilders.ScreenViewBuilder().build());
        addPreferencesFromResource(R.xml.prefs2);

        Preference goBack = getPreferenceScreen().findPreference("go_back");
        Preference clearPrefs = getPreferenceScreen().findPreference("clear_prefs");


        goBack.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
//                onBackPressed();
                return false;
            }
        });


        clearPrefs = getPreferenceScreen().findPreference("clear_prefs");
        clearPrefs.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {


                AlertDialog.Builder dialog = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AlertDialogTheme));
                dialog.setTitle("Reset all settings?");
                dialog.setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                        SharedPreferences.Editor editor = preferences.edit();
                        int oldImage = preferences.getInt("image_picker", 0);
                        editor.clear();
                        editor.putInt("image_picker", oldImage);
                        editor.commit();
//
//                        advancedSettingsActivityTracker.send(new HitBuilders.EventBuilder()
//                                .setCategory("Action")
//                                .setAction("Settings Reset")
//                                .build());

                        dialog.dismiss();
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
    }
}
