package com.belmedia.fakecallsandsms;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.crashlytics.android.Crashlytics;

/**
 * Created by B.E.L on 07/07/2015.
 */
public class ToggleButtonGroupTableLayout extends TableLayout implements View.OnClickListener {

    private static final String TAG = "ToggleButtonGroupTableLayout";
    private RadioButton activeRadioButton;

    /**
     * @param context
     */
    public ToggleButtonGroupTableLayout(Context context) {
        super(context);
    }

    /**
     * @param context
     * @param attrs
     */
    public ToggleButtonGroupTableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onClick(View v) {
        final RadioButton rb = (RadioButton) v;
        if ( activeRadioButton != null ) {
            activeRadioButton.setChecked(false);
        }
        rb.setChecked(true);
        activeRadioButton = rb;
    }

    /* (non-Javadoc)
     * @see android.widget.TableLayout#addView(android.view.View, int, android.view.ViewGroup.LayoutParams)
     */
    @Override
    public void addView(View child, int index,
                        android.view.ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        setChildrenOnClickListener((TableRow)child);
    }


    /* (non-Javadoc)
     * @see android.widget.TableLayout#addView(android.view.View, android.view.ViewGroup.LayoutParams)
     */
    @Override
    public void addView(View child, android.view.ViewGroup.LayoutParams params) {
        super.addView(child, params);
        setChildrenOnClickListener((TableRow)child);
    }


    private void setChildrenOnClickListener(TableRow tr) {
        final int c = tr.getChildCount();
        for (int i=0; i < c; i++) {
            final View v = tr.getChildAt(i);
            if (v instanceof RadioButton) {
                v.setOnClickListener(this);
                if (((RadioButton) v).isChecked())
                    activeRadioButton = (RadioButton) v;
            }
        }

    }

    public int getCheckedRadioButtonId() {
        if ( activeRadioButton != null ) {
            return activeRadioButton.getId();
        }
        return -1;
    }

    public void setCheckedRadioButtonId(int id) {
        try {
            findViewById(id).performClick();
        } catch (WindowManager.BadTokenException e){
            Crashlytics.logException(e);
        }
    }
}
