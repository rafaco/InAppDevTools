package es.rafaco.devtools.utils;

import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;

import static es.rafaco.devtools.utils.AppUtils.isEmulator;

public abstract class OnTouchSelectedListener
        implements AdapterView.OnItemSelectedListener,
                    AdapterView.OnItemClickListener,
                    View.OnTouchListener,
                    View.OnClickListener {

    boolean userSelect = false;

    public abstract void onTouchSelected(AdapterView<?> parent, View view, int pos, long id);

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
    public void onClick(View v){
        //For compatibility with emulator
        userSelect = true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
        if (userSelect) {
            onTouchSelected(parent, view, pos, id);
            userSelect = false;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}
