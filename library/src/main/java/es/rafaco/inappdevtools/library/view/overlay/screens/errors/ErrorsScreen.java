/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2019 Rafael Acosta Alvarez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package es.rafaco.inappdevtools.library.view.overlay.screens.errors;

import android.os.AsyncTask;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

//#ifdef ANDROIDX
//@import androidx.core.view.ViewCompat;
//@import androidx.annotation.NonNull;
//@import androidx.room.InvalidationTracker;
//@import androidx.recyclerview.widget.DefaultItemAnimator;
//@import androidx.recyclerview.widget.DividerItemDecoration;
//@import androidx.recyclerview.widget.LinearLayoutManager;
//@import androidx.recyclerview.widget.RecyclerView;
//#else
import android.arch.persistence.room.InvalidationTracker;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
//#endif

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TooManyListenersException;

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.storage.db.entities.Anr;
import es.rafaco.inappdevtools.library.storage.db.entities.Crash;
import es.rafaco.inappdevtools.library.storage.db.DevToolsDatabase;
import es.rafaco.inappdevtools.library.logic.events.detectors.crash.ForcedRuntimeException;
import es.rafaco.inappdevtools.library.view.utils.RecyclerViewUtils;
import es.rafaco.inappdevtools.library.view.overlay.ScreenManager;
import es.rafaco.inappdevtools.library.logic.navigation.NavigationStep;
import es.rafaco.inappdevtools.library.view.overlay.screens.Screen;
import es.rafaco.inappdevtools.library.view.components.deco.DecoratedToolInfo;
import es.rafaco.inappdevtools.library.view.components.deco.DecoratedToolInfoAdapter;
import es.rafaco.inappdevtools.library.logic.utils.ThreadUtils;

import static es.rafaco.inappdevtools.library.view.utils.Humanizer.getElapsedTimeLowered;

public class ErrorsScreen extends Screen {

    private DecoratedToolInfoAdapter adapter;
    private RecyclerView recyclerView;
    private TextView welcome;

    private Button crashUiButton;
    private Button crashBackgroundButton;
    private Button anrButton;

    private InvalidationTracker.Observer anrObserver;
    private InvalidationTracker.Observer crashObserver;
    private InvalidationTracker tracker;
    private TextView emptyView;

    public ErrorsScreen(ScreenManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "Error log";
    }

    @Override
    public int getBodyLayoutId() { return R.layout.tool_errors_body; }

    @Override
    public int getToolbarLayoutId() {
        return R.menu.errors;
    }

    @Override
    protected void onCreate() {
        //Nothing needed
    }


    @Override
    protected void onStart(ViewGroup view) {
        initView(bodyView);

        getErrors();

        anrObserver = new InvalidationTracker.Observer(new String[]{"anr"}){
            @Override
            public void onInvalidated(@NonNull Set<String> tables) {
                getErrors();
            }
        };
        crashObserver = new InvalidationTracker.Observer(new String[]{"crash"}){
            @Override
            public void onInvalidated(@NonNull Set<String> tables) {
                getErrors();
            }
        };
        tracker = IadtController.get().getDatabase().getInvalidationTracker();
        tracker.addObserver(anrObserver);
        tracker.addObserver(crashObserver);
    }

    @Override
    protected void onStop() {
        tracker.removeObserver(anrObserver);
        tracker.removeObserver(crashObserver);
        anrObserver = null;
        crashObserver = null;
    }

    @Override
    protected void onDestroy() {
        //Nothing needed
    }


    private void initView(View view) {
        welcome = view.findViewById(R.id.welcome);
        welcome.setText(getWelcomeMessage());

        initAdapter();

        anrButton = view.findViewById(R.id.anr_button);
        anrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAnrButton();
            }
        });

        crashUiButton = view.findViewById(R.id.crash_ui_button);
        crashUiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCrashUiButton();
            }
        });

        crashBackgroundButton = view.findViewById(R.id.crash_back_button);
        crashBackgroundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCrashBackgroundButton();
            }
        });
    }

    private void onRefresh() {
        getErrors();
    }

    private void onClearAll() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                DevToolsDatabase db = IadtController.get().getDatabase();
                db.crashDao().deleteAll();
                db.anrDao().deleteAll();

                ArrayList<DecoratedToolInfo> array = new ArrayList<>();
                updateList(array);
            }
        });
    }

    public void onAnrButton() {
        Log.i(Iadt.TAG, "ANR requested, sleeping main thread for a while...");
        ThreadUtils.runOnMain(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep((long)10 * 1000);
                } catch (InterruptedException e) {
                    Log.e(Iadt.TAG, "Something wrong happen", e);
                    Thread.currentThread().interrupt();
                }
            }
        });
    }

    public void onCrashUiButton() {
        Log.i(Iadt.TAG, getContext().getString(R.string.simulated_crash_foreground));
        final Exception cause = new TooManyListenersException(getContext().getString(R.string.simulated_crash_cause));
        ThreadUtils.runOnMain(new Runnable() {
            @Override
            public void run() {
                throw new ForcedRuntimeException(getContext().getString(R.string.simulated_crash_foreground), cause);
            }
        });
    }

    public void onCrashBackgroundButton() {
        Log.i(Iadt.TAG, getContext().getString(R.string.simulated_crash_background));
        final Exception cause = new TooManyListenersException(getContext().getString(R.string.simulated_crash_cause));
        ThreadUtils.runOnBack(new Runnable() {
            @Override
            public void run() {
                throw new ForcedRuntimeException(getContext().getString(R.string.simulated_crash_background), cause);
            }
        });
    }



    public String getWelcomeMessage(){
        return "Crashes and ANRs (memory leaks and logcat exceptions coming soon):";
    }

    private void getErrors(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                DevToolsDatabase db = IadtController.get().getDatabase();
                final ArrayList<DecoratedToolInfo> array = new ArrayList<>();
                List<Crash> crashes = db.crashDao().getAll();
                for (Crash crash : crashes){
                    NavigationStep step = new NavigationStep(CrashDetailScreen.class, String.valueOf(crash.getUid()));
                    array.add(new DecoratedToolInfo(
                            "Crash " + getElapsedTimeLowered(crash.getDate()),
                            crash.getException() + " - " + crash.getMessage(),
                            R.color.rally_orange,
                            crash.getDate(),
                            step));
                }

                /* TODO: AnrDetailScreen
                List<Anr> anrs = db.anrDao().getAll();
                for (Anr anr : anrs){
                    NavigationStep step = new NavigationStep(AnrDetailScreen.class, String.valueOf(anr.getUid()));
                    array.add(new DecoratedToolInfo(
                            "ANR " + getElapsedTimeLowered(anr.getDate()),
                            anr.getCause(),
                            R.color.rally_blue,
                            anr.getDate(),
                            step));
                }*/

                Collections.sort(array, new Comparator<DecoratedToolInfo>() {
                    @Override
                    public int compare(DecoratedToolInfo o1, DecoratedToolInfo o2) {
                        return o2.getOrder().compareTo(o1.getOrder());
                    }
                });

                ThreadUtils.runOnMain(new Runnable() {
                    @Override
                    public void run() {
                        updateList(array);
                    }
                });
            }
        });
    }

    private void initAdapter(){

        adapter = new DecoratedToolInfoAdapter(getContext(), new ArrayList<DecoratedToolInfo>());

        recyclerView = getView().findViewById(R.id.errors_list);
        ViewCompat.setNestedScrollingEnabled(recyclerView, false);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);

        emptyView = getView().findViewById(R.id.empty_errors_list);
    }

    private void updateList(List<DecoratedToolInfo> errors) {
        adapter.replaceAll(errors);
        RecyclerViewUtils.updateEmptyState(recyclerView, emptyView, errors);
    }


    //region [ TOOL BAR ]

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int selected = item.getItemId();
        if (selected == R.id.action_simulate)
        {
            //TODO: dialog or popup to choose type of error
            //TODO: show message before crash or anr
            onCrashUiButton();
        }
        else if (selected == R.id.action_send)
        {
            //TODO: send all errors
            Iadt.showMessage("Not already implemented");
        }
        /*else if (selected == R.id.action_delete)
        {
            onClearAll();
        }*/
        return super.onMenuItemClick(item);
    }

    //endregion
}
