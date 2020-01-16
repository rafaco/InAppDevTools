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

package es.rafaco.inappdevtools.library.logic.documents;

import android.content.Context;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.documents.generators.AbstractDocumentGenerator;
import es.rafaco.inappdevtools.library.logic.documents.generators.info.AppDocumentGenerator;
import es.rafaco.inappdevtools.library.logic.documents.generators.info.DeviceDocumentGenerator;
import es.rafaco.inappdevtools.library.logic.documents.generators.info.BuildDocumentGenerator;
import es.rafaco.inappdevtools.library.logic.documents.generators.info.LiveDocumentGenerator;
import es.rafaco.inappdevtools.library.logic.documents.generators.info.OSDocumentGenerator;
import es.rafaco.inappdevtools.library.logic.documents.generators.info.ToolsDocumentGenerator;

public enum Document {

    LIVE("Live", true, R.string.gmd_live_tv, LiveDocumentGenerator.class),
    BUILD("Build", true, R.string.gmd_build, BuildDocumentGenerator.class),
    APP("App", true, R.string.gmd_developer_board, AppDocumentGenerator.class),
    OS("OS", true, R.string.gmd_android, OSDocumentGenerator.class),
    DEVICE("Device", true, R.string.gmd_phone_android, DeviceDocumentGenerator.class),
    TOOLS("Iadt", true, R.string.gmd_extension, ToolsDocumentGenerator.class);

    private String title;
    private final boolean isInfo;
    private final int icon;
    private final Class<? extends AbstractDocumentGenerator> generatorClass;

    Document(String title, boolean isInfo, int icon, Class<? extends AbstractDocumentGenerator> generatorClass) {
        this.title = title;
        this.isInfo = isInfo;
        this.icon = icon;
        this.generatorClass = generatorClass;
    }

    public String getTitle() {
        return title;
    }
    public int getIcon() {
        return icon;
    }
    public Class<? extends AbstractDocumentGenerator> getGeneratorClass() {
        return generatorClass;
    }

    public AbstractDocumentGenerator getGenerator() {
        try {
            Class[] cArg = new Class[2];
            cArg[0] = Context.class;
            cArg[1] = Document.class;
            Context context = IadtController.get().getContext();
            return generatorClass.getDeclaredConstructor(cArg).newInstance(context, this);
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

    public static Document[] getInfoDocuments() {
        Document[] values = values();
        List<Document> result = new ArrayList<>();
        for (Document value: values) {
            if (value.isInfo)
                result.add(value);
        }
        return result.toArray(new Document[0]);
    }
}
