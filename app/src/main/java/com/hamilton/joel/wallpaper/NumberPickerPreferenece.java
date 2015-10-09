package com.hamilton.joel.wallpaper;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.preference.Preference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;

/**
 * Created by joel on 02/10/15.
 */
public class NumberPickerPreferenece extends DialogPreference {
    private final String TAG = "LOGNumberPickerPref";
    private final int DEFAULT_VALUE = 5;

    private TextView title;

    private int newValue;
    private int currentValue;
    private int value;
    NumberPicker picker;

    public NumberPickerPreferenece(Context context, AttributeSet attrs) {
        super(context, attrs);

        setDialogLayoutResource(R.layout.numberpicker_dialog);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);

        setDialogIcon(null);
    }

    @Override
    protected View onCreateDialogView() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.numberpicker_dialog, null);


        picker = (NumberPicker) v.findViewById(R.id.numberPicker);
        picker.setMinValue(1);
        picker.setMaxValue(5);
        picker.setWrapSelectorWheel(false);
        picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        picker.setValue(value);

        return v;
    }

    @Override
    protected void onBindView(View v) {
        super.onBindView(v);

        title = (TextView) v.findViewById(android.R.id.title);
        if (title != null) {
            title.setGravity(Gravity.CENTER);
            title.setTypeface(null, Typeface.BOLD);
//            v.setBackgroundColor(getContext().getResources().getColor(R.color.backgroundDark));
            v.setBackgroundColor(getContext().getResources().getColor(R.color.backgroundColor));

        }
    }

    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);
        picker.setValue(value);

    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            // Restore existing state
            value = this.getPersistedInt(DEFAULT_VALUE);
//            currentValue = this.getPersistedInt(DEFAULT_VALUE);
        } else {
            // Set default state from the XML attribute
            value = (Integer) defaultValue;
//            currentValue = (Integer) defaultValue;
            persistInt(value);
//            persistInt(currentValue);
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
//            Log.i(TAG, "dialog OK clicked, saving newValue");
//            newValue = picker.getValue();
            value = picker.getValue();
//            currentValue = picker.getValue();
            persistInt(value);
//            persistInt(newValue);
//            Log.i(TAG, "newValue = " + newValue);
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
//        Log.i(TAG, "onSaveInstanceState CALLED");
        final Parcelable superState = super.onSaveInstanceState();
        // Check whether this Preference is persistent (continually saved)
        if (isPersistent()) {
//            Log.i(TAG, "onSaveInstanceState IS PERSISTANT: not saving value");
//            myState.value = newValue;
            // No need to save instance state since it's persistent,
            // use superclass state
            return superState;
        }
            final SavedState myState = new SavedState(superState);
            myState.value = value;
//            Log.i(TAG, "out myState.value = " + myState.value);

        // Create instance of custom BaseSavedState
        // Set the state's value with the class member that holds current
        // setting value
//        newValue = picker.getValue();
//        myState.value = newValue;
//        Log.i(TAG, "onSaveInstanceState SETTING mystate.VALUE TO = ");
        return myState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
//        Log.i(TAG, "onRestoreInstanceState CALLED");
        // Check whether we saved the state in onSaveInstanceState
        if (state == null || !state.getClass().equals(SavedState.class)) {
            // Didn't save the state, so call superclass
//            Log.i(TAG, "onRestoreInstanceState NOT EXECUTED");
            super.onRestoreInstanceState(state);
            return;
        }

        // Cast state to custom BaseSavedState and pass to superclass
        SavedState myState = (SavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());

        // Set this Preference's widget to reflect the restored state
//        Log.i(TAG, "onRestoreInstanceState newValue SET TO = " + myState.value);
        value = myState.value;
//        newValue = myState.value;
    }

//------------------------------------------------------
    private static class SavedState extends BaseSavedState {
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
