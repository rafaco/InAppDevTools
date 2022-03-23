/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2022 Rafael Acosta Alvarez
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

package org.inappdevtools.library.view.overlay.screens.app;

import org.inappdevtools.library.logic.documents.DocumentType;
import org.inappdevtools.library.view.overlay.screens.builds.BuildsScreen;
import org.inappdevtools.library.view.overlay.ScreenManager;
import org.inappdevtools.library.view.overlay.screens.AbstractDocumentScreen;
import org.inappdevtools.library.view.overlay.screens.Screen;

public class AppInfoScreen extends AbstractDocumentScreen {

    public AppInfoScreen(ScreenManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "";
    }

    @Override
    protected DocumentType getDocumentType() {
        return DocumentType.APP_INFO;
    }

    @Override
    protected Class<? extends Screen> getMasterScreenClass() {
        return BuildsScreen.class;
    }
}
