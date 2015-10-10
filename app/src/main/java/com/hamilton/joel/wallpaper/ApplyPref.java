package com.hamilton.joel.wallpaper;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by joel on 06/10/15.
 */
public class ApplyPref extends Preference {

    private TextView title;

    public ApplyPref(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onBindView(View v) {

        super.onBindView(v);
        title = (TextView) v.findViewById(android.R.id.title);

        if (title != null) {
            title.setGravity(Gravity.CENTER);
            title.setTextColor(Color.parseColor("#FFFFFF"));
            title.setTypeface(null, Typeface.BOLD);

            v.setBackgroundResource(R.drawable.apply_button_background_selector);
        }
    }
}
