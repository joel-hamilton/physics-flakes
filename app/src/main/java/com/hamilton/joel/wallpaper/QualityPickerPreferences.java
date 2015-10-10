package com.hamilton.joel.wallpaper;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.preference.Preference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * Created by joel on 05/10/15.
 */
public class QualityPickerPreferences extends DialogPreference {
    private final int DEFAULT_VALUE = 2;
    private final String TAG = "LOGQualityPickerPrefs";

    private TextView title;
    Spinner picker;
    private int value;


    public QualityPickerPreferences(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public QualityPickerPreferences(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected View onCreateDialogView() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.quality_picker, null);


        picker = (Spinner) v.findViewById(R.id.qualityPicker);
        picker.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        value = 0;
                        Log.i(TAG, "VALUE = " + value);
                        break;
                    case 1:
                        value = 1;
                        Log.i(TAG, "VALUE = " + value);
                        break;
                    default:
                        value = 2;
                        Log.i(TAG, "VALUE = " + value);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return v;
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

    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);
        picker.setSelection(value);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        Log.i(TAG, "onSetInitialValue CALLED");
        if (restorePersistedValue) {
            // Restore existing state
            value = this.getPersistedInt(DEFAULT_VALUE);
        } else {
            // Set default state from the XML attribute
            value = (Integer) defaultValue;
            persistInt(value);
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInteger(index, DEFAULT_VALUE);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        // When the user selects "OK", persist the new value
        if (positiveResult) {
            value = picker.getSelectedItemPosition();
            persistInt(value);
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        // Check whether this Preference is persistent (continually saved)
        if (isPersistent()) {
            // No need to save instance state since it's persistent,
            // use superclass state
            return superState;
        }
        final SavedState myState = new SavedState(superState);
        myState.value = value;
        // Create instance of custom BaseSavedState
        // Set the state's value with the class member that holds current
        // setting value
        return myState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        // Check whether we saved the state in onSaveInstanceState
        if (state == null || !state.getClass().equals(SavedState.class)) {
            // Didn't save the state, so call superclass
            super.onRestoreInstanceState(state);
            return;
        }

        // Cast state to custom BaseSavedState and pass to superclass
        SavedState myState = (SavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());

        // Set this Preference's widget to reflect the restored state
        value = myState.value;
    }

//------------------------------------------------------
private static class SavedState extends Preference.BaseSavedState {
    // Member that holds the setting's value
    // Change this data type to match the type saved by your Preference
    int value;

    public SavedState(Parcelable superState) {
        super(superState);
    }

    public SavedState(Parcel source) {
        super(source);
        // Get the current preference's value
        value = source.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        // Write the preference's value
        dest.writeInt(value);
    }

    // Standard creator object using an instance of this class
    public static final Parcelable.Creator<SavedState> CREATOR =
            new Parcelable.Creator<SavedState>() {

                public SavedState createFromParcel(Parcel in) {
                    return new SavedState(in);
                }

                public SavedState[] newArray(int size) {
                    return new SavedState[size];
                }
            };
}
//------------------------------------------------
}
