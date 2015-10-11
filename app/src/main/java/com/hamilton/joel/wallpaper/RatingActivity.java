package com.hamilton.joel.wallpaper;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by joel on 11/10/15.
 */
public class RatingActivity extends AppCompatActivity {
    private final String TAG = "LOGRatingActivity";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rating_upgrade_activity);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.rating_upgrade_container);
        if (fragment == null) {
            Log.i(TAG, "onCreate CREATING NEW FRAGMENT");
            fragment = new RateFragment();
            fm.beginTransaction().add(R.id.rating_upgrade_container, fragment).commit();
        }
    }
}
