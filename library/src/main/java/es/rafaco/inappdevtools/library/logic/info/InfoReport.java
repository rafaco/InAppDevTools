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

package es.rafaco.inappdevtools.library.logic.info;

import android.content.Context;

import java.lang.reflect.InvocationTargetException;

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.info.reporters.AbstractInfoReporter;
import es.rafaco.inappdevtools.library.logic.info.reporters.DeviceInfoReporter;
import es.rafaco.inappdevtools.library.logic.info.reporters.AppInfoReporter;
import es.rafaco.inappdevtools.library.logic.info.reporters.BuildInfoReporter;
import es.rafaco.inappdevtools.library.logic.info.reporters.LiveInfoReporter;
import es.rafaco.inappdevtools.library.logic.info.reporters.OSInfoReporter;
import es.rafaco.inappdevtools.library.logic.info.reporters.ToolsInfoReporter;

public enum InfoReport {

    LIVE("Live", R.string.gmd_live_tv, LiveInfoReporter.class),
    BUILD("Build", R.string.gmd_build, BuildInfoReporter.class),
    APP("App", R.string.gmd_developer_board, AppInfoReporter.class),
    OS("OS", R.string.gmd_android, OSInfoReporter.class),
    DEVICE("Device", R.string.gmd_phone_android, DeviceInfoReporter.class),
    TOOLS("Iadt", R.string.gmd_extension, ToolsInfoReporter.class);

    private String title;
    private final int icon;
    private final Class<? extends AbstractInfoReporter> reporterClass;

    InfoReport(String title, int icon, Class<? extends AbstractInfoReporter> reporterClass) {
        this.title = title;
        this.icon = icon;
        this.reporterClass = reporterClass;
    }

    public String getTitle() {
        return title;
    }
    public int getIcon() {
        return icon;
    }
    public Class<? extends AbstractInfoReporter> getReporterClass() {
        return reporterClass;
    }

    public AbstractInfoReporter getReporter() {
        try {
            Class[] cArg = new Class[2];
            cArg[0] = Context.class;
            cArg[1] = InfoReport.class;
            Context context = IadtController.get().getContext();
            return reporterClass.getDeclaredConstructor(cArg).newInstance(context, this);
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        catch (InstantiationException e) {
            e.printStackTrace();
        }
        catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }
}
