package es.rafaco.devtools.view.overlay.layers;

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
import es.rafaco.devtools.view.overlay.OverlayLayersManager;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;


public class MainOverlayLayer extends OverlayLayer {

    private ImageView appIcon;
    private ViewGroup toolContainer;
    private Spinner toolsSpinner;
    private ImageView sizePositionButton;

    public MainOverlayLayer(OverlayLayersManager manager) {
        super(manager);
    }

    @Override
    public Type getType() {
        return Type.MAIN;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.overlay_layer_main;
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
                    manager.setMainVisibility(false);
                }
            });

        view.findViewById(R.id.full_close_button)
            .setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    manager.setMainVisibility(false);
                }
            });

        sizePositionButton = view.findViewById(R.id.full_half_position_button);
        sizePositionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    toogleSizePosition();
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


    public enum SizePosition { FULL, HALF_FIRST, HALF_SECOND}
    private SizePosition currentSizePosition = SizePosition.FULL;

    public void toogleSizePosition() {

        WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) view.getLayoutParams();
        if (currentSizePosition.equals(SizePosition.FULL)) {
            currentSizePosition = SizePosition.HALF_FIRST;
            sizePositionButton.setImageResource(R.drawable.ic_arrow_up_rally_24dp);

            int halfHeight = UiUtils.getDisplaySize(view.getContext()).y / 2;
            layoutParams.height = halfHeight;
            layoutParams.gravity = Gravity.BOTTOM | Gravity.CENTER;
        }
        else if (currentSizePosition.equals(SizePosition.HALF_FIRST)) {
            currentSizePosition = SizePosition.HALF_SECOND;
            sizePositionButton.setImageResource(R.drawable.ic_unfold_more_rally_24dp);

            layoutParams.gravity = Gravity.TOP | Gravity.CENTER;
        }
        else {
            currentSizePosition = SizePosition.FULL;
            sizePositionButton.setImageResource(R.drawable.ic_arrow_down_rally_24dp);

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
