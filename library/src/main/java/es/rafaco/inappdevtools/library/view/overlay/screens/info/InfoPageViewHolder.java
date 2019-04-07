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

public class InfoPageViewHolder {

    private String title;
    private String overview;
    private String content;

    TextView overviewView;
    TextView bodyView;
    private AppCompatButton button;

    public InfoPageViewHolder(String title, String overview, String body) {
        this.title = title; //Not in use
        this.content = body;
        this.overview = overview;
    }

    public View onCreatedView(ViewGroup view) {
        overviewView = view.findViewById(R.id.overview);
        bodyView = view.findViewById(R.id.body);
        button = view.findViewById(R.id.button);
        return view;
    }

    public void updateUI(String overview, String body) {
        this.content = body;
        this.overview = overview;

        populateUI();
    }

    public void populateUI() {
        overviewView.setVisibility((TextUtils.isEmpty(overview) ? View.GONE : View.VISIBLE));
        overviewView.setText(overview);

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
