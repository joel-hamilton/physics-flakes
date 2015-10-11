package com.hamilton.joel.wallpaper;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by joel on 11/10/15.
 */

public class UpgradeFragment extends Fragment {
    private final String TAG = "LOGUpgradeFragment";

    Button upgrade;
    ImageView starView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.i(TAG, "onCreateView CALLED");
        View v = inflater.inflate(R.layout.upgrade_fragment, container, false);
        v.setBackgroundResource(R.drawable.rounded_corners_background);


        starView = (ImageView) v.findViewById(R.id.star_image_view);
        Window window = getActivity().getWindow();
        BitmapFactory.Options options = new BitmapFactory.Options();
        int pngWidth = (int) getActivity().getResources().getDimension(R.dimen.star_png_width);
        starView.setImageBitmap(AnalyticsApplication.getScaledBitmapFromRes(R.drawable.stars, pngWidth));

        upgrade = (Button) v.findViewById(R.id.upgrade_button);

        upgrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO direct this to play store paid app
            try { //TODO change packagename
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.aldiko.android")));// + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.aldiko.android")));// + appPackageName)));
            }
            }
        });

        return v;
    }
}
