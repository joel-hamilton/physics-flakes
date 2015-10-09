package com.hamilton.joel.wallpaper;

import android.content.Context;
import android.graphics.Typeface;
import android.preference.CheckBoxPreference;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

/**
 * Created by joel on 03/10/15.
 */
public class CheckboxPreferences extends CheckBoxPreference {

    private TextView title;
    private TextView summary;


    public CheckboxPreferences(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CheckboxPreferences(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CheckboxPreferences(Context context) {
        super(context);
    }


    @Override
    protected void onBindView(View v) {
        super.onBindView(v);
        title = (TextView) v.findViewById(android.R.id.title);
        summary = (TextView) v.findViewById(android.R.id.summary);

        if (title != null) {
            title.setGravity(Gravity.CENTER);
            title.setTypeface(null, Typeface.BOLD);
//            v.setBackgroundColor(getContext().getResources().getColor(R.color.backgroundDark));
//            v.setBackgroundColor(getContext().getResources().getColor(R.color.backgroundColor));

            v.setBackgroundResource(R.drawable.click_background_selector);
            if (summary != null) {


//                summary.setGravity(Gravity.CENTER);
//                summary.setTypeface(null, Typeface.BOLD);
//                v.setBackgroundColor(getContext().getResources().getColor(R.color.backgroundColorDarker));
            }
        }
    }
}
