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

package es.rafaco.inappdevtools.library.view.overlay.screens.react;


import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.builds.BuildFilesRepository;
import es.rafaco.inappdevtools.library.storage.files.IadtPath;
import es.rafaco.inappdevtools.library.storage.files.utils.ReactNativeHelper;
import es.rafaco.inappdevtools.library.view.components.items.ButtonFlexData;
import es.rafaco.inappdevtools.library.view.components.items.OverviewData;
import es.rafaco.inappdevtools.library.view.components.items.TextFlexData;
import es.rafaco.inappdevtools.library.view.overlay.OverlayService;
import es.rafaco.inappdevtools.library.view.overlay.ScreenManager;
import es.rafaco.inappdevtools.library.view.overlay.screens.AbstractFlexibleScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.sources.SourceDetailScreen;

public class ReactNativeScreen extends AbstractFlexibleScreen {

    public ReactNativeScreen(ScreenManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "";
    }

    @Override
    public int getSpanCount() {
        return 2;
    }

    @Override
    protected void onAdapterStart() {
        updateAdapter(getFlexibleData());
    }

    private List<Object> getFlexibleData() {
        List<Object> data = new ArrayList<>();

        ReactNativeHelper helper = new ReactNativeHelper();

        OverviewData overviewData = new OverviewData("React Native",
                "Version: " + helper.getVersion(),
                R.string.gmd_filter_vintage,
                R.color.rally_white);
        data.add(overviewData);

        TextFlexData coming_soon = new TextFlexData("          Coming soon");
        coming_soon.setFontColor(R.color.iadt_primary);
        coming_soon.setSize(TextFlexData.Size.EXTRA_LARGE);
        data.add(coming_soon);

        data.add("");
        data.add("Shortcuts");
        data.add(new ButtonFlexData("View config file",
                R.drawable.ic_code_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        long currentUid = IadtController.get().getSessionManager().getCurrentUid();
                        String path = BuildFilesRepository.getBuildFile(currentUid,
                                IadtPath.REACT_NATIVE_CONFIG_FILE);
                        String params = SourceDetailScreen.buildInternalParams(path);
                        OverlayService.performNavigation(SourceDetailScreen.class, params);
                    }
                }));
        return data;
    }
}
