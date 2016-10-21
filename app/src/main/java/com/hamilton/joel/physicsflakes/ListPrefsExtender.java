package com.hamilton.joel.physicsflakes;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

/**
 * Created by joel on 14/10/15.
 */
public class ListPrefsExtender extends ListPreference {

    public ListPrefsExtender(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ListPrefsExtender(Context context) {
        super(context);
    }

    @Override
    protected void onBindView(View v) {
        super.onBindView(v);
        TextView title = (TextView) v.findViewById(android.R.id.title);
        if (title != null) {
            title.setGravity(Gravity.CENTER);
            title.setTypeface(null, Typeface.BOLD);
            v.setBackgroundResource(R.drawable.click_background_selector);
        }
    }

    @Override
    protected View onCreateDialogView() {
        SharedPreferences prefs = getSharedPreferences();
        setValue(prefs.getString(getKey(), "0"));
        return super.onCreateDialogView();
    }
}
