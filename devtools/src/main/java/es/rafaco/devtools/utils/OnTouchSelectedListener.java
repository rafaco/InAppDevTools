package es.rafaco.devtools.utils;

import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;

public abstract class OnTouchSelectedListener implements AdapterView.OnItemSelectedListener, View.OnTouchListener {

    boolean userSelect = false;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        userSelect = true;
        return false;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        if (userSelect) {
            onTouchSelected(parent, view, pos, id);
            userSelect = false;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public abstract void onTouchSelected(AdapterView<?> parent, View view, int pos, long id);
}
