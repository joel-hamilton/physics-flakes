package com.hamilton.joel.physicsflakes;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by joel on 15/10/15.
 */
public class AboutActivity extends AppCompatActivity {
    private final String TAG = "LOGRatingActivity";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_activity);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.about_container);
        if (fragment == null) {
            fragment = new AboutFragment();
            fm.beginTransaction().add(R.id.about_container, fragment).commit();
        }
    }
}
