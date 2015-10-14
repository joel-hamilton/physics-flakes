package com.hamilton.joel.wallpaper;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.List;

/**
 * Created by joel on 12/10/15.
 */
public class MainActivity extends Activity {
    private final String TAG = "MAIN ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        getFragmentManager().beginTransaction()
            .replace(R.id.prefs_fragment_container, new PreferenceFrag())
            .commit();
    }

}
