package com.hamilton.joel.physicsflakes;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joel on 29/09/15.
 */
public class SnowflakeList {
    private static final String TAG = "LOGSnowflakeList";
    private static final List<Flake> THE_LIST = new ArrayList<Flake>();

    public static List<Flake> getSnowflakeList() {
        return THE_LIST;
    }

    public SnowflakeList() {
        Log.e(TAG, "SnowflakeList tried to instantiate second instance");
    }
}
