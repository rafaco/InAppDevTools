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
import es.rafaco.inappdevtools.library.logic.config.GitInfo;
import es.rafaco.inappdevtools.library.logic.info.data.InfoReportData;
import es.rafaco.inappdevtools.library.logic.runnables.RunButton;
import es.rafaco.inappdevtools.library.logic.utils.ExternalIntentUtils;
import es.rafaco.inappdevtools.library.storage.files.GitAsset;
import es.rafaco.inappdevtools.library.storage.files.JsonAsset;
import es.rafaco.inappdevtools.library.storage.files.JsonAssetHelper;
import es.rafaco.inappdevtools.library.view.components.flex.FlexibleAdapter;
import es.rafaco.inappdevtools.library.view.overlay.OverlayService;
import es.rafaco.inappdevtools.library.view.overlay.screens.sources.SourceDetailScreen;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;

public class InfoPageViewHolder {

    InfoReportData report;

    TextView overviewView;
    TextView bodyView;
    private RecyclerView flexible;

    public InfoPageViewHolder(InfoReportData reportData) {
        this.report = reportData;
    }

    public View onCreatedView(ViewGroup view) {
        overviewView = view.findViewById(R.id.overview);
        flexible = view.findViewById(R.id.flexible_buttons);
        bodyView = view.findViewById(R.id.body);
        return view;
    }

    public void update(InfoReportData reportData) {
        report = reportData;
        populateUI();
    }

    public void populateUI() {
        overviewView.setVisibility((TextUtils.isEmpty(report.getOverview()) ? View.GONE : View.VISIBLE));
        overviewView.setText(report.getOverview());

        if (TextUtils.isEmpty(report.getEntries().toString())) {
            bodyView.setText("\n\n\n\n"+"( NO INFO )"+ "\n\n\n\n");
        } else {
            //TODO: report.getEntries().toString()
            bodyView.setText(report.toString());
        }

        //TODO: improve, it depends on a hardcoded string
        updateButtons();
    }

    private void updateButtons() {
        List<Object> data = new ArrayList<>();
        int spanCount = 0;

        JsonAssetHelper gitConfig = new JsonAssetHelper(bodyView.getContext(), JsonAsset.GIT_CONFIG);
        boolean gitEnabled = gitConfig.getBoolean(GitInfo.ENABLED);


        if (report.getTitle().equals("Build") && gitEnabled){
            final String remoteUrl = gitConfig.getString(GitInfo.REMOTE_URL);
            data.add(new RunButton("Remote",
                    R.drawable.ic_public_white_24dp,
                    new Runnable() {
                        @Override
                        public void run() {
                            ExternalIntentUtils.viewUrl(remoteUrl);
                        }
                    }));

            String local_commits = gitConfig.getString(GitInfo.LOCAL_COMMITS);
            int local_commits_count = Humanizer.countLines(local_commits);
            boolean hasLocalCommits = local_commits_count > 0;
            boolean hasLocalChanges = gitConfig.getBoolean(GitInfo.HAS_LOCAL_CHANGES);

            if (hasLocalCommits){
                data.add(new RunButton("Local Commits",
                        R.drawable.ic_add_circle_outline_white_24dp,
                        new Runnable() {
                            @Override
                            public void run() {
                                OverlayService.performNavigation(SourceDetailScreen.class,
                                        SourceDetailScreen.buildParams("", GitAsset.LOCAL_COMMITS, -1));
                            }
                        }));
            }

            if (hasLocalChanges) {
                data.add(new RunButton("Local Changes",
                        R.drawable.ic_remove_circle_outline_white_24dp,
                        new Runnable() {
                            @Override
                            public void run() {
                                OverlayService.performNavigation(SourceDetailScreen.class,
                                        SourceDetailScreen.buildParams("", GitAsset.LOCAL_CHANGES, -1));
                            }
                        }));
            }
            spanCount = data.size();
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
