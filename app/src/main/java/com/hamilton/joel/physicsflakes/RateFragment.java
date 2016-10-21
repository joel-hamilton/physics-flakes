package com.hamilton.joel.physicsflakes;

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
public class RateFragment extends Fragment{
    private final String TAG = "LOGRateFragment";

    Button rateApp;
    Button later;
    Button noThanks;
    ImageView starView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.i(TAG, "onCreateView CALLED");
        View v = inflater.inflate(R.layout.rate_fragment, container, false);
        v.setBackgroundResource(R.drawable.rounded_corners_background);


        starView = (ImageView) v.findViewById(R.id.star_image_view);
        Window window = getActivity().getWindow();
        BitmapFactory.Options options = new BitmapFactory.Options();
        int pngWidth = (int) getActivity().getResources().getDimension(R.dimen.star_png_width);
        starView.setImageBitmap(AnalyticsApplication.getScaledBitmapFromRes(R.drawable.stars, pngWidth));

        rateApp = (Button) v.findViewById(R.id.rate_button);
        later = (Button) v.findViewById(R.id.remind_me_button);
        noThanks = (Button) v.findViewById(R.id.no_thanks_button);

        rateApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                try { //TODO change packagename
//                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.aldiko.android")));// + appPackageName)));
//                } catch (android.content.ActivityNotFoundException anfe) {
//                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.aldiko.android")));// + appPackageName)));
//                }
//
                Uri uri = Uri.parse("market://details?id=" + getActivity().getPackageName()); //TODO change this back
                Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    startActivity(myAppLinkToMarket);
                } catch (Exception e) {
                    Uri uri2 = Uri.parse("http://play.google.com/store/apps/details?id=" + getActivity().getPackageName()); //TODO change this back
                    startActivity(new Intent(Intent.ACTION_VIEW, uri2));
                }
            }
        });

        later.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getActivity().getSharedPreferences(Keys.PREFS_PREFS_FILE_NAME, Context.MODE_PRIVATE);
                prefs.edit().putInt(Keys.PREFS_RUN_COUNT, 0).commit();
                RateFragment.this.getActivity().finish();
            }
        });

        noThanks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RateFragment.this.getActivity().finish();
            }
        });

        return v;
    }
}
