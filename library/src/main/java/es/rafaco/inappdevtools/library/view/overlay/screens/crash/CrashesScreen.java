/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2020 Rafael Acosta Alvarez
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

package es.rafaco.inappdevtools.library.view.overlay.screens.crash;

import android.os.AsyncTask;
import android.view.MenuItem;

//#ifdef ANDROIDX
//@import androidx.room.InvalidationTracker;
//@import androidx.annotation.NonNull;
//#else
import android.arch.persistence.room.InvalidationTracker;
import android.support.annotation.NonNull;
//#endif

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.utils.ThreadUtils;
import es.rafaco.inappdevtools.library.storage.db.IadtDatabase;
import es.rafaco.inappdevtools.library.storage.db.entities.Crash;
import es.rafaco.inappdevtools.library.view.components.cards.CardData;
import es.rafaco.inappdevtools.library.view.dialogs.ForceCrashDialog;
import es.rafaco.inappdevtools.library.view.overlay.OverlayService;
import es.rafaco.inappdevtools.library.view.overlay.ScreenManager;
import es.rafaco.inappdevtools.library.view.overlay.screens.AbstractFlexibleScreen;

import static es.rafaco.inappdevtools.library.view.utils.Humanizer.getElapsedTimeLowered;

public class CrashesScreen extends AbstractFlexibleScreen {

    private InvalidationTracker.Observer crashObserver;
    private InvalidationTracker tracker;

    public CrashesScreen(ScreenManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "Crashes";
    }

    @Override
    public int getToolbarLayoutId() {
        return R.menu.errors;
    }
    
    @Override
    protected void onAdapterStart() {
        requestData();
    }

    private void requestData() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                List<Crash> crashes = IadtDatabase.get().crashDao().getAll();
                final List<Object> array = prepareData(crashes);
                ThreadUtils.runOnMain(new Runnable() {
                    @Override
                    public void run() {
                        updateAdapter(array);
                    }
                });
            }
        });
    }

    private List<Object> prepareData(List<Crash> crashes) {
        List<Object> data = new ArrayList<>();
        if (crashes ==null || crashes.isEmpty()){
            CardData cardData = new CardData("There are no crashes.",
                    new Runnable() {
                        @Override
                        public void run() {
                            IadtController.get().getDialogManager().load(new ForceCrashDialog());
                        }
                    });
            cardData.setContent("Congratulations, your app haven't crashed! You can force a crash to test this features");
            cardData.setTitleColor(R.color.material_green);
            data.add(cardData);
            return data;
        }

        for (int i = 0; i<crashes.size(); i++) {
            final Crash crash = crashes.get(i);
            boolean isCurrent = (i==0);

            CardData cardData = new CardData(crash.getException() + ", " + getElapsedTimeLowered(crash.getDate()),
                    new Runnable() {
                        @Override
                        public void run() {
                            OverlayService.performNavigation(CrashScreen.class,
                                    crash.getUid() + "");
                        }
                    });
            cardData.setContent(crash.getMessage());
            if (isCurrent) {
                cardData.setBgColor(R.color.material_error);
            }
            else {
                cardData.setBgColor(R.color.rally_orange_alpha);
            }
            data.add(cardData);
        }
        return data;
    }

    @Override
    protected void onResume() {
        crashObserver = new InvalidationTracker.Observer(new String[]{"crash"}){
            @Override
            public void onInvalidated(@NonNull Set<String> tables) {
                requestData();
            }
        };
        tracker = IadtDatabase.get().getInvalidationTracker();
        tracker.addObserver(crashObserver);
    }

    @Override
    protected void onPause() {
        tracker.removeObserver(crashObserver);
        crashObserver = null;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int selected = item.getItemId();
        if (selected == R.id.action_simulate) {
            IadtController.get().getDialogManager().load(new ForceCrashDialog());
        }
        else if (selected == R.id.action_send) {
            Iadt.buildMessage("TODO: Not already implemented")
                    .isWarning().fire();
        }
        return super.onMenuItemClick(item);
    }

}
