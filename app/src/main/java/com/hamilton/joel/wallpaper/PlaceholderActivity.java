package com.hamilton.joel.wallpaper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by joel on 03/10/15.
 */
public class PlaceholderActivity extends AppCompatActivity {
    @Override

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        Intent two = new Intent(getApplicationContext(), MyPreferencesActivity.class);
//        two.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(two);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.slide_out_right);

//        Intent i = new Intent(getApplicationContext(), MyWallpaperService.class);
//        i.addCategory(Intent.CATEGORY_HOME);
//        startActivity(i);
//
//        Intent i = new Intent(Intent.ACTION_MAIN);
//        i.addCategory(Intent.CATEGORY_HOME);
//        startActivity(i);
//
//
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Intent two = new Intent(getApplicationContext(), MyPreferencesActivity.class);
//                two.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(two);
//                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//                Log.i("", "run STARTING NEW MYPREFACTIVITY");
//            }
//        }, 10);
//        finish();
//    }
    }
}
