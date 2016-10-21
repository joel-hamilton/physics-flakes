package com.hamilton.joel.physicsflakes;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * Created by joel on 15/10/15.
 */
public class AboutFragment extends Fragment {
    private final String TAG = "LOGAboutFragment";

    Button rate;
    Button contact;
    LinearLayout iconHolder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.about_fragment, container, false);
        v.setBackgroundResource(R.drawable.rounded_corners_background);

        iconHolder = (LinearLayout) v.findViewById(R.id.icon_holder_layout);
        rate = (Button) v.findViewById(R.id.about_rate_button);
        contact = (Button) v.findViewById(R.id.about_contact_button);

        Bitmap iconBitmap = AnalyticsApplication.getHeightScaledBitmapFromRes(R.drawable.snowflake_icon,
                (int)getResources().getDimension(R.dimen.iconAsterisk));
        for (int i = 0; i < 3; i++) {
            ImageView temp = new ImageView(getActivity());
            temp.setImageBitmap(iconBitmap);
            temp.setId(i);
            iconHolder.addView(temp);
        }

        rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("market://details?id=" + getActivity().getPackageName()); //TODO change this back
                Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    startActivity(myAppLinkToMarket);
                } catch (Exception e) {
                    Uri uri2 = Uri.parse("http://play.google.com/store/apps/details?id=" + getActivity().getPackageName()); //TODO change this back
                    startActivity(new Intent(Intent.ACTION_VIEW, uri2));// + appPackageName)));
                }
            }
        });

        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] email = new String[] {"joelhamilton5@gmail.com"};

                Intent i = new Intent(Intent.ACTION_SENDTO);
                i.setType("text/plain");
                i.setData(Uri.parse("mailto:"));
                i.putExtra(Intent.EXTRA_EMAIL, email);
                i.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
                startActivity(i);
                getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        return v;
    }
}
