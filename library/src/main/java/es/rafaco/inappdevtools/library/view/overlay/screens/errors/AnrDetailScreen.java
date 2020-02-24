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

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.storage.db.entities.Anr;
import es.rafaco.inappdevtools.library.logic.utils.ThreadUtils;
import es.rafaco.inappdevtools.library.view.overlay.ScreenManager;
import es.rafaco.inappdevtools.library.view.overlay.screens.Screen;
import es.rafaco.inappdevtools.library.logic.documents.data.DocumentData;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;

public class AnrDetailScreen extends Screen {

    private Anr anr;
    private TextView out;
    private TextView title;
    private TextView subtitle;
    private TextView console;

    public AnrDetailScreen(ScreenManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "Anr detail";
    }

    @Override
    public int getBodyLayoutId() {
        return R.layout.tool_crash_detail_body;
    }

    @Override
    public int getToolbarLayoutId() {
        return R.menu.crash_detail;
    }

    @Override
    protected void onCreate() {
        //Nothing needed
    }

    @Override
    protected void onStart(ViewGroup toolHead) {
        out = toolHead.findViewById(R.id.out);

        title = toolHead.findViewById(R.id.detail_title);
        subtitle = toolHead.findViewById(R.id.detail_subtitle);
        console = toolHead.findViewById(R.id.detail_console);

        if (!TextUtils.isEmpty(getParam())){
            final long anrId = Long.parseLong(getParam());

            ThreadUtils.runOnMain(new Runnable() {
                @Override
                public void run() {
                    anr = IadtController.get().getDatabase().anrDao().findById(anrId);
                    updateOutput();
                }
            });
        }

        /*View codeViewer = toolHead.findViewById(R.id.code_view);
        codeViewer.setVisibility(View.GONE);*/
    }

    private void updateOutput() {
        AnrHelper helper = new AnrHelper();
        DocumentData report = helper.parseToInfoGroup(anr);
        report.removeGroupEntries(1);

        title.setText(anr.getMessage() + " " + Humanizer.getElapsedTimeLowered(anr.getDate()));
        subtitle.setText(anr.getCause());
        out.setText(report.toString());
        console.setText(anr.getStacktrace());
    }

    @Override
    protected void onStop() {
        //Nothing needed
    }

    @Override
    protected void onDestroy() {
        //Nothing needed
    }
}
