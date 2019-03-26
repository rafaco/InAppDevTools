package es.rafaco.inappdevtools.library.view.overlay.screens.info;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.view.overlay.OverlayUIService;
import es.rafaco.inappdevtools.library.view.overlay.screens.sources.SourceDetailScreen;

public enum InfoPage {
    APP("App"),
    DEVICE("Device"),
    OS("OS"),
    CONFIG("Build"),
    LIVE("Status"),
    LIBRARY("Tools");


    private final InfoHelper helper;
    private InfoViewHolder viewHolder;
    private String mTitle;
    private String mContent;

    InfoPage(String title) {
        helper = new InfoHelper();
        mTitle = title;

        if (title.equals("Status")) {
            mContent = helper.getStatusReportContent();
        }
        else if (title.equals("App")) {
            mContent = helper.getApkReportContent();
        }
        if (title.equals("Device")) {
            mContent = helper.getDeviceReportContent();
        }
        else if (title.equals("OS")) {
            mContent = helper.getOSReportContent();
        }
        else if (title.equals("Build")) {
            mContent = helper.getBuildReportContent();
        }
        else if (title.equals("Tools")) {
            mContent = helper.getToolsReportContent();
        }

        viewHolder = new InfoViewHolder(title, mContent);
    }

    public String getTitle() {
        return mTitle;
    }
    public String getContent() {
        return mContent;
    }
    public InfoViewHolder getViewHolder() {
        return viewHolder;
    }





    public class InfoViewHolder {

        private final String content;
        private final String title;
        TextView titleView;
        TextView bodyView;
        private AppCompatButton button;

        public InfoViewHolder(String title, String body) {
            this.content = body;
            this.title = title;
        }

        public View onCreatedView(ViewGroup view) {
            titleView = view.findViewById(R.id.headers);
            bodyView = view.findViewById(R.id.body);
            button = view.findViewById(R.id.button);
            return view;
        }

        public void populateUI() {

            //Title disabled
            titleView.setVisibility(View.GONE);
            //titleView.setVisibility((TextUtils.isEmpty(title) ? View.GONE : View.VISIBLE));
            //titleView.setText(title);

            if (TextUtils.isEmpty(content)) {
                bodyView.setText("\n\n\n\n"+"( NO INFO )"+ "\n\n\n\n");
            } else {
                bodyView.setText(content);
            }

            setDiffButton(title.equals("Config"));
        }

        private void setDiffButton(boolean isConfigPage) {
            if (!isConfigPage){
                button.setVisibility(View.GONE);
                button.setOnClickListener(null);
            }else{
                button.setVisibility(View.VISIBLE);
                button.setText("View Diff file");
                int contextualizedColor = ContextCompat.getColor(button.getContext(), R.color.rally_bg_blur);
                button.getBackground().setColorFilter(contextualizedColor, PorterDuff.Mode.MULTIPLY);
                Drawable icon = button.getContext().getResources().getDrawable(R.drawable.ic_code_white_24dp);
                button.setCompoundDrawablesWithIntrinsicBounds( icon, null, null, null);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        OverlayUIService.performNavigation(SourceDetailScreen.class,
                                SourceDetailScreen.buildParams("", "assets/inappdevtools/git.diff", -1));
                    }
                });
            }
        }
    }
}