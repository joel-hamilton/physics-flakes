package com.hamilton.joel.physicsflakes;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by joel on 13/10/15.
 */
public class MyDialog extends Dialog {
    Button button;

    public MyDialog(Context context) {
        super(context);
    }

    public MyDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected MyDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_dialog);
        button = (Button) findViewById(R.id.dialog_button);
        button.setText(getContext().getResources().getString(R.string.cancel));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDialog.this.dismiss();
            }
        });
    }

}
