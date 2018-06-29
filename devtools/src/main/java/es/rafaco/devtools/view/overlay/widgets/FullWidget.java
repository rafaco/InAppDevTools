package es.rafaco.devtools.view.overlay.widgets;

import android.animation.LayoutTransition;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;

import java.util.ArrayList;

import es.rafaco.devtools.R;
import es.rafaco.devtools.utils.OnTouchSelectedListener;
import es.rafaco.devtools.utils.UiUtils;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;


public class FullWidget extends Widget {

    private ImageView appIcon;
    private ViewGroup toolContainer;
    private Spinner toolsSpinner;
    private boolean currentHalfMode = false;
    private boolean isFirstHalfMode = false;
    private ImageView halfPositionButton;

    public FullWidget(WidgetsManager manager) {
        super(manager);
    }

    @Override
    public Type getType() {
        return Type.FULL;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.widget_full;
    }

    @Override
    protected WindowManager.LayoutParams getLayoutParams() {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                getLayoutType(),
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.CENTER;

        return params;
    }

    @Override
    protected void beforeAttachView(View view) {
        appIcon = view.findViewById(R.id.full_app_icon);
        toolContainer = view.findViewById(R.id.content_container);

        //expandedView.setVisibility(View.GONE);
        UiUtils.setAppIconAsBackground(appIcon);

        appIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    manager.toogleFullMode(false);
                }
            });

        view.findViewById(R.id.full_close_button)
            .setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    manager.toogleFullMode(false);
                }
            });

        halfPositionButton = view.findViewById(R.id.full_half_position_button);
        halfPositionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    toogleHalfPosition();
                }
            });

        ((FrameLayout)view).setLayoutTransition(new LayoutTransition());
    }

    @Override
    protected void afterAttachView(View view){
        //Hide full view on start
        view.setVisibility(View.GONE);
    }

    public void initToolSelector(ArrayList<String> toolsList) {
        toolsSpinner = getView().findViewById(R.id.tools_spinner);

        final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getView().getContext(),
                android.R.layout.simple_spinner_item, toolsList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        toolsSpinner.setAdapter(spinnerAdapter);

        OnTouchSelectedListener listener = new OnTouchSelectedListener() {
            @Override
            public void onTouchSelected(AdapterView<?> parent, View view, int pos, long id) {
                String title = spinnerAdapter.getItem(pos);
                manager.startTool(title);
            }
        };
        toolsSpinner.setOnItemSelectedListener(listener);
        toolsSpinner.setOnTouchListener(listener);
    }

    public void selectTool(String title) {
        AdapterView.OnItemSelectedListener stored = toolsSpinner.getOnItemSelectedListener();
        toolsSpinner.setOnItemSelectedListener(null);
        toolsSpinner.setSelection(getPosition(title));
        toolsSpinner.setOnItemSelectedListener(stored);
    }

    private int getPosition(String title) {
        int count = toolsSpinner.getAdapter().getCount();
        for (int i = 0; i < count; i++){
            if (toolsSpinner.getAdapter().getItem(i).equals(title)){
                return i;
            }
        }
        return 0;
    }

    public ViewGroup getToolContainer() {
        return toolContainer;
    }


    public enum HalfPosition { FULL, HALF_FIRST, HALF_SECOND}
    private HalfPosition currentHalfPosition = HalfPosition.FULL;

    public void toogleHalfPosition() {

        WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) view.getLayoutParams();
        if (currentHalfPosition.equals(HalfPosition.FULL)) {
            currentHalfPosition = HalfPosition.HALF_FIRST;
            halfPositionButton.setImageResource(R.drawable.ic_arrow_up_rally_24dp);

            int halfHeight = UiUtils.getDisplaySize(view.getContext()).y / 2;
            layoutParams.height = halfHeight;
            layoutParams.gravity = Gravity.BOTTOM | Gravity.CENTER;
        }
        else if (currentHalfPosition.equals(HalfPosition.HALF_FIRST)) {
            currentHalfPosition = HalfPosition.HALF_SECOND;
            halfPositionButton.setImageResource(R.drawable.ic_unfold_more_rally_24dp);

            layoutParams.gravity = Gravity.TOP | Gravity.CENTER;
        }
        else {
            currentHalfPosition = HalfPosition.FULL;
            halfPositionButton.setImageResource(R.drawable.ic_arrow_down_rally_24dp);

            layoutParams.height = MATCH_PARENT;
            layoutParams.gravity = Gravity.TOP | Gravity.CENTER;
        }
        manager.getWindowManager().updateViewLayout(getView(), layoutParams);
    }

    @Override
    public void onConfigurationChange(Configuration newConfig) {
        //TODO
        // if half:  top is left and bottom is right
    }

}
