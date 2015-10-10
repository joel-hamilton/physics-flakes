package com.hamilton.joel.wallpaper;

import android.content.Context;
import android.graphics.Typeface;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by joel on 03/10/15.
 */
public class ClickPreference extends Preference {

    private TextView title;


    public ClickPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onBindView(View v) {

        super.onBindView(v);
        title = (TextView) v.findViewById(android.R.id.title);

        if (title != null) {
            title.setGravity(Gravity.CENTER);
            title.setTypeface(null, Typeface.BOLD);

            v.setBackgroundResource(R.drawable.click_background_selector);
        }
    }
}

