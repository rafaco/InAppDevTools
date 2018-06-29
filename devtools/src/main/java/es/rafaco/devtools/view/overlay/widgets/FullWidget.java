package es.rafaco.devtools.view.overlay.widgets;

import android.animation.LayoutTransition;
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


public class FullWidget extends Widget {

    private ImageView expandedIcon;
    private ViewGroup toolContainer;
    private Spinner toolsSpinner;

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
        //expandedView = view.findViewById(R.id.expanded_view);
        expandedIcon = view.findViewById(R.id.expanded_icon);
        toolContainer = view.findViewById(R.id.content_container);

        //expandedView.setVisibility(View.GONE);
        UiUtils.setAppIconAsBackground(expandedIcon);

        expandedIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    manager.toogleFullMode(false);
                }
            });

        view.findViewById(R.id.close_expanded_view)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        manager.toogleFullMode(false);
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

}
