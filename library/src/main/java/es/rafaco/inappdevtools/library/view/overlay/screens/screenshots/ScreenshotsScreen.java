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

package es.rafaco.inappdevtools.library.view.overlay.screens.screenshots;

import android.content.DialogInterface;
import android.os.Handler;

//#ifdef ANDROIDX
//@import androidx.appcompat.app.AlertDialog;
//#else
import android.support.v7.app.AlertDialog;
//#endif

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.view.components.groups.LinearGroupFlexData;
import es.rafaco.inappdevtools.library.view.components.items.ButtonFlexData;
import es.rafaco.inappdevtools.library.storage.db.DevToolsDatabase;
import es.rafaco.inappdevtools.library.storage.db.entities.Screenshot;
import es.rafaco.inappdevtools.library.storage.db.entities.ScreenshotDao;
import es.rafaco.inappdevtools.library.storage.files.utils.FileProviderUtils;
import es.rafaco.inappdevtools.library.view.components.cards.CardData;
import es.rafaco.inappdevtools.library.view.overlay.ScreenManager;
import es.rafaco.inappdevtools.library.view.overlay.layers.Layer;
import es.rafaco.inappdevtools.library.view.overlay.screens.AbstractFlexibleScreen;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;

public class ScreenshotsScreen extends AbstractFlexibleScreen {

    public ScreenshotsScreen(ScreenManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "Screenshots";
    }

    @Override
    protected void onAdapterStart() {
        updateAdapter(getFlexibleData());
    }

    private List<Object> getFlexibleData() {
        List<Object> data = new ArrayList<>();

        data.add("Take screenshots of your running activities, we will auto take a shot of your crashes. You can include them in your reports or share it directly.");

        ButtonFlexData take = new ButtonFlexData( "Take Screenshot",
                R.drawable.ic_add_a_photo_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        onScreenshotButton();
                    }
                });

        ButtonFlexData filter = new ButtonFlexData( "Filter by session",
                R.drawable.ic_history_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        Iadt.showMessage("Coming soon");
                    }
                });

        LinearGroupFlexData linearGroupData = new LinearGroupFlexData();
        linearGroupData.setHorizontal(true);
        linearGroupData.add(take);
        linearGroupData.add(filter);
        data.add(linearGroupData);

        data.add("");

        ScreenshotDao screenshotDao = DevToolsDatabase.getInstance().screenshotDao();
        long currentSession = IadtController.get().getSessionManager().getCurrentUid();
        final List<Screenshot> screenshots = screenshotDao.getAll();
        for (int i = 0; i<screenshots.size(); i++) {
            final Screenshot screenshot = screenshots.get(i);

            String title = String.format("Screenshot %s", screenshot.getUid());
            if (screenshot.getPath().contains("crash_")){
                title +=  " (Crash)";
            }

            String content = "Activity: " + screenshot.getActivityName()
                    + Humanizer.newLine()
                    + "Session: " + screenshot.getSessionId()
                    + Humanizer.newLine()
                    + "Elapsed: " + Humanizer.getElapsedTime(screenshot.getDate());

            CardData cardData = new CardData(title,
                    new Runnable() {
                        @Override
                        public void run() {
                            onCardClick(screenshot);
                        }
                    });
            cardData.setContent(content);
            cardData.setImagePath(screenshot.getPath());
            cardData.setNavIcon(R.string.gmd_open_in_new);

            if (screenshot.getSessionId() == currentSession) {
                cardData.setBgColor(R.color.rally_blue_dark);
            }
            else if (screenshot.getPath().contains("crash_")){
                cardData.setBgColor(R.color.rally_orange_alpha);
            }
            else {
                cardData.setBgColor(R.color.rally_dark_green);
            }
            data.add(cardData);
        }

        return data;
    }

    private void onScreenshotButton() {
        IadtController.get().takeScreenshot();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateAdapter(getFlexibleData());
            }
        }, 1000);
    }

    private void onCardClick(final Screenshot screenshot) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getView().getContext())
                .setTitle("What do you want to do?")
                //.setMessage("To apply your changes safely, we currently need to restart your app. You can also discard them by now.")
                .setCancelable(true)
                .setPositiveButton("VIEW",
                        new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FileProviderUtils.viewExternally("", screenshot.getPath() );
                            }})
                //TODO: add to report
                .setNegativeButton("SHARE",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FileProviderUtils.sendExternally("", screenshot.getPath() );
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setType(Layer.getLayoutType());
        alertDialog.show();
    }
}
