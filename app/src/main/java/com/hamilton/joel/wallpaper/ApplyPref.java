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
    private View v;
    private ViewGroup vg;


    public ApplyPref(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onBindView(View v) {

        this.v = v;
        super.onBindView(v);
        title = (TextView) v.findViewById(android.R.id.title);


        if (title != null) {
            title.setGravity(Gravity.CENTER);
            title.setTextColor(Color.parseColor("#FFFFFF"));
            title.setTypeface(null, Typeface.BOLD);

            v.setBackgroundColor(getContext().getResources().getColor(R.color.backgroundAccentColorDark));
//            v.setBackgroundColor(getContext().getResources().getColor(R.color.backgroundColor));

        }


    }
    //
    @Override
    protected View onCreateView(ViewGroup container) {
        this.vg = container;

//
        title = (TextView) container.findViewById(android.R.id.title);
        if (title != null) {
//            Log.i("HERE", "onCreateView ");
//            title.setGravity(Gravity.CENTER);
        }

        return super.onCreateView(container);
    }

}
