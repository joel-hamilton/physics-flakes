package com.hamilton.joel.wallpaper;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by joel on 12/10/15.
 */
public class Prefs2Activity extends Activity {
    private final String TAG = "MAIN ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.prefs_2_activity);

        getFragmentManager().beginTransaction()
                .replace(R.id.prefs_2_fragment_container, new PreferenceFrag2())
                .commit();
    }
}
