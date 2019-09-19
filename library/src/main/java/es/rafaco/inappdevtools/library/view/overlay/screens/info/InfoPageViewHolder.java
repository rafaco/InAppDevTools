package es.rafaco.inappdevtools.library.view.overlay.screens.info;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

//#ifdef ANDROIDX
//@import androidx.recyclerview.widget.RecyclerView;
//#else
import android.support.v7.widget.RecyclerView;
//#endif

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.config.GitConfig;
import es.rafaco.inappdevtools.library.logic.runnables.RunButton;
import es.rafaco.inappdevtools.library.logic.utils.ExternalIntentUtils;
import es.rafaco.inappdevtools.library.storage.files.JsonAsset;
import es.rafaco.inappdevtools.library.storage.files.JsonAssetHelper;
import es.rafaco.inappdevtools.library.view.components.flex.FlexibleAdapter;
import es.rafaco.inappdevtools.library.view.overlay.OverlayService;
import es.rafaco.inappdevtools.library.view.overlay.screens.sources.SourceDetailScreen;

public class InfoPageViewHolder {

    private String title;
    private String overview;
    private String content;

    TextView overviewView;
    TextView bodyView;
    private RecyclerView flexible;

    public InfoPageViewHolder(String title, String overview, String body) {
        this.title = title; //Not in use
        this.content = body;
        this.overview = overview;
    }

    public View onCreatedView(ViewGroup view) {
        overviewView = view.findViewById(R.id.overview);
        flexible = view.findViewById(R.id.flexible_buttons);
        bodyView = view.findViewById(R.id.body);
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

        //TODO: improve, it depends on a hardcoded string
        updateButtons();
    }

    private void updateButtons() {
        List<Object> data = new ArrayList<>();
        int spanCount = 0;

        JsonAssetHelper gitConfig = new JsonAssetHelper(bodyView.getContext(), JsonAsset.GIT_CONFIG);
        boolean gitEnabled = gitConfig.getBoolean(GitConfig.ENABLED);


        if (title.equals("Build") && gitEnabled){
            final String remoteUrl = gitConfig.getString(GitConfig.REMOTE_URL);
            data.add(new RunButton("Remote",
                    R.drawable.ic_public_white_24dp,
                    new Runnable() {
                        @Override
                        public void run() {
                            ExternalIntentUtils.viewUrl(remoteUrl);
                        }
                    }));

            data.add(new RunButton("Local Commits",
                    R.drawable.ic_add_circle_outline_white_24dp,
                    new Runnable() {
                        @Override
                        public void run() {
                            OverlayService.performNavigation(SourceDetailScreen.class,
                                    SourceDetailScreen.buildParams("", "assets/inappdevtools/local_commits.txt", -1));
                        }
                    }));

            data.add(new RunButton("Local Changes",
                    R.drawable.ic_remove_circle_outline_white_24dp,
                    new Runnable() {
                        @Override
                        public void run() {
                            OverlayService.performNavigation(SourceDetailScreen.class,
                                    SourceDetailScreen.buildParams("", "assets/inappdevtools/local_changes.diff", -1));
                        }
                    }));
            spanCount = 3;
        }
        else if (false){

        }

        if (data.isEmpty()){
            flexible.setVisibility(View.GONE);
        }else{
            FlexibleAdapter adapter = new FlexibleAdapter(spanCount, data);
            flexible.setAdapter(adapter);
            flexible.setVisibility(View.VISIBLE);
        }
    }
}
