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

package es.rafaco.inappdevtools.library.view.overlay.screens.device;

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.documents.DocumentRepository;
import es.rafaco.inappdevtools.library.logic.documents.DocumentType;
import es.rafaco.inappdevtools.library.logic.runnables.RunButton;
import es.rafaco.inappdevtools.library.logic.utils.AppUtils;
import es.rafaco.inappdevtools.library.view.components.flex.CardData;
import es.rafaco.inappdevtools.library.view.overlay.OverlayService;
import es.rafaco.inappdevtools.library.view.overlay.ScreenManager;
import es.rafaco.inappdevtools.library.view.overlay.screens.AbstractFlexibleScreen;

public class DeviceScreen extends AbstractFlexibleScreen {

    public DeviceScreen(ScreenManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "Device";
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

        data.add(DocumentRepository.getCardDataLink(DocumentType.DEVICE_INFO,
                DeviceInfoScreen.class, null));
        data.add(DocumentRepository.getCardDataLink(DocumentType.OS_INFO,
                OsInfoScreen.class, null));

        data.add(new CardData("Shell Terminal",
                "Run shell commands on this device",
                R.string.gmd_computer,
                new Runnable() {
                    @Override
                    public void run() {
                        OverlayService.performNavigation(TerminalScreen.class);
                    }
                }));

        data.add("");
        data.add("Android shortcuts");
        data.add(new RunButton("App Info",
                R.drawable.ic_info_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        IadtController.get().getOverlayHelper().showIcon();
                        AppUtils.openAppSettings(DeviceScreen.this.getContext());
                    }
                }));
        data.add(new RunButton("Dev Options",
                R.drawable.ic_developer_mode_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        IadtController.get().getOverlayHelper().showIcon();
                        AppUtils.openDeveloperOptions(DeviceScreen.this.getContext());
                    }
                }));

        return data;
    }
}
